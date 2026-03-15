package com.p1_7.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.p1_7.abstractengine.entity.Entity;
import com.p1_7.abstractengine.input.ActionId;
import com.p1_7.abstractengine.input.InputManager;
import com.p1_7.abstractengine.input.InputMapping;
import com.p1_7.abstractengine.render.IRenderItem;
import com.p1_7.abstractengine.scene.Scene;
import com.p1_7.abstractengine.scene.SceneContext;
import com.p1_7.abstractengine.transform.ITransform;
import com.p1_7.game.Settings;
import com.p1_7.game.display.DropdownMenu;
import com.p1_7.game.display.HoverableTextDisplay;
import com.p1_7.game.display.Slider;
import com.p1_7.game.display.TextDisplay;
import com.p1_7.game.display.Background;
import com.p1_7.game.entities.MenuButton;
import com.p1_7.game.entities.MousePointer;
import com.p1_7.game.input.MappableActions;
import com.p1_7.game.display.BrightnessOverlay;

import java.util.ArrayList;
import java.util.List;

public class SettingsScene extends Scene {

    private final InputManager inputManager;

    // Scrolling Math
    private float scrollOffset = 0f;
    private float targetScroll = 0f;
    private final float maxScroll = 800f; // Increased to accommodate the dropdown

    // Background & Colliders
    private Background background;
    private BrightnessOverlay brightnessOverlay;
    private MousePointer mousePointer;

    // UI Entities
    private final List<Entity> uiEntities = new ArrayList<>();
    
    // Direct references
    private Slider volSlider;
    private Slider brightSlider;
    private DropdownMenu resolutionDropdown;
    private MenuButton btnReturn;
    
    // Fonts (Owned by this scene)
    private BitmapFont titleFont;
    private BitmapFont labelFont;
    private BitmapFont buttonFont;

    // Remapping State
    private final List<RemapSlot> remapSlots = new ArrayList<>();
    private boolean isListeningForRemap = false;
    private ActionId actionToRemap = null;
    private HoverableTextDisplay activeRemapDisplay = null;
    private RemapSlot activeRemapSlot = null; // Tracks the specific slot being remapped

    private class RemapSlot {
        ActionId action;
        int currentKey; 
        HoverableTextDisplay display;

        RemapSlot(ActionId action, int currentKey, HoverableTextDisplay display) {
            this.action = action;
            this.currentKey = currentKey;
            this.display = display;
        }
    }

    public SettingsScene(InputManager inputManager) {
        this.name = "settings";
        this.inputManager = inputManager;
    }

    @Override
    public void onEnter(SceneContext context) {
        uiEntities.clear();
        remapSlots.clear();
        scrollOffset = 0f;
        targetScroll = 0f;

        // --- 1. FONTS GENERATION ---
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("menu/Kenney_Future.ttf"));

        FreeTypeFontParameter titleParams = new FreeTypeFontParameter();
        titleParams.size = 32;
        titleParams.color = new Color(1f, 0.92f, 0.55f, 1f); // Gold Headers
        titleParams.shadowOffsetX = 2;
        titleParams.shadowOffsetY = -2;
        titleParams.shadowColor = new Color(0f, 0f, 0f, 0.5f);
        titleFont = generator.generateFont(titleParams);

        FreeTypeFontParameter labelParams = new FreeTypeFontParameter();
        labelParams.size = 22;
        labelParams.color = Color.WHITE; // White Standard Text
        labelFont = generator.generateFont(labelParams);

        FreeTypeFontParameter btnParams = new FreeTypeFontParameter();
        btnParams.size = 26;
        btnParams.color = new Color(0.10f, 0.16f, 0.24f, 1f); // Navy button text
        buttonFont = generator.generateFont(btnParams);

        generator.dispose();

        // --- 2. ENTITIES ---
        background = new Background("menu/background.png", Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
        brightnessOverlay = new BrightnessOverlay(Settings.WINDOW_WIDTH, Settings.WINDOW_HEIGHT);
        mousePointer = (MousePointer) context.entities().createEntity(() -> new MousePointer());

        // Audio (Shifted up)
        uiEntities.add(context.entities().createEntity(() -> new TextDisplay("AUDIO VOLUME", 100, 650, titleFont)));
        volSlider = (Slider) context.entities().createEntity(() -> new Slider(100.0f, 600.0f, 400.0f, 30.0f, Settings.VOLUME_LEVEL));
        uiEntities.add(volSlider);

        // Video (Shifted up)
        uiEntities.add(context.entities().createEntity(() -> new TextDisplay("BRIGHTNESS LEVEL", 100, 500, titleFont)));
        brightSlider = (Slider) context.entities().createEntity(() -> new Slider(100, 450, 400, 30, Settings.BRIGHTNESS_LEVEL));
        uiEntities.add(brightSlider);

        // Resolution Dropdown (RESTORED)
        uiEntities.add(context.entities().createEntity(() -> new TextDisplay("SCREEN BOUNDS", 100, 350, titleFont)));
        String[] resOptions = {"800 x 600", "1280 x 720", "1600 x 900", "1920 x 1080"};
        resolutionDropdown = (DropdownMenu) context.entities().createEntity(() -> 
            new DropdownMenu(100, 275, 400, 40, labelFont, resOptions, Settings.currentResolutionIndex));
        uiEntities.add(resolutionDropdown);

        // Remapping (Shifted down)
        uiEntities.add(context.entities().createEntity(() -> new TextDisplay("KEY MAPPINGS", 100, 200, titleFont)));
        uiEntities.add(context.entities().createEntity(() -> new TextDisplay("ACTION", 100, 150, labelFont)));
        uiEntities.add(context.entities().createEntity(() -> new TextDisplay("PRIMARY", 350, 150, labelFont)));
        uiEntities.add(context.entities().createEntity(() -> new TextDisplay("ALTERNATE", 550, 150, labelFont)));

        addRemapRow(context, "Move Up", MappableActions.UP, 100);
        addRemapRow(context, "Move Down", MappableActions.DOWN, 50);
        addRemapRow(context, "Move Left", MappableActions.LEFT, 0);
        addRemapRow(context, "Move Right", MappableActions.RIGHT, -50);

        // Return Button (Bottom Right)
        btnReturn = MenuButton.withTexture("RETURN", Settings.WINDOW_WIDTH - 200f, 60f, buttonFont, "menu/button.png", "menu/button_hover.png");
    }

    private void addRemapRow(SceneContext context, String label, ActionId action, float y) {
        uiEntities.add(context.entities().createEntity(() -> new TextDisplay(label, 100, y, labelFont)));

        List<Integer> keys = inputManager.getInputMapping().getKeysForAction(action);
        int primaryKey = keys.size() > 0 ? keys.get(0) : -1;
        int altKey = keys.size() > 1 ? keys.get(1) : -1;

        HoverableTextDisplay primaryDisplay = (HoverableTextDisplay) context.entities().createEntity(() -> 
            new HoverableTextDisplay(getKeyString(primaryKey), 350, y, labelFont));
        uiEntities.add(primaryDisplay);
        remapSlots.add(new RemapSlot(action, primaryKey, primaryDisplay));

        HoverableTextDisplay altDisplay = (HoverableTextDisplay) context.entities().createEntity(() -> 
            new HoverableTextDisplay(getKeyString(altKey), 550, y, labelFont));
        uiEntities.add(altDisplay);
        remapSlots.add(new RemapSlot(action, altKey, altDisplay));
    }

    private String getKeyString(int keycode) {
        if (keycode == -1) return "[ None ]";
        return "[ " + Input.Keys.toString(keycode) + " ]";
    }

    @Override
    public void update(float deltaTime, SceneContext context) {
        
        // Handle Return Button Input First
        btnReturn.updateInput();
        if (btnReturn.isClicked() || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            btnReturn.resetClick();
            // Pops the current scene to de-load Settings and "resume" the suspended scene below it
            context.popScene();
            return;
        }

        // --- 1. DROPDOWN LOGIC ---
        if (resolutionDropdown != null) {
            resolutionDropdown.updateInput();
            if (resolutionDropdown.isClicked()) {
                resolutionDropdown.resetClick();
                Settings.setResolution(resolutionDropdown.getSelectedIndex());
                
                // Dynamically resize background to prevent artifacting
                background.getTransform().setSize(0, Settings.WINDOW_WIDTH);
                background.getTransform().setSize(1, Settings.WINDOW_HEIGHT);
                
                // Anchor the Return button dynamically so it stays anchored to the bottom right
                btnReturn.getTransform().setPosition(0, Settings.WINDOW_WIDTH - 200f - (MenuButton.BUTTON_WIDTH / 2f));
            }
        }

        // Protect UI underneath from receiving accidental clicks while the Dropdown is expanded
        boolean blockUI = resolutionDropdown != null && resolutionDropdown.blocksUI();

        // --- 2. REMAPPING LOGIC ---
        if (isListeningForRemap && !blockUI) {
            for (int i = 0; i < 256; i++) {
                if (Gdx.input.isKeyJustPressed(i)) {
                    if (i == Input.Keys.ESCAPE) {
                        isListeningForRemap = false;
                        activeRemapDisplay.setText(getKeyString(activeRemapSlot.currentKey));
                        actionToRemap = null;
                        activeRemapSlot = null;
                        return;
                    }
                    InputMapping mapping = inputManager.getInputMapping();
                    
                    // Unbind ONLY the specific key that was previously in this slot
                    if (activeRemapSlot.currentKey != -1) {
                        mapping.unbindKey(activeRemapSlot.currentKey);
                    }
                    
                    mapping.bindKey(i, actionToRemap);
                    activeRemapSlot.currentKey = i; // update the slot to remember the newly mapped key
                    
                    activeRemapDisplay.setText(getKeyString(i));
                    isListeningForRemap = false;
                    actionToRemap = null;
                    activeRemapSlot = null;
                    return;
                }
            }
            return; 
        }

        // --- 3. SCROLLING MATH ---
        if (context.input().isActionActive(MappableActions.UP)) targetScroll -= 400f * deltaTime;
        if (context.input().isActionActive(MappableActions.DOWN)) targetScroll += 400f * deltaTime;
        
        // Mouse Wheel Check
        if (context.input().getActionState(MappableActions.SCROLL_UP) == com.p1_7.abstractengine.input.InputState.PRESSED) targetScroll -= 60f;
        if (context.input().getActionState(MappableActions.SCROLL_DOWN) == com.p1_7.abstractengine.input.InputState.PRESSED) targetScroll += 60f;
        
        targetScroll = Math.max(0f, Math.min(targetScroll, maxScroll));
        float prev = scrollOffset;
        scrollOffset += (targetScroll - scrollOffset) * 10f * deltaTime;
        float deltaScroll = scrollOffset - prev;

        if (Math.abs(deltaScroll) > 0.01f) {
            for (Entity entity : uiEntities) {
                if (entity instanceof IRenderItem) {
                    ITransform t = ((IRenderItem) entity).getTransform();
                    t.setPosition(1, t.getPosition(1) + deltaScroll);
                }
            }
        }

        // --- 4. MOUSE COLLISION ---
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY(); 
        mousePointer.updatePosition(mouseX, mouseY);

        if (!blockUI) {
            for (RemapSlot slot : remapSlots) {
                if (mousePointer.getBounds().overlaps(slot.display.getBounds())) {
                    slot.display.onCollision(mousePointer); 
                }
            }

            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                if (mousePointer.getBounds().overlaps(volSlider.getBounds())) {
                    float posX = volSlider.getTransform().getPosition(0);
                    volSlider.setValue((mouseX - posX) / volSlider.getTransform().getSize(0));
                    Settings.VOLUME_LEVEL = volSlider.getValue(); 
                }
                if (mousePointer.getBounds().overlaps(brightSlider.getBounds())) {
                    float posX = brightSlider.getTransform().getPosition(0);
                    brightSlider.setValue((mouseX - posX) / brightSlider.getTransform().getSize(0));
                    Settings.BRIGHTNESS_LEVEL = brightSlider.getValue();
                }
            }
            
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                for (RemapSlot slot : remapSlots) {
                    if (mousePointer.getBounds().overlaps(slot.display.getBounds())) {
                        isListeningForRemap = true;
                        actionToRemap = slot.action;
                        activeRemapDisplay = slot.display;
                        activeRemapSlot = slot; 
                        slot.display.setText("[ Press Key ]");
                    }
                }
            }
        }
    }

    @Override
    public void submitRenderable(SceneContext context) {
        if (background != null) context.renderQueue().queue(background);
        
        if (brightnessOverlay != null) context.renderQueue().queue(brightnessOverlay);

        for (Entity entity : uiEntities) {
            if (entity.isActive() && entity instanceof IRenderItem && entity != resolutionDropdown) {
                context.renderQueue().queue((IRenderItem) entity);
            }
        }

        // Draw the dropdown LAST so its expanded list guarantees overlay priority
        if (resolutionDropdown != null && resolutionDropdown.isActive()) {
            context.renderQueue().queue(resolutionDropdown);
        }
        
        if (btnReturn != null) context.renderQueue().queue(btnReturn);
    }

    @Override
    public void onExit(SceneContext context) {
        context.entities().removeEntity(mousePointer.getId());
        for (Entity entity : uiEntities) {
            context.entities().removeEntity(entity.getId());
        }
        uiEntities.clear();
        remapSlots.clear();

        if (btnReturn != null) btnReturn.dispose();
        if (titleFont != null) titleFont.dispose();
        if (labelFont != null) labelFont.dispose();
        if (buttonFont != null) buttonFont.dispose();
    }
}