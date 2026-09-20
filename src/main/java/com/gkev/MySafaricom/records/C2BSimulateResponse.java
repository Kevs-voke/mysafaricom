package com.gkev.MySafaricom.records;

import com.fasterxml.jackson.annotation.JsonProperty;

public record C2BSimulateResponse(
        @JsonProperty("OriginatorCoversationID")
        String originatorConversationId,

        @JsonProperty("ResponseCode")
        String responseCode,

        @JsonProperty("ResponseDescription")
        String responseDescription
) {
}
