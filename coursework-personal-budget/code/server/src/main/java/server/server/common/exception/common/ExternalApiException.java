package server.server.common.exception.common;

import server.server.common.exception.BaseKeyException;

public class ExternalApiException extends BaseKeyException {
    public ExternalApiException(String message) {
        super(message, "EXTERNAL_API_ERROR");
    }
}
