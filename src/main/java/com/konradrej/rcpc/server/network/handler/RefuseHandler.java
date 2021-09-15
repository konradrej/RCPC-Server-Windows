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
 * @version 1.0
 */
public class RefuseHandler extends SocketHandler {
    private static final Logger LOGGER = LogManager.getLogger(RefuseHandler.class);

    /**
     * Constructor.
     *
     * @param socket the connected socket
     */
    public RefuseHandler(Socket socket) {
        super(socket, false, true, LOGGER);
    }

    @Override
    public void run() {
        Message message = new Message(MessageType.ERROR_SERVER_BUSY);
        outputQueue.add(message);

        disconnect();
    }
}
