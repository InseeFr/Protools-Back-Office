package fr.insee.protools.backend.service.platine.delegate.pilotatage;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.classic.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.dto.internal.CommunicationRequestDetails;
import fr.insee.protools.backend.dto.platine.pilotage.PlatinePilotageCommunicationEventDto;
import fr.insee.protools.backend.dto.platine.pilotage.PlatinePilotageCommunicationEventType;
import fr.insee.protools.backend.exception.ProtoolsProcessFlowBPMNError;
import fr.insee.protools.backend.service.platine.service.PlatinePilotageServiceImpl;
import fr.insee.protools.backend.service.utils.delegate.IDelegateWithVariables;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class PlatinePilotageCreateCommunicationEventTaskRESTTestDelegate implements IDelegateWithVariables {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    PlatinePilotageServiceImpl platinePilotageService;

    @InjectMocks
    PlatinePilotageCreateCommunicationEventTaskREST task;

    @Override
    public JavaDelegate getTaskUnderTest() {
        return task;
    }

    @Override
    public Map<String, Class> getVariablesAndTypes() {
        return Map.of(
                VARNAME_CURRENT_COMMUNICATION_ID, String.class,
                VARNAME_CURRENT_PARTITION_ID, String.class,
                VARNAME_COMMUNICATION_REQUEST_ID_AND_STATUS_FOR_INTERRO_ID_MAP, Map.class
        );
    }

    static Stream<Arguments> initExecuteParametersOK() {
        return Stream.of(
                Arguments.of(Map.of(), "comId", Boolean.FALSE),
                Arguments.of(Map.of("interroId1", new CommunicationRequestDetails("comReqId1",PlatinePilotageCommunicationEventType.COMMUNICATION_STATE_SENT)), "xxx", Boolean.TRUE),
                Arguments.of(Map.of(
                        "interroId1", new CommunicationRequestDetails("comReqId1",PlatinePilotageCommunicationEventType.COMMUNICATION_STATE_SENT),
                        "interroId2", new CommunicationRequestDetails("comReqId2",PlatinePilotageCommunicationEventType.COMMUNICATION_STATE_SENT),
                        "interroId3", new CommunicationRequestDetails("comReqId3",PlatinePilotageCommunicationEventType.COMMUNICATION_STATE_SENT)),
                        "xxx", Boolean.TRUE)
        );
    }

    @ParameterizedTest
    @MethodSource("initExecuteParametersOK")
    @DisplayName("Test execute method - should work and make correct call to service")
    void execute_should_work_whenEverythingOk(Map<String, CommunicationRequestDetails> commIdByInterro, String communcationId, Boolean expectedCallToService) {
        //Prepare
        DelegateExecution execution = createMockedExecution();
        doReturn(communcationId).when(execution).getVariable(VARNAME_CURRENT_COMMUNICATION_ID, String.class);
        doReturn(dumyId).when(execution).getVariable(VARNAME_CURRENT_PARTITION_ID, String.class);
        doReturn(commIdByInterro).when(execution).getVariable(VARNAME_COMMUNICATION_REQUEST_ID_AND_STATUS_FOR_INTERRO_ID_MAP, Map.class);

        //Execute method under test
        task.execute(execution);

        //verify

        //Case where the service is not supposed to be called
        if (!expectedCallToService) {
            verify(platinePilotageService, never()).postCommunicationEvents(any());
            return;
        }


        final ArgumentCaptor<List<PlatinePilotageCommunicationEventDto>> listCaptor
                = ArgumentCaptor.forClass((Class) List.class);

        verify(platinePilotageService, times(1)).postCommunicationEvents(listCaptor.capture());
        List<List<PlatinePilotageCommunicationEventDto>> allValues = listCaptor.getAllValues();
        assertEquals(1, allValues.size(), "We should have exactly one value");

        List<PlatinePilotageCommunicationEventDto> actualArgument = listCaptor.getValue();
        assertEquals(commIdByInterro.size(), actualArgument.size(), "The passed list of events has an incorrect size");

        //check that we passed the correct items
        Set<String> requiredInterroIds = new HashSet<>(commIdByInterro.keySet());
        for (var comEventDto : actualArgument) {
            String idInterro = comEventDto.interrogationId();
            if (requiredInterroIds.contains(idInterro)) {
                //ComRequestId is correct
                String expectedCommReqId = commIdByInterro.get(idInterro).communicationRequestId();
                String actualCommReqId = comEventDto.communicationRequestId();
                assertEquals(expectedCommReqId, actualCommReqId,
                        "Communcation Request ID is not the one expected for idInterro=" + idInterro + " - ");

                assertEquals(communcationId,comEventDto.communicationId());
                //CommId is
                requiredInterroIds.remove(idInterro); //this id has correctly been sent
            }
        }
    }

    @Test
    @DisplayName("Test execute method - should throw ProtoolsProcessFlowBPMNError when communication Id is null")
    public void execute_should_throw_when_varCommId_is_blank() {
        String commId="";
        //Prepare
        DelegateExecution execution = createMockedExecution();
        doReturn(commId).when(execution).getVariable(VARNAME_CURRENT_COMMUNICATION_ID, String.class);
        doReturn(dumyId).when(execution).getVariable(VARNAME_CURRENT_PARTITION_ID, String.class);
        doReturn(Map.of("interroId1", new CommunicationRequestDetails("comReqId1",PlatinePilotageCommunicationEventType.COMMUNICATION_STATE_SENT))).when(execution).getVariable(VARNAME_COMMUNICATION_REQUEST_ID_AND_STATUS_FOR_INTERRO_ID_MAP, Map.class);

        //Execute method under test
        assertThrows(ProtoolsProcessFlowBPMNError.class, () -> task.execute(execution));
    }

    @Test
    @DisplayName("Test execute method - should write a log and not call service when the communication Request Map is empty ")
    public void execute_should_writeLog_and_notCallService_when_comReqByIdMapIsEmpty() {
        //Prepare
        DelegateExecution execution = createMockedExecution();
        initDefaultVariables(execution);

        // create and start a ListAppender to capture logs
        Logger fooLogger = (Logger) LoggerFactory.getLogger(task.getClass());
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        fooLogger.addAppender(listAppender);
        listAppender.start();

         //Execute method under test
        task.execute(execution);

        //Verify
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList.get(0).getFormattedMessage())
                .contains("begin",execution.getProcessInstanceId());

        assertEquals(Level.INFO, logsList.get(1)
                .getLevel());
        assertThat(logsList.get(1).getFormattedMessage())
                .contains("end",execution.getProcessInstanceId(),"Nothing to do");

        verify(platinePilotageService,never()).postCommunicationEvents(any());
    }
}