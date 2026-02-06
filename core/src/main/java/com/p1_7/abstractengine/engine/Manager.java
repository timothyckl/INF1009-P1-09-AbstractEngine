package com.p1_7.abstractengine.engine;

/**
 * Abstract base class that provides the standard lifecycle template for
 * all managers in the engine.
 *
 * <p>Subclasses override {@link #onInit()} and {@link #onShutdown()} to
 * perform their own setup and teardown.  The public {@link #init()} and
 * {@link #shutdown()} methods are {@code final} and must not be
 * overridden — they manage the {@link #isInitialised()} flag and then
 * delegate to the hooks.</p>
 */
public abstract class Manager implements IManager {

    /** tracks whether this manager has been initialised */
    private boolean initialised;

    /**
     * Initialises the manager.  Sets the initialised flag and delegates
     * to {@link #onInit()}.
     */
    @Override
    public final void init() {
        initialised = true;
        onInit();
    }

    /**
     * Shuts down the manager.  Delegates to {@link #onShutdown()} and
     * clears the initialised flag.
     */
    @Override
    public final void shutdown() {
        onShutdown();
        initialised = false;
    }

    /**
     * Hook called during initialisation.  Override in subclasses to
     * perform setup work.  Default implementation is a no-op.
     */
    protected void onInit() {
        // no-op — subclasses may override
    }

    /**
     * Hook called during shutdown.  Override in subclasses to release
     * resources.  Default implementation is a no-op.
     */
    protected void onShutdown() {
        // no-op — subclasses may override
    }

    /**
     * Returns whether this manager has been initialised.
     *
     * @return {@code true} if {@link #init()} has been called and
     *         {@link #shutdown()} has not yet been called
     */
    public boolean isInitialised() {
        return initialised;
    }
}
