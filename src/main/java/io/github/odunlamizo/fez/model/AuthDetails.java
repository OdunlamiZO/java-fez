package io.github.odunlamizo.fez.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthDetails {

    /** Bearer token to include in the Authorization header for subsequent requests. */
    private String authToken;

    /** Expiry date/time of the bearer token (e.g. "2023-01-27 05:06:08"). */
    private String expireToken;
}
