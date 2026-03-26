package io.github.odunlamizo.fez.okhttp;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.odunlamizo.fez.Fez;
import io.github.odunlamizo.fez.model.AuthenticateRequest;
import io.github.odunlamizo.fez.model.AuthenticateResponse;
import io.github.odunlamizo.fez.model.CreateOrderRequest;
import io.github.odunlamizo.fez.model.CreateOrderResponse;
import io.github.odunlamizo.fez.model.FezResponse;
import io.github.odunlamizo.fez.model.OrderWebhookPayload;
import io.github.odunlamizo.fez.model.RegisterWebhookRequest;
import io.github.odunlamizo.fez.model.RegisterWebhookResponse;
import io.github.odunlamizo.fez.util.JsonUtil;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;
import lombok.NonNull;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/** Fez Delivery API implementation powered by OkHttp */
public class FezOkHttp implements Fez {

    private final String userId;

    private final String baseUrl;

    private final String password;

    private final OkHttpClient client;

    private volatile String authToken;

    private volatile String secretKey;

    private final OkHttpClient authClient;

    private volatile LocalDateTime tokenExpiry;

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final DateTimeFormatter EXPIRY_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public FezOkHttp(@NonNull String userId, @NonNull String password) {
        this(userId, password, "https://apisandbox.fezdelivery.co/v1");
    }

    public FezOkHttp(@NonNull String userId, @NonNull String password, @NonNull String baseUrl) {
        this.userId = userId;
        this.password = password;
        this.baseUrl = baseUrl;
        this.authClient = new OkHttpClient();
        this.client =
                new OkHttpClient.Builder()
                        .addInterceptor(new AuthInterceptor(() -> authToken, () -> secretKey))
                        .build();
    }

    @Override
    public CreateOrderResponse createOrder(@NonNull List<CreateOrderRequest> orders)
            throws IOException {
        ensureAuthenticated();
        final String URL = String.format("%s/order", baseUrl);
        Request request =
                new Request.Builder()
                        .url(URL)
                        .post(RequestBody.create(JsonUtil.toJson(orders), JSON))
                        .build();

        return newCall(request, new TypeReference<>() {});
    }

    @Override
    public RegisterWebhookResponse registerWebhook(@NonNull RegisterWebhookRequest payload)
            throws IOException {
        ensureAuthenticated();
        final String URL = String.format("%s/webhooks/store", baseUrl);
        Request request =
                new Request.Builder()
                        .url(URL)
                        .post(RequestBody.create(JsonUtil.toJson(payload), JSON))
                        .build();

        return newCall(request, new TypeReference<>() {});
    }

    @Override
    public void processWebhook(
            @NonNull String payload, @NonNull Consumer<OrderWebhookPayload> handler)
            throws IOException {
        OrderWebhookPayload webhookPayload = JsonUtil.toValue(payload, new TypeReference<>() {});

        handler.accept(webhookPayload);
    }

    private synchronized void ensureAuthenticated() throws IOException {
        if (authToken == null || isTokenExpired()) {
            AuthenticateRequest payload =
                    AuthenticateRequest.builder().userId(userId).password(password).build();

            final String URL = String.format("%s/user/authenticate", baseUrl);
            Request request =
                    new Request.Builder()
                            .url(URL)
                            .post(RequestBody.create(JsonUtil.toJson(payload), JSON))
                            .build();

            try (okhttp3.Response response = authClient.newCall(request).execute()) {
                String json = null;
                if (response.body() != null) {
                    json = response.body().string();
                }
                AuthenticateResponse authResponse =
                        JsonUtil.toValue(json, new TypeReference<>() {});
                authToken = authResponse.getAuthDetails().getAuthToken();
                secretKey = authResponse.getOrgDetails().getSecretKey();
                tokenExpiry =
                        LocalDateTime.parse(
                                authResponse.getAuthDetails().getExpireToken(), EXPIRY_FORMAT);
            }
        }
    }

    private boolean isTokenExpired() {
        return tokenExpiry != null && LocalDateTime.now().isAfter(tokenExpiry);
    }

    private <T extends FezResponse> T newCall(Request request, TypeReference<T> typeRef)
            throws IOException {
        return newCall(request, typeRef, true);
    }

    private <T extends FezResponse> T newCall(
            Request request, TypeReference<T> typeRef, boolean retryOnInvalidSession)
            throws IOException {
        try (okhttp3.Response response = client.newCall(request).execute()) {
            String json = null;
            if (response.body() != null) {
                json = response.body().string();
            }

            T result = JsonUtil.toValue(json, typeRef);
            result.setCode(String.valueOf(response.code()));

            if (retryOnInvalidSession
                    && "Invalid session".equalsIgnoreCase(result.getDescription())) {
                synchronized (this) {
                    authToken = null;
                }

                ensureAuthenticated();

                return newCall(request, typeRef, false);
            }

            return result;
        }
    }
}
