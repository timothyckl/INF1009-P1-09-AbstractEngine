package com.p1_7.abstractengine.managers.base;

import com.p1_7.abstractengine.managers.api.IManager;

/**
 * Abstract base class for all managers using the template method pattern.
 *
 * Provides lifecycle management with initialisation state tracking.
 * Subclasses should override the hook methods onInit(), onUpdate(float)},
 * onShutdown()})
 * rather than the final template methods.
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
     * Updates this manager. Silently skips if not initialised.
     *
     * @param deltaTime the time elapsed since the last update
     */
    @Override
    public final void update(float deltaTime) {
        if (!initialised) {
            return;
        }
        onUpdate(deltaTime);
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
     * Hook method called during update. Override to provide
     * manager-specific update logic.
     *
     * @param deltaTime the time elapsed since the last update
     */
    protected void onUpdate(float deltaTime) {
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
