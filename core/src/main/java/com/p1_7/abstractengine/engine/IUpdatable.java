package com.p1_7.abstractengine.engine;

/**
 * Contract for any object that must be updated once per frame.
 *
 * <p>The engine iterates all registered {@code IUpdatable} instances
 * each frame, passing the elapsed time since the previous frame.</p>
 */
public interface IUpdatable {

    /**
     * Advances the object's internal state by one tick.
     *
     * @param deltaTime seconds elapsed since the previous frame
     */
    void update(float deltaTime);
}
