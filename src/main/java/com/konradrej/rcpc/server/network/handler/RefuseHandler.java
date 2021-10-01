package com.konradrej.rcpc.server.network.handler;

import com.konradrej.rcpc.core.network.Message;
import com.konradrej.rcpc.core.network.MessageType;
import com.konradrej.rcpc.core.network.SocketHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

/**
 * Handler for refused connection.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.1
 * @since 1.0
 */
public class RefuseHandler extends SocketHandler {
    private static final Logger LOGGER = LogManager.getLogger(RefuseHandler.class);
    private final boolean userCancelled;

    /**
     * Constructor.
     *
     * @param socket        the connected socket
     * @param userCancelled true if user cancelled connection
     * @since 1.0
     */
    public RefuseHandler(Socket socket, boolean userCancelled) {
        super(socket, false, true, LOGGER);

        this.userCancelled = userCancelled;
    }

    @Override
    public void run() {
        Message message;

        if (userCancelled) {
            message = new Message(MessageType.INFO_USER_CLOSED_CONNECTION);
        } else {
            message = new Message(MessageType.ERROR_SERVER_BUSY);
        }

        outputQueue.add(message);

        disconnect();
    }
}
