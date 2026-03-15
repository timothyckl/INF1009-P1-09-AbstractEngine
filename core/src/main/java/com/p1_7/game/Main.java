package com.p1_7.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;

import com.p1_7.abstractengine.engine.Engine;
import com.p1_7.abstractengine.entity.EntityManager;
import com.p1_7.abstractengine.input.InputManager;
import com.p1_7.abstractengine.input.InputMapping;
import com.p1_7.abstractengine.scene.SceneManager;

import com.p1_7.game.input.MappableActions;
import com.p1_7.game.platform.GdxInputSource;
import com.p1_7.game.platform.GdxRenderManager;
import com.p1_7.game.scenes.MenuScene;
import com.p1_7.game.scenes.SettingsScene;
import com.p1_7.game.scenes.LevelCompleteScene;
import com.p1_7.game.managers.AudioManager;

/**
 * Entry point for the game application.
 *
 * Bootstraps the engine with the minimum set of managers required to
 * display the scenes: entity management, rendering, input, and
 * scene orchestration.
 */
public class Main extends ApplicationAdapter {

    private Engine engine;
    
    /**
     * Initialises the engine and registers all managers and scenes.
     * Called once by libGDX when the application window is ready.
     */
    @Override
    public void create() {
        engine = new Engine();

        // core managers, registration order does not matter
        engine.registerManager(new EntityManager());

        // Setup Input Manager & default bindings
        InputManager inputManager = new InputManager(new GdxInputSource());
        InputMapping mapping = inputManager.getInputMapping();
        
        // Default Keyboard bindings
        mapping.bindKey(Input.Keys.W, MappableActions.UP);
        mapping.bindKey(Input.Keys.S, MappableActions.DOWN);
        mapping.bindKey(Input.Keys.A, MappableActions.LEFT);
        mapping.bindKey(Input.Keys.D, MappableActions.RIGHT);
        
        // Bind the physical mouse scroll to logical scroll actions
        mapping.bindButton(GdxInputSource.SCROLL_UP, MappableActions.SCROLL_UP);
        mapping.bindButton(GdxInputSource.SCROLL_DOWN, MappableActions.SCROLL_DOWN);
        
        engine.registerManager(inputManager);
        engine.registerManager(new GdxRenderManager());

        // Scene setup
        SceneManager sceneManager = new SceneManager();
        sceneManager.registerScene(new MenuScene());
        sceneManager.registerScene(new SettingsScene(inputManager));
        sceneManager.registerScene(new LevelCompleteScene());
        sceneManager.setInitialScene("menu");
        engine.registerManager(sceneManager);

        engine.init();

        // --- AUDIO SETUP ---
        // Delegate all audio loading and playback to the AudioManager
        AudioManager.getInstance().loadMusic("bgMusic", "demo_archive/music.mp3");
        AudioManager.getInstance().playMusic("bgMusic", true);
    }

    /**
     * Called every frame by libGDX. Delegates update and render to the engine.
     */
    @Override
    public void render() {
        // 1. Wipe the screen black before drawing the new frame to prevent the "hall of mirrors" effect
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // --- DYNAMIC AUDIO UPDATE ---
        // Delegate volume updates to the AudioManager
        AudioManager.getInstance().updateVolume();

        // 2. Process engine logic and rendering
        engine.update(Gdx.graphics.getDeltaTime());
        engine.render();
    }

    /**
     * Called by libGDX when the application is closing.
     * Shuts down all managers in reverse dependency order.
     */
    @Override
    public void dispose() {
        // Delegate audio cleanup to the AudioManager
        AudioManager.getInstance().dispose();
        engine.shutdown();
    }
}