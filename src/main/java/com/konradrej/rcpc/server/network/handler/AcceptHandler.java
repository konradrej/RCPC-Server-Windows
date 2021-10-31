package com.konradrej.rcpc.server.network.handler;

import com.konradrej.rcpc.core.network.Message;
import com.konradrej.rcpc.core.network.MessageType;
import com.konradrej.rcpc.core.network.SocketHandler;
import com.konradrej.rcpc.server.util.NativeLibrary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

/**
 * Handler for accepted connections.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.3
 * @since 1.0
 */
public class AcceptHandler extends SocketHandler {
    private static final Logger LOGGER = LogManager.getLogger(AcceptHandler.class);
    private static Robot robot = null;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            LOGGER.error("Robot could not be enabled. Error: " + e.getLocalizedMessage());
        }
    }

    /**
     * Constructor.
     *
     * @param socket the connected socket
     * @since 1.0
     */
    public AcceptHandler(Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
        super(socket, LOGGER, objectOutputStream, objectInputStream);

        Message message = new Message(MessageType.INFO_USER_ACCEPTED_CONNECTION);
        outputQueue.add(message);
    }

    /**
     * Acts on messages read from input queue.
     *
     * @since 1.0
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
                    PointerInfo pointerInfo;
                    Point point;

                    Map<String, Object> additionalData;
                    float distanceX;
                    float distanceY;

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
                        case ACTION_PRIMARY_CLICK:
                            if (robot != null) {
                                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                            }
                            break;
                        case ACTION_MIDDLE_CLICK:
                            if (robot != null) {
                                robot.mousePress(InputEvent.BUTTON2_DOWN_MASK);
                                robot.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
                            }
                            break;
                        case ACTION_SECONDARY_CLICK:
                            if (robot != null) {
                                robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                                robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                            }
                            break;
                        case ACTION_MOVE:
                            pointerInfo = MouseInfo.getPointerInfo();
                            point = pointerInfo.getLocation();

                            additionalData = message.getAdditionalData();
                            distanceX = (float) additionalData.get("distanceX");
                            distanceY = (float) additionalData.get("distanceY");

                            int x = (int) (point.getX() - distanceX);
                            int y = (int) (point.getY() - distanceY);

                            robot.mouseMove(x, y);
                            break;
                        case ACTION_SCROLL:
                            additionalData = message.getAdditionalData();
                            distanceX = (float) additionalData.get("distanceX") * 2f;
                            distanceY = (float) additionalData.get("distanceY") * 2f;

                            NativeLibrary.INSTANCE.scroll(distanceX, -distanceY);
                            break;
                        case ACTION_GET_CURRENT_VOLUME:
                            float currentVolume = NativeLibrary.INSTANCE.getVolume();
                            Message toSend = new Message(MessageType.INFO_CURRENT_VOLUME_UPDATE, currentVolume);

                            outputQueue.add(toSend);
                            break;
                        case INFO_USER_CLOSED_CONNECTION:
                            disconnect();
                            break;
                        case ACTION_CLICK_AND_DRAG_START:
                            if (robot != null) {
                                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                            }
                            break;
                        case ACTION_CLICK_AND_DRAG_MOVE:
                            if (robot != null) {
                                pointerInfo = MouseInfo.getPointerInfo();
                                point = pointerInfo.getLocation();

                                additionalData = message.getAdditionalData();
                                distanceX = (float) additionalData.get("distanceX");
                                distanceY = (float) additionalData.get("distanceY");

                                x = (int) (point.getX() - distanceX);
                                y = (int) (point.getY() - distanceY);

                                robot.mouseMove(x, y);
                            }
                            break;
                        case ACTION_CLICK_AND_DRAG_END:
                            if (robot != null) {
                                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                            }
                            break;
                        default:
                            LOGGER.error("Message type not implemented: " + message.getMessageType().name());
                    }
                }
            }
        }
    }

    /**
     * Overrides disconnect to send a connection close message before disconnecting.
     *
     * @since 1.0
     */
    @Override
    public void disconnect() {
        sendMessage(new Message(MessageType.INFO_USER_CLOSED_CONNECTION));

        super.disconnect();
    }

    /**
     * Adds message to output queue.
     *
     * @param message message to send
     * @since 1.0
     */
    public void sendMessage(Message message) {
        outputQueue.add(message);
    }
}
