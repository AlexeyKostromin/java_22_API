package models;

import lombok.Data;

@Data
public class UserRegistrationRequestModel {
    String email, password;
}
