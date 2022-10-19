package com.protools.flowableDemo.services.coleman.pilotage;

public class PilotageCampaignContext {
    private String id;
    private String label;
    private Long collectionStartDate;
    private Long collectionEndDate;

    public PilotageCampaignContext() {
    }

    public PilotageCampaignContext(String id, String label, Long collectionStartDate, Long collectionEndDate) {
        this.id = id;
        this.label = label;
        this.collectionStartDate = collectionStartDate;
        this.collectionEndDate = collectionEndDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getCollectionStartDate() {
        return collectionStartDate;
    }

    public void setCollectionStartDate(Long collectionStartDate) {
        this.collectionStartDate = collectionStartDate;
    }

    public Long getCollectionEndDate() {
        return collectionEndDate;
    }

    public void setCollectionEndDate(Long collectionEndDate) {
        this.collectionEndDate = collectionEndDate;
    }
}
