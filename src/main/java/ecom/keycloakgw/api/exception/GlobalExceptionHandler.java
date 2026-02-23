package ecom.keycloakgw.api.exception;

import ecom.keycloakgw.application.dto.ApiResponse;
import ecom.keycloakgw.domain.exception.BusinessException;
import ecom.keycloakgw.domain.exception.ErrorCode;
import ecom.keycloakgw.infrastructure.filter.RequestIdWebFilter;
import ecom.log.utils.LoggerUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex, ServerWebExchange exchange) {

        String requestId = getRequestId(exchange);

        LoggerUtils.error(GlobalExceptionHandler.class,
                "BusinessException: code={}, message={}", ex.getErrorCode().getCode(), ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                requestId,
                ex.getErrorCode().getCode(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, ServerWebExchange exchange) {

        String requestId = getRequestId(exchange);
        LoggerUtils.error(GlobalExceptionHandler.class,
                "Unexpected error: {}", ex.getMessage(), ex);

        ApiResponse<Void> response = ApiResponse.error(
                requestId,
                ErrorCode.INTERNAL_ERROR.getCode(),
                ErrorCode.INTERNAL_ERROR.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String getRequestId(ServerWebExchange exchange) {
        return (String) exchange.getAttributes()
                .getOrDefault(RequestIdWebFilter.REQUEST_ID_KEY, "");
    }
}
