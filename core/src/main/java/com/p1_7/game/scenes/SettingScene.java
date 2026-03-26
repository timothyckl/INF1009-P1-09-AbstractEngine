package com.p1_7.game.scenes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.p1_7.abstractengine.input.IInputExtensionRegistry;
import com.p1_7.abstractengine.input.IInputManager;
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
import com.p1_7.game.managers.IAudioManager;
import com.p1_7.game.managers.IFontManager;
import com.p1_7.game.platform.GdxDrawContext;
import com.p1_7.game.ui.BackgroundImage;
import com.p1_7.game.ui.BrightnessOverlay;
import com.p1_7.game.ui.BrightnessSlider;
import com.p1_7.game.ui.MenuButton;
import com.p1_7.game.ui.RemapSlot;
import com.p1_7.game.ui.Text;
import com.p1_7.game.ui.VolumeSlider;

/**
 * lets the player adjust volume and brightness levels, and remap primary and alternate
 * key bindings for each game action.
 *
 * this version uses a themed popup panel as the actual layout container.
 */
public class SettingScene extends Scene {

    private static final String BG_ASSET = "background.png";
    private static final String POPUP_ASSET = "settings_popup.png";
    private static final String BTN_ASSET = "menu/button.png";
    private static final String HOVER_ASSET = "menu/button_hover.png";

    private static final Color FOCUS_OVERLAY_COLOUR = new Color(0f, 0f, 0f, 0.58f);

    private static final float POPUP_WIDTH = 1120f;
    private static final float POPUP_HEIGHT = 760f;

    private static final float BACK_BUTTON_Y = 72f;

    private static final float TOP_BOX_INSET_X = 285f;
    private static final float TOP_BOX_INSET_Y = 190f;
    private static final float TOP_BOX_WIDTH = 270f;
    private static final float TOP_BOX_HEIGHT = 150f;
    private static final float TOP_BOX_GAP = 30f;

    private static final float SLIDER_WIDTH = 180f;

    private static final float BOTTOM_BOX_INSET_X = 72f;
    private static final float BOTTOM_BOX_TOP_INSET = 450f;

    private static final float CONTROLS_HEADING_Y_OFFSET = 36f;
    private static final float TABLE_HEADER_Y_OFFSET = 6f;
    private static final float FIRST_ROW_Y_OFFSET = -34f;
    private static final float ROW_SPACING = 34f;
    private static final float REMAP_HINT_Y_OFFSET = -170f;

    private float centreX;
    private float centreY;
    private float popupX;
    private float popupY;
    private float popupCenterX;
    private float popupTopY;

    private BitmapFont headingFont;
    private BitmapFont labelFont;
    private BitmapFont tableFont;
    private BitmapFont buttonFont;

    private IInputManager inputManager;
    private InputProcessor previousInputProcessor;

    private final InputProcessor remapInputProcessor = new InputAdapter() {
        @Override
        public boolean keyDown(int keycode) {
            if (!isListeningForRemap()) {
                return false;
            }
            if (keycode == Input.Keys.ESCAPE) {
                cancelActiveRemap();
            } else {
                applyActiveRemap(keycode);
            }
            return true;
        }
    };

    private BackgroundImage background;
    private IRenderable focusOverlay;
    private IRenderable popupRenderable;

    private Text volumeLabel;
    private VolumeSlider volumeSlider;
    private Text brightnessLabel;
    private BrightnessSlider brightnessSlider;
    private Text controlsHeading;
    private Text remapHint;
    private Text actionHeader;
    private Text primaryHeader;
    private Text alternateHeader;
    private MenuButton backButton;
    private BrightnessOverlay brightnessOverlay;

    private final List<RemapSlot> remapSlots = new ArrayList<>();
    private RemapSlot activeRemapSlot;
    private RemapSlot.BindingColumn activeRemapColumn;

    private String returnScene;

    public SettingScene() {
        this.name = "settings";
    }

    @Override
    public void onEnter(SceneContext context) {
        returnScene = context.getSuspendedSceneKey() != null ? "pause" : "menu";
        computeSceneGeometry();
        resolveSceneServices(context);

        IFontManager fontManager = context.get(IFontManager.class);
        IAudioManager audio = context.get(IAudioManager.class);

        createFonts(fontManager);
        createSceneComponents(audio);
        syncRemapBindings();
    }

    @Override
    public void onExit(SceneContext context) {
        returnScene = null;
        stopListening();
        clearRemapState();
        disposeSceneComponents();
        disposeFonts();
        clearResolvedServices();
    }

    @Override
    public void update(float deltaTime, SceneContext context) {
        if (isListeningForRemap()) {
            return;
        }

        if (handleSceneExit(context)) {
            return;
        }

        IInputExtensionRegistry inputRegistry = context.get(IInputExtensionRegistry.class);
        ICursorSource cursorSource = inputRegistry.hasExtension(ICursorSource.class)
            ? inputRegistry.getExtension(ICursorSource.class)
            : null;

        if (cursorSource == null) {
            return;
        }

        IInputQuery inputQuery = context.get(IInputQuery.class);
        IAudioManager audio = context.get(IAudioManager.class);

        updateSliderInputs(cursorSource, inputQuery);
        updateRemapInput(cursorSource, inputQuery);
        updateBackButtonInput(cursorSource, inputQuery);
        applySliderChanges(audio);
        handleBackButtonClick(context);
    }

    @Override
    public void submitRenderable(IRenderQueue renderQueue) {
        renderQueue.queue(background);
        renderQueue.queue(focusOverlay);
        renderQueue.queue(popupRenderable);

        renderQueue.queue(volumeLabel);
        renderQueue.queue(volumeSlider);
        renderQueue.queue(brightnessLabel);
        renderQueue.queue(brightnessSlider);
        renderQueue.queue(controlsHeading);

        renderQueue.queue(actionHeader);
        renderQueue.queue(primaryHeader);
        renderQueue.queue(alternateHeader);

        for (int i = 0; i < remapSlots.size(); i++) {
            renderQueue.queue(remapSlots.get(i));
        }

        renderQueue.queue(remapHint);
        renderQueue.queue(backButton);
        renderQueue.queue(brightnessOverlay);
    }

    private void computeSceneGeometry() {
        centreX = Settings.getWindowWidth() / 2f;
        centreY = Settings.getWindowHeight() / 2f;

        popupX = centreX - POPUP_WIDTH / 2f;
        popupY = centreY - POPUP_HEIGHT / 2f + 10f;
        popupCenterX = centreX;
        popupTopY = popupY + POPUP_HEIGHT;
    }

    private void resolveSceneServices(SceneContext context) {
        inputManager = context.get(IInputManager.class);
    }

    private void createFonts(IFontManager fontManager) {
        headingFont = fontManager.getGoldDisplayFont(52);
        labelFont = fontManager.getDarkTextFont(22);
        tableFont = fontManager.getDarkTextFont(18);
        buttonFont = fontManager.getDarkTextFont(26);
    }

    private void createSceneComponents(IAudioManager audio) {
        background = new BackgroundImage(BG_ASSET);
        focusOverlay = createFocusOverlay();
        popupRenderable = createPopupRenderable();

        float leftBoxCenterX = popupX + TOP_BOX_INSET_X + TOP_BOX_WIDTH / 2f;
        float rightBoxCenterX = leftBoxCenterX + TOP_BOX_WIDTH + TOP_BOX_GAP;

        float topBoxLabelY = popupTopY - TOP_BOX_INSET_Y - 28f;
        float topBoxSliderY = topBoxLabelY - 42f;

        volumeLabel = new Text(volumeText(audio), leftBoxCenterX, topBoxLabelY, labelFont);
        volumeSlider = new VolumeSlider(leftBoxCenterX, topBoxSliderY, SLIDER_WIDTH, audio.getMusicVolume());

        brightnessLabel = new Text(brightnessText(), rightBoxCenterX, topBoxLabelY, labelFont);
        brightnessSlider = new BrightnessSlider(
            rightBoxCenterX,
            topBoxSliderY,
            SLIDER_WIDTH,
            Settings.getBrightnessLevel()
        );

        float bottomCenterX = popupCenterX;
        float controlsY = popupTopY - BOTTOM_BOX_TOP_INSET + CONTROLS_HEADING_Y_OFFSET;
        float tableHeaderY = popupTopY - BOTTOM_BOX_TOP_INSET + TABLE_HEADER_Y_OFFSET;
        float firstRowY = popupTopY - BOTTOM_BOX_TOP_INSET + FIRST_ROW_Y_OFFSET;
        float hintY = popupTopY - BOTTOM_BOX_TOP_INSET + REMAP_HINT_Y_OFFSET;

        controlsHeading = new Text("CONTROLS", bottomCenterX, controlsY, buttonFont);
        remapHint = new Text(idleRemapHintText(), bottomCenterX, hintY, tableFont);

        createRemapHeaders(tableHeaderY);
        buildRemapSlots(firstRowY, ROW_SPACING);

        backButton = MenuButton.withTexture("BACK", centreX, BACK_BUTTON_Y, buttonFont, BTN_ASSET, HOVER_ASSET);
        brightnessOverlay = new BrightnessOverlay();
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
                ((GdxDrawContext) ctx).drawTexture(
                    POPUP_ASSET,
                    transform.getPosition(0),
                    transform.getPosition(1),
                    transform.getSize(0),
                    transform.getSize(1)
                );
            }
        };
    }

    private void createRemapHeaders(float tableHeaderY) {
        float tableLeft = centreX - RemapSlot.TABLE_WIDTH / 2f;

        actionHeader = new Text(
            "ACTION",
            tableLeft + RemapSlot.ACTION_COLUMN_WIDTH / 2f,
            tableHeaderY,
            tableFont
        );

        primaryHeader = new Text(
            "PRIMARY",
            tableLeft + RemapSlot.ACTION_COLUMN_WIDTH + RemapSlot.CELL_GAP
                + RemapSlot.KEY_COLUMN_WIDTH / 2f,
            tableHeaderY,
            tableFont
        );

        alternateHeader = new Text(
            "ALTERNATE",
            tableLeft + RemapSlot.ACTION_COLUMN_WIDTH + RemapSlot.CELL_GAP * 2f
                + RemapSlot.KEY_COLUMN_WIDTH * 1.5f,
            tableHeaderY,
            tableFont
        );
    }

    private void buildRemapSlots(float firstRowY, float rowSpacing) {
        remapSlots.clear();
        List<GameActions.BindingSpec> bindings = GameActions.getMovementBindings();

        float rowY = firstRowY;
        for (int i = 0; i < bindings.size(); i++) {
            GameActions.BindingSpec binding = bindings.get(i);
            remapSlots.add(createRemapSlot(binding, rowY));
            rowY -= rowSpacing;
        }
    }

    private RemapSlot createRemapSlot(GameActions.BindingSpec binding, float rowCentreY) {
        List<Integer> keys = new ArrayList<>(inputManager.getKeysForAction(binding.getActionId()));
        Collections.sort(keys);

        boolean hasPrimaryDefault = keys.remove(Integer.valueOf(binding.getPrimaryKeyCode()));
        boolean hasAlternateDefault = keys.remove(Integer.valueOf(binding.getAlternateKeyCode()));

        int primaryKeyCode = hasPrimaryDefault
            ? binding.getPrimaryKeyCode()
            : takeFirstDistinctKey(keys, binding.getAlternateKeyCode(), binding.getPrimaryKeyCode());

        int alternateKeyCode = hasAlternateDefault
            ? binding.getAlternateKeyCode()
            : takeFirstDistinctKey(keys, primaryKeyCode, binding.getAlternateKeyCode());

        return new RemapSlot(
            binding.getLabel(),
            binding.getActionId(),
            primaryKeyCode,
            alternateKeyCode,
            centreX,
            rowCentreY,
            tableFont
        );
    }

    private boolean handleSceneExit(SceneContext context) {
        IInputQuery inputQuery = context.get(IInputQuery.class);
        if (inputQuery.getActionState(GameActions.MENU_BACK) == InputState.PRESSED) {
            navigateBack(context);
            return true;
        }
        return false;
    }

    private void navigateBack(SceneContext context) {
        context.changeScene(returnScene);
    }

    private void updateSliderInputs(ICursorSource cursorSource, IInputQuery inputQuery) {
        volumeSlider.updateInput(cursorSource, inputQuery);
        brightnessSlider.updateInput(cursorSource, inputQuery);
    }

    private void updateBackButtonInput(ICursorSource cursorSource, IInputQuery inputQuery) {
        backButton.updateInput(cursorSource, inputQuery);
    }

    private void applySliderChanges(IAudioManager audio) {
        if (volumeSlider.hasMoved()) {
            audio.setMusicVolume(volumeSlider.getValue());
            volumeLabel.setText(volumeText(audio));
            volumeSlider.resetMoved();
        }

        if (brightnessSlider.hasMoved()) {
            Settings.setBrightnessLevel(brightnessSlider.getValue());
            brightnessLabel.setText(brightnessText());
            brightnessSlider.resetMoved();
        }
    }

    private void handleBackButtonClick(SceneContext context) {
        if (backButton.isClicked()) {
            backButton.resetClick();
            navigateBack(context);
        }
    }

    private String volumeText(IAudioManager audio) {
        return "Music Volume: " + Math.round(audio.getMusicVolume() * 100) + "%";
    }

    private String brightnessText() {
        return "Brightness: " + Math.round(Settings.getBrightnessLevel() * 100) + "%";
    }

    private String idleRemapHintText() {
        return "Click a binding to remap it";
    }

    private void updateRemapInput(ICursorSource cursorSource, IInputQuery inputQuery) {
        float mx = cursorSource.getCursorX();
        float my = cursorSource.getCursorY();
        boolean clickStarted =
            inputQuery.getActionState(GameActions.POINTER_PRIMARY) == InputState.PRESSED;

        for (int i = 0; i < remapSlots.size(); i++) {
            RemapSlot slot = remapSlots.get(i);
            RemapSlot.BindingColumn hitColumn = slot.hitTest(mx, my);
            slot.setHoveredColumn(hitColumn);
            slot.setActiveColumn(activeRemapSlot == slot ? activeRemapColumn : null);

            if (clickStarted && hitColumn != null) {
                startListening(slot, hitColumn);
                return;
            }
        }
    }

    private void startListening(RemapSlot slot, RemapSlot.BindingColumn column) {
        activeRemapSlot = slot;
        activeRemapColumn = column;
        remapHint.setText("Press a key for " + slot.getLabel() + " (" + column.getLabel() + ")");
        previousInputProcessor = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(remapInputProcessor);
        refreshRemapVisualState();
    }

    private boolean isListeningForRemap() {
        return activeRemapSlot != null && activeRemapColumn != null;
    }

    private void cancelActiveRemap() {
        stopListening();
    }

    private void applyActiveRemap(int keyCode) {
        if (!isListeningForRemap()) {
            return;
        }

        String reservedUiKeyMessage = getReservedUiKeyMessage(keyCode);
        if (reservedUiKeyMessage != null) {
            remapHint.setText(reservedUiKeyMessage);
            refreshRemapVisualState();
            return;
        }

        int previousKeyCode = activeRemapSlot.getKeyCode(activeRemapColumn);
        int siblingKeyCode = activeRemapSlot.getOtherKeyCode(activeRemapColumn);

        if (keyCode == previousKeyCode || keyCode == siblingKeyCode) {
            stopListening();
            return;
        }

        RemapSlot ownerSlot = null;
        RemapSlot.BindingColumn ownerColumn = null;
        for (int i = 0; i < remapSlots.size(); i++) {
            RemapSlot slot = remapSlots.get(i);
            RemapSlot.BindingColumn column = slot.findColumnForKey(keyCode);
            if (column != null) {
                ownerSlot = slot;
                ownerColumn = column;
                break;
            }
        }

        if (ownerSlot != null && ownerColumn != null) {
            ownerSlot.setKeyCode(ownerColumn, previousKeyCode);
        }
        activeRemapSlot.setKeyCode(activeRemapColumn, keyCode);

        syncRemapBindings();
        stopListening();
    }

    private String getReservedUiKeyMessage(int keyCode) {
        if (keyCode == Input.Keys.SPACE) {
            return Input.Keys.toString(keyCode) + " is reserved for menu confirm";
        }
        if (keyCode == Input.Keys.ESCAPE || keyCode == Input.Keys.BACKSPACE) {
            return Input.Keys.toString(keyCode) + " is reserved for menu back";
        }
        return null;
    }

    private void syncRemapBindings() {
        for (int i = 0; i < remapSlots.size(); i++) {
            inputManager.unbindAction(remapSlots.get(i).getActionId());
        }
        for (int i = 0; i < remapSlots.size(); i++) {
            RemapSlot slot = remapSlots.get(i);
            inputManager.bindKey(slot.getPrimaryKeyCode(), slot.getActionId());
            inputManager.bindKey(slot.getAlternateKeyCode(), slot.getActionId());
        }
    }

    private void stopListening() {
        if (Gdx.input.getInputProcessor() == remapInputProcessor) {
            Gdx.input.setInputProcessor(previousInputProcessor);
        }
        previousInputProcessor = null;
        activeRemapSlot = null;
        activeRemapColumn = null;

        if (remapHint != null) {
            remapHint.setText(idleRemapHintText());
        }
        refreshRemapVisualState();
    }

    private void refreshRemapVisualState() {
        for (int i = 0; i < remapSlots.size(); i++) {
            RemapSlot slot = remapSlots.get(i);
            slot.setHoveredColumn(null);
            slot.setActiveColumn(activeRemapSlot == slot ? activeRemapColumn : null);
        }
    }

    private int takeFirstDistinctKey(List<Integer> keys, int disallowedKeyCode, int fallbackKeyCode) {
        for (int i = 0; i < keys.size(); i++) {
            int keyCode = keys.get(i);
            if (keyCode != disallowedKeyCode) {
                keys.remove(i);
                return keyCode;
            }
        }
        return fallbackKeyCode;
    }

    private void clearRemapState() {
        remapSlots.clear();
        activeRemapSlot = null;
        activeRemapColumn = null;
    }

    private void disposeSceneComponents() {
        if (backButton != null) {
            backButton.dispose();
        }
        background = null;
        focusOverlay = null;
        popupRenderable = null;
        volumeLabel = null;
        volumeSlider = null;
        brightnessLabel = null;
        brightnessSlider = null;
        controlsHeading = null;
        remapHint = null;
        actionHeader = null;
        primaryHeader = null;
        alternateHeader = null;
        backButton = null;
        brightnessOverlay = null;
    }

    private void disposeFonts() {
        headingFont = null;
        labelFont = null;
        tableFont = null;
        buttonFont = null;
    }

    private void clearResolvedServices() {
        inputManager = null;
        previousInputProcessor = null;
    }
}