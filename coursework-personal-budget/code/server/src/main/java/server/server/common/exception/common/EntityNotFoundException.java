package server.server.common.exception.common;

import server.server.common.exception.BaseKeyException;

public class EntityNotFoundException extends BaseKeyException {
    public EntityNotFoundException(String message, String key, Class<?> entityClass) {
        super(buildMessage(entityClass, message), key);
    }

    private static String buildMessage(Class<?> entityClass, String message) {
        return "Exception: " +
                entityClass.getSimpleName() +
                " not found" +
                (message == null || message.isBlank() ? "" : " (" + message + ")") +
                ".";
    }
}
