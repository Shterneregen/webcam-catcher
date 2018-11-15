/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webviewer;

import java.awt.image.BufferedImage;
import java.util.Date;
import javax.swing.ImageIcon;
import org.opencv.core.Mat;
import webviewer.util.ImgUtils;
import webviewer.util.ResUtils;

/**
 *
 * @author Yura
 */
public class WebViewer {

//    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    public static void main(String args[]) throws InterruptedException {
        // Вытаскиваем либы из jar
        ResUtils.extractLibs();

        WebCam cam = WebCam.getInsatance();

        if (args.length > 0) {
            String mode = args[0];
            if (mode.equals("-r")) {
                String filePath = args.length > 1
                        ? args[1]
                        : ResUtils.getCurrentDir() + String.format("%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS_cam.avi", new Date());
                System.out.println(filePath);
                save(cam, filePath);
            }
        } else {
            WebFrame webFrame = new WebFrame(cam);
            webFrame.setVisible(true);
            show(cam, webFrame);
        }
    }

    private static void show(WebCam cam, WebFrame webFrame) {
        try {
            while (true) {
                Mat frame = cam.getImage();
                if (frame == null) {
                    break;
                }
                if (frame.width() <= 0 || frame.height() <= 0) {
                    continue;
                }
                BufferedImage image = ImgUtils.createBufferedImage(frame);

                int w = webFrame.getWidth();
                int h = webFrame.getHeight();

                webFrame.setLb(new ImageIcon(ImgUtils.scale(image, w, h - 100)));
            }
        } finally {
            cam.release();
        }
    }

    private static void save(WebCam cam, String filePath) {
        try {
            cam.write(filePath);
        } finally {
            cam.release();
        }
    }
}
