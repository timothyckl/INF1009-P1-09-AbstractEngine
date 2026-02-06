package com.p1_7.abstractengine.engine;

/**
 * Core lifecycle contract for every manager in the abstract engine.
 *
 * <p>Implementations are responsible for allocating and releasing any
 * resources they hold during {@link #init()} and {@link #shutdown()}
 * respectively.</p>
 */
public interface IManager {

    /**
     * Initialises the manager and allocates any required resources.
     */
    void init();

    /**
     * Shuts down the manager and releases all held resources.
     */
    void shutdown();
}
