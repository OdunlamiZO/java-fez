package io.github.odunlamizo.fez.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticateResponse extends FezResponse {

    private AuthDetails authDetails;

    private UserDetails userDetails;

    private OrgDetails orgDetails;
}
