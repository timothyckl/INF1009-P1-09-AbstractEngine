package com.p1_7.abstractengine.managers.impl;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.p1_7.abstractengine.events.EventListener;
import com.p1_7.abstractengine.events.EventType;
import com.p1_7.abstractengine.managers.base.AbstractManager;

/**
 * Central event broker that manages event subscriptions and publishing.
 * Implements the publish/subscribe pattern for loose coupling between
 * components.
 * Note that this event manager is synchronous.
 */
public class EventManager extends AbstractManager {

    // maps event types to their registered listeners
    private final ObjectMap<EventType, Array<EventListener<?, ?>>> listeners;

    // flag to prevent modification during iteration
    private boolean publishing;

    // pending operations to process after publishing completes
    private final Array<Runnable> pendingOperations;

    public EventManager() {
        listeners = new ObjectMap<>();
        pendingOperations = new Array<>();
        publishing = false;
    }

    @Override
    protected void onInit() {
        // no special initialisation required
    }

    @Override
    protected void onShutdown() {
        listeners.clear();
        pendingOperations.clear();
    }

    /**
     * Subscribes a listener to events of the specified type.
     *
     * @param <T>      the event type enum
     * @param <D>      the data type passed with events
     * @param type     the event type to listen for
     * @param listener the listener to register
     */
    public <T extends Enum<T> & EventType, D> void subscribe(T type, EventListener<T, D> listener) {
        if (type == null || listener == null) {
            return;
        }

        Runnable operation = () -> {
            Array<EventListener<?, ?>> eventListeners = listeners.get(type);
            if (eventListeners == null) {
                eventListeners = new Array<>();
                listeners.put(type, eventListeners);
            }

            // prevent duplicate subscriptions
            if (!eventListeners.contains(listener, true)) {
                eventListeners.add(listener);
            }
        };

        if (publishing) {
            pendingOperations.add(operation);
        } else {
            operation.run();
        }
    }

    /**
     * Unsubscribes a listener from events of the specified type.
     *
     * @param <T>      the event type enum
     * @param <D>      the data type passed with events
     * @param type     the event type to unsubscribe from
     * @param listener the listener to remove
     */
    public <T extends Enum<T> & EventType, D> void unsubscribe(T type, EventListener<T, D> listener) {
        if (type == null || listener == null) {
            return;
        }

        Runnable operation = () -> {
            Array<EventListener<?, ?>> eventListeners = listeners.get(type);
            if (eventListeners != null) {
                eventListeners.removeValue(listener, true);
            }
        };

        if (publishing) {
            pendingOperations.add(operation);
        } else {
            operation.run();
        }
    }

    /**
     * Publishes an event to all registered listeners of its type.
     *
     * @param <T>  the event type enum
     * @param <D>  the data type passed with events
     * @param type the event type
     * @param data the data to pass to listeners
     */
    @SuppressWarnings("unchecked")
    public <T extends Enum<T> & EventType, D> void publish(T type, D data) {
        if (type == null) {
            return;
        }

        Array<EventListener<?, ?>> eventListeners = listeners.get(type);
        if (eventListeners == null || eventListeners.size == 0) {
            return;
        }

        publishing = true;
        try {
            for (EventListener<?, ?> listener : eventListeners) {
                ((EventListener<T, D>) listener).onEvent(type, data);
            }
        } finally {
            publishing = false;
            processPendingOperations();
        }
    }

    /**
     * Processes any subscribe/unsubscribe operations that occurred during
     * publishing.
     */
    private void processPendingOperations() {
        for (Runnable operation : pendingOperations) {
            operation.run();
        }
        pendingOperations.clear();
    }

    /**
     * Returns the number of listeners registered for a specific event type.
     *
     * @param type the event type to check
     * @return the number of registered listeners
     */
    public int getListenerCount(EventType type) {
        Array<EventListener<?, ?>> eventListeners = listeners.get(type);
        return eventListeners != null ? eventListeners.size : 0;
    }

    /**
     * Removes all listeners for a specific event type.
     *
     * @param type the event type to clear
     */
    public void clearListeners(EventType type) {
        if (type == null) {
            return;
        }

        Runnable operation = () -> listeners.remove(type);

        if (publishing) {
            pendingOperations.add(operation);
        } else {
            operation.run();
        }
    }

    @Override
    public String toString() {
        int totalListeners = 0;
        for (Array<EventListener<?, ?>> arr : listeners.values()) {
            totalListeners += arr.size;
        }
        return "EventManager{eventTypes=" + listeners.size + ", totalListeners=" + totalListeners + "}";
    }
}
