package love.korni.manipulator.core.gear.file.reader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sergei_Kornilov
 */
public interface FileReader {

    InputStream read(String path) throws IOException;

    boolean canRead(String path);

}
