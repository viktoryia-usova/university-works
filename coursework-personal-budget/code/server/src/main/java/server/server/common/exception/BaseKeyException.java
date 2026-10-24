package server.server.common.exception;

import lombok.Getter;

@Getter
public abstract class BaseKeyException extends RuntimeException {

    private final String key;

    protected BaseKeyException(String message, String key) {
        super(message);
        this.key = key;
    }
}
