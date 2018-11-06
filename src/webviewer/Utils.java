/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webviewer;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opencv.core.Mat;

/**
 *
 * @author Worker
 */
public class Utils {

    private static final String S = System.getProperty("file.separator");
    private static final String LIB_NAME_DLL = "opencv_java310.dll";
    private static final String LIB_NAME_JAR = "opencv-310.jar";

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
     * @param fileName путь к ресурсу
     * @throws Exception
     */
    public static void copy(String sourceStr, String fileName) throws Exception {
//        InputStream source = Thread.currentThread().getContextClassLoader().getResourceAsStream(sourceStr);
//        InputStream source = WebCam.class.getResourceAsStream(sourceStr);
        InputStream source = WebCam.class.getResourceAsStream(sourceStr.replace("\\", "\\\\"));
        if (source == null) {
            throw new Exception("Cannot get resource \"" + sourceStr + "\" from Jar file.");
        }
        String destination = getCurrentDir() + fileName;
        Path dest = Paths.get(destination);
        if (Files.exists(dest)) {
            System.out.println(dest + " exists!");
        } else {
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copying ->" + sourceStr + "\n\tto ->" + destination);
        }
    }

    /**
     * Извлекает библиотеки из jar
     */
    public static void extractLibs() {
//        String osName = System.getProperty("os.name").toLowerCase();
        String version = System.getProperty("os.arch").toLowerCase();
        String dllFolder = version.contains("64") ? "x64" : "x32";

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
//            180729 внутри jar не работает сепаратор
//            String libPath = libDir + LIB_NAME; // Почему так не работает не известно :(

            String libPath = "lib/" + dllFolder + "/" + LIB_NAME_DLL;
            copy(libPath, libPath);

            String jarPath = "lib" + "/" + LIB_NAME_JAR;
            copy(jarPath, jarPath);

            System.out.println("Copy is ok!");
        } catch (Exception ex) {
            System.out.println("Copy is not ok!");
            Logger.getLogger(WebCam.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.load(getCurrentDir() + libDir + LIB_NAME_DLL); // Нужен абсолютный путь
//        System.setProperty("java.library.path", libPath + ";" + System.getProperty("java.library.path"));
//        System.loadLibrary(LIB_NAME); // Необходимо название либы
    }

    public static BufferedImage createBufferedImage(Mat mat) {
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        mat.get(0, 0, data);
        return image;
    }

    /**
     * Подгоняет изображение под размеры
     *
     * @param img изображение
     * @param w ширина
     * @param h высота
     * @return изображение с измененными размерами
     */
    public static BufferedImage change(BufferedImage img, int w, int h) {
        if (img == null) {
            return null;
        }
        Image image2 = img.getScaledInstance(w, h, Image.SCALE_AREA_AVERAGING);
        BufferedImage changedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = changedImage.createGraphics();
        g2d.drawImage(image2, 0, 0, null);
        g2d.dispose();
        return changedImage;
    }

    /**
     * Масштабирует изображение при изменении размеров окна
     *
     * @param img исходное изображение
     * @param w ширина окна
     * @param h высота окна
     * @return масштабированное изображение
     */
    public static BufferedImage scale(BufferedImage img, int w, int h) {
        int type = BufferedImage.TYPE_INT_RGB;
        BufferedImage dst = new BufferedImage(w, h, type);
        Graphics2D g2 = dst.createGraphics();
        // Fill background for scale to fit.
//        g2.setBackground(UIManager.getColor("Panel.background"));
        g2.clearRect(0, 0, w, h);
        double xScale = (double) w / img.getWidth();
        double yScale = (double) h / img.getHeight();
        // Scaling options:
        // Scale to fit - image just fits in label.
        double scale = Math.min(xScale, yScale);
        // Scale to fill - image just fills label.
        //double scale = Math.max(xScale, yScale);
        int width = (int) (scale * img.getWidth());
        int height = (int) (scale * img.getHeight());
        int x = (w - width) / 2;
        int y = (h - height) / 2;
        g2.drawImage(img, x, y, width, height, null);
        g2.dispose();
        return dst;
    }

    //<editor-fold defaultstate="collapsed" desc="jic">
    /**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName ie.: "/SmartLibrary.dll"
     * @return The path to the exported resource
     * @throws Exception
     */
    static public String ExportResource(String resourceName, String fileName) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {
            //note that each / is a directory down in the "jar tree" been the jar the root of the tree
            stream = WebCam.class.getResourceAsStream(resourceName);
            if (stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = getCurrentDir();
//            jarFolder = new File(WebCam.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
//                    .getParentFile().getPath().replace('\\', '/');

            System.out.println(jarFolder);
            System.out.println(jarFolder + resourceName);

            resStreamOut = new FileOutputStream(jarFolder + fileName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }

        return jarFolder + resourceName;
    }
    //</editor-fold>
}
