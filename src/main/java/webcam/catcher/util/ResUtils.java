package webcam.catcher.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResUtils {

    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final String CONFIG_FILE_NAME = "/config.properties";

    private static Properties properties;

    static {
        try (InputStream source = MethodHandles.lookup().lookupClass().getResourceAsStream(CONFIG_FILE_NAME)) {
            properties = new Properties();
            properties.load(source);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Cannot load inner properties", ex);
        }
    }

    private ResUtils() {
    }

    public static String getCurrentDir() {
        File currentDir = new File(".");
        try {
            return currentDir.getCanonicalPath() + File.separator;
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return "";
        }
    }

    public static String getProperty(String str) {
        return properties.getProperty(str);
    }
}
