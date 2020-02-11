package webviewer;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import webviewer.util.ResUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
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
    private boolean stopped = false;

    public HttpStreamServer(Mat frame) {
        this.frame = frame;
        this.port = Integer.parseInt(ResUtils.getProperty("stream.port"));
    }

    public void run() {
        try (OutputStream outputStream = startStreamingServer()) {
            LOG.log(Level.INFO, "Go to  http://localhost:{0} with browser", Integer.toString(port));
            while (!stopped) {
                writeImageToStream(frame, outputStream);
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private OutputStream startStreamingServer() throws IOException {
        serverSocket = new ServerSocket(port);
        socket = serverSocket.accept();
        writeHeader(socket.getOutputStream(), boundary);
        return socket.getOutputStream();
    }

    public void writeImageToStream(Mat frame, OutputStream outputStream) throws IOException {
        if (frame == null) {
            return;
        }
        try {
            writeImageBytesToStream(outputStream, getImageBytes(frame));
        } catch (Exception ex) {
            socket = serverSocket.accept();
            writeHeader(socket.getOutputStream(), boundary);
        }
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

    private void writeImageBytesToStream(OutputStream outputStream, byte[] imageBytes) throws IOException {
        String headers = getHeaders(imageBytes.length);
        outputStream.write(headers.getBytes());
        outputStream.write(imageBytes);
        outputStream.write(("\r\n--" + boundary + "\r\n").getBytes());
    }

    private String getHeaders(int length) {
        return "Content-type: image/jpeg\r\n"
                + "Content-Length: " + length + "\r\n"
                + "\r\n";
    }

    private static byte[] getImageBytes(Mat image) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", image, matOfByte);
        return matOfByte.toArray();
    }

    public void setFrame(Mat frame) {
        this.frame = frame;
    }

    public void stopStreamingServer() {
        stopStreaming();
        close(socket);
        close(serverSocket);
    }

    public void stopStreaming() {
        stopped = true;
    }

    private void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
