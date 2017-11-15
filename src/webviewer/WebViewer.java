/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webviewer;

/**
 *
 * @author Yura
 */
public class WebViewer {

    public static void main(String args[]) throws InterruptedException {
        WebCam cam = new WebCam();
        cam.start();
    }
}
