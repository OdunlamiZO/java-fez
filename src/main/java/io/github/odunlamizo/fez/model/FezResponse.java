package io.github.odunlamizo.fez.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class FezResponse {

    /** HTTP status code set by the SDK, not from the response body. */
    private String code;

    /** Response status from Fez (e.g. "Success"). */
    private String status;

    /** Human-readable description of the response. */
    private String description;
}
