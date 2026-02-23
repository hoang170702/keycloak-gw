package ecom.keycloakgw.application.validator;

public interface Validator<T> {
    void validate(T request);
}
