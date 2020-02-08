package webviewer;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import webviewer.util.ResUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.invoke.MethodHandles;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpStreamServer implements Runnable {

    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final String boundary = "stream";

    private ServerSocket serverSocket;
    private Socket socket;
    private Mat frame;
    private int port;

    public HttpStreamServer(Mat frame) {
        this.frame = frame;
        this.port = Integer.parseInt(ResUtils.getProperty("stream.port"));
    }

    private void startStreamingServer() throws IOException {
        serverSocket = new ServerSocket(port);
        socket = serverSocket.accept();
        writeHeader(socket.getOutputStream(), boundary);
    }

    private void writeHeader(OutputStream stream, String boundary) throws IOException {
        stream.write(("HTTP/1.0 200 OK\r\n"
                + "Connection: close\r\n"
                + "Max-Age: 0\r\n"
                + "Expires: 0\r\n"
                + "Cache-Control: no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\r\n"
                + "Pragma: no-cache\r\n"
                + "Content-Type: multipart/x-mixed-replace; "
                + "boundary=" + boundary + "\r\n"
                + "\r\n"
                + "--" + boundary + "\r\n").getBytes());
    }

    public void pushImage(Mat frame) throws IOException {
        if (frame == null) {
            return;
        }
        try {
            OutputStream outputStream = socket.getOutputStream();
            BufferedImage img = mat2bufferedImage(frame);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();
            outputStream.write(("Content-type: image/jpeg\r\n"
                    + "Content-Length: " + imageBytes.length + "\r\n"
                    + "\r\n").getBytes());
            outputStream.write(imageBytes);
            outputStream.write(("\r\n--" + boundary + "\r\n").getBytes());
        } catch (Exception ex) {
            socket = serverSocket.accept();
            writeHeader(socket.getOutputStream(), boundary);
        }
    }

    public void run() {
        try {
            LOG.log(Level.INFO, "Go to  http://localhost:{0} with browser", Integer.toString(port));
            startStreamingServer();
            while (true) {
                pushImage(frame);
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }

    }

    public void stopStreamingServer() throws IOException {
        socket.close();
        serverSocket.close();
    }

    private static BufferedImage mat2bufferedImage(Mat image) throws IOException {
        MatOfByte bytemat = new MatOfByte();
        Imgcodecs.imencode(".jpg", image, bytemat);
        byte[] bytes = bytemat.toArray();
        InputStream in = new ByteArrayInputStream(bytes);
        return ImageIO.read(in);
    }

    public void setFrame(Mat frame) {
        this.frame = frame;
    }
}
