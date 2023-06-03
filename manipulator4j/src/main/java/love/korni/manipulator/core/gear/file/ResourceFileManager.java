/*
 * CivCraft. Do not reproduce without permission in writing.
 * Copyright (c) 2022 GrandProject Team.
 */

package love.korni.manipulator.core.gear.file;

import love.korni.manipulator.core.gear.file.exception.FileManagerException;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

/**
 * Имплементация {@link FileManager} для доступа к файлам из resource.
 *
 * @author Sergei_Konilov
 */
public class ResourceFileManager implements FileManager {

    public static final String CLASSPATH = "classpath:";

    @Override
    public InputStream readFile(String path) throws FileManagerException {
        if (path.startsWith(CLASSPATH)) {
            path = path.replace(CLASSPATH, "");
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(path);
            if (Objects.isNull(resourceAsStream)) {
                throw new FileManagerException(
                        String.format("Can't open resource stream. Maybe [%s] doesn't exist.", path));
            }
            return resourceAsStream;
        }
        throw new FileManagerException(
                String.format("Unsupported path format: [%s]. Use \"classpath:dir/example.yml\"", path));
    }

    @Override
    public String readFileAsString(String path) {
        try(InputStream inputStream = readFile(path)) {
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new FileManagerException(String.format("Exception while reading a file by path [%s]", path), e);
        }
    }

    public List<String> list() {
        try {
            Enumeration<URL> resources = getClass().getClassLoader().getResources("");
            return Collections.list(resources).stream().map(URL::getPath).toList();
        } catch (IOException e) {
            throw new FileManagerException(e);
        }
    }
}
