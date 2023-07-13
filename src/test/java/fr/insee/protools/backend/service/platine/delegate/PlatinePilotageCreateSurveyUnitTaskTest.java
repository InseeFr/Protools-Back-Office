package fr.insee.protools.backend.service.platine.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.protools.backend.ProtoolsTestUtils;
import fr.insee.protools.backend.service.context.ContextConstants;
import fr.insee.protools.backend.service.context.exception.BadContextIncorrectException;
import fr.insee.protools.backend.service.exception.IncorrectSUException;
import fr.insee.protools.backend.service.platine.pilotage.PlatinePilotageService;
import fr.insee.protools.backend.service.platine.pilotage.dto.query.QuestioningWebclientDto;
import fr.insee.protools.backend.service.platine.pilotage.metadata.MetadataDto;
import fr.insee.protools.backend.service.rem.dto.PersonDto;
import fr.insee.protools.backend.service.rem.dto.REMSurveyUnitDto;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import fr.insee.protools.backend.service.utils.TestWithContext;
import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.stream.Stream;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;
import static fr.insee.protools.backend.service.platine.utils.PlatineHelper.computePilotagePartitionID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlatinePilotageCreateSurveyUnitTaskTest extends TestWithContext {

    final static String ressourceFolder = ClassUtils.convertClassNameToResourcePath(PlatinePilotageCreateSurveyUnitTaskTest.class.getPackageName());

    @Mock
    PlatinePilotageService platinePilotageService;

    @InjectMocks
    PlatinePilotageCreateSurveyUnitTask platinePilotageTask;


    final static String platine_context_json = ressourceFolder+"/protools-contexte-platine-individu.json";
    final static String platine_context_logement_json = ressourceFolder+"/protools-contexte-platine-individu.json";

    final static String rem_su_1personne = ressourceFolder+"/rem-su_1personne.json";
    final static String rem_su_3personnes = ressourceFolder+"/rem-su_3personnes.json";
    final static String getRem_su_1personne_noContact = ressourceFolder+"/rem-su-noMainOrSurveyed.json";

    //To be able to run tests with differents protools contexts
    private static Stream<Arguments> protoolsContextArguments() {
        return Stream.of(
                Arguments.of(platine_context_json),
                Arguments.of(platine_context_logement_json)
        );
    }


    @Test
    void execute_should_throw_BadContextIncorrectException_when_noContext() {
        DelegateExecution execution=createMockedExecution();
        //Execute the unit under test
        assertThrows(BadContextIncorrectException.class,() -> platinePilotageTask.execute(execution));
    }

    @ParameterizedTest
    @MethodSource("protoolsContextArguments")
    void execute_should_work_when_ContextOK(String context_json) {
        //Prepare
        DelegateExecution execution=createMockedExecution();
        JsonNode contextRootNode = initContexteMock(context_json);
        JsonNode remSU = ProtoolsTestUtils.asJsonNode(rem_su_3personnes);
        String idPArtition="1";
        lenient().doReturn(idPArtition).when(execution).getVariable(VARNAME_CURRENT_PARTITION_ID,String.class);
        lenient().doReturn(remSU).when(execution).getVariable(VARNAME_REM_SURVEY_UNIT,JsonNode.class);
        lenient().doReturn("TOTO").when(execution).getVariable(VARNAME_SUGOI_ID_CONTACT,String.class);

        //Execute the unit under test
        platinePilotageTask.execute(execution);

        //Post Conditions
        ArgumentCaptor<QuestioningWebclientDto> acQuestionningDto = ArgumentCaptor.forClass(QuestioningWebclientDto.class);
        verify(platinePilotageService,times(1)).putQuestionings(acQuestionningDto.capture());
        QuestioningWebclientDto valueParam = acQuestionningDto.getValue();
        assertEquals("tic2023a00_web", valueParam.getModelName(),"Bad Questionnaire model");
        assertEquals(1, valueParam.getContacts().size(),"Expected exactly one contact");
        assertEquals("TOBEFOUND", valueParam.getContacts().get(0).getFirstName(),"Wrong contact found");

        String campagneId=contextRootNode.path(ContextConstants.CTX_CAMPAGNE_ID).asText();
        assertEquals(computePilotagePartitionID(campagneId,idPArtition), valueParam.getIdPartitioning(),"Wrong Partition ID");
        assertEquals("3043280", valueParam.getSurveyUnit().getIdSu(),"Wrong ID SU (repository id)");
    }

    void findContact_should_return_CorrectContact(String suTestFilePath, boolean isLogement, boolean shouldThrow){
        //Prepare
        JsonNode remSU = ProtoolsTestUtils.asJsonNode(suTestFilePath);
        REMSurveyUnitDto dto = ProtoolsTestUtils.asObject(suTestFilePath,REMSurveyUnitDto.class);


        if(!shouldThrow) {
            //Execute
            PersonDto contact = platinePilotageTask.findContact(remSU,dto,isLogement);
            //Found the right contact
            assertEquals("TOBEFOUND", contact.getFirstName());
        }
        else{
            //Execute the unit under test
            assertThrows(IncorrectSUException.class,() -> platinePilotageTask.findContact(remSU,dto,isLogement));
        }
    }

    @Test
    void findContact_should_return_CorrectContactForLogement() {
        findContact_should_return_CorrectContact(rem_su_1personne,true,false);
        findContact_should_return_CorrectContact(rem_su_1personne,false,false);
        findContact_should_return_CorrectContact(rem_su_3personnes,true,false);
        findContact_should_return_CorrectContact(rem_su_3personnes,false,false);
        findContact_should_return_CorrectContact(getRem_su_1personne_noContact,false,true);
        findContact_should_return_CorrectContact(getRem_su_1personne_noContact,true,true);
    }


    @Test
    @DisplayName("convertREMGenderToPlatineCivility should return Mme only id parameter is '2' ")
    void convertREMGenderToPlatineCivility_should_ReturnMadameOnlyWhenParamIs2() {
        String monsieur="Mr";
        String madame="Mme";
        assertThat(PlatinePilotageCreateSurveyUnitTask.convertREMGenderToPlatineCivility("2")).isEqualTo(madame);
        assertThat(PlatinePilotageCreateSurveyUnitTask.convertREMGenderToPlatineCivility(" 2")).isEqualTo(monsieur);
        assertThat(PlatinePilotageCreateSurveyUnitTask.convertREMGenderToPlatineCivility(" 2 ")).isEqualTo(monsieur);
        assertThat(PlatinePilotageCreateSurveyUnitTask.convertREMGenderToPlatineCivility("3")).isEqualTo(monsieur);
        assertThat(PlatinePilotageCreateSurveyUnitTask.convertREMGenderToPlatineCivility("23JZKEOSJF")).isEqualTo(monsieur);
        assertThat(PlatinePilotageCreateSurveyUnitTask.convertREMGenderToPlatineCivility("0")).isEqualTo(monsieur);
        assertThat(PlatinePilotageCreateSurveyUnitTask.convertREMGenderToPlatineCivility("1")).isEqualTo(monsieur);
        assertThat(PlatinePilotageCreateSurveyUnitTask.convertREMGenderToPlatineCivility("-1")).isEqualTo(monsieur);
        assertThat(PlatinePilotageCreateSurveyUnitTask.convertREMGenderToPlatineCivility("-2")).isEqualTo(monsieur);
        assertThat(PlatinePilotageCreateSurveyUnitTask.convertREMGenderToPlatineCivility("& ukdslw,kvlk,l")).isEqualTo(monsieur);
    }

    @Test
    @DisplayName("getPlatineLastname should return lastname if not null else return birthname if not null else return empty string")
    void getPlatineLastname_should_work(){
        String lastname="lastname";
        String birthname="birthname";
        assertThat(PlatinePilotageCreateSurveyUnitTask.getPlatineLastname(lastname,birthname)).isEqualTo(lastname);
        assertThat(PlatinePilotageCreateSurveyUnitTask.getPlatineLastname(null,birthname)).isEqualTo(birthname);
        assertThat(PlatinePilotageCreateSurveyUnitTask.getPlatineLastname(lastname,null)).isEqualTo(lastname);
        assertThat(PlatinePilotageCreateSurveyUnitTask.getPlatineLastname(null,null)).isEqualTo("");

    }


    @Test
    @DisplayName("Should throw if REM SU Json is invalid")
    void execute_should_throw_IncorrectSUException_when_wrongSU() {
        DelegateExecution execution=createMockedExecution();
        initContexteMock(platine_context_json);
        JsonNode remSU = ProtoolsTestUtils.asJsonNode(rem_su_3personnes);
        //Break this node
        ((ObjectNode) remSU).remove("repositoryId");
        String idPartition="1";
        lenient().doReturn(idPartition).when(execution).getVariable(VARNAME_CURRENT_PARTITION_ID,String.class);
        lenient().doReturn(remSU).when(execution).getVariable(VARNAME_REM_SURVEY_UNIT,JsonNode.class);
        lenient().doReturn("TOTO").when(execution).getVariable(VARNAME_SUGOI_ID_CONTACT,String.class);


        //Execute the unit under test
        assertThrows(IncorrectSUException.class,() -> platinePilotageTask.execute(execution));
    }
}