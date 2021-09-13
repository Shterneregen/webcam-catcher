package webcam.catcher;

import nu.pattern.OpenCV;
import org.opencv.highgui.HighGui;
import webcam.catcher.cam.WebCam;
import webcam.catcher.cam.WebFrame;
import webcam.catcher.util.ImgUtils;
import webcam.catcher.util.PropUtils;
import webcam.catcher.util.ResUtils;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public static void main(String[] args) {
        OpenCV.loadLocally();

        WebCam cam = WebCam.getInstance();
        if (args.length <= 0) {
            show(cam);
            return;
        }

        int index = 0;
        String mode = args[index++];
        String[] params = Arrays.copyOfRange(args, index, args.length);

        if (mode.equals("-r")) {
            recordVideo(params, cam);
        } else if (mode.equals("-sh")) {
            show(cam);
        } else if (mode.equals("-cap")) {
            photograph(params, cam);
        } else if (mode.equals("-stream")) {
            cam.stream();
        }
    }

    private static void recordVideo(String[] args, WebCam cam) {
        int sec = args.length > 1
                ? Integer.parseInt(args[1])
                : PropUtils.getDefaultRecSeconds();
        String filePath = args.length > 2
                ? args[2]
                : ResUtils.getCurrentDir() + String.format("%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS_cam.avi", new Date());
        LOG.log(Level.INFO, "Path: {0}", filePath);
        LOG.log(Level.INFO, "Seconds: {0}", sec);
        cam.write(filePath, sec);
    }

    private static void photograph(String[] args, WebCam cam) {
        String filePath = args.length > 1
                ? args[1]
                : ResUtils.getCurrentDir() + String.format("%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS.jpg", new Date());
        cam.photograph(filePath);
    }

    private static void show(WebCam cam) {
        WebFrame webFrame = new WebFrame(cam);
        webFrame.setVisible(true);

        while (true) {
            int width = webFrame.getWidth();
            int height = webFrame.getHeight();
            BufferedImage image = (BufferedImage) HighGui.toBufferedImage(cam.getImage());
            if (width > 0 && height > 0) {
                webFrame.setCamLabelImage(new ImageIcon(ImgUtils.scale(image, width, height)));
            }
        }

//        HighGui.namedWindow("window");
//        while (true) {
//            HighGui.imshow("window", cam.getImage());
//            HighGui.waitKey(100);
//        }
    }

}
