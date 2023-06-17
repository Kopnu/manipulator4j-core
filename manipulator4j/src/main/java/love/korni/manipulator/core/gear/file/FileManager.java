/*
 * CivCraft. Do not reproduce without permission in writing.
 * Copyright (c) 2022 GrandProject Team.
 */

package love.korni.manipulator.core.gear.file;

import love.korni.manipulator.core.gear.file.exception.FileManagerException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Интерфейс для получения файлов.
 *
 * @author Sergei_Konilov
 */
public interface FileManager {

    InputStream readFile(String path) throws FileManagerException;

    String readFileAsString(String path) throws FileManagerException;

    boolean fileExists(String path);
}
