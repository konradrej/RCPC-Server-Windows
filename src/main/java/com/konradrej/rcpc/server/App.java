package com.konradrej.rcpc.server;

import com.konradrej.rcpc.server.database.HibernateUtil;
import com.konradrej.rcpc.server.network.ServiceHostHandler;
import com.konradrej.rcpc.server.network.SocketHostHandler;
import com.konradrej.rcpc.server.network.handler.HandlerManager;
import com.konradrej.rcpc.server.ui.SystemTrayHandler;

import javax.swing.*;

/**
 * Entry point for application.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.2
 * @since 1.0
 */
public class App {
    public static void main(String[] args) {
        // Gets logger config file from resources and sets system property.
        String logConfigPath = "log4j2.configurationFile.xml";
        System.setProperty("log4j2.configurationFile", logConfigPath);

        int port = 666;

        SocketHostHandler.start(new HandlerManager(), port);
        ServiceHostHandler.start(port);
        HibernateUtil.initialize();

        SwingUtilities.invokeLater(SystemTrayHandler::getInstance);
    }
}
