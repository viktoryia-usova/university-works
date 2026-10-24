package server.server.common.exception.auth;

import server.server.common.exception.BaseKeyException;

public class AlreadyInitializedException extends BaseKeyException {

    public AlreadyInitializedException() {
        super("Application is already initialized", "ALREADY_INITIALIZED");
    }
}
