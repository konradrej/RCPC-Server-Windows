package com.konradrej.rcpc.server.network;

import com.konradrej.rcpc.server.network.handler.SocketHandler;
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
 * @version 2.0
 * @since 1.0
 */
public class SocketHostHandler {
    private static final Logger LOGGER = LogManager.getLogger(SocketHostHandler.class);
    private static final List<SocketHandler> socketHandlers = new ArrayList<>();

    private static boolean available = true;
    private static boolean stop = false;
    private static ServerSocket serverSocket;
    private static SocketHandler socketHandler = null;
    private static Thread socketHandlerThread = null;

    /**
     * Starts connection handling.
     *
     * @param socketHandlerManager implementation of SocketHandlerManager for picking handler
     * @param port                 port to start server on
     * @throws IllegalArgumentException if SocketHandlerManager is null
     * @since 1.0
     */
    public static void start(SocketHandlerManager socketHandlerManager, int port) throws IllegalArgumentException {
        if (socketHandlerManager == null) {
            throw new IllegalArgumentException("Handler manager can not be null");
        }

        new Thread(() -> {
            ServerSocketFactory serverSocketFactory;

            try {
                serverSocketFactory = SSLContextFactory.getPreconfigured().getServerSocketFactory();
            } catch (IOException e) {
                LOGGER.error("Could not get secure server socket factory. Using default instead. Error: " + e.getMessage());
                serverSocketFactory = ServerSocketFactory.getDefault();
            }

            try {
                serverSocket = serverSocketFactory.createServerSocket(port);

                LOGGER.info("Started server socket.");
                LOGGER.info("Waiting for connections...");

                while (!stop) {
                    Socket socket = serverSocket.accept();
                    LOGGER.info("Socket accepted.");

                    if (socketHandlerThread == null || !socketHandlerThread.isAlive()) {
                        available = true;
                    }

                    SocketHandler socketHandler = socketHandlerManager.getHandler(socket, available);
                    socketHandlers.add(socketHandler);

                    Thread socketHandlerThread = new Thread(socketHandler);
                    socketHandlerThread.start();

                    if (available) {
                        LOGGER.info("New connection established.");

                        SocketHostHandler.socketHandler = socketHandler;
                        SocketHostHandler.socketHandlerThread = socketHandlerThread;

                        available = false;
                    } else {
                        LOGGER.info("Connection refused.");
                    }
                }
            } catch (IOException e) {
                if (!stop) {
                    LOGGER.error("Error with server socket. Error: " + e.getLocalizedMessage());
                } else {
                    LOGGER.info("Server socket closed.");
                }
            }
        }).start();
    }

    /**
     * Stops service discover, active connections and server socket.
     *
     * @since 1.0
     */
    public static void stop() {
        stop = true;

        for (SocketHandler socketHandler : socketHandlers) {
            socketHandler.disconnect();
        }

        try {
            serverSocket.close();
        } catch (IOException ignored) {
        }
    }

    public static SocketHandler getSocketHandler() {
        return SocketHostHandler.socketHandler;
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
