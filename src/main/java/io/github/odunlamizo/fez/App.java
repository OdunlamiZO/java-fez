package io.github.odunlamizo.fez;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.odunlamizo.fez.model.CreateOrderRequest;
import io.github.odunlamizo.fez.model.CreateOrderResponse;
import io.github.odunlamizo.fez.okhttp.FezOkHttp;
import java.io.IOException;
import java.util.List;

public class App {

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure().load();

        // Get credentials from .env
        String userId = dotenv.get("FEZ_USER_ID");
        String password = dotenv.get("FEZ_PASSWORD");

        if (userId == null || userId.isEmpty()) {
            System.err.println("Error: FEZ_USER_ID not found in .env file");
            System.exit(1);
        }

        if (password == null || password.isEmpty()) {
            System.err.println("Error: FEZ_PASSWORD not found in .env file");
            System.exit(1);
        }

        Fez fez = new FezOkHttp(userId, password);

        try {
            CreateOrderResponse response =
                    fez.createOrder(
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
                                            .build()));
            System.out.println(response);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
