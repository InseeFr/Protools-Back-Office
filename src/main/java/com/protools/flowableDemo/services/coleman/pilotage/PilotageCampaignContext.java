package com.protools.flowableDemo.services.coleman.pilotage;

import lombok.Data;

@Data
public class PilotageCampaignContext {
    private final String id;

    private final String label;

    private final Long collectionStartDate;

    private final Long collectionEndDate;
}
