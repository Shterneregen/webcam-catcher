package webviewer.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import org.opencv.core.Mat;

public class ImgUtils {

    private ImgUtils() {
    }

    public static BufferedImage createBufferedImage(Mat mat) {
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        mat.get(0, 0, data);
        return image;
    }

    /**
     * Подгоняет изображение под размеры
     *
     * @param img изображение
     * @param w   ширина
     * @param h   высота
     * @return изображение с измененными размерами
     */
    public static BufferedImage change(BufferedImage img, int w, int h) {
        if (img == null) {
            return null;
        }
        Image image2 = img.getScaledInstance(w, h, Image.SCALE_AREA_AVERAGING);
        BufferedImage changedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = changedImage.createGraphics();
        g2d.drawImage(image2, 0, 0, null);
        g2d.dispose();
        return changedImage;
    }

    /**
     * Масштабирует изображение при изменении размеров окна
     *
     * @param img исходное изображение
     * @param w   ширина окна
     * @param h   высота окна
     * @return масштабированное изображение
     */
    public static BufferedImage scale(BufferedImage img, int w, int h) {
        int type = BufferedImage.TYPE_INT_RGB;
        BufferedImage dst = new BufferedImage(w, h, type);
        Graphics2D g2 = dst.createGraphics();
        // Fill background for scale to fit.
//        g2.setBackground(UIManager.getColor("Panel.background"));
        g2.clearRect(0, 0, w, h);
        double xScale = (double) w / img.getWidth();
        double yScale = (double) h / img.getHeight();
        // Scaling options:
        // Scale to fit - image just fits in label.
        double scale = Math.min(xScale, yScale);
        // Scale to fill - image just fills label.
        //double scale = Math.max(xScale, yScale);
        int width = (int) (scale * img.getWidth());
        int height = (int) (scale * img.getHeight());
        int x = (w - width) / 2;
        int y = (h - height) / 2;
        g2.drawImage(img, x, y, width, height, null);
        g2.dispose();
        return dst;
    }
}
