package webviewer;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import webviewer.util.ImgUtils;
import webviewer.util.ResUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebCam {

    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private static WebCam instance;

    private static final int WIDTH = 1280; // 1366 1280
    private static final int HEIGHT = 1280;

    private VideoCapture camera;

    public static synchronized WebCam getInstance() {
        if (instance == null) {
            instance = new WebCam();
        }
        return instance;
    }

    private WebCam() {
        camera = new VideoCapture(0);
        camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, WIDTH);
        camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, HEIGHT);
        LOG.log(Level.INFO, "WebCam is open: {0}", camera.isOpened());
    }

    public void setStop() {
        release();
    }

    private VideoWriter initWriter(String filePath) {
        Size frameSize = new Size(
                (int) camera.get(Videoio.CAP_PROP_FRAME_WIDTH),
                (int) camera.get(Videoio.CAP_PROP_FRAME_HEIGHT)
        );
        int fps = Integer.parseInt(ResUtils.getProperty("fps"));
        LOG.log(Level.INFO, "fps: {0}", fps);
        return new VideoWriter(
                filePath,
                Codec.MOTIONJPEG,
                fps,
                frameSize,
                true
        );
    }

    private Mat getImage() {
        if (!camera.isOpened()) {
            LOG.info("Camera is not open!");
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
            return ImgUtils.createBufferedImage(Objects.requireNonNull(frame));
        } catch (Exception e) {
            return null;
        }
    }

    public void photograph(String path) {
        Mat mat = null;
        try {
            int i = 0;
            while (i < 4) {
                mat = this.getImage();
                LOG.log(Level.INFO, "capture {0}", i);
                if (mat != null && !mat.empty()) {
                    Imgcodecs.imwrite(path, mat);
                }
                i++;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            this.release();
            if (mat != null) {
                mat.release();
            }
        }
    }

    private Timer tmrVideoProcess;

    public void stream() {
        if (!camera.isOpened()) {
            return;
        }

        Mat frame = new Mat();
        HttpStreamServer httpStreamService = new HttpStreamServer(frame);
        new Thread(httpStreamService).start();

        tmrVideoProcess = new Timer(100, (ActionEvent e) -> {
            if (!camera.read(frame)) {
                tmrVideoProcess.stop();
            }
            httpStreamService.setFrame(frame);
        });
        tmrVideoProcess.start();
    }

    private void release() {
        if (camera != null) {
            camera.release();
        }
        LOG.info("WebCam release");
    }

}
