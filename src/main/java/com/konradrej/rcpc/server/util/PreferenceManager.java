package com.konradrej.rcpc.server.util;

import java.util.prefs.Preferences;

/**
 * Class for handling system and user specific preferences.
 *
 * @author Konrad Rej
 * @author www.konradrej.com
 * @version 1.0
 */
public class PreferenceManager {
    private static final Preferences userPreferences = Preferences.userNodeForPackage(PreferenceManager.class);
    private static final Preferences systemPreferences = Preferences.systemNodeForPackage(PreferenceManager.class);

    /**
     * Gets preference value primarily from user preferences, if not found in user preferences
     * it checks system preferences and if not found there it returns null.
     *
     * @param preference preference to get
     * @return user preference value, system preference or null if neither is found
     */
    public static String getPreference(Preference preference) {
        String preferenceString = String.valueOf(preference);

        return userPreferences.get(
                preferenceString,
                systemPreferences.get(
                        preferenceString,
                        null
                )
        );
    }

    /**
     * Set given preference for the system to a given value.
     *
     * @param preference preference to set
     * @param value      value for preference to set
     */
    public static void setSystemPreference(Preference preference, String value) {
        String preferenceString = String.valueOf(preference);

        systemPreferences.put(preferenceString, value);
    }

    /**
     * Set given preference for the user to a given value.
     *
     * @param preference preference to set
     * @param value      value for preference to set
     */
    public static void setUserPreference(Preference preference, String value) {
        String preferenceString = String.valueOf(preference);

        userPreferences.put(preferenceString, value);
    }

    /**
     * List of different preferences.
     */
    public enum Preference {
        CONFIRM_CONNECT,
        CLOSE_ON_DISCONNECT
    }
}
