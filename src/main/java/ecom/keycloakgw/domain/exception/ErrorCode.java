package ecom.keycloakgw.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    SUCCESS("00", "Success"),
    INVALID_REQUEST("001", "Invalid request"),
    UNAUTHORIZED("002", "Unauthorized"),
    KEYCLOAK_ERROR("003", "Keycloak service error"),
    USERNAME_REQUIRED("004", "Username is required"),
    PASSWORD_REQUIRED("005", "Password is required"),
    EMAIL_REQUIRED("006", "Email is required"),
    FIRST_NAME_REQUIRED("007", "First name is required"),
    LAST_NAME_REQUIRED("008", "Last name is required"),
    INTERNAL_ERROR("999", "Internal server error");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
