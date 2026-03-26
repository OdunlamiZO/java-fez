package io.github.odunlamizo.fez.okhttp;

import static org.junit.jupiter.api.Assertions.*;

import io.github.odunlamizo.fez.model.CreateOrderRequest;
import io.github.odunlamizo.fez.model.CreateOrderResponse;
import io.github.odunlamizo.fez.model.OrderWebhookPayload;
import io.github.odunlamizo.fez.model.RegisterWebhookRequest;
import io.github.odunlamizo.fez.model.RegisterWebhookResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

class FezOkHttpTest {

    private static MockWebServer mockWebServer;
    private FezOkHttp fez;

    private static final String AUTH_SUCCESS_JSON =
            """
            {
                "status": "Success",
                "description": "Login Successfull",
                "authDetails": {
                    "authToken": "test_auth_token",
                    "expireToken": "2099-12-31 23:59:59"
                },
                "userDetails": {
                    "userID": "G-4568-3493",
                    "Full Name": "King One Admin",
                    "Username": "kingOneAdmin"
                },
                "orgDetails": {
                    "secret-key": "test_secret_key",
                    "Org Full Name": "King One Enterprise"
                }
            }
            """;

    private static final String AUTH_EXPIRED_JSON =
            """
            {
                "status": "Success",
                "description": "Login Successfull",
                "authDetails": {
                    "authToken": "expired_token",
                    "expireToken": "2000-01-01 00:00:00"
                },
                "userDetails": {
                    "userID": "G-4568-3493",
                    "Full Name": "King One Admin",
                    "Username": "kingOneAdmin"
                },
                "orgDetails": {
                    "secret-key": "expired_secret",
                    "Org Full Name": "King One Enterprise"
                }
            }
            """;

    private static final List<CreateOrderRequest> SAMPLE_ORDERS =
            List.of(
                    CreateOrderRequest.builder()
                            .recipientAddress("Idumota")
                            .recipientState("Lagos")
                            .recipientName("Femi")
                            .recipientPhone("08000000000000")
                            .uniqueId("KingOne-1234")
                            .batchId("KingOne-1")
                            .valueOfItem("20000")
                            .weight(1)
                            .build());

    @BeforeAll
    static void startServer() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void stopServer() throws Exception {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() {
        String mockBaseUrl = mockWebServer.url("/").toString().replaceAll("/$", "");
        fez = new FezOkHttp("test_user", "test_password", mockBaseUrl);
    }

    private void enqueueAuth() {
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(AUTH_SUCCESS_JSON)
                        .addHeader("Content-Type", "application/json"));
    }

    @Test
    void testAuthenticatesAutomaticallyOnFirstCall() throws IOException, InterruptedException {
        enqueueAuth();
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(201)
                        .setBody(
                                """
                                {
                                    "status": "Success",
                                    "description": "Order Successfully Created",
                                    "orderNos": { "KingOne-1234": "ASAC27012319" }
                                }
                                """)
                        .addHeader("Content-Type", "application/json"));

        fez.createOrder(SAMPLE_ORDERS);

        assertEquals(2, mockWebServer.getRequestCount());
        assertEquals("/user/authenticate", mockWebServer.takeRequest().getPath());
        assertEquals("/order", mockWebServer.takeRequest().getPath());
    }

    @Test
    void testDoesNotReauthenticateWhenTokenIsValid() throws IOException {
        enqueueAuth();
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(201)
                        .setBody(
                                """
                                {
                                    "status": "Success",
                                    "description": "Order Successfully Created",
                                    "orderNos": { "KingOne-1234": "ASAC27012319" }
                                }
                                """)
                        .addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(201)
                        .setBody(
                                """
                                {
                                    "status": "Success",
                                    "description": "Order Successfully Created",
                                    "orderNos": { "KingOne-1235": "JHAZ27012319" }
                                }
                                """)
                        .addHeader("Content-Type", "application/json"));

        int before = mockWebServer.getRequestCount();
        fez.createOrder(SAMPLE_ORDERS);
        fez.createOrder(SAMPLE_ORDERS);

        // Auth only fires once for both calls
        assertEquals(3, mockWebServer.getRequestCount() - before);
    }

    @Test
    void testReauthenticatesWhenTokenIsExpired() throws IOException {
        int before = mockWebServer.getRequestCount();

        // First auth returns an already-expired token
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(AUTH_EXPIRED_JSON)
                        .addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(201)
                        .setBody(
                                """
                                {
                                    "status": "Success",
                                    "description": "Order Successfully Created",
                                    "orderNos": { "KingOne-1234": "ASAC27012319" }
                                }
                                """)
                        .addHeader("Content-Type", "application/json"));

        fez.createOrder(SAMPLE_ORDERS);

        // Second call — token is expired, so re-auth fires
        enqueueAuth();
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(201)
                        .setBody(
                                """
                                {
                                    "status": "Success",
                                    "description": "Order Successfully Created",
                                    "orderNos": { "KingOne-1235": "JHAZ27012319" }
                                }
                                """)
                        .addHeader("Content-Type", "application/json"));

        fez.createOrder(SAMPLE_ORDERS);

        // 4 total: auth, order, re-auth, order
        assertEquals(4, mockWebServer.getRequestCount() - before);
    }

    @Test
    void testCreateOrderSuccess() throws IOException {
        enqueueAuth();
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(201)
                        .setBody(
                                """
                                {
                                    "status": "Success",
                                    "description": "Order Successfully Created",
                                    "orderNos": {
                                        "KingOne-1234": "ASAC27012319",
                                        "KingOne-1235": "JHAZ27012319"
                                    }
                                }
                                """)
                        .addHeader("Content-Type", "application/json"));

        List<CreateOrderRequest> orders =
                List.of(
                        CreateOrderRequest.builder()
                                .recipientAddress("Idumota")
                                .recipientState("Lagos")
                                .recipientName("Femi")
                                .recipientPhone("08000000000000")
                                .uniqueId("KingOne-1234")
                                .batchId("KingOne-1")
                                .valueOfItem("20000")
                                .weight(1)
                                .build(),
                        CreateOrderRequest.builder()
                                .recipientAddress("Idumota3")
                                .recipientState("Lagos")
                                .recipientName("Femi2")
                                .recipientPhone("08000000000000")
                                .uniqueId("KingOne-1235")
                                .batchId("KingOne-1")
                                .valueOfItem("20000")
                                .weight(1)
                                .build());

        CreateOrderResponse response = fez.createOrder(orders);

        assertEquals("201", response.getCode());
        assertEquals("Success", response.getStatus());
        assertEquals("Order Successfully Created", response.getDescription());
        assertNotNull(response.getOrderNos());
        assertEquals(2, response.getOrderNos().size());
        assertEquals("ASAC27012319", response.getOrderNos().get("KingOne-1234"));
        assertEquals("JHAZ27012319", response.getOrderNos().get("KingOne-1235"));
    }

    @Test
    void testCreateOrderFailure() throws IOException {
        enqueueAuth();
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(401)
                        .setBody(
                                """
                                {
                                    "status": "Failed",
                                    "description": "Unauthorized"
                                }
                                """)
                        .addHeader("Content-Type", "application/json"));

        CreateOrderResponse response = fez.createOrder(SAMPLE_ORDERS);

        assertEquals("401", response.getCode());
        assertEquals("Failed", response.getStatus());
        assertNull(response.getOrderNos());
    }

    @Test
    void testRegisterWebhookSuccess() throws IOException {
        enqueueAuth();
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(
                                """
                                {
                                    "status": "Success",
                                    "description": "Request successful",
                                    "data": {
                                        "webhook": "https://example.com",
                                        "webhooks": [
                                            {
                                                "url": "https://example.com",
                                                "type": "App\\\\Models\\\\Order",
                                                "is_active": 1
                                            }
                                        ]
                                    }
                                }
                                """)
                        .addHeader("Content-Type", "application/json"));

        RegisterWebhookRequest request =
                RegisterWebhookRequest.builder().webhook("https://example.com").build();

        RegisterWebhookResponse response = fez.registerWebhook(request);

        assertEquals("200", response.getCode());
        assertEquals("Success", response.getStatus());
        assertEquals("Request successful", response.getDescription());
        assertNotNull(response.getData());
        assertEquals("https://example.com", response.getData().getWebhook());
        assertEquals(1, response.getData().getWebhooks().size());
        assertEquals("https://example.com", response.getData().getWebhooks().get(0).getUrl());
        assertEquals(1, response.getData().getWebhooks().get(0).getIsActive());
    }

    @Test
    void testRegisterWebhookFailure() throws IOException {
        enqueueAuth();
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(401)
                        .setBody(
                                """
                                {
                                    "status": "Failed",
                                    "description": "Unauthorized"
                                }
                                """)
                        .addHeader("Content-Type", "application/json"));

        RegisterWebhookRequest request =
                RegisterWebhookRequest.builder().webhook("https://example.com").build();

        RegisterWebhookResponse response = fez.registerWebhook(request);

        assertEquals("401", response.getCode());
        assertEquals("Failed", response.getStatus());
        assertNull(response.getData());
    }

    @Test
    void testProcessWebhook() throws IOException {
        String payload =
                """
                {
                    "orderNumber": "UKOOIE001F35",
                    "status": "Delivered"
                }
                """;

        AtomicReference<OrderWebhookPayload> captured = new AtomicReference<>();
        fez.processWebhook(payload, captured::set);

        assertNotNull(captured.get());
        assertEquals("UKOOIE001F35", captured.get().getOrderNumber());
        assertEquals("Delivered", captured.get().getStatus());
    }
}
