package fr.insee.protools.backend.service;

/**
 * Class to store flowable variable identifiers
 */
public class FlowableVariableNameConstants {

    //TODO : renommer toutes ces variables pour remplacer les - par des _ (comme ca ca sera utilisable dans les resolvers)
    //Protools context
    public static final String VARNAME_CONTEXT="context";
    //To treat partitions one by one (Long)
    public static final String VARNAME_CURRENT_PARTITION_ID="current_partition_id";

    //Sugoi ID/PWD
    public static final String VARNAME_DIRECTORYACCESS_PWD_CONTACT ="directory_access_pwd_contact";

    public static final String VARNAME_DIRECTORYACCESS_PWD_FOR_INTERRO_ID_MAP ="directory_access_pwd_contact_by_interroration_id";

    //For communication
    public static final String VARNAME_CURRENT_COMMUNICATION_ID="current_communication_id";
    public static final String VARNAME_COMMUNICATION_REQUEST_ID_AND_STATUS_FOR_INTERRO_ID_MAP="communication_request_id_and_status_by_interrogation_id";
    //List of the UUIDs of the communications that have already been scheduled
    public static final String VARNAME_ALREADY_SCHEDULED_COMMUNICATION_ID_SET = "communication_already_scheduled_id_set";
    //List of the UUIDs of the communcation that are in error (echeance was too far past)
    public static final String VARNAME_COMMUNICATION_ERROR_ID_SET = "communication_in_error_id_set";


    //To pass a list of REM interrogations content (JsonNode)
    public static final String VARNAME_REM_INTERRO_LIST ="rem_interrogation_list";
    //REM PAGEABLE
    public static final String VARNAME_INTERRO_LIST_PAGEABLE_IS_LAST_PAGE ="interro_list_page_is_last";
    public static final String VARNAME_INTERRO_LIST_PAGEABLE_CURRENT_PAGE ="interro_list_page_current";

    //Platine Pilotage Follow-up pageable
    public static final String VARNAME_ID_INTERRO_LIST ="id_interro_list";
    public static final String VARNAME_FOLLOW_UP_LIST_PAGEABLE_IS_LAST_PAGE ="follow_up_page_is_last";
    public static final String VARNAME_FOLLOW_UP_LIST_PAGEABLE_CURRENT_PAGE ="follow_up_page_current";
    //Contacts
    public static final String VARNAME_PLATINE_CONTACT_LIST ="platine_contact_list";

    //Remise en collecte
    public static final String VARNAME_INTERRO_REMISE_EN_COLLECTE_LIST ="interro_remise_en_collecte_list";



    private FlowableVariableNameConstants(){}
}
