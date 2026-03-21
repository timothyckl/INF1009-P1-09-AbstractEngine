package com.p1_7.game.scenes;

import com.p1_7.abstractengine.render.IRenderQueue;
import com.p1_7.abstractengine.scene.Scene;
import com.p1_7.abstractengine.scene.SceneContext;

/**
 * placeholder gameplay scene pending full implementation in issue #100.
 *
 * all lifecycle and render hooks are no-ops until the game scene orchestration
 * work lands. the scene is registered under the key "game" in main.
 */
public class GameScene extends Scene {

    /**
     * constructs the game scene with the scene key "game".
     */
    public GameScene() {
        this.name = "game";
    }

    /**
     * called when this scene becomes active; no-op until issue #100 is implemented.
     *
     * @param context the engine service context
     */
    @Override
    public void onEnter(SceneContext context) {
    }

    /**
     * called when this scene is replaced; no-op until issue #100 is implemented.
     *
     * @param context the engine service context
     */
    @Override
    public void onExit(SceneContext context) {
    }

    /**
     * per-frame update hook; no-op until issue #100 is implemented.
     *
     * @param deltaTime elapsed seconds since the last frame
     * @param context   the engine service context
     */
    @Override
    public void update(float deltaTime, SceneContext context) {
    }

    /**
     * per-frame render submission hook; no-op until issue #100 is implemented.
     *
     * @param renderQueue the render queue accumulator for this frame
     */
    @Override
    public void submitRenderable(IRenderQueue renderQueue) {
    }
}
