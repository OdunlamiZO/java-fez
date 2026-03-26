package io.github.odunlamizo.fez;

import io.github.odunlamizo.fez.model.CreateOrderRequest;
import io.github.odunlamizo.fez.model.CreateOrderResponse;
import io.github.odunlamizo.fez.model.OrderWebhookPayload;
import io.github.odunlamizo.fez.model.RegisterWebhookRequest;
import io.github.odunlamizo.fez.model.RegisterWebhookResponse;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import lombok.NonNull;

/** Fez Delivery API */
public interface Fez {

    /**
     * Creates one or more delivery orders (or requests a pick-up).
     *
     * <p>This endpoint supports single and batch requests. Each element in the list represents one
     * delivery request. All requests in a single call should share the same {@code batchId}.
     *
     * @param orders list of order payloads to create
     * @return a {@link CreateOrderResponse} containing the assigned order numbers keyed by uniqueId
     * @throws IOException if a network or I/O error occurs while making the request
     */
    CreateOrderResponse createOrder(@NonNull List<CreateOrderRequest> orders) throws IOException;

    /**
     * Registers a webhook URL to receive order status updates from Fez.
     *
     * <p>The registered URL must be a POST route. Fez will send a JSON body containing {@code
     * orderNumber} and {@code status} whenever an order is created or its state changes.
     *
     * @param payload the webhook registration payload containing the callback URL
     * @return a {@link RegisterWebhookResponse} with the registered webhook and full webhook list
     * @throws IOException if a network or I/O error occurs while making the request
     */
    RegisterWebhookResponse registerWebhook(@NonNull RegisterWebhookRequest payload)
            throws IOException;

    /**
     * Parses and processes an incoming order webhook payload from Fez.
     *
     * <p>This method deserializes the raw JSON body that Fez POSTs to the client's registered
     * webhook URL and passes the resulting {@link OrderWebhookPayload} to the supplied handler.
     *
     * @param payload the raw JSON string received from Fez
     * @param handler a consumer that processes the deserialized webhook payload
     * @throws IOException if the payload cannot be parsed
     */
    void processWebhook(@NonNull String payload, @NonNull Consumer<OrderWebhookPayload> handler)
            throws IOException;
}
