package com.gkev.MySafaricom.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StkPushQueryResponse {

    @JsonProperty("ResponseCode")
    private String responseCode;

    @JsonProperty("ResponseDescription")
    private String responseDescription;

    @JsonProperty("MerchantRequestID")
    private String merchantRequestId;

    @JsonProperty("CheckoutRequestID")
    private String checkoutRequestId;

    @JsonProperty("ResultCode")
    private String resultCode;

    @JsonProperty("ResultDesc")
    private String resultDesc;
}