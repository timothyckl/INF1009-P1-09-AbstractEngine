package com.p1_7.abstractengine.scene;

/**
 * Abstract base class for every scene (game state) in the engine.
 *
 * <p>Concrete scenes override the lifecycle and per-frame hooks to
 * implement their own behaviour.  A scene's {@link #update} hook is
 * skipped when the scene is {@link #setPaused(boolean) paused}, but
 * {@link #submitRenderable(SceneContext)} is always called so that
 * paused scenes can still render a static frame.</p>
 */
public abstract class Scene {

    /** the unique key used to register and look up this scene */
    protected String name;

    /** when {@code true} the per-frame update hook is skipped */
    protected boolean paused;

    // ---------------------------------------------------------------
    // lifecycle hooks — implemented by concrete scenes
    // ---------------------------------------------------------------

    /**
     * Called once when this scene becomes the active scene.
     *
     * @param context the current engine context
     */
    public abstract void onEnter(SceneContext context);

    /**
     * Called once when this scene is about to be replaced by another.
     *
     * @param context the current engine context
     */
    public abstract void onExit(SceneContext context);

    /**
     * Per-frame update hook.  Not called while the scene is paused.
     *
     * @param deltaTime seconds elapsed since the previous frame
     * @param context   the current engine context
     */
    public abstract void update(float deltaTime, SceneContext context);

    /**
     * Per-frame hook where the scene pushes its visible entities into
     * the render queue.  Always called, even when the scene is paused.
     *
     * @param context the current engine context
     */
    public abstract void submitRenderable(SceneContext context);

    // ---------------------------------------------------------------
    // concrete accessors
    // ---------------------------------------------------------------

    /**
     * Returns the name (key) of this scene.
     *
     * @return the scene name; never {@code null}
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether this scene is currently paused.
     *
     * @return {@code true} if the scene's update hook is being skipped
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Sets the paused state of this scene.
     *
     * @param paused {@code true} to suspend per-frame updates
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
