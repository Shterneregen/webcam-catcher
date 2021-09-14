package webcam.catcher;

import nu.pattern.OpenCV;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.opencv.highgui.HighGui;
import webcam.catcher.cam.WebCam;
import webcam.catcher.cam.WebFrame;
import webcam.catcher.util.ImgUtils;
import webcam.catcher.util.PropUtils;
import webcam.catcher.util.ResUtils;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public static void main(String[] args) {
        OpenCV.loadLocally();

        WebCam cam = WebCam.getInstance();
        Options options = getOptions();
        if (args.length <= 0) {
            showHelp(options);
            showInFrame(cam);
            return;
        }

        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException pe) {
            showHelp(options);
            return;
        }

        handleOption(cmd, cam);
    }

    private static void handleOption(CommandLine cmd, WebCam cam) {
        if (cmd.hasOption("r")) {
            recordVideo(cmd.getOptionValues("r"), cam);
        } else if (cmd.hasOption("f")) {
            showInFrame(cam);
        } else if (cmd.hasOption("p")) {
            photograph(cmd.getOptionValues("f"), cam);
        } else if (cmd.hasOption("s")) {
            cam.stream();
        }
    }

    private static void showHelp(Options options) {
        new HelpFormatter().printHelp("WCatch", options);
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption(Option.builder("r").longOpt("record-video").desc("Record video")
                .hasArgs().valueSeparator(' ').numberOfArgs(Option.UNLIMITED_VALUES).required(false).build());
        options.addOption(Option.builder("f").longOpt("frame").desc("Show on frame")
                .hasArg(false).required(false).build());
        options.addOption(Option.builder("p").longOpt("photo").desc("Take a photo")
                .hasArg().required(false).build());
        options.addOption(Option.builder("s").longOpt("stream").desc("Stream video to port")
                .hasArg(false).required(false).build());
        return options;
    }

    private static void recordVideo(String[] args, WebCam cam) {
        LOG.log(Level.INFO, "Args: {0}", args);
        int sec = args != null && args.length > 0
                ? Integer.parseInt(args[0])
                : PropUtils.getDefaultRecSeconds();
        String filePath = args != null && args.length > 1
                ? args[1]
                : ResUtils.getCurrentDir() + String.format("%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS_cam.avi", new Date());
        LOG.log(Level.INFO, "Path: {0}", filePath);
        LOG.log(Level.INFO, "Seconds: {0}", sec);
        cam.write(filePath, sec);
    }

    private static void photograph(String[] args, WebCam cam) {
        LOG.log(Level.INFO, "Args: {0}", args);
        String filePath = args != null && args.length > 0
                ? args[0]
                : ResUtils.getCurrentDir() + String.format("%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS.jpg", new Date());
        cam.photograph(filePath);
    }

    private static void showInFrame(WebCam cam) {
        WebFrame webFrame = new WebFrame(cam);
        webFrame.setVisible(true);

        while (true) {
            int width = webFrame.getWidth();
            int height = webFrame.getHeight();
            BufferedImage image = cam != null
                    ? (BufferedImage) HighGui.toBufferedImage(cam.getImage())
                    : null;
            if (image != null && width > 0 && height > 0) {
                webFrame.setCamLabelImage(new ImageIcon(ImgUtils.scale(image, width, height)));
            }
        }

//        HighGui.namedWindow("window");
//        while (true) {
//            HighGui.imshow("window", cam.getImage());
//            HighGui.waitKey(100);
//        }
    }

}
