package webviewer;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

public class WebCam {

    private static WebCam insatance;

    public static synchronized WebCam getInsatance() {
        insatance = insatance == null ? new WebCam() : insatance;
        return insatance;
    }

    private static final int WIDTH = 1280; // 1366 1280
    private static final int HEIGHT = 720;

    private VideoCapture camera;
    private VideoWriter writer;

    private WebCam() {
        camera = new VideoCapture(0);
        camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, WIDTH);
        camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, HEIGHT);

        System.out.println("WebCam " + camera.isOpened());
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
        //<editor-fold defaultstate="collapsed" desc="Возможные значения для VideoWriter.fourcc">
//        int fourcc = VideoWriter.fourcc('X', 'V', 'I', 'D'); //= кодек XviD
//        int fourcc = VideoWriter.fourcc('P', 'I', 'M', '1'); // = MPEG-1
        int fourcc = VideoWriter.fourcc('M', 'J', 'P', 'G'); // = motion-jpeg
//        int fourcc = VideoWriter.fourcc('M', 'P', '4', '2'); // = MPEG-4.2
//        int fourcc = VideoWriter.fourcc('D', 'I', 'V', '3'); // = MPEG-4.3
//        int fourcc = VideoWriter.fourcc('D', 'I', 'V', 'X');// = MPEG-4
//        int fourcc = VideoWriter.fourcc('U', '2', '6', '3'); // = H263
//        int fourcc = VideoWriter.fourcc('U', '2', '6', '4'); // = H264
//        int fourcc = VideoWriter.fourcc('U', '2', '6', '3'); // = H263
//        int fourcc = VideoWriter.fourcc('I', '2', '6', '3'); // = H263I
//        int fourcc = VideoWriter.fourcc('F', 'L', 'V', '1'); //= FLV1
        //</editor-fold>

//Size frameSize = new Size((int) videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH),(int) videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT));
//        Mat fr = new Mat(HEIGHT, WIDTH, CvType.CV_8UC3, Scalar.all(127));
        int fps = 15;
        writer = new VideoWriter(filePath,
                fourcc,
                fps,
                new Size(WIDTH, HEIGHT),
                true);

//        while (true) {
        for (int i = 0; i < 30; i++) {
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
