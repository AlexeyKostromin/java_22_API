package models.lombok;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UsersListResponseModel {
    String page;
    @JsonProperty("per_page")
    String perPage;
    int total;
    @JsonProperty("total_pages")
    int totalPages;
    UserData [] data;
    Support support;

    @Data
    public static class UserData {
        int id;
        String email;
        @JsonProperty("first_name")
        String firstName;
        @JsonProperty("last_name")
        String lastName;
        String avatar;
    }

    @Data
    public static class Support {
        String url, text;
    }
}