package com.p1_7.abstractengine.managers.base;

import com.p1_7.abstractengine.managers.api.IManager;

/**
 * Abstract base class for all managers using the template method pattern.
 *
 * Provides lifecycle management with initialisation state tracking.
 * Subclasses should override the hook methods like onInit() and onShutdown()
 * rather than the final template methods.
 *
 * @see Updatable for managers requiring per-frame updates
 */
public abstract class AbstractManager implements IManager {

    private boolean initialised = false;

    /**
     * Initialises this manager. Can only be called once.
     *
     * @throws IllegalStateException if already initialised
     */
    @Override
    public final void init() {
        if (initialised) {
            throw new IllegalStateException("Manager already initialised: " + getClass().getSimpleName());
        }
        onInit();
        initialised = true;
    }

    /**
     * Shuts down this manager. Silently skips if not initialised.
     */
    @Override
    public final void shutdown() {
        if (!initialised) {
            return;
        }
        onShutdown();
        initialised = false;
    }

    /**
     * Checks whether this manager has been initialised.
     *
     * @return true if initialised, false otherwise
     */
    public boolean isInitialised() {
        return initialised;
    }

    /**
     * Hook method called during initialisation. Override to provide
     * manager-specific initialisation logic.
     */
    protected void onInit() {
        // default empty implementation
    }

    /**
     * Hook method called during shutdown. Override to provide
     * manager-specific cleanup logic.
     */
    protected void onShutdown() {
        // default empty implementation
    }
}
