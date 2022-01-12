package com.konradrej.rcpc.server.network.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

/**
 * Handler for refused connection.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 2.0
 * @since 1.0
 */
public class RefuseHandler extends SocketHandler {
    private static final Logger LOGGER = LogManager.getLogger(RefuseHandler.class);
    private final boolean userCancelled;

    /**
     * Simplified constructor.
     *
     * @param socket        the connected socket
     * @param userCancelled true if user cancelled connection
     * @since 1.0
     */
    public RefuseHandler(Socket socket, boolean userCancelled) {
        super(socket, false, true, LOGGER, null, null);

        this.userCancelled = userCancelled;
    }

    /**
     * Full constructor.
     *
     * @param socket         the connected socket
     * @param userCancelled  true if user cancelled connection
     * @param bufferedWriter object output stream to pass, null to create from socket
     * @param bufferedReader object input stream to pass, null to create from socket
     */
    public RefuseHandler(Socket socket, boolean userCancelled, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        super(socket, false, true, LOGGER, bufferedWriter, bufferedReader);

        this.userCancelled = userCancelled;
    }

    @Override
    public void run() {
        JSONObject message = new JSONObject();

        if (userCancelled) {
            message.put("type", "INFO_USER_CLOSED_CONNECTION");
        } else {
            message.put("type", "ERROR_SERVER_BUSY");
        }

        outputQueue.add(message);

        disconnect();
    }
}
