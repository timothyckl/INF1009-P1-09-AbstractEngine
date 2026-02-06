package com.p1_7.abstractengine.engine;

import com.badlogic.gdx.utils.Array;
import com.p1_7.abstractengine.render.RenderManager;

/**
 * Central orchestrator for the abstract engine. Manages the lifecycle
 * and per-frame update of all registered {@link IManager} and
 * {@link IUpdatable} instances, and delegates the explicit render call
 * to the {@link RenderManager}.
 *
 * <p>
 * Managers are initialised in registration order and shut down in
 * reverse order so that dependencies are torn down after the objects
 * that depend on them.
 * </p>
 */
public class Engine {

    /** all managers registered with the engine */
    private final Array<IManager> managers = new Array<>();

    /** subset of managers (or standalone updatables) that update each frame */
    private final Array<IUpdatable> updatables = new Array<>();

    /** the render manager used for the explicit render step */
    private RenderManager renderManager;

    /**
     * Registers a manager. If the manager also implements
     * {@link IUpdatable} it is automatically added to the update loop.
     *
     * @param manager the manager to register
     */
    public void registerManager(IManager manager) {
        managers.add(manager);
        if (manager instanceof IUpdatable) {
            updatables.add((IUpdatable) manager);
        }
    }

    /**
     * Registers a standalone updatable that is not itself a manager.
     *
     * @param updatable the updatable to add to the per-frame loop
     */
    public void registerUpdatable(IUpdatable updatable) {
        updatables.add(updatable);
    }

    /**
     * Stores the render manager for the explicit render step. The
     * engine calls {@link RenderManager#render()} once per frame
     * separately from the update loop.
     *
     * @param renderManager the render manager instance
     */
    public void setRenderManager(RenderManager renderManager) {
        this.renderManager = renderManager;
    }

    /**
     * Initialises every registered manager in registration order.
     */
    public void init() {
        for (int i = 0; i < managers.size; i++) {
            managers.get(i).init();
        }
    }

    /**
     * Advances all registered updatables by one frame.
     *
     * @param deltaTime seconds elapsed since the previous frame
     */
    public void update(float deltaTime) {
        for (int i = 0; i < updatables.size; i++) {
            updatables.get(i).update(deltaTime);
        }
    }

    /**
     * Delegates the current frame's draw call to the render manager.
     * Does nothing if no render manager has been set.
     */
    public void render() {
        if (renderManager != null) {
            renderManager.render();
        }
    }

    /**
     * Shuts down every registered manager in reverse registration
     * order so that dependants are torn down before their dependencies.
     */
    public void shutdown() {
        for (int i = managers.size - 1; i >= 0; i--) {
            managers.get(i).shutdown();
        }
    }
}
