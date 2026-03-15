package com.p1_7.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.p1_7.abstractengine.render.IRenderItem; //redundant
import com.p1_7.abstractengine.scene.Scene;
import com.p1_7.abstractengine.scene.SceneContext;
import com.p1_7.abstractengine.transform.ITransform; //redundant
import com.p1_7.game.Settings;
import com.p1_7.game.core.Transform2D; //redundant
import com.p1_7.game.entities.MenuButton;
import com.p1_7.game.display.TextDisplay;
import com.p1_7.game.display.Background;
import com.p1_7.game.display.BrightnessOverlay;

public class LevelCompleteScene extends Scene {

    private static final int MAX_LEVEL = 3;
    private static final float INPUT_COOLDOWN_SECONDS = 0.18f;
    private static final String BG_ASSET = "menu/background.png";
    private static final String BTN_ASSET = "menu/button.png";
    private static final String HOVER_ASSET = "menu/button_hover.png";
    private static final String TTF_ASSET = "menu/Kenney_Future.ttf";

    private BitmapFont titleFont;
    private BitmapFont promptFont;
    private BitmapFont buttonFont;
    private Background background;
    private BrightnessOverlay brightnessOverlay;
    
    // --- Refactored to TextDisplay ---
    private TextDisplay title;
    private TextDisplay promptStatus;
    private TextDisplay hintSpace;
    private TextDisplay hintEsc;
    
    private MenuButton btnContinue;
    private MenuButton btnMainMenu;
    private int currentLevel = 1;
    private float inputCooldown;

    public LevelCompleteScene() {
        this.name = "level-complete";
    }

    @Override
    public void onEnter(SceneContext context) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(TTF_ASSET));

        FreeTypeFontParameter titleParams = new FreeTypeFontParameter();
        titleParams.size = 54;
        titleParams.color = new Color(1f, 0.92f, 0.55f, 1f);
        titleParams.shadowOffsetX = 2;
        titleParams.shadowOffsetY = -2;
        titleParams.shadowColor = new Color(0f, 0f, 0f, 0.5f);
        titleFont = generator.generateFont(titleParams);

        FreeTypeFontParameter promptParams = new FreeTypeFontParameter();
        promptParams.size = 28;
        promptParams.color = new Color(0.10f, 0.16f, 0.24f, 1f); 
        promptParams.shadowOffsetX = 1;
        promptParams.shadowOffsetY = -1;
        promptParams.shadowColor = new Color(1f, 1f, 1f, 0.35f);
        promptFont = generator.generateFont(promptParams);

        FreeTypeFontParameter buttonParams = new FreeTypeFontParameter();
        buttonParams.size = 22;
        buttonParams.color = new Color(0.10f, 0.16f, 0.24f, 1f);
        buttonFont = generator.generateFont(buttonParams);

        generator.dispose();

        float cx = Settings.WINDOW_WIDTH / 2f;
        float cy = Settings.WINDOW_HEIGHT / 2f;
        boolean lastLevel = isLastLevel();
        int nextLevel = lastLevel ? 1 : currentLevel + 1;
        String continueLabel = lastLevel ? "PLAY AGAIN" : "CONTINUE";
        String spaceHint = lastLevel ? "SPACE - Play Again" : "SPACE - Continue";
        
        background = new Background(Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
        brightnessOverlay = (BrightnessOverlay) context.entities().createEntity(() -> new BrightnessOverlay(Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT));
        
        // --- Refactored to TextDisplay with center = true ---
        title = new TextDisplay("LEVEL " + currentLevel + " COMPLETE!", cx, cy + 120f, titleFont, true);
        promptStatus = new TextDisplay("Next up: Level " + nextLevel, cx, cy + 55f, promptFont, true);
        hintSpace = new TextDisplay(spaceHint, cx, cy - 175f, promptFont, true);
        hintEsc = new TextDisplay("ESC - Main Menu", cx, cy - 220f, promptFont, true);
        
        btnContinue = MenuButton.withTexture(continueLabel, cx, cy - 10f, buttonFont, BTN_ASSET, HOVER_ASSET);
        btnMainMenu = MenuButton.withTexture("MAIN MENU", cx, cy - 85f, buttonFont, BTN_ASSET, HOVER_ASSET);

        inputCooldown = INPUT_COOLDOWN_SECONDS;
    }

    @Override
    public void onExit(SceneContext context) {
        if (btnContinue != null) btnContinue.dispose();
        if (btnMainMenu != null) btnMainMenu.dispose();
        if (titleFont != null) titleFont.dispose();
        if (promptFont != null) promptFont.dispose();
        if (buttonFont != null) buttonFont.dispose();
    }

    @Override
    public void update(float deltaTime, SceneContext context) {
        if (inputCooldown > 0f) {
            inputCooldown -= deltaTime;
            return;
        }

        btnContinue.updateInput();
        btnMainMenu.updateInput();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || btnMainMenu.isClicked()) {
            btnMainMenu.resetClick();
            context.changeScene("menu");
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || btnContinue.isClicked()) {
            btnContinue.resetClick();
            if (isLastLevel()) {
                currentLevel = 1;
            } else {
                currentLevel++;
            }
            context.changeScene("level-complete");
        }
    }

    @Override
    public void submitRenderable(SceneContext context) {
        context.renderQueue().queue(background);
        context.renderQueue().queue(brightnessOverlay);
        context.renderQueue().queue(title);
        context.renderQueue().queue(promptStatus);
        context.renderQueue().queue(btnContinue);
        context.renderQueue().queue(btnMainMenu);
        context.renderQueue().queue(hintSpace);
        context.renderQueue().queue(hintEsc);
    }

    private boolean isLastLevel() {
        return currentLevel >= MAX_LEVEL;
    }

    /* Not deleting, but Background uses Background.java class instead
    private static class Background implements IRenderItem {
        private final Transform2D transform;
        private final String assetPath;

        Background(String assetPath) {
            this.assetPath = assetPath;
            this.transform = new Transform2D(0, 0, Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
        }

        @Override public String getAssetPath() { return assetPath; }
        @Override public ITransform getTransform() { return transform; }
    }
    */
}