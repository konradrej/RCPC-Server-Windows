package com.konradrej.rcpc.server.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Handler for JFrames.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.0
 * @since 1.0
 */
public class FrameHandler {
    private static final Logger LOGGER = LogManager.getLogger(FrameHandler.class);
    private static JFrame transparentFrame;

    /**
     * Gets a configured transparent JFrame instance.
     *
     * @return a transparent JFrame instance
     * @since 1.0
     */
    public static JFrame getTransparentFrame() {
        if (transparentFrame == null) {
            createTransparentFrame();
        }

        return transparentFrame;
    }

    private static void createTransparentFrame() {
        transparentFrame = new JFrame();
        transparentFrame.setUndecorated(true);
        transparentFrame.setLocationRelativeTo(null);
        transparentFrame.setAlwaysOnTop(true);

        try {
            InputStream inputStream = FrameHandler.class.getResourceAsStream("icon.png");

            if (inputStream != null) {
                ImageIcon icon = new ImageIcon(ImageIO.read(inputStream));
                transparentFrame.setIconImage(icon.getImage());
            } else {
                LOGGER.error("Icon resource could not be found or accessed.");
            }
        } catch (IOException e) {
            LOGGER.error("Could not load icon. Error: " + e.getMessage());
        }
    }
}
