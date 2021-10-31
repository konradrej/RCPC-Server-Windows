package com.konradrej.rcpc.server.ui;

import javax.swing.*;

/**
 * Helper class which handles creation of specific swing dialogs.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.0
 * @since 1.0
 */
public class DialogUtil {
    /**
     * Creates a "connect dialog" and returns the user picked value.
     *
     * @param frame frame to attach the dialog to
     * @return the user picked value
     * @since 1.0
     */
    public static ConnectDialogAction showConnectDialog(JFrame frame) {
        String[] options = {
                "Connect",
                "Save and connect",
                "Cancel"
        };

        int selectedOption = JOptionPane.showOptionDialog(
                frame,
                "Would you like to accept this connection?",
                "Confirm connection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[2]
        );

        switch (selectedOption) {
            case 0:
                return ConnectDialogAction.CONNECT;
            case 1:
                return ConnectDialogAction.SAVE_AND_CONNECT;
            default:
                return ConnectDialogAction.CANCEL;
        }
    }

    /**
     * Value enum for connect dialog.
     *
     * @since 1.0
     */
    public enum ConnectDialogAction {
        CONNECT,
        SAVE_AND_CONNECT,
        CANCEL
    }

    /**
     * Creates an informative dialog on a new thread to not block until user closes the dialog.
     *
     * @param frame   frame to attach the dialog to
     * @param title   title to display
     * @param message message to display
     * @since 1.0
     */
    public static void showInformationDialog(JFrame frame, String title, String message) {
        new Thread(() -> {
            JOptionPane.showMessageDialog(
                    frame,
                    message,
                    title,
                    JOptionPane.INFORMATION_MESSAGE,
                    null
            );
        }).start();
    }
}
