package models.lombok;

import lombok.Data;

@Data
public class RegistrationFailedResponseModel {
    String id, token, error;
}
