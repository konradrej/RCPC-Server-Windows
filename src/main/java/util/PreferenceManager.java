package util;

import java.util.prefs.Preferences;

public class PreferenceManager {
    private static final Preferences userPreferences = Preferences.userNodeForPackage(PreferenceManager.class);
    private static final Preferences systemPreferences = Preferences.systemNodeForPackage(PreferenceManager.class);

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

    public static void setSystemPreference(Preference preference, String value) {
        String preferenceString = String.valueOf(preference);

        systemPreferences.put(preferenceString, value);
    }

    public static void setUserPreference(Preference preference, String value) {
        String preferenceString = String.valueOf(preference);

        userPreferences.put(preferenceString, value);
    }

    public enum Preference {
        CONFIRM_CONNECT,
        CLOSE_ON_DISCONNECT
    }
}
