package ecom.keycloakgw.infrastructure.service;

import ecom.keycloakgw.application.dto.request.AdminLoginRequest;
import ecom.keycloakgw.application.dto.response.TokenResponse;
import ecom.keycloakgw.application.service.AuthService;
import ecom.keycloakgw.domain.exception.BusinessException;
import ecom.keycloakgw.domain.exception.ErrorCode;
import ecom.keycloakgw.infrastructure.client.KeycloakAuthClient;
import ecom.log.annotation.UseAspect;
import ecom.log.utils.LoggerUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@UseAspect
public class AuthServiceImpl implements AuthService {

    private final KeycloakAuthClient keycloakAuthClient;

    public AuthServiceImpl(KeycloakAuthClient keycloakAuthClient) {
        this.keycloakAuthClient = keycloakAuthClient;
    }

    @Override
    public Mono<TokenResponse> adminLogin(AdminLoginRequest request) {
        LoggerUtils.info(AuthServiceImpl.class, "Processing admin login for username={}", request.getUsername());

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            return Mono.error(new BusinessException(ErrorCode.INVALID_REQUEST, "Username is required"));
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return Mono.error(new BusinessException(ErrorCode.INVALID_REQUEST, "Password is required"));
        }

        return keycloakAuthClient.adminLogin(request.getUsername(), request.getPassword());
    }
}
