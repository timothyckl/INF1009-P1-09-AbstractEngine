package com.p1_7.demo;

/**
 * demo application configuration values.
 *
 * all fields are public static so that any demo class can read or
 * override them at startup. the default values mirror those set in
 * Lwjgl3Launcher; if the launcher changes the window size the demo
 * should update these fields accordingly.
 */
public class Settings {

    /** width of the application window in pixels */
    public static int windowWidth = 640;

    /** height of the application window in pixels */
    public static int windowHeight = 480;

    /** music volume level (0.0 = silent, 1.0 = maximum) */
    public static float musicVolume = 0.5f;

    /**
     * sets the music volume with validation.
     * clamps the value between 0.0 (silent) and 1.0 (maximum).
     *
     * @param volume the desired volume level
     */
    public static void setMusicVolume(float volume) {
        musicVolume = Math.max(0f, Math.min(1f, volume));
    }
}
