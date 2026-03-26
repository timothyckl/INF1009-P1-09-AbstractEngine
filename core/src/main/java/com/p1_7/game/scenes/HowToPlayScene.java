package com.p1_7.game.scenes;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
import com.p1_7.game.managers.IAudioManager;
import com.p1_7.game.managers.IFontManager;
import com.p1_7.game.platform.GdxDrawContext;
import com.p1_7.game.ui.BackgroundImage;
import com.p1_7.game.ui.BrightnessOverlay;
import com.p1_7.game.ui.MenuButton;
import com.p1_7.game.ui.Text;

/**
 * Guide scene that explains the core game loop and controls.
 * Uses a themed popup panel over a dimmed background.
 */
public final class HowToPlayScene extends Scene {

    private static final String BG_ASSET = "background.png";
    private static final String POPUP_ASSET = "howtoplay_popup.png";
    private static final String BTN_ASSET = "menu/button.png";
    private static final String HOVER_ASSET = "menu/button_hover.png";
    private static final String PLAYER_ASSET = "player.png";
    private static final String GOBLIN_ASSET = "goblin-walk.png";
    private static final String SKELETON_ASSET = "skeleton-walk.png";
    private static final String HEART_ASSET = "Heart.png";

    private static final int STRIP_FRAME_W = 96;
    private static final int STRIP_FRAME_H = 64;
    private static final int HEART_FRAME_SIZE = 16;
    private static final int HEART_FULL_FRAME = 4;

    private static final Color FOCUS_OVERLAY_COLOUR = new Color(0f, 0f, 0f, 0.56f);

    private static final float POPUP_WIDTH = 1650f;
    private static final float POPUP_HEIGHT = 980f;

    private static final float TITLE_Y_OFFSET_FROM_TOP = 72f;
    private static final float SUBTITLE_Y_OFFSET = 240f;

    private static final float CONTENT_TOP_PADDING = 255f;
    private static final float CONTENT_LEFT_PADDING = 470f;
    private static final float CONTENT_RIGHT_PADDING = 88f;
    private static final float CONTENT_BOTTOM_PADDING = 80f;

    private static final float LEFT_COL_WIDTH = 500f;
    private static final float RIGHT_COL_OFFSET = 610f;

    private static final float SECTION_GAP = 44f;
    private static final float BODY_LINE_GAP = 24f;
    private static final float LEGEND_FIRST_ROW_GAP = 40f;
    private static final float LEGEND_ROW_GAP = 84f;

    private static final float LEGEND_SPRITE_W = 142f;
    private static final float LEGEND_SPRITE_H = 112f;
    private static final float LEGEND_HEART_SIZE = 28f;

    private static final float BACK_BUTTON_CENTRE_Y = 70f;

    private BitmapFont headingFont;
    private BitmapFont subtitleFont;
    private BitmapFont sectionFont;
    private BitmapFont bodyFont;
    private BitmapFont buttonFont;

    private BackgroundImage background;
    private IRenderable focusOverlay;
    private IRenderable popupRenderable;

    private Text heading;
    private Text subtitle;
    private final List<IRenderable> ornaments = new ArrayList<>();
    private final List<IRenderable> guideText = new ArrayList<>();
    private MenuButton backButton;
    private BrightnessOverlay brightnessOverlay;

    private float centreX;
    private float centreY;
    private float popupX;
    private float popupY;
    private float popupTopY;

    public HowToPlayScene() {
        this.name = "how-to-play";
    }

    @Override
    public void onEnter(SceneContext context) {
        context.get(IAudioManager.class).playMusic("menu", true);

        IFontManager fontManager = context.get(IFontManager.class);
        headingFont = fontManager.getGoldDisplayFont(32);
        subtitleFont = fontManager.getDarkTextFont(18);
        sectionFont = fontManager.getDarkTextFont(19);
        bodyFont = fontManager.getDarkTextFont(16);
        buttonFont = fontManager.getDarkTextFont(24);

        centreX = Settings.getWindowWidth() / 2f;
        centreY = Settings.getWindowHeight() / 2f;
        popupX = centreX - POPUP_WIDTH / 2f;
        popupY = centreY - POPUP_HEIGHT / 2f - 35f;
        popupTopY = popupY + POPUP_HEIGHT;

        background = new BackgroundImage(BG_ASSET);
        focusOverlay = createFocusOverlay();
        popupRenderable = createPopupRenderable();

        subtitle = new Text(
            "The basics before you step into the maze.",
            centreX,
            popupTopY - SUBTITLE_Y_OFFSET,
            subtitleFont
        );

        backButton = MenuButton.withTexture(
            "BACK",
            centreX,
            BACK_BUTTON_CENTRE_Y,
            buttonFont,
            BTN_ASSET,
            HOVER_ASSET
        );

        brightnessOverlay = new BrightnessOverlay();

        buildLayout();
    }

    private void buildLayout() {
        ornaments.clear();
        guideText.clear();

        float contentLeft = popupX + CONTENT_LEFT_PADDING;
        float contentRight = popupX + POPUP_WIDTH - CONTENT_RIGHT_PADDING;
        float contentTop = popupTopY - CONTENT_TOP_PADDING;

        float leftX = contentLeft;
        float rightX = contentLeft + LEFT_COL_WIDTH + 60f;
        float legendLabelX = rightX + 86f;
        float legendSpriteCentreX = rightX + 24f;

        float y = contentTop;

        addGuideLine("OBJECTIVE", leftX, y, sectionFont);
        y -= 36f;
        addGuideLine("Reach the room with the correct answer to solve the question.", leftX, y, bodyFont);
        y -= BODY_LINE_GAP;
        addGuideLine("Step into the matching answer room to advance the round.", leftX, y, bodyFont);

        y -= SECTION_GAP;
        addGuideLine("CONTROLS", leftX, y, sectionFont);
        y -= 36f;
        addGuideLine("Move with WASD or the arrow keys.", leftX, y, bodyFont);
        y -= BODY_LINE_GAP;
        addGuideLine("Press ESC during a round to pause.", leftX, y, bodyFont);

        y -= SECTION_GAP;
        addGuideLine("DANGERS", leftX, y, sectionFont);
        y -= 36f;
        addGuideLine("Goblins guard the corner rooms.", leftX, y, bodyFont);
        y -= BODY_LINE_GAP;
        addGuideLine("Skeletons roam the corridors.", leftX, y, bodyFont);
        y -= BODY_LINE_GAP;
        addGuideLine("Touching an attacking enemy costs 1 health.", leftX, y, bodyFont);

        y -= SECTION_GAP;
        addGuideLine("SURVIVE & SCORE", leftX, y, sectionFont);
        y -= 36f;
        addGuideLine("Hearts restore health when picked up.", leftX, y, bodyFont);
        y -= BODY_LINE_GAP;
        addGuideLine("Correct answers raise your score and push you to the next level.", leftX, y, bodyFont);
        y -= BODY_LINE_GAP;
        addGuideLine("Read the prompt, move fast, and choose carefully.", leftX, y, bodyFont);

        float legendY = contentTop;
        addGuideLine("MAZE LEGEND", rightX, legendY, sectionFont);

        legendY -= LEGEND_FIRST_ROW_GAP;
        addSprite(PLAYER_ASSET, 0, legendSpriteCentreX, legendY - 20f, LEGEND_SPRITE_W, LEGEND_SPRITE_H, false);
        addGuideLine("You", legendLabelX, legendY, bodyFont);

        legendY -= LEGEND_ROW_GAP;
        addSprite(GOBLIN_ASSET, 0, legendSpriteCentreX, legendY - 20f, LEGEND_SPRITE_W, LEGEND_SPRITE_H, false);
        addGuideLine("Goblin enemy", legendLabelX, legendY, bodyFont);

        legendY -= LEGEND_ROW_GAP;
        addSprite(SKELETON_ASSET, 0, legendSpriteCentreX, legendY - 20f, LEGEND_SPRITE_W, LEGEND_SPRITE_H, false);
        addGuideLine("Skeleton enemy", legendLabelX, legendY, bodyFont);

        legendY -= LEGEND_ROW_GAP;
        addHeart(legendSpriteCentreX, legendY - 4f, LEGEND_HEART_SIZE);
        addGuideLine("Health pickup", legendLabelX, legendY, bodyFont);
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

    private void addGuideLine(String text, float x, float baselineY, BitmapFont font) {
        guideText.add(new LeftAlignedText(text, x, baselineY, font));
    }

    private void addSprite(String assetPath, int frameIndex, float centreX, float centreY,
                           float width, float height, boolean flipX) {
        float x = centreX - width / 2f;
        float y = centreY - height / 2f;
        int srcX = frameIndex * STRIP_FRAME_W;
        Transform2D transform = new Transform2D(x, y, width, height);

        ornaments.add(new IRenderable() {
            @Override
            public String getAssetPath() {
                return assetPath;
            }

            @Override
            public ITransform getTransform() {
                return transform;
            }

            @Override
            public void render(IDrawContext ctx) {
                ((GdxDrawContext) ctx).drawTextureRegion(
                    assetPath,
                    srcX,
                    0,
                    STRIP_FRAME_W,
                    STRIP_FRAME_H,
                    x,
                    y,
                    width,
                    height,
                    flipX
                );
            }
        });
    }

    private void addHeart(float centreX, float centreY, float size) {
        float x = centreX - size / 2f;
        float y = centreY - size / 2f;
        int srcX = HEART_FULL_FRAME * HEART_FRAME_SIZE;
        Transform2D transform = new Transform2D(x, y, size, size);

        ornaments.add(new IRenderable() {
            @Override
            public String getAssetPath() {
                return HEART_ASSET;
            }

            @Override
            public ITransform getTransform() {
                return transform;
            }

            @Override
            public void render(IDrawContext ctx) {
                ((GdxDrawContext) ctx).drawTextureRegion(
                    HEART_ASSET,
                    srcX,
                    0,
                    HEART_FRAME_SIZE,
                    HEART_FRAME_SIZE,
                    x,
                    y,
                    size,
                    size,
                    false
                );
            }
        });
    }

    @Override
    public void onExit(SceneContext context) {
        if (backButton != null) {
            backButton.dispose();
        }

        background = null;
        focusOverlay = null;
        popupRenderable = null;
        heading = null;
        subtitle = null;
        ornaments.clear();
        guideText.clear();
        backButton = null;
        brightnessOverlay = null;

        headingFont = null;
        subtitleFont = null;
        sectionFont = null;
        bodyFont = null;
        buttonFont = null;
    }

    @Override
    public void update(float deltaTime, SceneContext context) {
        IInputQuery inputQuery = context.get(IInputQuery.class);
        if (inputQuery.getActionState(GameActions.MENU_BACK) == InputState.PRESSED) {
            context.changeScene("menu");
            return;
        }

        IInputExtensionRegistry inputRegistry = context.get(IInputExtensionRegistry.class);
        ICursorSource cursorSource = inputRegistry.hasExtension(ICursorSource.class)
            ? inputRegistry.getExtension(ICursorSource.class)
            : null;

        if (cursorSource == null) {
            return;
        }

        backButton.updateInput(cursorSource, inputQuery);
        if (backButton.isClicked()) {
            backButton.resetClick();
            context.changeScene("menu");
        }
    }

    @Override
    public void submitRenderable(IRenderQueue renderQueue) {
        renderQueue.queue(background);
        renderQueue.queue(focusOverlay);
        renderQueue.queue(popupRenderable);
        renderQueue.queue(subtitle);

        for (IRenderable ornament : ornaments) {
            renderQueue.queue(ornament);
        }

        for (IRenderable line : guideText) {
            renderQueue.queue(line);
        }

        renderQueue.queue(backButton);
        renderQueue.queue(brightnessOverlay);
    }

    private static final class LeftAlignedText implements IRenderable {
        private final String text;
        private final float x;
        private final float baselineY;
        private final BitmapFont font;
        private final GlyphLayout layout;
        private final Transform2D transform;

        private LeftAlignedText(String text, float x, float baselineY, BitmapFont font) {
            this.text = text;
            this.x = x;
            this.baselineY = baselineY;
            this.font = font;
            this.layout = new GlyphLayout(font, text);
            this.transform = new Transform2D(
                x,
                baselineY - layout.height,
                layout.width,
                layout.height
            );
        }

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
            ((GdxDrawContext) ctx).drawFont(font, text, x, baselineY);
        }
    }
}