package com.p1_7.abstractengine.managers.api;

/**
 * Capability interface for managers requiring per-frame updates.
 *
 * Implement this on managers needing time-based state changes (rendering,
 * physics, animation). Service-style managers should not implement this.
 */
public interface Updatable {
    /**
     * Updates this manager's state for the current frame.
     *
     * @param deltaTime the time elapsed since the last update, in seconds
     */
    void update(float deltaTime);
}
