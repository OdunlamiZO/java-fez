package io.github.odunlamizo.fez.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateOrderRequest {

    /** Recipient Address (delivery destination). */
    private String recipientAddress;

    /** Must be any of the 36 states or FCT. */
    private String recipientState;

    /** Recipient Name. */
    private String recipientName;

    /** Recipient Phone or Mobile number. */
    private String recipientPhone;

    /** Recipient Email. */
    private String recipientEmail;

    /** Any value that uniquely identifies a delivery request from the client's side. */
    @JsonProperty("uniqueID")
    private String uniqueId;

    /**
     * Any value that identifies the batch; all delivery requests in one API call share the same
     * Batch ID.
     */
    @JsonProperty("BatchID")
    private String batchId;

    /** Token sent to recipient for confirmation of receipt. */
    @JsonProperty("CustToken")
    private String custToken;

    /** Description of the order. */
    private String itemDescription;

    /**
     * Any additional details about the order (e.g. additional phone numbers or address landmarks).
     */
    private String additionalDetails;

    /** The value of items. */
    private String valueOfItem;

    /** Weight of item in Kg (Kilogram). */
    private Integer weight;

    /** Pick-up state. Must be any of the 36 states or FCT. */
    private String pickUpState;

    /**
     * The address from which the item should be picked up. Defaults to business address if not
     * supplied.
     */
    private String pickUpAddress;

    /** Unique string from 3rd-party logistics partners for a given order. */
    private String waybillNumber;

    /** Date on which the order should be picked up (e.g. "2024-01-01"). */
    private String pickUpDate;

    /** Indicates if the item is to be paid for at the point of delivery. Defaults to false. */
    private Boolean isItemCod;

    /** Amount to be paid for the item at delivery. */
    private BigDecimal cashOnDeliveryAmount;

    /** Indicates the item is fragile. Defaults to false. */
    private Boolean fragile;

    /** The selected locker ID to deliver the item to. */
    @JsonProperty("lockerID")
    private String lockerId;

    // Third-party sender fields — set thirdparty to "true" to use these

    /** Set to "true" to specify a different sender from the default business profile. */
    private String thirdparty;

    /** Name of the sender (required when thirdparty is "true"). */
    private String senderName;

    /** Address of the sender (required when thirdparty is "true"). */
    private String senderAddress;

    /** Phone number of the sender (required when thirdparty is "true"). */
    private String senderPhone;
}
