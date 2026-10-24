package server.server.common.exception.auth;

import server.server.common.exception.BaseKeyException;

public class AuthException extends BaseKeyException {

    public AuthException(String message, String key) {
        super(message, key);
    }
}
