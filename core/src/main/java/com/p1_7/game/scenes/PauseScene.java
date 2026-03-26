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
import com.p1_7.game.input.GameActions;
import com.p1_7.game.input.ICursorSource;
import com.p1_7.game.managers.IFontManager;
import com.p1_7.game.platform.GdxDrawContext;
import com.p1_7.game.ui.MenuButton;
import com.p1_7.game.ui.Text;

/**
 * overlay scene shown when ESC is pressed during the CHOOSING phase.
 *
 * renders a dim quad over the frozen game world with a centred popup panel containing
 * a PAUSED title and three buttons: Resume, Settings, and Main Menu.
 */
public class PauseScene extends Scene {

    private static final String BTN_ASSET = "menu/button.png";
    private static final String HOVER_ASSET = "menu/button_hover.png";
    private static final String POPUP_ASSET = "pause_popup.png";

    private static final float POPUP_WIDTH = 885f;
    private static final float POPUP_HEIGHT = 520f;

    private static final Color DIM_COLOUR = new Color(0f, 0f, 0f, 0.62f);

    private float centreX;
    private float centreY;

    private float popupX;
    private float popupY;
    private float popupTopY;

    /** cached on onEnter so buttons can navigate back without a context parameter */
    private String suspendedKey;

    private BitmapFont titleFont;
    private BitmapFont buttonFont;

    private IRenderable dimOverlay;
    private IRenderable popupRenderable;

    private Text pauseTitle;
    private MenuButton resumeButton;
    private MenuButton settingsButton;
    private MenuButton returnToMenuButton;

    public PauseScene() {
        this.name = "pause";
    }

    @Override
    public void onEnter(SceneContext context) {
        centreX = Settings.getWindowWidth() / 2f;
        centreY = Settings.getWindowHeight() / 2f;

        popupX = centreX - POPUP_WIDTH / 2f;
        popupY = centreY - POPUP_HEIGHT / 2f;
        popupTopY = popupY + POPUP_HEIGHT;

        suspendedKey = context.getSuspendedSceneKey();

        IFontManager fontManager = context.get(IFontManager.class);
        titleFont = fontManager.getGoldDisplayFont(50);
        buttonFont = fontManager.getDarkTextFont(25);

        dimOverlay = buildDimOverlay();
        popupRenderable = buildPopupRenderable();

        // title inside the popup header area
        pauseTitle = new Text(
            "PAUSED",
            centreX,
            popupTopY - 162f,
            titleFont
        );

        // buttons placed inside the popup body
        resumeButton = MenuButton.withTexture(
            "RESUME",
            centreX,
            popupY + 285f,
            buttonFont,
            BTN_ASSET,
            HOVER_ASSET
        );

        settingsButton = MenuButton.withTexture(
            "SETTINGS",
            centreX,
            popupY + 205f,
            buttonFont,
            BTN_ASSET,
            HOVER_ASSET
        );

        returnToMenuButton = MenuButton.withTexture(
            "MAIN MENU",
            centreX,
            popupY + 125f,
            buttonFont,
            BTN_ASSET,
            HOVER_ASSET
        );
    }

    @Override
    public void onExit(SceneContext context) {
        if (resumeButton != null) resumeButton.dispose();
        if (settingsButton != null) settingsButton.dispose();
        if (returnToMenuButton != null) returnToMenuButton.dispose();

        dimOverlay = null;
        popupRenderable = null;
        pauseTitle = null;
        resumeButton = null;
        settingsButton = null;
        returnToMenuButton = null;
        titleFont = null;
        buttonFont = null;
        suspendedKey = null;
    }

    @Override
    public void update(float deltaTime, SceneContext context) {
        IInputQuery inputQuery = context.get(IInputQuery.class);

        // ESC resumes the suspended game
        if (inputQuery.getActionState(GameActions.MENU_BACK) == InputState.PRESSED) {
            resumeGame(context);
            return;
        }

        IInputExtensionRegistry reg = context.get(IInputExtensionRegistry.class);
        ICursorSource cursor = reg.hasExtension(ICursorSource.class)
            ? reg.getExtension(ICursorSource.class)
            : null;

        if (cursor == null) return;

        resumeButton.updateInput(cursor, inputQuery);
        settingsButton.updateInput(cursor, inputQuery);
        returnToMenuButton.updateInput(cursor, inputQuery);

        if (resumeButton.isClicked()) {
            resumeButton.resetClick();
            resumeGame(context);
        } else if (settingsButton.isClicked()) {
            settingsButton.resetClick();
            context.changeScene("settings");
        } else if (returnToMenuButton.isClicked()) {
            returnToMenuButton.resetClick();
            returnToMenu(context);
        }
    }

    @Override
    public void submitRenderable(IRenderQueue renderQueue) {
        if (dimOverlay == null || popupRenderable == null) return;

        renderQueue.queue(dimOverlay);
        renderQueue.queue(popupRenderable);
        renderQueue.queue(pauseTitle);
        renderQueue.queue(resumeButton);
        renderQueue.queue(settingsButton);
        renderQueue.queue(returnToMenuButton);
    }

    private void resumeGame(SceneContext context) {
        if (suspendedKey == null) return;
        context.changeScene(suspendedKey);
    }

    private void returnToMenu(SceneContext context) {
        if (suspendedKey != null) {
            Scene suspended = context.getScene(suspendedKey);
            if (suspended != null) {
                suspended.onExit(context);
            }
            context.clearSuspendedScene();
        }
        context.changeScene("menu");
    }

    private IRenderable buildDimOverlay() {
        final Transform2D fullScreen = new Transform2D(
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
                return fullScreen;
            }

            @Override
            public void render(IDrawContext ctx) {
                GdxDrawContext gdx = (GdxDrawContext) ctx;
                gdx.drawTintedQuad(
                    DIM_COLOUR,
                    0f,
                    0f,
                    Settings.getWindowWidth(),
                    Settings.getWindowHeight()
                );
            }
        };
    }

    private IRenderable buildPopupRenderable() {
        final Transform2D popupTransform = new Transform2D(
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
                return popupTransform;
            }

            @Override
            public void render(IDrawContext ctx) {
                GdxDrawContext gdx = (GdxDrawContext) ctx;
                gdx.drawTexture(
                    POPUP_ASSET,
                    popupTransform.getPosition(0),
                    popupTransform.getPosition(1),
                    popupTransform.getSize(0),
                    popupTransform.getSize(1)
                );
            }
        };
    }
}