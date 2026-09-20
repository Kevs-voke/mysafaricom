package com.gkev.MySafaricom.Controller;

import com.gkev.MySafaricom.records.StkPushInitiateRequest;
import com.gkev.MySafaricom.records.StkPushQueryResponse;
import com.gkev.MySafaricom.records.StkPushResponse;
import com.gkev.MySafaricom.service.MpesaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mpesa")
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
    @PostMapping("/callback")
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
}
