package com.konradrej.rcpc.server.network.handler;

import com.konradrej.rcpc.core.network.Message;
import com.konradrej.rcpc.core.network.SocketHandler;
import com.konradrej.rcpc.server.database.HibernateUtil;
import com.konradrej.rcpc.server.database.entity.AutoConnectDevice;
import com.konradrej.rcpc.server.network.SocketHostHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Handles picking the correct SocketHandler.
 * <p>
 * TODO: Send some sort of notification when "always accept device" connects
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.2
 * @since 1.0
 */
public class HandlerManager implements SocketHostHandler.SocketHandlerManager {
    private static final Logger LOGGER = LogManager.getLogger(HandlerManager.class);

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
                    "Connect",
                    "Save and connect",
                    "Cancel"
            };

            JFrame transparentFrame = new JFrame();
            transparentFrame.setUndecorated(true);
            transparentFrame.setLocationRelativeTo(null);
            transparentFrame.setAlwaysOnTop(true);
            transparentFrame.setVisible(true);

            try {
                InputStream inputStream = HandlerManager.class.getClassLoader().getResource("icon.png").openStream();
                ImageIcon icon = new ImageIcon(ImageIO.read(inputStream));

                Image image = icon.getImage();
                transparentFrame.setIconImage(image);

            } catch (IOException e) {
                LOGGER.error("Could not load icon. Error: " + e.getMessage());
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
                LOGGER.error("Could not get remote device UUID. Error: " + e.getMessage());
            }

            boolean deviceSaved = false;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<AutoConnectDevice> query = session.createQuery("FROM AutoConnectDevice A WHERE A.uuid = :uuid", AutoConnectDevice.class);
                query.setParameter("uuid", uuid);

                List<AutoConnectDevice> autoConnectDevices = query.list();

                if (autoConnectDevices.size() == 1) {
                    deviceSaved = true;
                }
            } catch (Exception e) {
                LOGGER.error("Could not get saved devices. Error: " + e.getMessage());
            }

            int acceptConnection = 0;
            if (!deviceSaved) {
                acceptConnection = JOptionPane.showOptionDialog(
                        transparentFrame,
                        "Would you like to accept this connection?",
                        "Confirm connection",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[2]
                );
            }

            transparentFrame.setVisible(false);

            switch (acceptConnection) {
                case 0:
                    return new AcceptHandler(socket, objectOutputStream, objectInputStream);
                case 1:
                    AutoConnectDevice autoConnectDevice = new AutoConnectDevice(uuid);
                    Transaction transaction = null;
                    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                        transaction = session.beginTransaction();

                        session.save(autoConnectDevice);

                        transaction.commit();
                    } catch (Exception e) {
                        if (transaction != null) {
                            transaction.rollback();
                        }

                        LOGGER.error("Could not save device. Error: " + e.getMessage());
                    }
                    return new AcceptHandler(socket, objectOutputStream, objectInputStream);
                case 2:
                    return new RefuseHandler(socket, true, objectOutputStream, objectInputStream);
            }
        }

        return new RefuseHandler(socket, false);
    }
}
