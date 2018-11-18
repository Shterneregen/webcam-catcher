/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webviewer;

import java.awt.image.BufferedImage;
import java.util.Date;
import javax.swing.ImageIcon;
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
                int sec = args.length > 1 ? Integer.parseInt(args[1]) : 10;
                String filePath = args.length > 2
                        ? args[2]
                        : ResUtils.getCurrentDir() + String.format("%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS_cam.avi", new Date());
                System.out.println("Path: " + filePath);
                System.out.println("Seconds: " + sec);
                cam.write(filePath, sec);
            } else if (mode.equals("-sh")) {
                show(cam);
            } else if (mode.equals("-cap")) {
                String filePath = args.length > 1
                        ? args[1]
                        : ResUtils.getCurrentDir() + String.format("%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS.jpg", new Date());
                cam.cap(filePath);
            }
        } else {
            show(cam);
        }
    }

    private static void show(WebCam cam) {
        WebFrame webFrame = new WebFrame(cam);
        webFrame.setVisible(true);

        while (true) {
            int w = webFrame.getWidth();
            int h = webFrame.getHeight();
            BufferedImage image = cam.show();
            if (w > 0 && h > 0 && image != null) {
                webFrame.setLb(new ImageIcon(ImgUtils.scale(image, w, h - 100)));
            }
        }

    }

}
