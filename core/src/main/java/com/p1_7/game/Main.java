package com.p1_7.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import com.p1_7.abstractengine.engine.Engine;
import com.p1_7.abstractengine.entity.EntityManager;
import com.p1_7.abstractengine.input.InputManager;
import com.p1_7.abstractengine.movement.MovementManager;
import com.p1_7.abstractengine.scene.SceneManager;

import com.badlogic.gdx.Input;
import com.p1_7.game.input.GameActions;
import com.p1_7.game.managers.GameCollisionManager;
import com.p1_7.game.platform.GdxInputSource;
import com.p1_7.game.platform.GdxRenderManager;
import com.p1_7.game.scenes.LevelCompleteScene;
import com.p1_7.game.scenes.MenuScene;
import com.p1_7.game.scenes.SettingScene;
import com.p1_7.game.scenes.TestGameScene;

/**
 * Entry point for the game application.
 *
 * Bootstraps the engine with the minimum set of managers required to
 * display the hello-world scene: entity management, rendering, and
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

        // ── core managers ────────────────────────────────────────────────────
        engine.registerManager(new EntityManager());

        InputManager inputManager = new InputManager(new GdxInputSource());
        inputManager.getInputMapping().bindKey(Input.Keys.W,     GameActions.UP);
        inputManager.getInputMapping().bindKey(Input.Keys.S,     GameActions.DOWN);
        inputManager.getInputMapping().bindKey(Input.Keys.A,     GameActions.LEFT);
        inputManager.getInputMapping().bindKey(Input.Keys.D,     GameActions.RIGHT);
        inputManager.getInputMapping().bindKey(Input.Keys.UP,    GameActions.UP);
        inputManager.getInputMapping().bindKey(Input.Keys.DOWN,  GameActions.DOWN);
        inputManager.getInputMapping().bindKey(Input.Keys.LEFT,  GameActions.LEFT);
        inputManager.getInputMapping().bindKey(Input.Keys.RIGHT, GameActions.RIGHT);
        engine.registerManager(inputManager);
        engine.registerManager(new GdxRenderManager());

        MovementManager movementManager = new MovementManager();
        movementManager.setWorldBounds(
            new float[]{ 0f, 0f },
            new float[]{ Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT });
        engine.registerManager(movementManager);

        GameCollisionManager collisionManager = new GameCollisionManager();
        engine.registerManager(collisionManager);

        // ── scene setup ──────────────────────────────────────────────────────
        SceneManager sceneManager = new SceneManager();

        sceneManager.registerScene(new MenuScene());
        sceneManager.registerScene(new SettingScene());
        sceneManager.registerScene(new LevelCompleteScene());
        sceneManager.registerScene(new TestGameScene(movementManager, collisionManager));

        sceneManager.setInitialScene("testgame"); // ← switch to "menu" when done testing
        engine.registerManager(sceneManager);

        engine.init();
    }

    /**
     * Called every frame by libGDX. Delegates update and render to the engine.
     */
    @Override
    public void render() {
        engine.update(Gdx.graphics.getDeltaTime());
        engine.render();
    }

    /**
     * Called by libGDX when the application is closing.
     * Shuts down all managers in reverse dependency order.
     */
    @Override
    public void dispose() {
        engine.shutdown();
    }
}
