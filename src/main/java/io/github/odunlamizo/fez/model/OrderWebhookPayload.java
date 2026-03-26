package io.github.odunlamizo.fez.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** The payload Fez POSTs to the client's registered webhook URL when an order changes state. */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderWebhookPayload {

    /** The order's unique number on Fez. */
    private String orderNumber;

    /** The new status of the order (e.g. "Delivered"). */
    private String status;
}
