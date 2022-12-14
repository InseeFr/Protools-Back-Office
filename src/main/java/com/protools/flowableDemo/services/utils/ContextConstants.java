package com.protools.flowableDemo.services.utils;

import javax.swing.plaf.PanelUI;

public class ContextConstants {
    private ContextConstants() {
    }
    // Campaign context constants - Metadata
    public static final String ID = "id";
    public static final String LABEL = "label";
    public static final String CONTEXTE = "Contexte";
    public static final String LABEL_LONG_OPERATION = "LabelLongOperation";
    public static final String LABEL_COURT_OPERATION = "LabelCourtOperation";
    public static final String LABEL_LONG_SERIE = "LabelLongSerie";
    public static final String LABEL_COURT_SERIE = "LabelCourtSerie";
    public static final String OBJECTIFS_LONGS = "ObjectifsLongs";
    public static final String OBJECTIFS_COURTS = "ObjectifsCourts";
    public static final String PORTAIL_ENQUETE = "PortailMesEnquetesOperation";
    public static final String CARACTERE_OBLIGATOIRE = "CaractereObligatoire";
    public static final String QUALITE_STATISTIQUE = "QualiteStatistique";
    public static final String ANNEE_VISA = "AnneeVisa";
    public static final String NUMEROVISA = "NumeroVisa";
    public static final String MINISTERE_TUTELLE = "MinistereTutelle";
    public static final String PARUTION_JO = "ParutionJo";
    public static final String DATE_PARUTION_JO = "DateParutionJo";
    public static final String RESPONSABLE_OPERATIONNEL = "ResponsableOperationnel";
    public static final String RESPONSABLE_TRAITEMENT = "ResponsableTraitement";
    public static final String PRESTATAIRE = "Prestataire";
    public static final String LOGO_PRESTATAIRE = "LogoPrestataire";
    public static final String URL_ENQUETE = "UrlEnquete";
    public static final String SERVICE_COLLECTEUR_SIGNATAIRE_FONCTION = "ServiceCollecteurSignataireFonction";
    public static final String SERVICE_COLLECTEUR_SIGNATAIRE_NOM = "ServiceCollecteurSignataireNom";
    public static final String MAIL_RESPONSABLE_OPERATIONNEL = "MailResponsableOperationnel";
    public static final String CNIS_URL = "CnisUrl";
    public static final String DIFFUSION_URL = "DiffusionUrl";
    public static final String NOTICE_URL = "NoticeUrl";
    public static final String SPECIMENT_URL = "SpecimentUrl";
    public static final String PROPRIETAIRE = "Proprietaire";
    public static final String SUPPORT = "Support";

    // Campaign context constants - Partitions
    public static final String PARTITION = "Partition";
    public static final String DATES = "Dates";
    public static final String DATE_DEBUT_COLLECTE = "DateDebutCollecte";
    public static final String DATE_FIN_COLLECTE = "DateFinCollecte";
    public static final String DATE_RETOUR = "DateRetour";
    public static final String COMMUNICATION = "Communication";
    public static final String PROTOCOLE = "Protocole";
    public static final String MOYEN_COMMUNICATION = "MoyenCommunication";
    public static final String TYPE_COMMUNICATION = "TypeCommunication";
    public static final String MODELE_COMMUNICATION = "ModeleCommunication";
    public static final String ECHEANCE = "Echeance";
    public static final String OBJET = "Objet";
    public static final String MAILRETOUR = "MailRetour";
    public static final String CONTENU_COMMUNICATION = "ContenuCommunication";


    // Campaign context constants - QuestionnairesModel
    public static final String QUESTIONNAIRE_MODEL = "QuestionnaireModel";
    public static final String REQUIRED_NOMENCLATURES = "RequiredNomenclatures";

    // Campaign context constants - Naming
    public static final String NOMENCLATURE = "Nomenclature";
}
