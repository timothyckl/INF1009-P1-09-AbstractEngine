package com.p1_7.abstractengine.events;

/**
 * Generic listener interface for events.
 *
 * @param <T> the event type enum (must implement EventType)
 * @param <D> the data type passed with events
 */
@FunctionalInterface
public interface EventListener<T extends Enum<T> & EventType, D> {

    /**
     * Handles the received event.
     *
     * @param type the event type
     * @param data the data associated with the event
     */
    void onEvent(T type, D data);
}
