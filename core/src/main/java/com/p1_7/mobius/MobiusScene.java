package com.p1_7.mobius;

import com.badlogic.gdx.Gdx;
import com.p1_7.abstractengine.input.IInputQuery;
import com.p1_7.abstractengine.input.InputState;
import com.p1_7.abstractengine.scene.Scene;
import com.p1_7.abstractengine.scene.SceneContext;

/**
 * Scene that hosts the interactive Möbius strip simulation.
 *
 * <p>On enter it creates the strip entity and maps arrow keys to rotation;
 * ESC closes the application.</p>
 */
public class MobiusScene extends Scene {

    /** rotation speed in radians per second applied while an arrow key is held */
    private static final float ROTATE_SPEED = 1.5f;

    /** the Möbius strip entity submitted to the render queue each frame */
    private MobiusStrip strip;

    /**
     * Constructs the scene and assigns its unique registry key.
     */
    public MobiusScene() {
        this.name = "mobius";
    }

    /**
     * Initialises the strip entity.
     *
     * @param context the current engine context
     */
    @Override
    public void onEnter(SceneContext context) {
        strip = new MobiusStrip();
    }

    /**
     * No resources to release.
     *
     * @param context the current engine context
     */
    @Override
    public void onExit(SceneContext context) {
        // no-op — MobiusStrip holds no disposable resources
    }

    /**
     * Reads held arrow keys each frame and applies incremental rotation to
     * the strip. ESC exits the application.
     *
     * @param deltaTime seconds elapsed since the previous frame
     * @param context   the current engine context
     */
    @Override
    public void update(float deltaTime, SceneContext context) {
        IInputQuery input = context.input();

        // exit on the first frame escape is pressed (PRESSED = just-pressed, not held)
        if (input.getActionState(MobiusActions.QUIT) == InputState.PRESSED) {
            Gdx.app.exit();
            return;
        }

        // left/right arrows rotate around the Y-axis (continuous while held)
        if (input.isActionActive(MobiusActions.ROTATE_LEFT)) {
            strip.rotateY(-ROTATE_SPEED * deltaTime);
        }
        if (input.isActionActive(MobiusActions.ROTATE_RIGHT)) {
            strip.rotateY(ROTATE_SPEED * deltaTime);
        }

        // up/down arrows rotate around the X-axis (continuous while held)
        if (input.isActionActive(MobiusActions.ROTATE_UP)) {
            strip.rotateX(-ROTATE_SPEED * deltaTime);
        }
        if (input.isActionActive(MobiusActions.ROTATE_DOWN)) {
            strip.rotateX(ROTATE_SPEED * deltaTime);
        }
    }

    /**
     * Submits the strip to the render queue for drawing this frame.
     *
     * @param context the current engine context
     */
    @Override
    public void submitRenderable(SceneContext context) {
        context.renderQueue().queue(strip);
    }
}
