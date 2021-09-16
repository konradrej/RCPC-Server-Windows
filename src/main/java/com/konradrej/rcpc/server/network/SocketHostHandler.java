package com.konradrej.rcpc.server.network;

import com.konradrej.rcpc.core.network.SocketHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for managing hosting server side of connections.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.0
 * @since 1.0
 */
public class SocketHostHandler {
    private static final Logger LOGGER = LogManager.getLogger(SocketHostHandler.class);
    private static final List<SocketHandler> socketHandlers = new ArrayList<>();

    private static boolean available = true;
    private static boolean stop = false;
    private static ServerSocket serverSocket;
    private static Thread handlerThread = null;

    /**
     * Starts connection handling.
     *
     * @param socketHandlerManager implementation of SocketHandlerManager for picking handler
     * @throws IllegalArgumentException if SocketHandlerManager is null
     * @since 1.0
     */
    public static void start(SocketHandlerManager socketHandlerManager) throws IllegalArgumentException {
        if (socketHandlerManager == null) {
            throw new IllegalArgumentException("Handler manager can not be null");
        }

        ServerSocketFactory serverSocketFactory;

        try {
            serverSocketFactory = SSLContextFactory.getPreconfigured().getServerSocketFactory();
        } catch (IOException e) {
            serverSocketFactory = ServerSocketFactory.getDefault();
        }

        try {
            serverSocket = serverSocketFactory.createServerSocket(666);

            LOGGER.info("Started server socket.");
            LOGGER.info("Waiting for connection.");

            ServiceHostHandler.start(serverSocket.getLocalPort());

            while (!stop) {
                Socket socket = serverSocket.accept();
                LOGGER.info("Socket accepted.");

                if (handlerThread == null || !handlerThread.isAlive()) {
                    available = true;
                }

                SocketHandler socketHandler = socketHandlerManager.getHandler(socket, available);
                socketHandlers.add(socketHandler);

                Thread temp = new Thread(socketHandler);
                temp.start();

                if (available) {
                    LOGGER.info("New connection established.");

                    handlerThread = temp;
                    available = false;
                } else {
                    LOGGER.info("Connection refused.");
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error with server socket. Error: " + e.getLocalizedMessage());
        }

        stop();
    }

    /**
     * Stops service discover, active connections and server socket.
     *
     * @since 1.0
     */
    public static void stop() {
        ServiceHostHandler.stop();
        stop = true;

        for (SocketHandler socketHandler : socketHandlers) {
            socketHandler.disconnect();
        }

        try {
            serverSocket.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * Used for defining handlers the server should use depending on conditions.
     *
     * @since 1.0
     */
    public interface SocketHandlerManager {
        SocketHandler getHandler(Socket socket, boolean alreadyConnected);
    }
}
