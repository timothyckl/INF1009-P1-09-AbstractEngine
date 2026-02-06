package com.p1_7.abstractengine.input;

/**
 * Read-only query interface for the current frame's input state.
 *
 * <p>Scenes and other engine components receive this interface so
 * that they can interrogate input without coupling to the
 * {@link InputOutputManager} directly.</p>
 */
public interface IInputQuery {

    /**
     * Returns whether the specified action is currently active
     * (either {@link InputState#PRESSED} or {@link InputState#HELD}).
     *
     * @param actionId the logical action to query
     * @return {@code true} if the action is active this frame
     */
    boolean isActionActive(ActionId actionId);

    /**
     * Returns the precise input state for the specified action.
     *
     * @param actionId the logical action to query
     * @return the {@link InputState}, or {@code null} if the action
     *         is not active this frame
     */
    InputState getActionState(ActionId actionId);
}
