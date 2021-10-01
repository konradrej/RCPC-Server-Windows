package com.konradrej.rcpc.server.network.handler;

import com.konradrej.rcpc.core.network.SocketHandler;
import com.konradrej.rcpc.server.TempApp;
import com.konradrej.rcpc.server.network.SocketHostHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * TODO
 * <p>
 * TODO: Implement "always accept device" option
 *       Send some sort of notification when "always accept device" connects
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.0
 * @since 1.0
 */
public class HandlerManager implements SocketHostHandler.SocketHandlerManager {
    /**
     * TODO
     *
     * @param socket
     * @param alreadyConnected
     * @return
     * @since 1.0
     */
    @Override
    public SocketHandler getHandler(Socket socket, boolean alreadyConnected) {
        if (alreadyConnected) {
            String[] options = {
                    "Yes, connect.",
                    "No, abort connection."
            };

            JFrame transparentFrame = new JFrame();
            transparentFrame.setUndecorated(true);
            transparentFrame.setLocationRelativeTo(null);
            transparentFrame.setAlwaysOnTop(true);
            transparentFrame.setVisible(true);

            try {
                InputStream stream = HandlerManager.class.getClassLoader().getResource("icon.png").openStream();
                ImageIcon icon = new ImageIcon(ImageIO.read(stream));

                Image image = icon.getImage();
                transparentFrame.setIconImage(image);

            } catch (IOException e) {
                e.printStackTrace();
            }

            int acceptConnection = JOptionPane.showOptionDialog(
                    transparentFrame,
                    "Would you like to accept this connection?",
                    "Confirm connection",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]
            );

            transparentFrame.setVisible(false);

            if (acceptConnection == JOptionPane.YES_OPTION) {
                return new AcceptHandler(socket);
            } else {
                return new RefuseHandler(socket, true);
            }
        }

        return new RefuseHandler(socket, false);
    }
}
