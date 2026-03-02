package ecom.keycloakgw.infrastructure.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

    private String baseUrl;
    private Master master;
    private Ecom ecom;

    @Getter
    @Setter
    public static class Master {
        private String adminRealm;
        private String adminClientId;
        private UrlMaster url;

        @Getter
        @Setter
        public static class UrlMaster {
            private String adminToken;
        }
    }

    @Getter
    @Setter
    public static class Ecom {
        private String ecomRealm;
        private String ecomClientId;
        private String ecomClientSecret;
        private UrlEcom url;

        @Getter
        @Setter
        public static class UrlEcom {
            private String createUser;
            private String getUserByUsername;
            private String getUserById;
            private String getRealmRole;
            private String assignRealmRole;
            private String userLogin;
            private String validateToken;
            private String logout;
            private String updateUser;
            private String resetPassword;
        }
    }


}
