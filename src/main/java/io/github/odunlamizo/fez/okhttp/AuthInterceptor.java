package io.github.odunlamizo.fez.okhttp;

import java.io.IOException;
import java.util.function.Supplier;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class AuthInterceptor implements Interceptor {

    private final Supplier<String> secretKeySupplier;

    private final Supplier<String> bearerTokenSupplier;

    public AuthInterceptor(
            Supplier<String> bearerTokenSupplier, Supplier<String> secretKeySupplier) {
        this.bearerTokenSupplier = bearerTokenSupplier;
        this.secretKeySupplier = secretKeySupplier;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request requestWithAuth =
                original.newBuilder()
                        .header("Authorization", "Bearer " + bearerTokenSupplier.get())
                        .header("secret-key", secretKeySupplier.get())
                        .build();

        return chain.proceed(requestWithAuth);
    }
}
