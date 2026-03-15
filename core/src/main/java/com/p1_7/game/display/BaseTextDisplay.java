package com.p1_7.game.display;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.p1_7.abstractengine.entity.Entity;
import com.p1_7.abstractengine.render.ICustomRenderable;
import com.p1_7.abstractengine.render.IRenderItem;
import com.p1_7.abstractengine.render.IShapeRenderer;
import com.p1_7.abstractengine.render.ISpriteBatch;
import com.p1_7.abstractengine.transform.ITransform;
import com.p1_7.game.core.Transform2D;
import com.p1_7.game.platform.GdxShapeRenderer;
import com.p1_7.game.platform.GdxSpriteBatch;

public abstract class BaseTextDisplay extends Entity implements IRenderItem, ICustomRenderable {

    protected final Transform2D transform;
    protected final BitmapFont font;
    protected final GlyphLayout layout; // NEW: Calculates exact text dimensions
    protected boolean centered = false; // NEW: Supports centered text for menus

    protected BaseTextDisplay(float x, float y, float width, float height, BitmapFont font) {
        super();
        this.font = font;
        this.layout = new GlyphLayout();
        this.transform = new Transform2D(x, y, width, height);
    }

    public void setCentered(boolean centered) {
        this.centered = centered;
    }

    protected void updateLayout(String text) {
        layout.setText(font, text);
        // Automatically update the entity's size based on the rendered text!
        transform.setSize(0, layout.width);
        transform.setSize(1, layout.height);
    }

    @Override
    public void renderCustom(ISpriteBatch batch, IShapeRenderer shapeRenderer) {
        ((GdxShapeRenderer) shapeRenderer).unwrap().end();
        ((GdxSpriteBatch) batch).unwrap().begin();

        float drawX = transform.getPosition(0);
        float drawY = transform.getPosition(1);

        // Apply centering math exactly like your teammate's old inner classes
        if (centered) {
            drawX -= layout.width / 2f;
            drawY += layout.height / 2f;
        }

        font.draw(((GdxSpriteBatch) batch).unwrap(), getText(), drawX, drawY);

        ((GdxSpriteBatch) batch).unwrap().end();
        ((GdxShapeRenderer) shapeRenderer).unwrap().begin(ShapeType.Filled);
    }

    public abstract String getText();
    public BitmapFont getFont() { return font; }

    @Override
    public String getAssetPath() { return null; }

    @Override
    public ITransform getTransform() { return transform; }
}