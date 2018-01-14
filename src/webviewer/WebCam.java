package webviewer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

public class WebCam {

    private static final String S = System.getProperty("file.separator"); // separator
    private static final String LIB_NAME = "opencv_java310.dll";

    static {
        extractLibs();
    }

    private static final String FILE_PATH = "D:/Projects/src/test.avi";

    private static final int WIDTH = 1280; // 1366 1280
    private static final int HEIGHT = 720;
    private static boolean stop = false;

    public void setStop() {
        stop = true;
    }

    public static BufferedImage createBufferedImage(Mat mat) {
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        mat.get(0, 0, data);
        return image;
    }

    public final void start() {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture camera = new VideoCapture(0);
        WebFrame webFrame = new WebFrame(this);
        webFrame.setVisible(true);

        //<editor-fold defaultstate="collapsed" desc="Возможные значения для VideoWriter.fourcc">
//            VideoWriter.fourcc(‘X’,’V’,’I’,’D’) = кодек XviD
//            VideoWriter.fourcc(‘P’,’I’,’M’,’1′) = MPEG-1
//            VideoWriter.fourcc(‘M’,’J’,’P’,’G’) = motion-jpeg
//            VideoWriter.fourcc(‘M’, ‘P’, ‘4’, ‘2’) = MPEG-4.2
//            VideoWriter.fourcc(‘D’, ‘I’, ‘V’, ‘3’) = MPEG-4.3
//            VideoWriter.fourcc(‘D’, ‘I’, ‘V’, ‘X’) = MPEG-4
//            VideoWriter.fourcc(‘U’, ‘2’, ‘6’, ‘3’) = H263
//            VideoWriter.fourcc(‘I’, ‘2’, ‘6’, ‘3’) = H263I
//            VideoWriter.fourcc(‘F’, ‘L’, ‘V’, ‘1’) = FLV1
        //</editor-fold>
        VideoWriter writer = new VideoWriter(FILE_PATH,
                VideoWriter.fourcc('D', 'I', 'V', '3'),
                15,
                new Size(WIDTH, HEIGHT),
                true);
        camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, WIDTH);
        camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, HEIGHT);

        if (!camera.isOpened()) {
            System.out.println("Error");
        } else {
            int index = 0;
            Mat frame = new Mat();

            while (!stop) {
                if (camera.read(frame)) {
                    writer.write(frame);
                    BufferedImage image = createBufferedImage(frame);
                    webFrame.setLb(new ImageIcon(image));
                    index++;
                }
                frame.release();
            }
        }
        writer.release();
        camera.release();
    }

    private static String getCurrentDir() {
        // определяем текущий каталог
        File currentDir = new File(".");
        try {
            return currentDir.getCanonicalPath() + S;
        } catch (IOException ex) {
            Logger.getLogger(WebCam.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public static boolean copy(String sourceStr, String fileName) throws Exception {
//        InputStream source = Thread.currentThread().getContextClassLoader().getResourceAsStream(sourceStr);
//        InputStream source = WebCam.class.getResourceAsStream(sourceStr);
        InputStream source = WebCam.class.getResourceAsStream(sourceStr.replace("\\", "\\\\"));
        if (source == null) {
            throw new Exception("Cannot get resource \"" + sourceStr + "\" from Jar file.");
        }
//        String destination = getBasePathForClass(WebCam.class)+fileName;
        String destination = getCurrentDir() + fileName;

        boolean succeess = true;

        System.out.println("Copying ->" + source + "\n\tto ->" + destination);

        try {
            Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(WebCam.class.getName()).log(Level.SEVERE, null, ex);
            succeess = false;
        }

        return succeess;
    }

    private static void extractLibs() {
//        String osName = System.getProperty("os.name").toLowerCase();
        String version = System.getProperty("os.arch").toLowerCase();
        String libDir = "lib" + S + "x" + (version.contains("64") ? "64" : "32") + S;
        String dirPath = getCurrentDir() + libDir;
        System.out.println("dirPath: " + dirPath);
        File file = new File(dirPath);
        if (!file.exists()) {
            if (file.mkdirs()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        } else {
            System.out.println("Directory is exist!");
        }

        try {
//            String libPath = libDir + LIB_NAME; // Почему так не работает не известно :(
            String libPath = "lib/x64/" + LIB_NAME;
            System.out.println("libPath:" + libPath);

            copy(libPath, libPath);
            copy("lib/opencv-310.jar", "lib/opencv-310.jar");
            System.out.println("Copy is ok!");
        } catch (Exception ex) {
            System.out.println("Copy is not ok!");
            Logger.getLogger(WebCam.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.load(getCurrentDir() + libDir + LIB_NAME); // Нужен абсолютный путь
//        System.setProperty("java.library.path", libPath + ";" + System.getProperty("java.library.path"));
//        System.loadLibrary(LIB_NAME); // Необходимо название либы
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
