package util;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * JNA interface for the RCPC Native Library
 * https://github.com/konradrej/RCPC-Native-Library
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.0
 */
public interface NativeLibrary extends Library {
    /**
     * Instance to use to access the methods defined in this interface.
     */
    NativeLibrary INSTANCE = Native.load("RCPC-Native-Library", NativeLibrary.class);

    /**
     * Retrieves RCPC Native Library version.
     */
    double getVersion();

    /**
     * Simulates next track key press.
     */
    void nextTrack();

    /**
     * Simulates previous track key press.
     */
    void prevTrack();

    /**
     * Simulates stop media key press.
     */
    void stop();

    /**
     * Simulates play/pause media key press.
     */
    void playPause();

    /**
     * Simulates increase volume key press.
     */
    void incVolume();

    /**
     * Simulates decrease volume key press.
     */
    void decVolume();

    /**
     * Simulates mute volume key press.
     */
    void toggleMute();

    /**
     * Sets volume to given value and returns success status.
     *
     * @param volume volume value to set (0-1)
     * @return success status
     */
    boolean setVolume(float volume);

    /**
     * Gets current volume.
     *
     * @return current volume
     */
    float getVolume();
}
