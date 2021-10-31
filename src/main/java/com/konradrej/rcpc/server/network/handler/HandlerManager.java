package com.konradrej.rcpc.server.network.handler;

import com.konradrej.rcpc.core.network.Message;
import com.konradrej.rcpc.core.network.SocketHandler;
import com.konradrej.rcpc.server.database.AutoConnectDeviceUtil;
import com.konradrej.rcpc.server.database.entity.AutoConnectDevice;
import com.konradrej.rcpc.server.network.SocketHostHandler;
import com.konradrej.rcpc.server.ui.DialogUtil;
import com.konradrej.rcpc.server.ui.FrameHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handles picking the correct SocketHandler.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.3
 * @since 1.0
 */
public class HandlerManager implements SocketHostHandler.SocketHandlerManager {
    private static final Logger LOGGER = LogManager.getLogger(HandlerManager.class);

    /**
     * Given socket and available status returns the correct socket handler.
     *
     * @param socket    connected socket
     * @param available server available status
     * @return an instance of SocketHandler setup with given socket
     * @since 1.0
     */
    @Override
    public SocketHandler getHandler(Socket socket, boolean available) {
        if (available) {
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

            JFrame frame = FrameHandler.getTransparentFrame();
            frame.setVisible(true);

            boolean deviceSaved = isDeviceSaved(uuid);
            DialogUtil.ConnectDialogAction dialogResponse;
            if (deviceSaved) {
                dialogResponse = DialogUtil.ConnectDialogAction.CONNECT;
                DialogUtil.showInformationDialog(frame, "Device connected.", "A saved device has connected.");
            } else {
                dialogResponse = DialogUtil.showConnectDialog(frame);
            }

            frame.setVisible(false);

            switch (dialogResponse) {
                case CONNECT:
                    return new AcceptHandler(socket, objectOutputStream, objectInputStream);
                case SAVE_AND_CONNECT:
                    AutoConnectDevice autoConnectDevice = new AutoConnectDevice(uuid);
                    AutoConnectDeviceUtil.saveDevice(autoConnectDevice);

                    return new AcceptHandler(socket, objectOutputStream, objectInputStream);
                case CANCEL:
                    return new RefuseHandler(socket, true, objectOutputStream, objectInputStream);
            }
        }

        return new RefuseHandler(socket, false);
    }

    private boolean isDeviceSaved(String uuid) {
        if (uuid != null) {
            return AutoConnectDeviceUtil.containsDevice(uuid);
        }

        return false;
    }
}
