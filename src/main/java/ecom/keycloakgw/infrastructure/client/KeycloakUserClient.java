package ecom.keycloakgw.infrastructure.client;

import ecom.keycloakgw.application.dto.response.IntrospectResponse;
import ecom.keycloakgw.application.dto.response.RoleResponse;
import ecom.keycloakgw.application.dto.response.TokenResponse;
import ecom.keycloakgw.application.dto.response.UserResponse;
import ecom.keycloakgw.domain.exception.BusinessException;
import ecom.keycloakgw.domain.exception.ErrorCode;
import ecom.keycloakgw.infrastructure.config.KeycloakProperties;
import ecom.log.utils.LoggerUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KeycloakUserClient {

    private final WebClient keycloakWebClient;
    private final KeycloakProperties properties;

    public KeycloakUserClient(WebClient keycloakWebClient, KeycloakProperties properties) {
        this.keycloakWebClient = keycloakWebClient;
        this.properties = properties;
    }

    // ======================== CREATE USER ========================

    public Mono<Void> createUser(String adminToken, String username, String email,
                                 String firstName, String lastName, String password) {
        String url = String.format("/admin/realms/%s/users", properties.getEcomRealm());
        LoggerUtils.info(KeycloakUserClient.class,
                "Creating user: POST {} username={}", url, username);

        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("enabled", true);
        body.put("email", email);
        body.put("firstName", firstName != null ? firstName : "");
        body.put("lastName", lastName != null ? lastName : "");
        body.put("emailVerified", true);
        body.put("requiredActions", List.of());
        body.put("credentials", List.of(Map.of(
                "type", "password",
                "value", password,
                "temporary", false
        )));

        return keycloakWebClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(r -> LoggerUtils.info(KeycloakUserClient.class,
                        "User created successfully: {}", username))
                .then()
                .onErrorResume(WebClientResponseException.class,
                        ex -> mapError(ex, "Create user failed"));
    }

    // ======================== GET USER BY USERNAME ========================

    public Mono<UserResponse> getUserByUsername(String adminToken, String username) {
        String url = String.format("/admin/realms/%s/users", properties.getEcomRealm());
        LoggerUtils.info(KeycloakUserClient.class, "Getting userId: GET {} username={}", url, username);

        return keycloakWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(url)
                        .queryParam("username", username)
                        .queryParam("exact", true)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .retrieve()
                .bodyToFlux(UserResponse.class)
                .next()
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.INVALID_REQUEST, "User not found: " + username)))
                .doOnSuccess(user -> LoggerUtils.info(KeycloakUserClient.class, "Found userId={} for username={}", user.getId(), username))
                .onErrorResume(WebClientResponseException.class, ex -> mapError(ex, "Get user failed"));
    }

    // ======================== GET USER BY ID ========================

    public Mono<UserResponse> getUserById(String adminToken, String userId) {
        String url = String.format("/admin/realms/%s/users/%s", properties.getEcomRealm(), userId);
        LoggerUtils.info(KeycloakUserClient.class, "Getting user detail: GET {} userId={}", url, userId);

        return keycloakWebClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .doOnSuccess(user -> LoggerUtils.info(KeycloakUserClient.class, "Got user detail for userId={}", userId))
                .onErrorResume(WebClientResponseException.class, ex -> mapError(ex, "Get user detail failed"));
    }

    // ======================== GET REALM ROLE ========================

    public Mono<RoleResponse> getRealmRole(String adminToken, String roleName) {
        String url = String.format("/admin/realms/%s/roles/%s", properties.getEcomRealm(), roleName);
        LoggerUtils.info(KeycloakUserClient.class, "Getting realm role: GET {} role={}", url, roleName);

        return keycloakWebClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .retrieve()
                .bodyToMono(RoleResponse.class)
                .doOnSuccess(role -> LoggerUtils.info(KeycloakUserClient.class, "Found role id={} name={}", role.getId(), role.getName()))
                .onErrorResume(WebClientResponseException.class, ex -> mapError(ex, "Get realm role failed"));
    }

    // ======================== ASSIGN ROLE ========================

    public Mono<Void> assignRealmRole(String adminToken, String userId, String roleId, String roleName) {
        String url = String.format("/admin/realms/%s/users/%s/role-mappings/realm", properties.getEcomRealm(), userId);
        LoggerUtils.info(KeycloakUserClient.class, "Assigning role: POST {} userId={} role={}", url, userId, roleName);

        List<Map<String, String>> body = List.of(Map.of("id", roleId, "name", roleName));

        return keycloakWebClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(r -> LoggerUtils.info(KeycloakUserClient.class, "Role {} assigned to userId={}", roleName, userId))
                .then()
                .onErrorResume(WebClientResponseException.class, ex -> mapError(ex, "Assign role failed"));
    }

    // ======================== USER LOGIN (ecom realm) ========================

    public Mono<TokenResponse> userLogin(String username, String password) {
        String url = String.format("/realms/%s/protocol/openid-connect/token", properties.getEcomRealm());
        LoggerUtils.info(KeycloakUserClient.class, "User login: POST {} username={}", url, username);

        return keycloakWebClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", properties.getEcomClientId())
                        .with("client_secret", properties.getEcomClientSecret())
                        .with("username", username)
                        .with("password", password))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .doOnSuccess(t -> LoggerUtils.info(KeycloakUserClient.class, "User login successful for {}", username))
                .onErrorResume(WebClientResponseException.class, ex -> mapError(ex, "User login failed"));
    }

    // ======================== VALIDATE TOKEN ========================

    public Mono<IntrospectResponse> validateToken(String token) {
        String url = String.format("/realms/%s/protocol/openid-connect/token/introspect", properties.getEcomRealm());
        LoggerUtils.info(KeycloakUserClient.class, "Validating token: POST {}", url);

        return keycloakWebClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("client_id", properties.getEcomClientId())
                        .with("client_secret", properties.getEcomClientSecret())
                        .with("token", token))
                .retrieve()
                .bodyToMono(IntrospectResponse.class)
                .doOnSuccess(r -> LoggerUtils.info(KeycloakUserClient.class, "Token validation result: active={}", r.getActive()))
                .onErrorResume(WebClientResponseException.class, ex -> mapError(ex, "Validate token failed"));
    }

    // ======================== LOGOUT ========================

    public Mono<Void> logout(String refreshToken) {
        String url = String.format("/realms/%s/protocol/openid-connect/logout", properties.getEcomRealm());
        LoggerUtils.info(KeycloakUserClient.class, "User logout: POST {}", url);

        return keycloakWebClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("client_id", properties.getEcomClientId())
                        .with("refresh_token", refreshToken))
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(r -> LoggerUtils.info(KeycloakUserClient.class, "User logged out successfully"))
                .then()
                .onErrorResume(WebClientResponseException.class, ex -> mapError(ex, "Logout failed"));
    }

    // ======================== UPDATE USER ========================

    public Mono<Void> updateUser(String adminToken, String userId, Map<String, Object> updateBody) {
        String url = String.format("/admin/realms/%s/users/%s", properties.getEcomRealm(), userId);
        LoggerUtils.info(KeycloakUserClient.class, "Updating user: PUT {} userId={}", url, userId);

        return keycloakWebClient.put()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateBody)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(r -> LoggerUtils.info(KeycloakUserClient.class, "User updated successfully: userId={}", userId))
                .then()
                .onErrorResume(WebClientResponseException.class, ex -> mapError(ex, "Update user failed"));
    }

    // ======================== RESET PASSWORD ========================

    public Mono<Void> resetPassword(String adminToken, String userId, String newPassword, boolean temporary) {
        String url = String.format("/admin/realms/%s/users/%s/reset-password", properties.getEcomRealm(), userId);
        LoggerUtils.info(KeycloakUserClient.class, "Resetting password: PUT {} userId={}", url, userId);

        Map<String, Object> body = Map.of(
                "type", "password",
                "value", newPassword,
                "temporary", temporary
        );

        return keycloakWebClient.put()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(r -> LoggerUtils.info(KeycloakUserClient.class, "Password reset for userId={}", userId))
                .then()
                .onErrorResume(WebClientResponseException.class, ex -> mapError(ex, "Reset password failed"));
    }

    private <T> Mono<T> mapError(WebClientResponseException ex, String context) {
        LoggerUtils.error(KeycloakUserClient.class, "{}: status={}, body={}",
                context, ex.getStatusCode(), ex.getResponseBodyAsString());
        return Mono.error(new BusinessException(ErrorCode.KEYCLOAK_ERROR,
                context + ": " + ex.getResponseBodyAsString()));
    }
}
