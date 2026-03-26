package com.p1_7.game.scenes;

import com.badlogic.gdx.graphics.Color;
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
import com.p1_7.game.ui.MenuButton;
import com.p1_7.game.ui.Text;

/**
 * scene shown after completing a level.
 * uses a centered popup panel with dimmed background.
 */
public class LevelCompleteScene extends Scene {

    private static final String BG_ASSET = "background.png";
    private static final String POPUP_ASSET = "Popup.png";
    private static final String BTN_ASSET = "menu/button.png";
    private static final String HOVER_ASSET = "menu/button_hover.png";

    private static final float POPUP_WIDTH = 1500f;
    private static final float POPUP_HEIGHT = 920f;

    private static final Color FOCUS_OVERLAY_COLOUR = new Color(0f, 0f, 0f, 0.58f);

    private float centreX;
    private float centreY;

    private float popupX;
    private float popupY;
    private float popupTopY;

    private BackgroundImage background;
    private IRenderable focusOverlay;
    private IRenderable popupRenderable;

    private BitmapFont titleFont;
    private BitmapFont subtitleFont;
    private BitmapFont hintFont;
    private BitmapFont buttonFont;

    private Text titleText;
    private Text subtitleText;
    private Text continueHintText;
    private Text menuHintText;

    private MenuButton continueButton;
    private MenuButton mainMenuButton;

    public LevelCompleteScene() {
        this.name = "level-complete";
    }

    @Override
    public void onEnter(SceneContext context) {
        centreX = Settings.getWindowWidth() / 2f;
        centreY = Settings.getWindowHeight() / 2f;

        popupX = centreX - POPUP_WIDTH / 2f;
        popupY = centreY - POPUP_HEIGHT / 2f;
        popupTopY = popupY + POPUP_HEIGHT;

        IFontManager fontManager = context.get(IFontManager.class);
        IAudioManager audioManager = context.get(IAudioManager.class);
        ILevelOrchestrator orchestrator = context.get(ILevelOrchestrator.class);

        audioManager.playMusic("menu", true);

        titleFont = fontManager.getGoldDisplayFont(35);
        subtitleFont = fontManager.getDarkTextFont(24);
        hintFont = fontManager.getDarkTextFont(20);
        buttonFont = fontManager.getDarkTextFont(26);

        background = new BackgroundImage(BG_ASSET);
        focusOverlay = createFocusOverlay();
        popupRenderable = createPopupRenderable();

        Difficulty currentDifficulty = orchestrator.getCurrentDifficulty();
        int completedLevel = getLevelNumber(currentDifficulty);
        String nextText = completedLevel < 3
            ? "Next Up: Level " + (completedLevel + 1)
            : "All Levels Complete!";

        titleText = new Text(
            "LEVEL " + completedLevel + " COMPLETE!",
            centreX,
            popupTopY - 320f,
            titleFont
        );

        subtitleText = new Text(
            nextText,
            centreX,
            popupY + 530f,
            subtitleFont
        );

        continueButton = MenuButton.withTexture(
            completedLevel < 3 ? "CONTINUE" : "MAIN MENU",
            centreX,
            popupY + 465f,
            buttonFont,
            BTN_ASSET,
            HOVER_ASSET
        );

        mainMenuButton = MenuButton.withTexture(
            "MAIN MENU",
            centreX,
            popupY + 380f,
            buttonFont,
            BTN_ASSET,
            HOVER_ASSET
        );

        continueHintText = new Text(
            "[SPACE] " + (completedLevel < 3 ? "Continue" : "Main Menu"),
            centreX,
            popupY + 282f,
            hintFont
        );

        menuHintText = new Text(
            "[ESC] Main Menu",
            centreX,
            popupY + 252f,
            hintFont
        );
    }

    @Override
    public void onExit(SceneContext context) {
        if (continueButton != null) continueButton.dispose();
        if (mainMenuButton != null) mainMenuButton.dispose();

        background = null;
        focusOverlay = null;
        popupRenderable = null;
        titleText = null;
        subtitleText = null;
        continueHintText = null;
        menuHintText = null;
        continueButton = null;
        mainMenuButton = null;

        titleFont = null;
        subtitleFont = null;
        hintFont = null;
        buttonFont = null;
    }

    @Override
    public void update(float deltaTime, SceneContext context) {
        IInputQuery inputQuery = context.get(IInputQuery.class);
        ILevelOrchestrator orchestrator = context.get(ILevelOrchestrator.class);

        if (inputQuery.getActionState(GameActions.MENU_BACK) == InputState.PRESSED) {
            context.changeScene("menu");
            return;
        }

        if (inputQuery.getActionState(GameActions.MENU_CONFIRM) == InputState.PRESSED) {
            handleContinue(context, orchestrator);
            return;
        }

        IInputExtensionRegistry inputRegistry = context.get(IInputExtensionRegistry.class);
        ICursorSource cursorSource = inputRegistry.hasExtension(ICursorSource.class)
            ? inputRegistry.getExtension(ICursorSource.class)
            : null;

        if (cursorSource == null) {
            return;
        }

        continueButton.updateInput(cursorSource, inputQuery);
        mainMenuButton.updateInput(cursorSource, inputQuery);

        if (continueButton.isClicked()) {
            continueButton.resetClick();
            handleContinue(context, orchestrator);
            return;
        }

        if (mainMenuButton.isClicked()) {
            mainMenuButton.resetClick();
            context.changeScene("menu");
        }
    }

    @Override
    public void submitRenderable(IRenderQueue renderQueue) {
        renderQueue.queue(background);
        renderQueue.queue(focusOverlay);
        renderQueue.queue(popupRenderable);

        renderQueue.queue(titleText);
        renderQueue.queue(subtitleText);
        renderQueue.queue(continueButton);
        renderQueue.queue(mainMenuButton);
        renderQueue.queue(continueHintText);
        renderQueue.queue(menuHintText);
    }

    private void handleContinue(SceneContext context, ILevelOrchestrator orchestrator) {
        Difficulty current = orchestrator.getCurrentDifficulty();

        if (current == Difficulty.EASY) {
            orchestrator.setCurrentDifficulty(Difficulty.MEDIUM);
            context.changeScene("game");
            return;
        }

        if (current == Difficulty.MEDIUM) {
            orchestrator.setCurrentDifficulty(Difficulty.HARD);
            context.changeScene("game");
            return;
        }

        context.changeScene("menu");
    }

    private int getLevelNumber(Difficulty difficulty) {
        if (difficulty == Difficulty.MEDIUM) return 2;
        if (difficulty == Difficulty.HARD) return 3;
        return 1;
    }

    private IRenderable createFocusOverlay() {
        final Transform2D transform = new Transform2D(
            0f,
            0f,
            Settings.getWindowWidth(),
            Settings.getWindowHeight()
        );

        return new IRenderable() {
            @Override
            public String getAssetPath() {
                return null;
            }

            @Override
            public ITransform getTransform() {
                return transform;
            }

            @Override
            public void render(IDrawContext ctx) {
                ((GdxDrawContext) ctx).drawTintedQuad(
                    FOCUS_OVERLAY_COLOUR,
                    0f,
                    0f,
                    Settings.getWindowWidth(),
                    Settings.getWindowHeight()
                );
            }
        };
    }

    private IRenderable createPopupRenderable() {
        final Transform2D transform = new Transform2D(
            popupX,
            popupY,
            POPUP_WIDTH,
            POPUP_HEIGHT
        );

        return new IRenderable() {
            @Override
            public String getAssetPath() {
                return POPUP_ASSET;
            }

            @Override
            public ITransform getTransform() {
                return transform;
            }

            @Override
            public void render(IDrawContext ctx) {
                GdxDrawContext gdxCtx = (GdxDrawContext) ctx;
                gdxCtx.drawTexture(
                    POPUP_ASSET,
                    transform.getPosition(0),
                    transform.getPosition(1),
                    transform.getSize(0),
                    transform.getSize(1)
                );
            }
        };
    }
}