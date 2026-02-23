package ecom.keycloakgw.application.validator.impl;

import ecom.keycloakgw.application.dto.request.UserLoginRequest;
import ecom.keycloakgw.application.validator.Validator;
import ecom.keycloakgw.domain.exception.BusinessException;
import ecom.keycloakgw.domain.exception.ErrorCode;
import ecom.log.utils.LoggerUtils;
import org.springframework.stereotype.Component;

@Component
public class UserLoginValidate implements Validator<UserLoginRequest> {

    private final Class<?> clazz = UserLoginRequest.class;

    @Override
    public void validate(UserLoginRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            LoggerUtils.warn(clazz, ErrorCode.USERNAME_REQUIRED.getMessage());
            throw new BusinessException(ErrorCode.USERNAME_REQUIRED, ErrorCode.USERNAME_REQUIRED.getMessage());
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            LoggerUtils.warn(clazz, ErrorCode.PASSWORD_REQUIRED.getMessage());
            throw new BusinessException(ErrorCode.PASSWORD_REQUIRED, ErrorCode.PASSWORD_REQUIRED.getMessage());
        }
    }
}
