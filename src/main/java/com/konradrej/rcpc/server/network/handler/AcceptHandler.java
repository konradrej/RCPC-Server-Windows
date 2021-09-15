package com.konradrej.rcpc.server.network.handler;

import com.konradrej.rcpc.core.network.SocketHandler;
import com.konradrej.rcpc.core.network.Message;
import com.konradrej.rcpc.server.util.NativeLibrary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

/**
 * Handler for accepted connections.
 *
 * TODO: Handle rest of received message types
 *       Add support for sending messages like current volume info
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.0
 */
public class AcceptHandler extends SocketHandler {
    private static final Logger LOGGER = LogManager.getLogger(AcceptHandler.class);

    /**
     * Constructor.
     *
     * @param socket the connected socket
     */
    public AcceptHandler(Socket socket) {
        super(socket, LOGGER);
    }

    /**
     * Acts on messages read from input queue.
     */
    @Override
    public void run() {
        while (!disconnect) {
            if (inputQueue.size() > 0) {

                Message message = null;
                try {
                    message = inputQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (message != null) {
                    switch (message.getMessageType()) {
                        case ACTION_STOP:
                            NativeLibrary.INSTANCE.stop();
                            break;
                        case ACTION_INCREASE_VOLUME:
                            NativeLibrary.INSTANCE.incVolume();
                            break;
                        case ACTION_DECREASE_VOLUME:
                            NativeLibrary.INSTANCE.decVolume();
                            break;
                        case ACTION_PLAY_PAUSE:
                            NativeLibrary.INSTANCE.playPause();
                            break;
                        case ACTION_NEXT_TRACK:
                            NativeLibrary.INSTANCE.nextTrack();
                            break;
                        case ACTION_PREVIOUS_TRACK:
                            NativeLibrary.INSTANCE.prevTrack();
                            break;
                        case ACTION_SET_VOLUME:
                            NativeLibrary.INSTANCE.setVolume((Float) message.getMessageData() / 100f);
                            break;
                        default:
                            LOGGER.error("Message type not implemented: " + message.getMessageType().name());
                    }
                }
            }
        }
    }

    /**
     * Adds message to output queue.
     *
     * @param message message to send
     */
    public void sendMessage(Message message) {
        outputQueue.add(message);
    }
}
