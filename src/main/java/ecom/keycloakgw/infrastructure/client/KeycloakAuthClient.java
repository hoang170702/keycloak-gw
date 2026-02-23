package ecom.keycloakgw.infrastructure.client;

import ecom.keycloakgw.application.dto.response.TokenResponse;
import ecom.keycloakgw.domain.exception.BusinessException;
import ecom.keycloakgw.domain.exception.ErrorCode;
import ecom.keycloakgw.infrastructure.config.KeycloakProperties;
import ecom.log.utils.LoggerUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class KeycloakAuthClient {

    private final WebClient keycloakWebClient;
    private final KeycloakProperties properties;
    @Value("${keycloak.admin.admin-token}")
    private String adminConnectToken;

    public KeycloakAuthClient(WebClient keycloakWebClient, KeycloakProperties properties) {
        this.keycloakWebClient = keycloakWebClient;
        this.properties = properties;
    }

    public Mono<TokenResponse> adminLogin(String username, String password) {
        String tokenUrl = String.format("/realms/%s/protocol/openid-connect/token", properties.getAdminRealm());

        LoggerUtils.info(KeycloakAuthClient.class, "Calling Keycloak admin login: POST {}", tokenUrl);

        return keycloakWebClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", properties.getAdminClientId())
                        .with("username", username)
                        .with("password", password))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .doOnSuccess(token -> LoggerUtils.info(KeycloakAuthClient.class,
                        "Admin login successful, token_type={}, expires_in={}",
                        token.getTokenType(), token.getExpiresIn()))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    LoggerUtils.error(KeycloakAuthClient.class,
                            "Keycloak admin login failed: status={}, body={}",
                            ex.getStatusCode(), ex.getResponseBodyAsString());
                    return Mono.error(new BusinessException(
                            ErrorCode.KEYCLOAK_ERROR,
                            "Admin login failed: " + ex.getResponseBodyAsString()));
                });
    }
}
