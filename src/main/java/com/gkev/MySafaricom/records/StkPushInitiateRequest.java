package com.gkev.MySafaricom.records;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record StkPushInitiateRequest(

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^254(7|1)\\d{8}$",
                message = "Phone number must be in format 2547XXXXXXXX or 2541XXXXXXXX"
        )
        String phoneNumber,

        @NotNull(message = "Amount is required")
        @DecimalMin(
                value = "1",
                message = "Amount must be at least 1"
        )
        @Digits(
                integer = 10,
                fraction = 0,
                message = "Amount must be a whole number (no decimals)"
        )
        BigDecimal amount,

        @NotBlank(message = "Account reference is required")
        @Size(max = 12, message = "Account reference must not exceed 12 characters")
        String accountReference,

        @NotBlank(message = "Description is required")
        @Size(max = 13, message = "Description must not exceed 13 characters")
        String description

) {}