package webcam.catcher.util;


public class PropUtils {

    private static final String DEF_REC_SECONDS = "def.rec.seconds";
    private static final String STREAM_PORT = "stream.port";
    private static final String FPS = "fps";
    private static final String COMMON_HEADERS = "common.headers";
    private static final String IMAGE_HEADERS = "image.headers";
    private static final String IMAGE_BOUNDARY = "image.boundary";

    private PropUtils() {
    }

    public static int getDefaultRecSeconds() {
        return Integer.parseInt(ResUtils.getProperty(DEF_REC_SECONDS));
    }

    public static int getPort() {
        return Integer.parseInt(ResUtils.getProperty(STREAM_PORT));
    }

    public static int getFps() {
        return Integer.parseInt(ResUtils.getProperty(FPS));
    }

    public static String getCommonHeaders() {
        return ResUtils.getProperty(COMMON_HEADERS);
    }

    public static String getImageHeaders() {
        return ResUtils.getProperty(IMAGE_HEADERS);
    }

    public static String getImageBoundary() {
        return ResUtils.getProperty(IMAGE_BOUNDARY);
    }
}
