package webviewer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

public class WebCam {

    static {
//        try {
//        System.setProperty("java.library.path", "." + File.pathSeparator + "libs" + File.pathSeparator + "x64");
//        System.load("./" + File.pathSeparator + "libs" + File.pathSeparator + "x64" + File.pathSeparator + "opencv_java310.dll");
//        System.load("/libs/x64/opencv_java310.dll");
//            Field fieldSysPath = ClassLoader.class.getDeclaredField("opencv_java310.dll");
//            fieldSysPath.setAccessible(true);
////        fieldSysPath.set(null, null);
//        } catch (NoSuchFieldException | SecurityException ex) {
//            Logger.getLogger(WebCam.class.getName()).log(Level.SEVERE, null, ex);
//        }
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
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
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
}
