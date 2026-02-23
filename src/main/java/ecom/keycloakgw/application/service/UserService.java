package ecom.keycloakgw.application.service;

import ecom.keycloakgw.application.dto.request.*;
import ecom.keycloakgw.application.dto.response.*;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<CreateUserResponse> registerUser(CreateUserRequest request, String authorization);

    Mono<TokenResponse> userLogin(UserLoginRequest request);

    Mono<UserResponse> getUserDetail(String userId, String authorization);

    Mono<Void> updateUser(String userId, UpdateUserRequest request, String authorization);

    Mono<Void> updatePassword(String userId, UpdatePasswordRequest request, String authorization);

    Mono<IntrospectResponse> validateToken(ValidateTokenRequest request);

    Mono<Void> logout(LogoutRequest request);

    default String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return authorization;
    }

}
