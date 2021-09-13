package webcam.catcher.cam;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class WebFrame extends JFrame {

    private static final int DEFAULT_WIDTH = 1280;
    private static final int DEFAULT_HEIGHT = 1280;

    private JLabel camLabel;
    private WebCam cam;

    public WebFrame(WebCam cam) {
        this.cam = cam;
        camLabel = new JLabel();
        setTitle("WCatch");
        add(camLabel);
        addWindowListener(windowListener);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private final WindowListener windowListener = new WindowListener() {
        public void windowActivated(WindowEvent event) {
        }

        public void windowClosed(WindowEvent event) {
        }

        public void windowClosing(WindowEvent event) {
            cam.setStop();
            System.exit(0);
        }

        public void windowDeactivated(WindowEvent event) {
        }

        public void windowDeiconified(WindowEvent event) {
        }

        public void windowIconified(WindowEvent event) {
        }

        public void windowOpened(WindowEvent event) {
        }
    };

    public void setCamLabelImage(ImageIcon image) {
        camLabel.setIcon(image);
    }
}
