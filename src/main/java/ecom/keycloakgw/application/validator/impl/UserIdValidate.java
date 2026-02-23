package ecom.keycloakgw.application.validator.impl;

import ecom.keycloakgw.application.validator.Validator;
import ecom.keycloakgw.domain.exception.BusinessException;
import ecom.keycloakgw.domain.exception.ErrorCode;

public class UserIdValidate implements Validator<String> {
    @Override
    public void validate(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "UserId is required");
        }
    }
}
