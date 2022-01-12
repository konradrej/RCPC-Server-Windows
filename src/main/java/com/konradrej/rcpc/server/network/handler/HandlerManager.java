package com.konradrej.rcpc.server.network.handler;

import com.konradrej.rcpc.server.database.AutoConnectDeviceUtil;
import com.konradrej.rcpc.server.database.entity.AutoConnectDevice;
import com.konradrej.rcpc.server.network.SocketHostHandler;
import com.konradrej.rcpc.server.ui.DialogUtil;
import com.konradrej.rcpc.server.ui.FrameHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Handles picking the correct SocketHandler.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 2.0
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
            BufferedWriter bufferedWriter = null;
            BufferedReader bufferedReader = null;
            String uuid = null;

            try {
                bufferedWriter = new BufferedWriter((new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)));
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                JSONObject message = new JSONObject(bufferedReader.readLine());
                uuid = message.getString("uuid");
            } catch (IOException e) {
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
                    return new AcceptHandler(socket, bufferedWriter, bufferedReader);
                case SAVE_AND_CONNECT:
                    AutoConnectDevice autoConnectDevice = new AutoConnectDevice(uuid);
                    AutoConnectDeviceUtil.saveDevice(autoConnectDevice);

                    return new AcceptHandler(socket, bufferedWriter, bufferedReader);
                case CANCEL:
                    return new RefuseHandler(socket, true, bufferedWriter, bufferedReader);
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
