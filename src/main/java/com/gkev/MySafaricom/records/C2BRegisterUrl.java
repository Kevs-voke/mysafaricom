package com.gkev.MySafaricom.records;

import com.fasterxml.jackson.annotation.JsonProperty;

public record C2BRegisterUrl(
        @JsonProperty("ShortCode")
        String shortCode,

        @JsonProperty("ResponseType")
        String responseType,

        @JsonProperty("ConfirmationURL")
        String confirmationUrl,

        @JsonProperty("ValidationURL")
        String validationUrl
) {
}
