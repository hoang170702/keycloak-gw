package ecom.keycloakgw;

import ecom.log.aspect.AutoLogAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class KeycloakGwApplication {

    public static void main(String[] args) {
        SpringApplication.run(KeycloakGwApplication.class, args);
    }

    @Bean
    public AutoLogAspect autoLogAspect() {
        return new AutoLogAspect();
    }
}
