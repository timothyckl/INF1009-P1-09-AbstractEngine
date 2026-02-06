package com.p1_7.abstractengine.demo;

import com.p1_7.abstractengine.input.ActionId;

/**
 * Defines logical input actions for the demo game.
 *
 * <p>These action constants are bound to physical keys in the demo's
 * Main.java and queried by game entities to determine player intent.</p>
 */
public class DemoActions {

    /** move bucket left */
    public static final ActionId LEFT = new ActionId("LEFT");

    /** move bucket right */
    public static final ActionId RIGHT = new ActionId("RIGHT");

    // private constructor prevents instantiation
    private DemoActions() {
    }
}
