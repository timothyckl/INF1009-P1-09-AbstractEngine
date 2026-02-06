package com.p1_7.abstractengine.scene;

import com.p1_7.abstractengine.entity.IEntityRepository;
import com.p1_7.abstractengine.input.IInputQuery;
import com.p1_7.abstractengine.render.IRenderQueue;

/**
 * Read-only snapshot of engine state passed into every {@link Scene}
 * callback.
 *
 * <p>A {@link Scene} receives a {@code SceneContext} so that it can
 * query entities, submit render items and read input without holding
 * direct references to the underlying managers.</p>
 */
public interface SceneContext {

    /**
     * Returns the read-only entity repository.
     *
     * @return the {@link IEntityRepository}; never {@code null}
     */
    IEntityRepository entities();

    /**
     * Returns the render queue for submitting items this frame.
     *
     * @return the {@link IRenderQueue}; never {@code null}
     */
    IRenderQueue renderQueue();

    /**
     * Returns the input query interface for the current frame.
     *
     * @return the {@link IInputQuery}; never {@code null}
     */
    IInputQuery input();

    /**
     * Returns the scene manager for requesting scene transitions.
     *
     * @return the {@link SceneManager}; never {@code null}
     */
    SceneManager sceneManager();
}
