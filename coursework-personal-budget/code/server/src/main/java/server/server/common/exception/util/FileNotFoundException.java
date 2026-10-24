package server.server.common.exception.util;

import server.server.common.exception.BaseKeyException;

public class FileNotFoundException extends BaseKeyException {

    public FileNotFoundException(String filename) {
        super("File not found (filename = " + filename + ").", "FILENAME");
    }
}
