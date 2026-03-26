package io.github.odunlamizo.fez.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetails {

    @JsonProperty("userID")
    private String userId;

    @JsonProperty("Full Name")
    private String fullName;

    @JsonProperty("Username")
    private String username;
}
