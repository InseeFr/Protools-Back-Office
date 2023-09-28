package fr.insee.protools.backend.service;

/**
 * Class to store flowable variable identifiers
 */
public class FlowableVariableNameConstants {

    //TODO : renommer toutes ces variables pour remplacer les - par des _ (comme ca ca sera utilisable dans les resolvers)
    //Protools context
    public static final String VARNAME_CONTEXT="context";
    public static final String VARNAME_CONTEXT_PARTITION_ID_LIST="contexte_partition_id_list";
    public static final String VARNAME_CONTEXT_PARTITION_VARIABLES_BY_ID="contexte_partition_variables_by_partitionid";
    //To treat partitions one by one (Long)
    public static final String VARNAME_CURRENT_PARTITION_ID="current_partition_id";
    //to pass a list of REM survey ids (List<Long>)
    public static final String VARNAME_REM_SU_ID_LIST="rem_survey_unit_id_list";
    //to pass a single REM survey unit's ID (Long)
    public static final String VARNAME_REM_SURVEY_UNIT_IDENTIFIER ="rem_survey_unit_id";
    //to pass a REM survey unit content (JsonNode)
    public static final String VARNAME_REM_SURVEY_UNIT ="rem_survey_unit";
    public static final String VARNAME_DIRECTORYACCESS_ID_CONTACT ="directory_access-id-contact";
    //ERA response
    public static final String VARNAME_ERA_RESPONSE="era_response_list";
    public static final String VARNAME_ERA_QUERY_START_DATE="era_query_start_date";
    public static final String VARNAME_ERA_QUERY_END_DATE="era_query_end_date";
    //Platine Contact details
    public static final String VARNAME_PLATINE_CONTACT="platine_contact";

    private FlowableVariableNameConstants(){}
}
