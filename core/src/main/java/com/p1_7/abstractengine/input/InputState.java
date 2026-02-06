package com.p1_7.abstractengine.input;

/**
 * Represents the discrete state of a logical input action within a
 * single frame.
 *
 * <ul>
 *   <li>{@link #PRESSED}  — the action transitioned from inactive to
 *       active this frame.</li>
 *   <li>{@link #HELD}     — the action was already active last frame
 *       and remains active.</li>
 *   <li>{@link #RELEASED} — the action transitioned from active to
 *       inactive this frame.</li>
 * </ul>
 */
public enum InputState {

    /** action just became active this frame */
    PRESSED,

    /** action has been held since a previous frame */
    HELD,

    /** action just became inactive this frame */
    RELEASED
}
