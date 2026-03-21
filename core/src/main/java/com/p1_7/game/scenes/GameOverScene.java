package com.p1_7.game.scenes;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.p1_7.abstractengine.input.IInputExtensionRegistry;
import com.p1_7.abstractengine.input.IInputQuery;
import com.p1_7.abstractengine.input.InputState;
import com.p1_7.abstractengine.render.IRenderQueue;
import com.p1_7.abstractengine.scene.Scene;
import com.p1_7.abstractengine.scene.SceneContext;
import com.p1_7.game.Settings;
import com.p1_7.game.entities.BackgroundImage;
import com.p1_7.game.entities.BrightnessOverlay;
import com.p1_7.game.entities.MenuButton;
import com.p1_7.game.entities.Text;
import com.p1_7.game.input.GameActions;
import com.p1_7.game.input.ICursorSource;
import com.p1_7.game.managers.IFontManager;

/**
 * Shown when the player's health reaches zero and the game ends.
 *
 * Offers a RETRY button to restart the game and a MAIN MENU button
 * to return to the main menu.
 */
public class GameOverScene extends Scene {

    private static final float INPUT_COOLDOWN_SECONDS = 0.18f;
    private static final String BG_ASSET = "menu/background.png";
    private static final String BTN_ASSET = "menu/button.png";
    private static final String HOVER_ASSET = "menu/button_hover.png";

    // ── input ──────────────────────────────────────────────────────────────────
    private ICursorSource cursorSource;
    private IInputQuery inputQuery;

    // ── ui elements ────────────────────────────────────────────────────────────
    private BitmapFont titleFont;
    private BitmapFont promptFont;
    private BitmapFont buttonFont;
    private BackgroundImage background;
    private Text title;
    private Text promptStatus;
    private Text hintSpace;
    private Text hintEsc;
    private MenuButton retryButton;
    private MenuButton mainMenuButton;
    private BrightnessOverlay brightnessOverlay;

    private float inputCooldown;

    public GameOverScene() {
        this.name = "game-over";
    }

    @Override
    public void onEnter(SceneContext context) {
        IFontManager fontManager = context.get(IFontManager.class);
        titleFont = fontManager.getGoldDisplayFont(54);
        promptFont = fontManager.getPromptFont();
        buttonFont = fontManager.getDarkTextFont(22);

        IInputExtensionRegistry inputRegistry = context.get(IInputExtensionRegistry.class);
        inputQuery = context.get(IInputQuery.class);
        if (inputRegistry.hasExtension(ICursorSource.class)) {
            cursorSource = inputRegistry.getExtension(ICursorSource.class);
        }
        // cursorSource stays null if not registered; update() guard handles it cleanly

        float cx = Settings.getWindowWidth() / 2f;
        float cy = Settings.getWindowHeight() / 2f;
        background = new BackgroundImage(BG_ASSET);
        title = new Text("GAME OVER", cx, cy + 120f, titleFont);
        promptStatus = new Text("Better luck next time!", cx, cy + 55f, promptFont);
        retryButton = MenuButton.withTexture("RETRY", cx, cy - 10f, buttonFont, BTN_ASSET, HOVER_ASSET);
        mainMenuButton = MenuButton.withTexture("MAIN MENU", cx, cy - 85f, buttonFont, BTN_ASSET, HOVER_ASSET);
        hintSpace = new Text("SPACE - Retry", cx, cy - 175f, promptFont);
        hintEsc = new Text("ESC - Main Menu", cx, cy - 220f, promptFont);
        brightnessOverlay = new BrightnessOverlay();

        inputCooldown = INPUT_COOLDOWN_SECONDS;
    }

    @Override
    public void onExit(SceneContext context) {
        if (retryButton != null) retryButton.dispose();
        if (mainMenuButton != null) mainMenuButton.dispose();
        if (brightnessOverlay != null) brightnessOverlay.dispose();
        titleFont = null;
        promptFont = null;
        buttonFont = null;
        inputQuery = null;
        cursorSource = null;
    }

    @Override
    public void update(float deltaTime, SceneContext context) {
        if (inputCooldown > 0f) {
            inputCooldown -= deltaTime;
            return;
        }

        if (cursorSource != null) {
            retryButton.updateInput(cursorSource, inputQuery);
            mainMenuButton.updateInput(cursorSource, inputQuery);
        }

        boolean backPressed = inputQuery.getActionState(GameActions.MENU_BACK) == InputState.PRESSED;
        boolean confirmPressed =
            inputQuery.getActionState(GameActions.MENU_CONFIRM) == InputState.PRESSED;

        if (backPressed || mainMenuButton.isClicked()) {
            mainMenuButton.resetClick();
            context.changeScene("menu");
            return;
        }

        if (confirmPressed || retryButton.isClicked()) {
            retryButton.resetClick();
            context.changeScene("game");
        }
    }

    @Override
    public void submitRenderable(IRenderQueue renderQueue) {
        renderQueue.queue(background);
        renderQueue.queue(title);
        renderQueue.queue(promptStatus);
        renderQueue.queue(retryButton);
        renderQueue.queue(mainMenuButton);
        renderQueue.queue(hintSpace);
        renderQueue.queue(hintEsc);
        renderQueue.queue(brightnessOverlay);
    }
}
