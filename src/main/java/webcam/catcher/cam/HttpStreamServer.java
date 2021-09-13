package webcam.catcher.cam;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import webcam.catcher.util.PropUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpStreamServer extends Thread {

    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private static final String COMMON_HEADERS = PropUtils.getCommonHeaders();
    private static final String IMAGE_HEADERS = PropUtils.getImageHeaders();
    private static final String IMAGE_BOUNDARY = PropUtils.getImageBoundary();
    private static final String JPG_EXT = ".jpg";

    private final int port;
    private ServerSocket serverSocket;
    private Socket socket;
    private Mat frame;
    private boolean stopped = false;

    public HttpStreamServer(Mat frame) {
        this.frame = frame;
        this.port = PropUtils.getPort();
    }

    @Override
    public void run() {
        try (OutputStream outputStream = getStreamingOutputStream()) {
            LOG.log(Level.INFO, "Go to  http://localhost:{0} with browser", Integer.toString(port));
            while (!stopped) {
                writeImageToStream(frame, outputStream);
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private OutputStream getStreamingOutputStream() throws IOException {
        serverSocket = new ServerSocket(port);
        socket = serverSocket.accept();
        writeHeader(socket.getOutputStream());
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
            writeHeader(socket.getOutputStream());
        }
    }

    private void writeHeader(OutputStream outputStream) throws IOException {
        outputStream.write(COMMON_HEADERS.getBytes(StandardCharsets.UTF_8));
    }

    private void writeImageBytesToStream(OutputStream outputStream, byte[] imageBytes) throws IOException {
        String headers = MessageFormat.format(IMAGE_HEADERS, imageBytes.length);
        outputStream.write(headers.getBytes());
        outputStream.write(imageBytes);
        outputStream.write(IMAGE_BOUNDARY.getBytes());
    }

    private static byte[] getImageBytes(Mat image) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(JPG_EXT, image, matOfByte);
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
