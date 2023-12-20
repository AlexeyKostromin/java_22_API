package models;

import lombok.Data;

@Data
public class UserRegistrationFailedResponseModel {
    String id, token, error;
}
