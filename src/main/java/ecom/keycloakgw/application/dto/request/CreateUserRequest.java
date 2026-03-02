package ecom.keycloakgw.application.dto.request;

import ecom.keycloakgw.domain.utils.ERole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private ERole roleName;
}
