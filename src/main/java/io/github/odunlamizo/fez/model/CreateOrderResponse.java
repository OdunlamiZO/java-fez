package io.github.odunlamizo.fez.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateOrderResponse extends FezResponse {

    /**
     * Map of uniqueID to assigned order number for each delivery request in the batch.
     *
     * <p>Example: {@code {"KingOne-1234": "ASAC27012319", "KingOne-1235": "JHAZ27012319"}}
     */
    private Map<String, String> orderNos;
}
