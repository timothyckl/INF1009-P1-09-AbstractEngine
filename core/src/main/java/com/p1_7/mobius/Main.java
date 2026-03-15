package com.p1_7.mobius;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.p1_7.abstractengine.engine.Engine;
import com.p1_7.abstractengine.entity.EntityManager;
import com.p1_7.abstractengine.input.InputManager;
import com.p1_7.abstractengine.input.InputMapping;
import com.p1_7.abstractengine.scene.SceneManager;
import com.p1_7.mobius.platform.GdxInputSource;
import com.p1_7.mobius.platform.GdxRenderManager;

// application entry point for the möbius strip simulation
public class Main extends ApplicationAdapter {

    // engine instance that owns all managers for this session
    private Engine engine;

    /**
     * creates and wires up all engine managers, registers the Möbius scene,
     * and initialises the engine. called once by libGDX when the window is ready.
     */
    @Override
    public void create() {
        engine = new Engine();

        // register the core managers (dependency order is resolved internally)
        engine.registerManager(new EntityManager());

        // build the input manager and bind all simulation keys before registering
        InputManager inputManager = new InputManager(new GdxInputSource());
        InputMapping mapping = inputManager.getInputMapping();
        mapping.bindKey(Input.Keys.LEFT,   MobiusActions.ROTATE_LEFT);
        mapping.bindKey(Input.Keys.RIGHT,  MobiusActions.ROTATE_RIGHT);
        mapping.bindKey(Input.Keys.UP,     MobiusActions.ROTATE_UP);
        mapping.bindKey(Input.Keys.DOWN,   MobiusActions.ROTATE_DOWN);
        mapping.bindKey(Input.Keys.ESCAPE, MobiusActions.QUIT);
        engine.registerManager(inputManager);

        engine.registerManager(new GdxRenderManager());

        // set up the scene graph with a single mobius scene
        SceneManager sceneManager = new SceneManager();
        sceneManager.registerScene(new MobiusScene());
        sceneManager.setInitialScene("mobius");
        engine.registerManager(sceneManager);

        engine.init();
    }

    @Override
    public void render() {
        engine.update(Gdx.graphics.getDeltaTime());
        engine.render();
    }

    @Override
    public void dispose() {
        engine.shutdown();
    }
}
