package com.p1_7.mobius;

import com.p1_7.abstractengine.input.ActionId;

// centralises all ActionId constants so key bindings in Main and state
// queries in MobiusScene share the same references
public final class MobiusActions {

    // prevent instantiation — this class is a constants holder only
    private MobiusActions() {}

    // rotate the strip counter-clockwise around the Y-axis
    public static final ActionId ROTATE_LEFT  = new ActionId("ROTATE_LEFT");

    // rotate the strip clockwise around the Y-axis
    public static final ActionId ROTATE_RIGHT = new ActionId("ROTATE_RIGHT");

    // rotate the strip upward around the X-axis
    public static final ActionId ROTATE_UP    = new ActionId("ROTATE_UP");

    // rotate the strip downward around the X-axis
    public static final ActionId ROTATE_DOWN  = new ActionId("ROTATE_DOWN");

    // exit the simulation
    public static final ActionId QUIT         = new ActionId("QUIT");
}
