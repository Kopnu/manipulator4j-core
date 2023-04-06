/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.exception;

/**
 * ManipulatorRuntimeException
 *
 * @author Sergei_Konilov
 */
public class ManipulatorRuntimeException extends RuntimeException {

    public ManipulatorRuntimeException(String message) {
        super(message);
    }

    public ManipulatorRuntimeException(Throwable cause) {
        super(cause);
    }

    public ManipulatorRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
