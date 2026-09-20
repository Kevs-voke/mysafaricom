package com.gkev.MySafaricom.records;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MpesaC2BConfirmation(
        @NotBlank
        @JsonProperty("TransactionType")
        String transactionType,

        @NotBlank
        @JsonProperty("TransID")
        String transId,

        @NotBlank
        @JsonProperty("TransTime")
        String transTime,

        @NotNull
        @DecimalMin("0.01")
        @JsonProperty("TransAmount")
        BigDecimal transAmount,

        @NotBlank
        @JsonProperty("BusinessShortCode")
        String businessShortCode,

        @NotBlank
        @JsonProperty("BillRefNumber")
        String billRefNumber,

        @JsonProperty("InvoiceNumber")
        String invoiceNumber,

        @JsonProperty("OrgAccountBalance")
        BigDecimal orgAccountBalance,

        @JsonProperty("ThirdPartyTransID")
        String thirdPartyTransId,

        @NotBlank
        @JsonProperty("MSISDN")
        String msisdn,

        @JsonProperty("FirstName")
        String firstName,

        @JsonProperty("MiddleName")
        String middleName,

        @JsonProperty("LastName")
        String lastName
) {
}
