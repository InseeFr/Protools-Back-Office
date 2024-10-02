package fr.insee.protools.backend.service.context.resolvers;

import fr.insee.protools.backend.dto.Communication;
import fr.insee.protools.backend.dto.Lot;
import fr.insee.protools.backend.service.context.IContextService;
import fr.insee.protools.backend.service.exception.IncoherentBPMNContextError;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import fr.insee.protools.backend.service.utils.log.TimeLogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAmount;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;

/**
 * Used to make protools context variables available in BPMN expressions
 * example:
 *   <intermediateCatchEvent id="id1" name="dummy">
 *     <timerEventDefinition>
 *       <timeDate>${partitionCtxResolver.getCollectionStartDate(execution,current_partition_id)}</timeDate>
 *     </timerEventDefinition>
 *   </intermediateCatchEvent>
 * <p>
 *   Flowable doc : https://documentation.flowable.com/latest/develop/be/be-expressions#customization
 */
@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")     //used in BPMNS
public class PartitionCtxResolver {

    //Date in a far away future
    private static final Instant farAwayInstant = LocalDate.parse("9999-12-31").atStartOfDay(ZoneId.of("Europe/Paris")).toInstant();
    private final IContextService protoolsContext;
    //Window during that we allow to send communcation whose date is passed
    private final TemporalAmount maxSendCommunicationWindowHours = Duration.ofHours(12);

    private Optional<Lot> getPartition(ExecutionEntity execution, String partitionId) {
        var context = protoolsContext.getContextDtoByProcessInstance(execution.getProcessInstanceId());
         return context.getLots().stream()
                .filter(x -> String.valueOf(x.getId()).equalsIgnoreCase(partitionId))
                .findAny();
    }

    public Instant getCollectionStartDate(ExecutionEntity execution, String partitionId) {
        return getPartition(execution,partitionId)
                .orElseThrow(() -> new IncoherentBPMNContextError("Tried to get Collection Start date on undefined partition"))
                .getDateDebutCollecte();
    }

    public Instant getCollectionEndDate(ExecutionEntity execution, String partitionId) {
        Instant partitionEndDate = getPartition(execution, partitionId).orElseThrow(() -> new IncoherentBPMNContextError("Tried to get Collection Start date on undefined partition")).getDateFinCollecte();
        log.info("getCollectionEndDate: ProcessInstanceId={} - partitionId={} - dateFinCollecte={}", execution.getProcessInstanceId(), partitionId, TimeLogUtils.format(partitionEndDate));
        return partitionEndDate;
    }

    public String getCommunicationType(ExecutionEntity execution, String partitionId, String communicationId) {
        return getPartition(execution,partitionId)
                .orElseThrow(() -> new IncoherentBPMNContextError("Tried to get communication type on an unknown partition : "+partitionId))
                .getCommunications().stream()
                .filter(x -> String.valueOf(x.getId()).equalsIgnoreCase(communicationId))
                .findAny()
                .orElseThrow(() -> new IncoherentBPMNContextError("Tried to get communication type on an unknown communication : "+communicationId))
                .getTypeCommunication().toString();
    }

    /**
     * Considers all communications with a due date in the future, as well as those up to 12 hours in the past.
     * <p>
     * This allows scheduling even if two communications have identical or very close send times.
     * It also handles cases where the process might experience slight delays.
     * <p>
     * If there is no communication to be sent, the returned instant is set to a distant future (year 9999).
     * <b>It also sets the VARNAME_CURRENT_COMMUNICATION_ID process variable with the identifier of the next scheduled communication</b>.
     *
     * @param execution   used to retrieve the Protools context
     * @param partitionId the current partition identifier used to retrieve the communications in the context
     * @return an Instant that can be used to schedule a timer for the next communication to be sent
     */
    public Instant scheduleNextCommunication(ExecutionEntity execution, String partitionId) {
        //Get the current time
        Instant now = Instant.now();

        // Retrieve the partition or throw an error if not found
        Lot partition =  getPartition(execution,partitionId)
                .orElseThrow(() -> new IncoherentBPMNContextError("Tried to get schedule next communication on an unknown partition : "+partitionId));

        Set<String> scheduledCommunicationIds = FlowableVariableUtils.getVariableOrNull(execution, VARNAME_ALREADY_SCHEDULED_COMMUNICATION_ID_SET, Set.class);
        Set<String> errorCommunicationIds = FlowableVariableUtils.getVariableOrNull(execution, VARNAME_COMMUNICATION_ERROR_ID_SET, Set.class);

        if (scheduledCommunicationIds == null) {
            scheduledCommunicationIds = new HashSet<>();
        }
        if (errorCommunicationIds == null) {
            errorCommunicationIds = new HashSet<>();
        }

        // Check if the collection period has ended
        if (partition.getDateFinCollecte().isBefore(now)) {
            log.error("ProcessInstanceId={} - partitionId={} - dateFinCollecte={} is in the past, returning an instant to a far away future",
                    execution.getProcessInstanceId(), partitionId, TimeLogUtils.format(partition.getDateFinCollecte()));
            return farAwayInstant;
        }

        Instant nextCommEcheance = null;
        String nextCommId = null;

        // Iterate through communications in the partition
        for (Communication communication : partition.getCommunications()) {
            if (communication == null || communication.getEcheance() == null || scheduledCommunicationIds.contains(String.valueOf(communication.getId())) || errorCommunicationIds.contains(communication.getId().toString())) {
                continue; // Skip already processed or errored communications
            }

            // Check if the communication is too far past the deadline (window)
            if (communication.getEcheance().plus(maxSendCommunicationWindowHours).isBefore(now)) {
                log.warn("Partition id={} : Communication id={} has expired (echeance: {}), marking it as error", partitionId, communication.getId(), TimeLogUtils.format(communication.getEcheance()));

                // Add communication to the error set and update the process variable
                errorCommunicationIds.add(String.valueOf(communication.getId()));
                execution.getRootProcessInstance().setVariableLocal(VARNAME_COMMUNICATION_ERROR_ID_SET, errorCommunicationIds);
            }

            // Find the communication with the earliest echeance
            else if (nextCommEcheance == null || nextCommEcheance.isAfter(communication.getEcheance())) {
                nextCommEcheance = communication.getEcheance();
                nextCommId = String.valueOf(communication.getId());
            }
        }

        // If no communication is found, set to a far future instant
        if (nextCommId == null) {
            nextCommEcheance = farAwayInstant;
        } else {
            // Schedule the next communication and update process variables
            scheduledCommunicationIds.add(nextCommId);
            execution.getParent().setVariableLocal(VARNAME_CURRENT_COMMUNICATION_ID, nextCommId);
            execution.getRootProcessInstance().setVariableLocal(VARNAME_ALREADY_SCHEDULED_COMMUNICATION_ID_SET, scheduledCommunicationIds);
        }
        log.info("scheduleNextCommunication: part={} - nextCommId={} - nextComEcheance={}", partitionId, nextCommId, TimeLogUtils.format(nextCommEcheance));
        return nextCommEcheance;
    }

}