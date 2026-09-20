package com.gkev.MySafaricom.service;

import com.gkev.MySafaricom.records.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
@Service
public class MpesaService {

    private static final Logger log = LoggerFactory.getLogger(MpesaService.class);

    private final String consumerKey;
    private final String consumerSecret;
    private final String authUrl;
    private final String callbackUrl;
    private final String shortcode;
    private final String passkey;
    private final String stkPushUrl;
    private final String stkPushQueryUrl;
    private final WebClient webClient;

    private final Mono<String> cachedAccessToken;

    public MpesaService(
            @Value("${safaricom.consumer_key}") String consumerKey,
            @Value("${safaricom.consumer_secret}") String consumerSecret,
            @Value("${safaricom.auth_url}") String authUrl,
            @Value("${ngrok.callback}") String callbackUrl,
            @Value("${safaricom.shortcode}") String shortcode,
            @Value("${safaricom.passkey}") String passkey,
            @Value("${safaricom.stk_push_url}") String stkPushUrl,
            @Value("${safaricom.stk_push_query_url}") String stkPushQueryUrl,
            WebClient.Builder webClientBuilder) {

        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.authUrl = authUrl;
        this.callbackUrl = callbackUrl;
        this.shortcode = shortcode;
        this.passkey = passkey;
        this.stkPushUrl = stkPushUrl;
        this.stkPushQueryUrl = stkPushQueryUrl;
        this.webClient = webClientBuilder.build();

        this.cachedAccessToken = Mono.defer(this::fetchAccessToken)
                .cache(Duration.ofMinutes(55));
    }

    private Mono<String> fetchAccessToken() {

        return webClient.get()
                .uri(authUrl)
                .headers(headers ->
                        headers.setBasicAuth(consumerKey, consumerSecret)
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {

                                    log.error(
                                            "Token fetch failed {} → {}",
                                            clientResponse.statusCode(),
                                            errorBody
                                    );

                                    return Mono.error(
                                            new RuntimeException(
                                                    "Token fetch "
                                                            + clientResponse.statusCode()
                                                            + ": "
                                                            + errorBody
                                            )
                                    );
                                })
                )
                .bodyToMono(TokenResponse.class)
                .map(TokenResponse::accessToken)
                .doOnSuccess(token ->
                        log.info("Successfully obtained new M-Pesa access token")
                )
                .doOnError(e ->
                        log.error("Failed to fetch M-Pesa access token", e)
                );
    }


    public Mono<String> getAccessToken() {
        return cachedAccessToken;
    }

    public Mono<StkPushResponse> initiateStkPush(
            String phoneNumber,
            BigDecimal amount,
            String accountReference,
            String description) {

        String timestamp = generateTimestamp();
        String password = generatePassword(timestamp);

        StkPushRequest request = new StkPushRequest(
                shortcode,
                password,
                timestamp,
                "CustomerPayBillOnline",
                amount,
                phoneNumber,
                shortcode,
                phoneNumber,
                callbackUrl,
                accountReference,
                description
        );

        return getAccessToken()
                .flatMap(token ->
                        webClient.post()
                                .uri(stkPushUrl)
                                .headers(headers ->
                                        headers.setBearerAuth(token)
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(request)
                                .retrieve()
                                .onStatus(
                                        HttpStatusCode::isError,
                                        response ->
                                                response.bodyToMono(String.class)
                                                        .flatMap(errorBody -> {

                                                            log.error(
                                                                    "Daraja error {} → {}",
                                                                    response.statusCode(),
                                                                    errorBody
                                                            );

                                                            return Mono.error(
                                                                    new RuntimeException(
                                                                            "Daraja "
                                                                                    + response.statusCode()
                                                                                    + ": "
                                                                                    + errorBody
                                                                    )
                                                            );
                                                        })
                                )
                                .bodyToMono(StkPushResponse.class)
                );
    }

    private String generateTimestamp() {
        return DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .format(LocalDateTime.now());
    }

    private String generatePassword(String timestamp) {
        String raw = shortcode + passkey + timestamp;

        return Base64.getEncoder()
                .encodeToString(
                        raw.getBytes(StandardCharsets.UTF_8)
                );
    }

    public Mono<StkPushQueryResponse> queryStkPush(String checkoutRequestId) {

        String timestamp = generateTimestamp();
        String password = generatePassword(timestamp);

        StkPushQueryRequest stkPushQueryRequest = new StkPushQueryRequest(
                shortcode,
                password,
                timestamp,
                checkoutRequestId
        );

        return getAccessToken()
                .flatMap(token ->
                        webClient.post()
                                .uri(stkPushQueryUrl)
                                .headers(headers -> headers.setBearerAuth(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(stkPushQueryRequest)
                                .retrieve()
                                .onStatus(
                                        HttpStatusCode::isError,
                                        response -> response.bodyToMono(String.class)
                                                .flatMap(errorBody -> {
                                                    log.error("Daraja errors {} → {}", response.statusCode(), errorBody);
                                                    return Mono.error(
                                                            new RuntimeException(
                                                                    "Daraja " + response.statusCode() + ": " + errorBody
                                                            )
                                                    );
                                                })
                                )
                                .bodyToMono(StkPushQueryResponse.class)
                );
    }
}