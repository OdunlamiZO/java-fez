package io.github.odunlamizo.fez;

import java.io.Serial;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FezException extends RuntimeException {

    @Serial private static final long serialVersionUID = 1L;

    public FezException(String message) {
        super(message);
    }

    public FezException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
