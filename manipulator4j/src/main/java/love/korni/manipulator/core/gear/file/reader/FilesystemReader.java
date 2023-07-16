package love.korni.manipulator.core.gear.file.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sergei_Kornilov
 */
public class FilesystemReader implements FileReader {

    public static final String FILE = "file:";

    @Override
    public InputStream read(String path) throws IOException {
        path = path.replace(FILE, "");
        File file = new File(path);
        return new FileInputStream(file);
    }

    @Override
    public boolean canRead(String path) {
        return path.startsWith(FILE);
    }
}
