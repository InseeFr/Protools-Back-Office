package fr.insee.protools.backend.service.rem;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.service.exception.IncorrectSUBPMNError;
import fr.insee.protools.backend.service.rem.dto.PersonDto;
import fr.insee.protools.backend.service.rem.dto.REMSurveyUnitDto;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_DIRECTORYACCESS_ID_CONTACT;
import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_REM_SURVEY_UNIT;
import static fr.insee.protools.backend.service.rem.delegate.ExtractContactIdentifierFromREMSUTask.*;

public class RemDtoUtils {
    private RemDtoUtils(){}


    private static final String REM_ADDITIONALINFOS = "additionalInformations";
    private static final String REM_ADDITIONALINFOS_KEY = "key";
    private static final String REM_ADDITIONALINFOS_VALUE = "value";

    public static final String REM_ADDITIONALINFOS_POLE_GESTION_OPALE = "pole_gestion_opale";

    /**
     * Search for the right contact according to the SU Type :
     * For Logement : Find the first person flagged as Main
     * For Individu : Find the first person flagged as Surveyed
     * @param remSUNode : Used only to add it to exception in case of error
     * @param remSurveyUnitDto
     * @param isLogement true if logement (false if individu)
     * @return the contact to be used
     */
    public static PersonDto findContact(JsonNode remSUNode, REMSurveyUnitDto remSurveyUnitDto, boolean isLogement) {
        //Search for right contact
        PersonDto contact;
        //SU Logement
        if (isLogement) {
            //We use the "main" person (actually it is the Declarant)
            contact = remSurveyUnitDto.getPersons().stream()
                    .filter(personDto -> (Boolean.TRUE.equals(personDto.getMain())))
                    .findFirst().orElseThrow(() -> new IncorrectSUBPMNError("No main person found in SU [id=" + remSurveyUnitDto.getRepositoryId() + "]", remSUNode));
        }
        //SU INDIVIDU
        else {
            contact = remSurveyUnitDto.getPersons().stream()
                    .filter(personDto -> (Boolean.TRUE.equals(personDto.getSurveyed())))
                    .findFirst().orElseThrow(() -> new IncorrectSUBPMNError("No surveyed person found in SU [id="+ remSurveyUnitDto.getRepositoryId()+"]", remSUNode));
        }
        return contact;
    }


    /**
     * Search for the right Main contact according to the SU Type :
     * For Logement : Find the first person flagged as Main + The Co-declarant
     * For Individu : Find the first person flagged as Surveyed + either the declarant or the co-declarant
     *
     * @param remSUNode        : Used only to add it to exception in case of error
     * @param remSurveyUnitDto
     * @param isLogement       true if logement (false if individu)
     * @return A pair with the contacts; Secondary one is optional
     */
    public static Pair<PersonDto, Optional<PersonDto>> findContactAndSecondary(JsonNode remSUNode, REMSurveyUnitDto remSurveyUnitDto, boolean isLogement) {
        //Search for right contact
        PersonDto mainContact = findContact(remSUNode, remSurveyUnitDto, isLogement);
        PersonDto secondaryContact;

        //SU Logement
        if (isLogement) {
            //Secondary is the co-declarant
            secondaryContact = remSurveyUnitDto.getPersons().stream()
                    .filter(personDto -> (Boolean.TRUE.equals(personDto.getCoDeclarant())))
                    .findFirst().orElse(null);
        }
        //SU INDIVIDU
        else {
            //If main contact is declarant ==> secondary is the co-declarant
            if(mainContact.getMain()){
                secondaryContact = remSurveyUnitDto.getPersons().stream()
                        .filter(personDto -> (Boolean.TRUE.equals(personDto.getCoDeclarant())))
                        .findFirst().orElse(null);
            }
            //else ==> secondary is the declarant (main==true)
            else{
                secondaryContact = remSurveyUnitDto.getPersons().stream()
                        .filter(personDto -> (Boolean.TRUE.equals(personDto.getMain())))
                        .findFirst().orElse(null);
            }
        }
        return Pair.of(mainContact,Optional.ofNullable(secondaryContact));
    }


    public static Optional<String> searchAdditionalInformation(String searchedKey,JsonNode remSUNode){
        JsonNode additionalInfoNode = remSUNode.path(REM_ADDITIONALINFOS);
        if (additionalInfoNode!=null && additionalInfoNode.isArray()) {
            for (JsonNode jsonNode : additionalInfoNode) {
                String key = jsonNode.path(REM_ADDITIONALINFOS_KEY).asText();
                if(key.equalsIgnoreCase(searchedKey)){
                    String value=jsonNode.path(REM_ADDITIONALINFOS_VALUE).asText();
                    return Optional.of(value);
                }
            }
        }
        return Optional.empty();
    }
}
