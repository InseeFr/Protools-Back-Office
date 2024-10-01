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
 *
 *   <intermediateCatchEvent id="id1" name="dummy">
 *     <timerEventDefinition>
 *       <timeDate>${partitionCtxResolver.getCollectionStartDate(execution,current_partition_id)}</timeDate>
 *     </timerEventDefinition>
 *   </intermediateCatchEvent>
 *
 *
 *   Flowable doc : https://documentation.flowable.com/latest/develop/be/be-expressions#customization
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PartitionCtxResolver {

    private final IContextService protoolsContext;

    //Window during that we allow to send communcation whose date is passed
    private final TemporalAmount maxSendCommunicationWindowHours = Duration.ofHours(12);;

    //Date in a far away future
    private static final Instant farAwayInstant = LocalDate.parse("9999-12-31").atStartOfDay(ZoneId.of("Europe/Paris")).toInstant();


    private Optional<Lot> getPartition(ExecutionEntity execution, String partitionId){
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
        Instant partitionEndDate=getPartition(execution,partitionId)
                .orElseThrow(() -> new IncoherentBPMNContextError("Tried to get Collection Start date on undefined partition"))
                .getDateFinCollecte();
        log.info("getCollectionEndDate: ProcessInstanceId={} - partitionId={} - dateFinCollecte={}",
                execution.getProcessInstanceId(), partitionId, TimeLogUtils.format(partitionEndDate));
        return partitionEndDate;
    }

    @SuppressWarnings("unused")     //used in BPMNS
    public String getCommunicationType(ExecutionEntity execution, String partitionId, String communicationId) {
        return getPartition(execution,partitionId)
                .orElseThrow(() -> new IncoherentBPMNContextError("Tried to get communication type on an unknown partition : "+partitionId))
                .getCommunications().stream()
                .filter(x -> String.valueOf(x.getId()).equalsIgnoreCase(communicationId))
                .findAny()
                .orElseThrow(() -> new IncoherentBPMNContextError("Tried to get communication type on an unknown communication : "+communicationId))
                .getTypeCommunication().toString();
    }


    //TODO: documenter l'histoire des 24H
    @SuppressWarnings("unused") //Used in BPMN
    public Instant scheduleNextCommunication(ExecutionEntity execution, String partitionId) {
        //TODO: logs?
        Instant now = Instant.now();

        //Context
        Lot partition =  getPartition(execution,partitionId)
                .orElseThrow(() -> new IncoherentBPMNContextError("Tried to get schedule next communication on an unknown partition : "+partitionId));

        Set<String> scheduledCommunicationIds = FlowableVariableUtils.getVariableOrNull(execution, VARNAME_ALREADY_SCHEDULED_COMMUNICATION_ID_SET, Set.class);
        Set<String> errorCommunicationIds = FlowableVariableUtils.getVariableOrNull(execution, VARNAME_COMMUNICATION_ERROR_ID_SET, Set.class);


        if(scheduledCommunicationIds==null){
            scheduledCommunicationIds=new HashSet<>();
        }
        if(errorCommunicationIds==null){
            errorCommunicationIds=new HashSet<>();
        }

        //Collection End if after current time
        if(partition.getDateFinCollecte().isBefore(now)){
            log.error("ProcessInstanceId={} - partitionId={} - dateFinCollecte={} is in the past  ==> Timer is set to a far away Instant ",
                    execution.getProcessInstanceId(), partitionId, TimeLogUtils.format(partition.getDateFinCollecte()));
            return farAwayInstant;
        }

        Instant nextCommEcheance = null;
        String nextCommId=null;

        for (Communication communication: partition.getCommunications()){

            //null is an error in config so we ignore it;
            // and we also discard communications that have already been sent or that have been marked as in error
            if(communication==null || communication.getEcheance()==null || scheduledCommunicationIds.contains(String.valueOf(communication.getId())) || errorCommunicationIds.contains(communication.getId().toString())){
                continue;
            }

            //verify that echeance of communication to be sent is not too old
            if(communication.getEcheance().plus(maxSendCommunicationWindowHours).isBefore(now)){
                log.warn("Partition id={} : Communication id={} has not been sent. Its echeance [ {} ] is too far past so it will not be sent",
                        partitionId,communication.getId(),TimeLogUtils.format(communication.getEcheance()));

                errorCommunicationIds.add(String.valueOf(communication.getId()));
                execution.getRootProcessInstance().setVariableLocal(VARNAME_COMMUNICATION_ERROR_ID_SET,errorCommunicationIds);
            }

            //Among the not treated communications we compute the one with the first echeance
            else if(nextCommEcheance == null || nextCommEcheance.isAfter(communication.getEcheance())){
                nextCommEcheance=communication.getEcheance();
                nextCommId=String.valueOf(communication.getId());
            }
        }

        //If no communication left (all have already been treated) ==> far away future
        if(nextCommId==null){
            nextCommEcheance = farAwayInstant;
        }
        else{
            scheduledCommunicationIds.add(nextCommId);

            execution.getParent().setVariableLocal(VARNAME_CURRENT_COMMUNICATION_ID,nextCommId);
            execution.getRootProcessInstance().setVariableLocal(VARNAME_ALREADY_SCHEDULED_COMMUNICATION_ID_SET,scheduledCommunicationIds);
        }

        log.info("scheduleNextCommunication: part="+partitionId+" - nextCom="+TimeLogUtils.format(nextCommEcheance));
        return nextCommEcheance;
    }

}