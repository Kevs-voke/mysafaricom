package com.gkev.MySafaricom.Controller;

import com.gkev.MySafaricom.records.*;
import com.gkev.MySafaricom.service.MpesaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class MpesaController {
    private final MpesaService mpesaService;
    private static final Logger log = LoggerFactory.getLogger(MpesaController.class);

    @PostMapping("/stkpush")
    public Mono<StkPushResponse> stkPush(
            @Valid @RequestBody StkPushInitiateRequest request) {

        return mpesaService.initiateStkPush(
                request.phoneNumber(),
                request.amount(),
                request.accountReference(),
                request.description()
        );
    }
    @PostMapping("/stkPushCallback")
    public Mono<String> handleCallback(@RequestBody String payload) {
        log.info("M-Pesa Callback received: {}", payload);
        return Mono.just("{\"ResultCode\":0,\"ResultDesc\":\"Accepted\"}");
    }

    @GetMapping("/token")
    public Mono<String> getToken() {
        return mpesaService.getAccessToken();
    }

    @PostMapping("/query/{checkoutRequestId}")
    public Mono<StkPushQueryResponse> queryTransaction(@PathVariable String checkoutRequestId) {
        return mpesaService.queryStkPush(checkoutRequestId);
    }

    @PostMapping("/c2b/validation")
    public Mono<MpesaC2BValidationResponse> validatePayment(
            @Valid @RequestBody MpesaC2BValidateRequest request
    ) {

        log.info("M-Pesa C2B validation request: {}", request);

        return Mono.just(
                new MpesaC2BValidationResponse(
                        "0",
                        "Accepted"
                )
        );
    }

    @PostMapping("/c2b/confirmation")
    public Mono<Void> paymentConfirmation(
            @Valid @RequestBody MpesaC2BConfirmation confirmation
    ) {
        log.info("M-Pesa C2B confirmation received: {}", confirmation);

        return Mono.empty();
    }
    @PostMapping("/c2b/register")
    public Mono<RegisterUrlsResponse> registerC2BUrls() {

        return mpesaService.registerC2BUrls()
                .doOnNext(response ->
                        log.info("C2B Register URL Response: {}", response)
                );
    }
    @PostMapping("/c2b/simulate")
    public Mono<C2BSimulateResponse> simulateC2B( @RequestBody C2BSimulateRequest request ) {
        log.info("C2B simulation request received: {}", request);
        return mpesaService.simulateC2B(request)
                .doOnNext(response -> log.info("C2B simulation response: {}", response) )
                .doOnError(error -> log.error("C2B simulation failed: {}", error.getMessage(), error) );
    }

}
