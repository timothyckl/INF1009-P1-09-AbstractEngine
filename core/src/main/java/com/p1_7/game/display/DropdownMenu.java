package com.p1_7.game.display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.p1_7.abstractengine.entity.Entity;
import com.p1_7.abstractengine.render.ICustomRenderable;
import com.p1_7.abstractengine.render.IRenderItem;
import com.p1_7.abstractengine.render.IShapeRenderer;
import com.p1_7.abstractengine.render.ISpriteBatch;
import com.p1_7.abstractengine.transform.ITransform;
import com.p1_7.game.core.Transform2D;
import com.p1_7.game.platform.GdxShapeRenderer;
import com.p1_7.game.platform.GdxSpriteBatch;

public class DropdownMenu extends Entity implements IRenderItem, ICustomRenderable {

    // Match the MenuButton procedural fallback colors for theme consistency
    private static final Color COLOUR_NORMAL = new Color(0.20f, 0.20f, 0.55f, 1f);
    private static final Color COLOUR_HOVER  = new Color(0.35f, 0.35f, 0.80f, 1f);
    private static final Color COLOUR_BORDER = new Color(0.80f, 0.80f, 1.00f, 1f);
    private static final float BORDER_THICK  = 3f;

    private final Transform2D transform;
    private final BitmapFont font;
    private final GlyphLayout layout;
    private final String[] options;
    private int selectedIndex;

    private boolean expanded = false;
    private boolean hoveredMain = false;
    private int hoveredOption = -1;
    private boolean clicked = false;
    private boolean justClosed = false;

    public DropdownMenu(float x, float y, float width, float height, BitmapFont font, String[] options, int defaultIndex) {
        this.transform = new Transform2D(x, y, width, height);
        this.font = font;
        this.layout = new GlyphLayout();
        this.options = options;
        this.selectedIndex = defaultIndex;
    }

    public void updateInput() {
        justClosed = false;
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();

        float bx = transform.getPosition(0);
        float by = transform.getPosition(1);
        float bw = transform.getSize(0);
        float bh = transform.getSize(1);

        boolean justClicked = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);

        hoveredMain = mx >= bx && mx <= bx + bw && my >= by && my <= by + bh;
        hoveredOption = -1;

        if (expanded) {
            for (int i = 0; i < options.length; i++) {
                float oy = by - ((i + 1) * bh);
                if (mx >= bx && mx <= bx + bw && my >= oy && my <= oy + bh) {
                    hoveredOption = i;
                    break;
                }
            }
        }

        if (justClicked) {
            if (expanded) {
                if (hoveredOption != -1) {
                    selectedIndex = hoveredOption;
                    clicked = true;
                }
                expanded = false; 
                justClosed = true; // Signal that we just closed, to prevent click-bleeding to UI underneath
            } else {
                if (hoveredMain) {
                    expanded = true;
                }
            }
        }
    }

    public boolean blocksUI() { return expanded || justClosed; }
    public boolean isClicked() { return clicked; }
    public void resetClick() { clicked = false; }
    public int getSelectedIndex() { return selectedIndex; }

    @Override
    public String getAssetPath() { return null; }

    @Override
    public ITransform getTransform() { return transform; }

    @Override
    public void renderCustom(ISpriteBatch batch, IShapeRenderer shapeRenderer) {
        ShapeRenderer sr = ((GdxShapeRenderer) shapeRenderer).unwrap();
        SpriteBatch sb = ((GdxSpriteBatch) batch).unwrap();

        float x = transform.getPosition(0);
        float y = transform.getPosition(1);
        float w = transform.getSize(0);
        float h = transform.getSize(1);

        // Draw Main Box
        drawBox(sr, x, y, w, h, hoveredMain ? COLOUR_HOVER : COLOUR_NORMAL);

        // Draw Main Text
        sr.end();
        sb.begin();
        sb.setColor(Color.WHITE);
        String mainText = options[selectedIndex] + (expanded ? "   ^" : "   v");
        layout.setText(font, mainText);
        font.draw(sb, mainText, x + (w - layout.width) / 2f, y + (h + layout.height) / 2f);
        sb.end();
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Draw Expanded Options
        if (expanded) {
            for (int i = 0; i < options.length; i++) {
                float oy = y - ((i + 1) * h);
                boolean isHovered = (hoveredOption == i);
                
                drawBox(sr, x, oy, w, h, isHovered ? COLOUR_HOVER : COLOUR_NORMAL);
                
                sr.end();
                sb.begin();
                layout.setText(font, options[i]);
                font.draw(sb, options[i], x + (w - layout.width) / 2f, oy + (h + layout.height) / 2f);
                sb.end();
                sr.begin(ShapeRenderer.ShapeType.Filled);
            }
        }
    }

    private void drawBox(ShapeRenderer sr, float x, float y, float w, float h, Color fill) {
        sr.setColor(COLOUR_BORDER);
        sr.rect(x - BORDER_THICK, y - BORDER_THICK, w + BORDER_THICK * 2, h + BORDER_THICK * 2);
        sr.setColor(fill);
        sr.rect(x, y, w, h);
    }
}