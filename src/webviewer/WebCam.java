package webviewer;

import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import webviewer.util.ImgUtils;

public class WebCam {

    private static WebCam insatance;

    private static final int WIDTH = 1280; // 1366 1280
    private static final int HEIGHT = 1280;

    private VideoCapture camera;

    public static synchronized WebCam getInsatance() {
        if (insatance == null) {
            insatance = new WebCam();
        }
        return insatance;
    }

    private WebCam() {
        camera = new VideoCapture(0);
        camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, WIDTH);
        camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, HEIGHT);

        System.out.println("WebCam is open: " + camera.isOpened());
    }

    public void setStop() {
        release();
    }

    public VideoWriter initWriter(String filePath) {
        Size frameSize = new Size(
                (int) camera.get(Videoio.CAP_PROP_FRAME_WIDTH),
                (int) camera.get(Videoio.CAP_PROP_FRAME_HEIGHT)
        );
        int fps = 20;
        VideoWriter writer = new VideoWriter(
                filePath,
                Codec.MOTIONJPEG,
                fps,
                frameSize,
                true
        );
        return writer;
    }

    public Mat getImage() {
        if (!camera.isOpened()) {
            System.out.println("Camera is not open!");
        } else {
            Mat frame = new Mat();
            if (camera.read(frame)) {
                return frame;
            }
        }
        return null;
    }

    public void write(String filePath, int sec) {
        VideoWriter writer = null;
        try {
            writer = initWriter(filePath);
            int i = 0;
            while (i < sec) {
                Mat frame = this.getImage();
                if (frame == null) {
                    break;
                }
                if (frame.width() <= 0 || frame.height() <= 0) {
                    continue;
                }
                writer.write(frame);
                frame.release();
                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(WebCam.class.getName()).log(Level.SEVERE, null, ex);
                }
                i++;
            }
        } finally {
            if (writer != null) {
                writer.release();
            }
            this.release();
        }
    }

    public BufferedImage show() {
        try {
            Mat frame = this.getImage();
            BufferedImage image = ImgUtils.createBufferedImage(frame);
            return image;
        } catch (Exception e) {
            return null;
        }
    }

    public void cap(String path) {
        Mat mat = null;
        try {
            int i = 0;
            while (i < 4) {
                mat = this.getImage();
                System.out.println("cap " + i);
                if (mat != null && !mat.empty()) {
//                    System.out.println("write cap " + mat.elemSize());
                    Imgcodecs.imwrite(path, mat);
//                    break;
                }
                i++;
            }
        } catch (Exception e) {
        } finally {
            this.release();
            if (mat != null) {
                mat.release();
            }
        }
    }

    public void release() {
        if (camera != null) {
            camera.release();
        }
        System.out.println("WebCam release");
    }

}
