package webviewer.util;

import webviewer.WebCam;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResUtils {

    private static final String S = System.getProperty("file.separator");
    private static final String CONFIG_FILE = "config.properties";
    private static final String LIB_NAME_DLL = "opencvDll";

    private static Properties properties;

    /**
     * Возвращает текущий каталог
     *
     * @return текущий каталог
     */
    public static String getCurrentDir() {
        File currentDir = new File(".");
        try {
            return currentDir.getCanonicalPath() + S;
        } catch (IOException ex) {
            Logger.getLogger(WebCam.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    /**
     * Копирует ресурсы из jar наружу
     *
     * @param sourceStr путь к ресурсу внутри jar
     * @param fileName  путь к ресурсу
     * @throws Exception
     */
    public static void copy(String sourceStr, String fileName) throws Exception {
        InputStream source = WebCam.class.getResourceAsStream(sourceStr.replace("\\", "\\\\"));
        if (source == null) {
            System.out.println("getCurrentDir " + getCurrentDir());
            throw new Exception("Cannot get resource \"" + sourceStr + "\" from Jar file.");
        }
        String destination = getCurrentDir() + fileName;
        Path dest = Paths.get(destination);
        if (Files.exists(dest)) {
            System.out.println(dest + " exists!");
        } else {
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copying ->" + sourceStr + "\n\tto ->" + destination);
            System.out.println("Copy is ok!");
        }
    }

    /**
     * Извлекает библиотеки из jar
     */
    public static void extractLibs() {
        String version = System.getProperty("os.arch").toLowerCase();
        String dllFolder = version.contains("64") ? "x86_64" : "x86_32";

        String libDir = "lib" + S + dllFolder + S;
        String dirPath = getCurrentDir() + libDir;
        System.out.println("dirPath: " + dirPath);
        File file = new File(dirPath);

        if (!file.exists()) {
            System.out.println(file.mkdirs()
                    ? "Directory is created!"
                    : "Failed to create directory!");
        } else {
            System.out.println("Directory exists!");
        }

        try {
            String configPath = "/" + CONFIG_FILE;
            copy(configPath, CONFIG_FILE);

            InputStream inputStream = new FileInputStream(CONFIG_FILE);
            properties = new Properties();
            properties.load(inputStream);
            String dll = properties.getProperty(LIB_NAME_DLL);
            String libPath = "/nu/pattern/opencv/windows/" + dllFolder + "/" + dll;
            copy(libPath, "lib/" + dllFolder + "/" + dll);

            System.load(getCurrentDir() + libDir + dll); // Нужен абсолютный путь
        } catch (Exception ex) {
            System.out.println("Copy is not ok!");
            Logger.getLogger(WebCam.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getProperty(String str) {
        return properties.getProperty(str);
    }
}
