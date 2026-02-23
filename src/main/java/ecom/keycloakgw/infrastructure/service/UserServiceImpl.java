package ecom.keycloakgw.infrastructure.service;

import ecom.keycloakgw.application.dto.request.*;
import ecom.keycloakgw.application.dto.response.CreateUserResponse;
import ecom.keycloakgw.application.dto.response.IntrospectResponse;
import ecom.keycloakgw.application.dto.response.TokenResponse;
import ecom.keycloakgw.application.dto.response.UserResponse;
import ecom.keycloakgw.application.service.UserService;
import ecom.keycloakgw.application.validator.Validator;
import ecom.keycloakgw.domain.exception.BusinessException;
import ecom.keycloakgw.domain.exception.ErrorCode;
import ecom.keycloakgw.infrastructure.client.KeycloakUserClient;
import ecom.log.annotation.UseAspect;
import ecom.log.utils.LoggerUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@UseAspect
public class UserServiceImpl implements UserService {

    private final KeycloakUserClient keycloakUserClient;
    private final Validator<CreateUserRequest> createUserValidator;
    private final Validator<UserLoginRequest> userLoginValidator;
    private final Validator<String> userIdValidator;

    public UserServiceImpl(KeycloakUserClient keycloakUserClient, Validator<CreateUserRequest> createUserValidator, Validator<UserLoginRequest> userLoginValidator, Validator<String> userIdValidator) {
        this.keycloakUserClient = keycloakUserClient;
        this.createUserValidator = createUserValidator;
        this.userLoginValidator = userLoginValidator;
        this.userIdValidator = userIdValidator;
    }

    @Override
    public Mono<CreateUserResponse> registerUser(CreateUserRequest request, String authorization) {

        return Mono.defer(() -> {
            LoggerUtils.info(UserServiceImpl.class,
                    "Starting user registration flow for username={}", request.getUsername());

            createUserValidator.validate(request);

            String roleName = request.getRoleName() != null ? request.getRoleName() : "CUSTOMER";
            String adminToken = extractToken(authorization);
            return keycloakUserClient.createUser(
                            adminToken,
                            request.getUsername(),
                            request.getEmail(),
                            request.getFirstName(),
                            request.getLastName(),
                            request.getPassword()
                    )
                    .then(keycloakUserClient.getUserByUsername(adminToken, request.getUsername()))
                    .flatMap(user -> {
                        String userId = user.getId();
                        LoggerUtils.info(UserServiceImpl.class,
                                "User created, userId={}, getting role={}", userId, roleName);
                        return keycloakUserClient.getRealmRole(adminToken, roleName)
                                .flatMap(role -> {

                                    LoggerUtils.info(UserServiceImpl.class,
                                            "Role found: id={}, assigning to userId={}",
                                            role.getId(), userId);

                                    return keycloakUserClient.assignRealmRole(
                                                    adminToken,
                                                    userId,
                                                    role.getId(),
                                                    role.getName()
                                            )
                                            .thenReturn(
                                                    CreateUserResponse.builder()
                                                            .userId(userId)
                                                            .username(request.getUsername())
                                                            .roleName(roleName)
                                                            .message("User registered and role assigned successfully")
                                                            .build()
                                            );
                                });
                    });
        });
    }

    @Override
    public Mono<TokenResponse> userLogin(UserLoginRequest request) {
        return Mono.just(request)
                .doOnNext(r -> LoggerUtils.info(UserServiceImpl.class,
                        "Processing user login for username={}", r.getUsername()))
                .doOnNext(userLoginValidator::validate)
                .flatMap(r -> keycloakUserClient.userLogin(
                        request.getUsername(),
                        request.getPassword())
                );
    }

    @Override
    public Mono<UserResponse> getUserDetail(String userId, String authorization) {
        return Mono.defer(() -> {
            LoggerUtils.info(UserServiceImpl.class, "Getting user detail for userId={}", userId);
            String adminToken = extractToken(authorization);

            userIdValidator.validate(userId);

            return keycloakUserClient.getUserById(adminToken, userId);
        });

    }

    @Override
    public Mono<Void> updateUser(String userId, UpdateUserRequest request, String authorization) {
        return Mono.defer(() -> {
            LoggerUtils.info(UserServiceImpl.class, "Updating user userId={}", userId);
            String adminToken = extractToken(authorization);

            userIdValidator.validate(userId);

            Map<String, Object> body = new HashMap<>();
            if (request.getEmail() != null) body.put("email", request.getEmail());
            if (request.getFirstName() != null) body.put("firstName", request.getFirstName());
            if (request.getLastName() != null) body.put("lastName", request.getLastName());
            if (request.getEnabled() != null) body.put("enabled", request.getEnabled());
            if (request.getEmailVerified() != null) body.put("emailVerified", request.getEmailVerified());
            if (request.getRequiredActions() != null) body.put("requiredActions", request.getRequiredActions());

            return keycloakUserClient.updateUser(adminToken, userId, body);
        });
    }

    @Override
    public Mono<Void> updatePassword(String userId, UpdatePasswordRequest request, String authorization) {
        return Mono.defer(() -> {
            LoggerUtils.info(UserServiceImpl.class, "Updating password for userId={}", userId);
            String adminToken = extractToken(authorization);

            if (userId == null || userId.isBlank()) {
                return Mono.error(new BusinessException(ErrorCode.INVALID_REQUEST, "UserId is required"));
            }
            if (request.getPassword() == null || request.getPassword().isBlank()) {
                return Mono.error(new BusinessException(ErrorCode.INVALID_REQUEST, "Password is required"));
            }

            boolean temporary = request.getTemporary() != null ? request.getTemporary() : false;
            return keycloakUserClient.resetPassword(adminToken, userId, request.getPassword(), temporary);
        });
    }

    @Override
    public Mono<IntrospectResponse> validateToken(ValidateTokenRequest request) {
        return Mono.defer(() -> {
            LoggerUtils.info(UserServiceImpl.class, "Validating token");

            if (request.getToken() == null || request.getToken().isBlank()) {
                return Mono.error(new BusinessException(ErrorCode.INVALID_REQUEST, "Token is required"));
            }

            return keycloakUserClient.validateToken(request.getToken());
        });

    }

    @Override
    public Mono<Void> logout(LogoutRequest request) {
        return Mono.defer(() -> {
            LoggerUtils.info(UserServiceImpl.class, "Processing logout");

            if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
                return Mono.error(new BusinessException(ErrorCode.INVALID_REQUEST, "Refresh token is required"));
            }

            return keycloakUserClient.logout(request.getRefreshToken());
        });
    }

}
