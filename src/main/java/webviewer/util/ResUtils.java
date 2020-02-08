package webviewer.util;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResUtils {

    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static final String S = System.getProperty("file.separator");

    private static Properties properties;

    private ResUtils() {
    }

    public static String getCurrentDir() {
        File currentDir = new File(".");
        try {
            return currentDir.getCanonicalPath() + S;
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return "";
        }
    }

    public static String getProperty(String str) {
        return properties.getProperty(str);
    }
}
