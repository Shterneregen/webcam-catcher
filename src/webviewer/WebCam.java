package webviewer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import javax.swing.ImageIcon;
import org.opencv.core.Core;  
import org.opencv.core.Mat;  
import org.opencv.core.Size;  
import org.opencv.videoio.VideoCapture;  
import org.opencv.videoio.VideoWriter;  
import org.opencv.videoio.Videoio;  
  
public class WebCam {  

//    public WebCam() {
//        getVideo();
//    }
    
    
    public static BufferedImage createBufferedImage(Mat mat) {
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        mat.get(0, 0, data);
        return image;
    }
    
    public static void main (String args[]) throws InterruptedException{
//    public static void getVideo () {  
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);  
        VideoCapture camera = new VideoCapture(0); 
        WebFrame webFrame = new WebFrame();
          webFrame.setVisible(true);
//        VideoWriter writer = new VideoWriter("d:/test.avi", VideoWriter.fourcc('X', 'V', 'I', 'D'), 15, new Size(1366, 720), true);
//        VideoWriter writer = new VideoWriter("d:/test.avi", VideoWriter.fourcc('D', 'I', 'V', 'X'), 15, new Size(1366, 720), true);  
        VideoWriter writer = new VideoWriter("d:/test1.avi", VideoWriter.fourcc('D', 'I', 'V', '3'), 15, new Size(1366, 720), true);
        
//            Возможные значения для VideoWriter.fourcc:
//            VideoWriter.fourcc(‘X’,’V’,’I’,’D’) = кодек XviD
//            VideoWriter.fourcc(‘P’,’I’,’M’,’1′) = MPEG-1
//            VideoWriter.fourcc(‘M’,’J’,’P’,’G’) = motion-jpeg
//            VideoWriter.fourcc(‘M’, ‘P’, ‘4’, ‘2’) = MPEG-4.2
//            VideoWriter.fourcc(‘D’, ‘I’, ‘V’, ‘3’) = MPEG-4.3
//            VideoWriter.fourcc(‘D’, ‘I’, ‘V’, ‘X’) = MPEG-4
//            VideoWriter.fourcc(‘U’, ‘2’, ‘6’, ‘3’) = H263
//            VideoWriter.fourcc(‘I’, ‘2’, ‘6’, ‘3’) = H263I
//            VideoWriter.fourcc(‘F’, ‘L’, ‘V’, ‘1’) = FLV1
          
        camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 1280);  
        camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 720);  
          
        if(!camera.isOpened()){  
            System.out.println("Error");  
        }  
        else {  
            int index = 0;  
            Mat frame = new Mat();  
                 
            while(true){  
                if (camera.read(frame)){  
//                    System.out.println("Captured Frame Width " + frame.width() + " Height " + frame.height());  
                    writer.write(frame);  
                    BufferedImage image = createBufferedImage(frame);
                    webFrame.setLb(new ImageIcon(image));
//                    webFrame.setContentPane(webFrame);
//                    Thread.currentThread().sleep(66);  
                    index++;  
                }  
  
//                if (index > 100) {  
//                    break;  
//                }  
                  
                frame.release();  
            }     
        }  
        writer.release();  
        camera.release();  
    }  
}     
