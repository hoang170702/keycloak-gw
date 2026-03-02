package ecom.keycloakgw.domain.utils;

import lombok.Getter;

public enum ERole {
    SELLER("SELLER"),
    CUSTOMER("CUSTOMER");

    @Getter
    private final String role;

    ERole(String role) {
        this.role = role;
    }
}
