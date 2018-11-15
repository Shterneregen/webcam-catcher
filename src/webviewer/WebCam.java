package webviewer;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

public class WebCam {

    private static WebCam insatance;

    private static final int WIDTH = 1280; // 1366 1280
    private static final int HEIGHT = 1280;

    private VideoCapture camera;
    private VideoWriter writer;

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

    public Mat getImage() {
//        return getImage(false);

        if (!camera.isOpened()) {
            System.out.println("Camera is not open");
        } else {
            Mat frame = new Mat();
//            try {
            if (camera.read(frame)) {
                return frame;
            }
//            } 
//            finally {
//                frame.release();
//            }
        }
        return null;
    }

    public Mat getImage(boolean save) {
        if (!camera.isOpened()) {
            System.out.println("Camera is not open");
        } else {
            Mat frame = new Mat();
            try {
                if (camera.read(frame)) {
                    if (save) {
                        writer.write(frame);
                    }
//                    return new ImageIcon(Utils.createBufferedImage(frame));
                    System.out.println(frame.width() + " " + frame.height());
                    return frame;
                }
            } finally {
                frame.release();
            }
        }
        return null;
    }

    public void write(String filePath) {
        System.out.println("filePath: " + filePath);

//Size frameSize = new Size((int) videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH),(int) videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT));
//        Mat fr = new Mat(HEIGHT, WIDTH, CvType.CV_8UC3, Scalar.all(127));
        int fps = 15;
        writer = new VideoWriter(filePath,
                Codec.XVID,
                fps,
                new Size(WIDTH, HEIGHT),
                true);

//        while (true) {
        for (int i = 0; i < 10; i++) {
            getImage(true);
        }

    }

    public void release() {
        if (writer != null) {
            writer.release();
        }
        if (camera != null) {
            camera.release();
        }
        System.out.println("WebCam release");
    }

}
