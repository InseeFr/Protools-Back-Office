package com.protools.flowableDemo.services.coleman;

import lombok.Data;

@Data
public class PilotageCampaignContext {
    private final String id;

    private final String label;

    private final Long collectionStartDate;

    private final Long collectionEndDate;
}
