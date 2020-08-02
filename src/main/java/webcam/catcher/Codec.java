package webcam.catcher;

import org.opencv.videoio.VideoWriter;

public class Codec {

    private Codec() {
    }

    public static final int XVID = VideoWriter.fourcc('X', 'V', 'I', 'D');
    public static final int MPEG1 = VideoWriter.fourcc('P', 'I', 'M', '1');
    public static final int MOTIONJPEG = VideoWriter.fourcc('M', 'J', 'P', 'G');
    public static final int MPEG42 = VideoWriter.fourcc('M', 'P', '4', '2');
    public static final int MPEG43 = VideoWriter.fourcc('D', 'I', 'V', '3');
    public static final int MPEG4 = VideoWriter.fourcc('D', 'I', 'V', 'X');
    public static final int H263 = VideoWriter.fourcc('U', '2', '6', '3');
    public static final int H264 = VideoWriter.fourcc('U', '2', '6', '4');
    public static final int H263I = VideoWriter.fourcc('I', '2', '6', '3');
    public static final int FLV1 = VideoWriter.fourcc('F', 'L', 'V', '1');
}
