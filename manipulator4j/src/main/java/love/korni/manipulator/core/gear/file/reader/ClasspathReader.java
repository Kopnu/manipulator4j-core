package love.korni.manipulator.core.gear.file.reader;

import love.korni.manipulator.core.gear.file.exception.FileManagerException;

import java.io.InputStream;
import java.util.Objects;

/**
 * @author Sergei_Kornilov
 */
public class ClasspathReader implements FileReader {

    public static final String CLASSPATH = "classpath:";

    @Override
    public InputStream read(String path) {
        path = path.replace(CLASSPATH, "");
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(path);
        if (Objects.isNull(resourceAsStream)) {
            throw new FileManagerException(
                    String.format("Can't open resource stream. Maybe [%s] doesn't exist.", path));
        }
        return resourceAsStream;
    }

    @Override
    public boolean canRead(String path) {
        return path.startsWith(CLASSPATH);
    }
}
