package ecom.keycloakgw.api.controller;

import ecom.keycloakgw.application.dto.ApiResponse;
import ecom.keycloakgw.application.dto.request.*;
import ecom.keycloakgw.application.dto.response.*;
import ecom.keycloakgw.application.service.UserService;
import ecom.keycloakgw.domain.utils.BaseController;
import ecom.keycloakgw.infrastructure.filter.RequestIdWebFilter;
import ecom.log.annotation.UseAspect;
import ecom.log.utils.LoggerUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@UseAspect
public class UserController extends BaseController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    public Mono<ApiResponse<CreateUserResponse>> registerUser(
            @RequestBody CreateUserRequest request,
            @RequestHeader("Authorization") String authorization,
            ServerWebExchange exchange) {

        String requestId = getRequestId(exchange);
        return userService.registerUser(request, authorization)
                .map(result -> ApiResponse.success(requestId, result));
    }


    @PostMapping("/login")
    public Mono<ApiResponse<TokenResponse>> userLogin(
            @RequestBody UserLoginRequest request,
            ServerWebExchange exchange) {

        String requestId = getRequestId(exchange);
        return userService.userLogin(request)
                .map(token -> ApiResponse.success(requestId, token));
    }


    @GetMapping("/{userId}")
    public Mono<ApiResponse<UserResponse>> getUserDetail(
            @PathVariable String userId,
            @RequestHeader("Authorization") String authorization,
            ServerWebExchange exchange) {

        String requestId = getRequestId(exchange);
        return userService.getUserDetail(userId, authorization)
                .map(user -> ApiResponse.success(requestId, user));
    }


    @PutMapping("/{userId}")
    public Mono<ApiResponse<String>> updateUser(
            @PathVariable String userId,
            @RequestBody UpdateUserRequest request,
            @RequestHeader("Authorization") String authorization,
            ServerWebExchange exchange) {

        String requestId = getRequestId(exchange);
        return userService.updateUser(userId, request, authorization)
                .thenReturn(ApiResponse.success(requestId, "User updated successfully"));
    }


    @PutMapping("/{userId}/reset-password")
    public Mono<ApiResponse<String>> updatePassword(
            @PathVariable String userId,
            @RequestBody UpdatePasswordRequest request,
            @RequestHeader("Authorization") String authorization,
            ServerWebExchange exchange) {

        String requestId = getRequestId(exchange);
        return userService.updatePassword(userId, request, authorization)
                .thenReturn(ApiResponse.success(requestId, "Password updated successfully"));
    }


    @PostMapping("/validate-token")
    public Mono<ApiResponse<IntrospectResponse>> validateToken(
            @RequestBody ValidateTokenRequest request,
            ServerWebExchange exchange) {

        String requestId = getRequestId(exchange);
        return userService.validateToken(request)
                .map(result -> ApiResponse.success(requestId, result));
    }


    @PostMapping("/logout")
    public Mono<ApiResponse<String>> logout(
            @RequestBody LogoutRequest request,
            ServerWebExchange exchange) {

        String requestId = getRequestId(exchange);
        return userService.logout(request)
                .thenReturn(ApiResponse.success(requestId, "Logged out successfully"));
    }


}
