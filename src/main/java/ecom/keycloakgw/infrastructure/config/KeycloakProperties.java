package ecom.keycloakgw.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

    private String baseUrl;
    private String adminRealm;
    private String adminClientId;
    private String ecomRealm;
    private String ecomClientId;
    private String ecomClientSecret;
}
