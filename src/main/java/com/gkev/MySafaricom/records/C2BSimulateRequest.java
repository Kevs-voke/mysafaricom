package com.gkev.MySafaricom.records;

import com.fasterxml.jackson.annotation.JsonProperty;

public record C2BSimulateRequest(
        @JsonProperty("ShortCode")
                Integer shortCode,

        @JsonProperty("CommandID")
        String commandId,

        @JsonProperty("Amount")
        Integer amount,

        @JsonProperty("Msisdn")
        Long msisdn,

        @JsonProperty("BillRefNumber")
        String billRefNumber
) {
}
