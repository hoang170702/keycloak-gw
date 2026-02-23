package ecom.keycloakgw.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import ecom.keycloakgw.domain.utils.EApiStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String requestId;
    private EApiStatus status;
    private String code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(String requestId, T data) {
        return ApiResponse.<T>builder()
                .requestId(requestId)
                .status(EApiStatus.EXECUTE)
                .code("000")
                .message("Success")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String requestId, String code, String message) {
        return ApiResponse.<T>builder()
                .requestId(requestId)
                .status(EApiStatus.FAIL)
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
