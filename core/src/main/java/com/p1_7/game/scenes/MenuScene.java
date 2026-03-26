package com.p1_7.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.p1_7.abstractengine.input.IInputExtensionRegistry;
import com.p1_7.abstractengine.input.IInputQuery;
import com.p1_7.abstractengine.input.InputState;
import com.p1_7.abstractengine.render.IDrawContext;
import com.p1_7.abstractengine.render.IRenderQueue;
import com.p1_7.abstractengine.render.IRenderable;
import com.p1_7.abstractengine.scene.Scene;
import com.p1_7.abstractengine.scene.SceneContext;
import com.p1_7.abstractengine.transform.ITransform;
import com.p1_7.game.Settings;
import com.p1_7.game.core.Transform2D;
import com.p1_7.game.gameplay.Difficulty;
import com.p1_7.game.input.GameActions;
import com.p1_7.game.input.ICursorSource;
import com.p1_7.game.level.ILevelOrchestrator;
import com.p1_7.game.managers.IAudioManager;
import com.p1_7.game.managers.IFontManager;
import com.p1_7.game.platform.GdxDrawContext;
import com.p1_7.game.ui.BackgroundImage;
import com.p1_7.game.ui.BrightnessOverlay;
import com.p1_7.game.ui.MenuButton;
import com.p1_7.game.ui.Text;

/**
 * Main menu scene for Math Quest Maze.
 */
public class MenuScene extends Scene {

    // asset paths ─────────────────────────────────────────────
    private static final String BG_ASSET    = "background.png";
    private static final String BTN_ASSET   = "menu/button.png";
    private static final String HOVER_ASSET = "menu/button_hover.png";
    private static final String LOGO_ASSET  = "menu/MathQuestMazeLogo.png";

    // layout ──────────────────────────────────────────────────
    private float centreX;
    private float firstButtonY;
    private static final float BUTTON_SPACING = 80f;

    // logo placement / size
    private static final float LOGO_WIDTH  = 660f;
    private static final float LOGO_HEIGHT = 260f;
    private static final float LOGO_CENTRE_Y_RATIO = 0.76f;
    private static final float TEAM_Y_RATIO        = 0.56f;
    private static final float FIRST_BUTTON_Y_RATIO = 0.45f;

    // fonts ───────────────────────────────────────────────────
    private BitmapFont subtitleFont;
    private BitmapFont buttonFont;

    // ui components ───────────────────────────────────────────
    private BackgroundImage background;
    private IRenderable     logoRenderable;
    private Text            teamText;
    private MenuButton      startButton;
    private MenuButton      howToPlayButton;
    private MenuButton      settingsButton;
    private MenuButton      exitButton;
    private BrightnessOverlay brightnessOverlay;

    public MenuScene() {
        this.name = "menu";
    }

    @Override
    public void onEnter(SceneContext context) {
        centreX      = Settings.getWindowWidth() / 2f;
        firstButtonY = Settings.getWindowHeight() * FIRST_BUTTON_Y_RATIO;

        IAudioManager audio = context.get(IAudioManager.class);
        IFontManager fontManager = context.get(IFontManager.class);

        audio.playMusic("menu", true);

        subtitleFont = fontManager.getLightTextFont(22);
        buttonFont   = fontManager.getDarkTextFont(26);

        background = new BackgroundImage(BG_ASSET);
        logoRenderable = createLogoRenderable();
        teamText = new Text(
            "By Team P1-09",
            centreX,
            Settings.getWindowHeight() * TEAM_Y_RATIO,
            subtitleFont
        );

        startButton = MenuButton.withTexture(
            "START",
            centreX,
            firstButtonY,
            buttonFont,
            BTN_ASSET,
            HOVER_ASSET
        );

        howToPlayButton = MenuButton.withTexture(
            "HOW TO PLAY",
            centreX,
            firstButtonY - BUTTON_SPACING,
            buttonFont,
            BTN_ASSET,
            HOVER_ASSET
        );

        settingsButton = MenuButton.withTexture(
            "SETTINGS",
            centreX,
            firstButtonY - BUTTON_SPACING * 2f,
            buttonFont,
            BTN_ASSET,
            HOVER_ASSET
        );

        exitButton = MenuButton.withTexture(
            "EXIT",
            centreX,
            firstButtonY - BUTTON_SPACING * 3f,
            buttonFont,
            BTN_ASSET,
            HOVER_ASSET
        );

        brightnessOverlay = new BrightnessOverlay();
    }

    @Override
    public void onExit(SceneContext context) {
        if (startButton != null) startButton.dispose();
        if (howToPlayButton != null) howToPlayButton.dispose();
        if (settingsButton != null) settingsButton.dispose();
        if (exitButton != null) exitButton.dispose();

        background = null;
        logoRenderable = null;
        teamText = null;
        startButton = null;
        howToPlayButton = null;
        settingsButton = null;
        exitButton = null;
        brightnessOverlay = null;
        subtitleFont = null;
        buttonFont = null;
    }

    @Override
    public void update(float deltaTime, SceneContext context) {
        IInputQuery inputQuery = context.get(IInputQuery.class);
        if (inputQuery.getActionState(GameActions.MENU_BACK) == InputState.PRESSED) {
            Gdx.app.exit();
            return;
        }

        IInputExtensionRegistry inputRegistry = context.get(IInputExtensionRegistry.class);
        ICursorSource cursorSource = inputRegistry.hasExtension(ICursorSource.class)
            ? inputRegistry.getExtension(ICursorSource.class)
            : null;

        if (cursorSource == null) return;

        startButton.updateInput(cursorSource, inputQuery);
        howToPlayButton.updateInput(cursorSource, inputQuery);
        settingsButton.updateInput(cursorSource, inputQuery);
        exitButton.updateInput(cursorSource, inputQuery);

        if (startButton.isClicked()) {
            startButton.resetClick();
            context.get(ILevelOrchestrator.class).setCurrentDifficulty(Difficulty.EASY);
            context.changeScene("game");
            return;
        }

        if (settingsButton.isClicked()) {
            settingsButton.resetClick();
            context.changeScene("settings");
            return;
        }

        if (howToPlayButton.isClicked()) {
            howToPlayButton.resetClick();
            context.changeScene("how-to-play");
            return;
        }

        if (exitButton.isClicked()) {
            exitButton.resetClick();
            Gdx.app.exit();
        }
    }

    @Override
    public void submitRenderable(IRenderQueue renderQueue) {
        renderQueue.queue(background);
        renderQueue.queue(logoRenderable);
        renderQueue.queue(teamText);
        renderQueue.queue(startButton);
        renderQueue.queue(howToPlayButton);
        renderQueue.queue(settingsButton);
        renderQueue.queue(exitButton);
        renderQueue.queue(brightnessOverlay);
    }

    /**
     * draws the menu logo centered above the button stack.
     */
    private IRenderable createLogoRenderable() {
        final float logoX = centreX - LOGO_WIDTH / 2f;
        final float logoY = Settings.getWindowHeight() * LOGO_CENTRE_Y_RATIO - LOGO_HEIGHT / 2f;
        final Transform2D transform = new Transform2D(logoX, logoY, LOGO_WIDTH, LOGO_HEIGHT);

        return new IRenderable() {
            @Override
            public String getAssetPath() {
                return LOGO_ASSET;
            }

            @Override
            public ITransform getTransform() {
                return transform;
            }

            @Override
            public void render(IDrawContext ctx) {
                GdxDrawContext gdxCtx = (GdxDrawContext) ctx;
                gdxCtx.drawTexture(
                    LOGO_ASSET,
                    transform.getPosition(0),
                    transform.getPosition(1),
                    transform.getSize(0),
                    transform.getSize(1)
                );
            }
        };
    }
}