package com.konradrej.rcpc.server.network.handler;

import com.konradrej.rcpc.core.network.Message;
import com.konradrej.rcpc.core.network.SocketHandler;
import com.konradrej.rcpc.server.network.SocketHostHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * TODO
 * <p>
 * TODO: Implement "always accept device" option
 *       Send some sort of notification when "always accept device" connects
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.1
 * @since 1.0
 */
public class HandlerManager implements SocketHostHandler.SocketHandlerManager {
    /**
     * Given socket and available status returns the correct socket handler.
     *
     * @param socket    connected socket
     * @param available server available status
     * @return a instance of SocketHandler setup with given socket
     * @since 1.0
     */
    @Override
    public SocketHandler getHandler(Socket socket, boolean available) {
        if (available) {
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

            ObjectOutputStream objectOutputStream = null;
            ObjectInputStream objectInputStream = null;
            String uuid = null;

            try {
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectInputStream = new ObjectInputStream(socket.getInputStream());

                Message message = (Message) objectInputStream.readObject();
                uuid = (String) message.getMessageData();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error: " + e.getMessage());
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
                return new AcceptHandler(socket, objectOutputStream, objectInputStream);
            } else {
                return new RefuseHandler(socket, true, objectOutputStream, objectInputStream);
            }
        }

        return new RefuseHandler(socket, false);
    }
}
