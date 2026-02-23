package ecom.keycloakgw.application.service;

import ecom.keycloakgw.application.dto.request.AdminLoginRequest;
import ecom.keycloakgw.application.dto.response.TokenResponse;
import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<TokenResponse> adminLogin(AdminLoginRequest request);
}
