package com.p1_7.abstractengine.engine;

/**
 * Abstract manager that participates in the per-frame update loop in
 * addition to the standard lifecycle provided by {@link Manager}.
 *
 * <p>Subclasses implement {@link #onUpdate(float)} to define their
 * frame-tick behaviour.  The public {@link #update(float)} method is
 * {@code final} and delegates directly to that hook.</p>
 */
public abstract class UpdatableManager extends Manager implements IUpdatable {

    /**
     * Called once per frame by the engine.  Delegates to
     * {@link #onUpdate(float)}.
     *
     * @param deltaTime seconds elapsed since the previous frame
     */
    @Override
    public final void update(float deltaTime) {
        onUpdate(deltaTime);
    }

    /**
     * Hook that subclasses must implement to perform their per-frame logic.
     *
     * @param deltaTime seconds elapsed since the previous frame
     */
    protected abstract void onUpdate(float deltaTime);
}
