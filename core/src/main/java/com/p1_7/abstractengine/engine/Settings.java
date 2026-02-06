package com.p1_7.abstractengine.engine;

/**
 * Application-wide configuration values shared across the engine and
 * any demo code that runs on top of it.
 *
 * <p>All fields are {@code public static} so that any manager or
 * application class can read or override them at startup.  The default
 * values mirror those set in {@code Lwjgl3Launcher}; if the launcher
 * changes the window size the demo should update these fields
 * accordingly.</p>
 */
public class Settings {

    /** width of the application window in pixels */
    public static int WINDOW_WIDTH = 640;

    /** height of the application window in pixels */
    public static int WINDOW_HEIGHT = 480;
}
