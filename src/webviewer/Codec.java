/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webviewer;

import org.opencv.videoio.VideoWriter;

public class Codec {

    public static int XVID = VideoWriter.fourcc('X', 'V', 'I', 'D'); //= кодек XviD
    public static int MPEG1 = VideoWriter.fourcc('P', 'I', 'M', '1'); // = MPEG-1
    public static int MOTIONJPEG = VideoWriter.fourcc('M', 'J', 'P', 'G'); // = motion-jpeg
    public static int MPEG42 = VideoWriter.fourcc('M', 'P', '4', '2'); // = MPEG-4.2
    public static int MPEG43 = VideoWriter.fourcc('D', 'I', 'V', '3'); // = MPEG-4.3
    public static int MPEG4 = VideoWriter.fourcc('D', 'I', 'V', 'X');// = MPEG-4
    public static int H263 = VideoWriter.fourcc('U', '2', '6', '3'); // = H263
    public static int H264 = VideoWriter.fourcc('U', '2', '6', '4'); // = H264
    public static int H263I = VideoWriter.fourcc('I', '2', '6', '3'); // = H263I
    public static int FLV1 = VideoWriter.fourcc('F', 'L', 'V', '1'); //= FLV1
}
