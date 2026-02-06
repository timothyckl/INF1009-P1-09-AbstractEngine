package com.p1_7.abstractengine.input;

/**
 * Abstract base class for all input events produced by the engine.
 *
 * <p>Concrete event types (digital, analogue, pointer, etc.) are
 * context-specific and belong in the demo phase.  This class defines
 * the fields and accessors that every event shares: the logical
 * {@link ActionId} that triggered it and the system timestamp at
 * which it occurred.</p>
 */
public abstract class InputEvent {

    /** the logical action associated with this event */
    private final ActionId actionId;

    /** the system timestamp (in milliseconds) when the event occurred */
    private final long timestamp;

    /**
     * Constructs an input event with the given action and timestamp.
     *
     * @param actionId  the logical action that triggered this event
     * @param timestamp the system time in milliseconds
     */
    protected InputEvent(ActionId actionId, long timestamp) {
        this.actionId = actionId;
        this.timestamp = timestamp;
    }

    /**
     * Returns the logical action associated with this event.
     *
     * @return the action identifier; never {@code null}
     */
    public ActionId getActionId() {
        return actionId;
    }

    /**
     * Returns the system timestamp at which this event occurred.
     *
     * @return the timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }
}
