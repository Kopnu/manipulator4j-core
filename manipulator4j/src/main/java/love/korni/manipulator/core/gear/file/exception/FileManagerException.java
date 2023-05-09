/*
 * CivCraft. Do not reproduce without permission in writing.
 * Copyright (c) 2022 GrandProject Team.
 */

package love.korni.manipulator.core.gear.file.exception;

/**
 * FileManagerException
 *
 * @author Sergei_Konilov
 */
public class FileManagerException extends RuntimeException {

    public FileManagerException() {
        super("Unknown exception in FileManagerException");
    }

    public FileManagerException(String message) {
        super(message);
    }

    public FileManagerException(Throwable cause) {
        super(cause);
    }

    public FileManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
