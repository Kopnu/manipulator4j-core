/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.exception;

/**
 * GearConstructionException
 *
 * @author Sergei_Konilov
 */
public class GearConstructionException extends RuntimeException {

    public GearConstructionException(String message) {
        super(message);
    }

    public GearConstructionException(String message, Throwable cause) {
        super(message, cause);
    }
}
