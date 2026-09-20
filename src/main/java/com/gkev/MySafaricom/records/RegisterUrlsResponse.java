package com.gkev.MySafaricom.records;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterUrlsResponse(
        @JsonProperty("OriginatorCoversationID")
        String originatorConversationID,

        @JsonProperty("ResponseCode")
        String responseCode,

        @JsonProperty("ResponseDescription")
        String responseDescription
) {
}