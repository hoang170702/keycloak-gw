package ecom.keycloakgw.application.validator.impl;

import ecom.keycloakgw.application.dto.request.CreateUserRequest;
import ecom.keycloakgw.application.validator.Validator;
import ecom.keycloakgw.domain.exception.BusinessException;
import ecom.keycloakgw.domain.exception.ErrorCode;
import ecom.log.utils.LoggerUtils;
import org.springframework.stereotype.Component;

@Component
public class UserRegisterValidate implements Validator<CreateUserRequest> {

    private final static Class<?> clazz = UserRegisterValidate.class;

    @Override
    public void validate(CreateUserRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            LoggerUtils.warn(clazz, ErrorCode.USERNAME_REQUIRED.getMessage());
            throw new BusinessException(ErrorCode.USERNAME_REQUIRED, ErrorCode.USERNAME_REQUIRED.getMessage());
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            LoggerUtils.warn(clazz, ErrorCode.PASSWORD_REQUIRED.getMessage());
            throw new BusinessException(ErrorCode.PASSWORD_REQUIRED, ErrorCode.PASSWORD_REQUIRED.getMessage());
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            LoggerUtils.warn(clazz, ErrorCode.EMAIL_REQUIRED.getMessage());
            throw new BusinessException(ErrorCode.EMAIL_REQUIRED, ErrorCode.EMAIL_REQUIRED.getMessage());
        }
        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            LoggerUtils.warn(clazz, ErrorCode.FIRST_NAME_REQUIRED.getMessage());
            throw new BusinessException(ErrorCode.FIRST_NAME_REQUIRED, ErrorCode.FIRST_NAME_REQUIRED.getMessage());
        }
        if (request.getLastName() == null || request.getLastName().isBlank()) {
            LoggerUtils.warn(clazz, ErrorCode.LAST_NAME_REQUIRED.getMessage());
            throw new BusinessException(ErrorCode.LAST_NAME_REQUIRED, ErrorCode.LAST_NAME_REQUIRED.getMessage());
        }
    }
}
