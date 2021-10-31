package com.konradrej.rcpc.server.ui;

import com.konradrej.rcpc.server.database.AutoConnectDeviceUtil;
import com.konradrej.rcpc.server.database.HibernateUtil;
import com.konradrej.rcpc.server.network.SocketHostHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class to handle functions related to the system tray.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.0
 * @since 1.0
 */
public class SystemTrayHandler {
    private static final Logger LOGGER = LogManager.getLogger(SystemTrayHandler.class);

    private static SystemTrayHandler INSTANCE = null;
    private PopupMenu popupMenu;
    private TrayIcon trayIcon;
    private SystemTray systemTray;

    /**
     * Gets an instance of SystemTrayHandler.
     *
     * @return an instance of SystemTrayHandler
     */
    public static SystemTrayHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SystemTrayHandler();
        }

        return INSTANCE;
    }

    private SystemTrayHandler() {
        if (!SystemTray.isSupported()) {
            // TODO: Send error for now, in future change to info/warning and
            //       implement always showing jframe to allow for disconnecting
            //       without killing process
            LOGGER.error("System tray is not supported, no clean disconnect/quit available.");
            return;
        }

        initializePopupMenu();
        initializeTrayIcon();

        systemTray = SystemTray.getSystemTray();
        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            LOGGER.error("Error adding tray icon to system tray. Error: " + e.getLocalizedMessage());
        }
    }

    private void initializePopupMenu() {
        popupMenu = new PopupMenu();

        MenuItem disconnectItem = new MenuItem("Disconnect");
        MenuItem clearSavedDevicesItem = new MenuItem("Clear saved devices");
        MenuItem exitItem = new MenuItem("Exit");

        disconnectItem.addActionListener((actionEvent) ->
                SocketHostHandler.getSocketHandler().disconnect());

        clearSavedDevicesItem.addActionListener((actionEvent) ->
                AutoConnectDeviceUtil.clearDevices());

        exitItem.addActionListener((actionEvent) -> {
            SocketHostHandler.stop();
            HibernateUtil.shutdown();
            systemTray.remove(trayIcon);
        });

        popupMenu.add(disconnectItem);
        popupMenu.addSeparator();
        popupMenu.add(clearSavedDevicesItem);
        popupMenu.addSeparator();
        popupMenu.add(exitItem);
    }

    private void initializeTrayIcon() {
        ImageIcon icon;
        try {
            InputStream inputStream = SystemTrayHandler.class.getResourceAsStream("icon.png");

            if (inputStream != null) {
                icon = new ImageIcon(ImageIO.read(inputStream));

                if (trayIcon != null) {
                    trayIcon = new TrayIcon(icon.getImage());
                    trayIcon.setImageAutoSize(true);
                    trayIcon.setPopupMenu(popupMenu);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Could not load tray icon. Error: " + e.getMessage());
        }
    }
}
