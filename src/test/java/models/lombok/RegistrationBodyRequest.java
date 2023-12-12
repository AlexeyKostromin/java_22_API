package models.lombok;

import lombok.Data;

@Data
public class RegistrationBodyRequest {
    String email, password;
}
