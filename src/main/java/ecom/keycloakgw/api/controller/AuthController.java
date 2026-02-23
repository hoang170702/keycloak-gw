package ecom.keycloakgw.api.controller;

import ecom.keycloakgw.application.dto.ApiResponse;
import ecom.keycloakgw.application.dto.request.AdminLoginRequest;
import ecom.keycloakgw.application.dto.response.TokenResponse;
import ecom.keycloakgw.application.service.AuthService;
import ecom.keycloakgw.domain.utils.BaseController;
import ecom.log.annotation.UseAspect;
import ecom.log.utils.LoggerUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@UseAspect
public class AuthController extends BaseController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/admin/login")
    public Mono<ApiResponse<TokenResponse>> adminLogin(
            @RequestBody AdminLoginRequest request,
            ServerWebExchange exchange) {

        String requestId = getRequestId(exchange);

        LoggerUtils.info(AuthController.class, "Admin login request received, username={}", request.getUsername());
        return authService.adminLogin(request)
                .map(tokenResponse -> ApiResponse.success(requestId, tokenResponse));
    }


}
