/*
 * CivCraft. Do not reproduce without permission in writing.
 * Copyright (c) 2022 GrandProject Team.
 */

package love.korni.manipulator.core.gear.file;

import love.korni.manipulator.core.gear.file.exception.FileManagerException;
import love.korni.manipulator.core.gear.file.reader.ClasspathReader;
import love.korni.manipulator.core.gear.file.reader.FileReader;
import love.korni.manipulator.core.gear.file.reader.FilesystemReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Имплементация {@link FileManager} для доступа к файлам из resource.
 *
 * @author Sergei_Konilov
 */
public class ResourceFileManager implements FileManager {

    private final List<FileReader> fileReaders = List.of(new ClasspathReader(), new FilesystemReader());

    @Override
    public InputStream readFile(String path) throws FileManagerException {
        try {
            for (FileReader reader : fileReaders) {
                if (reader.canRead(path)) {
                    return reader.read(path);
                }
            }
        } catch (IOException e) {
            throw new FileManagerException(
                    String.format("Unsupported path format: [%s]. Use \"classpath:dir/example.yml\"", path), e);
        }
        throw new FileManagerException(
            String.format("Unsupported path format: [%s]. Use \"classpath:dir/example.yml\"", path));
    }

    @Override
    public String readFileAsString(String path) {
        try (InputStream inputStream = readFile(path)) {
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new FileManagerException(String.format("Exception while reading a file by path [%s]", path), e);
        }
    }

    @Override
    public boolean fileExists(String path) {
        return readFile(path) != null;
    }
}
