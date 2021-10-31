package com.konradrej.rcpc.server;

import com.konradrej.rcpc.server.network.ServiceHostHandler;
import com.konradrej.rcpc.server.network.SocketHostHandler;
import com.konradrej.rcpc.server.network.handler.HandlerManager;
import com.konradrej.rcpc.server.ui.SystemTrayHandler;

import javax.swing.*;
import java.io.File;

/**
 * Entry point for application.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.1
 * @since 1.0
 */
public class App {
    public static void main(String[] args) {
        // Gets logger config file from resources and sets system property.
        String logConfigPath = new File(App.class.getClassLoader().getResource("log4j2.configurationFile.xml").getFile()).getAbsolutePath();
        System.setProperty("log4j2.configurationFile", logConfigPath);

        int port = 666;

        SocketHostHandler.start(new HandlerManager(), port);
        ServiceHostHandler.start(port);

        SwingUtilities.invokeLater(SystemTrayHandler::getInstance);
    }
}
