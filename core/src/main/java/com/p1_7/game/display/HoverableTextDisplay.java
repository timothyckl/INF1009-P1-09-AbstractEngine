package com.p1_7.game.display;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.p1_7.abstractengine.collision.IBounds;
import com.p1_7.abstractengine.collision.ICollidable;
import com.p1_7.abstractengine.render.IShapeRenderer;
import com.p1_7.abstractengine.render.ISpriteBatch;
import com.p1_7.game.entities.MousePointer;

public class HoverableTextDisplay extends TextDisplay implements ICollidable {

    private boolean isHovered = false;

    public HoverableTextDisplay(String text, float x, float y, BitmapFont font) {
        super(text, x, y, font);
    }

    // Overloaded to support centered collision detection
    public HoverableTextDisplay(String text, float x, float y, BitmapFont font, boolean centered) {
        super(text, x, y, font, centered);
    }

    public HoverableTextDisplay(String text, float x, float y, float width, float height, BitmapFont font) {
        super(text, x, y, font);
        this.transform.setSize(0, width);
        this.transform.setSize(1, height);
    }

    public boolean isHovered() { return this.isHovered; }

    @Override
    public IBounds getBounds() {
        float x = transform.getPosition(0);
        float y = transform.getPosition(1);
        
        // We no longer need the 300/30 fallback hack! BaseTextDisplay calculates the exact size.
        float w = transform.getSize(0);
        float h = transform.getSize(1);
        
        // Adjust the collision box if the text is drawn centered
        if (centered) {
            return new MousePointer.SimpleBounds(x - (w / 2f), y - (h / 2f), w, h);
        }
        
        return new MousePointer.SimpleBounds(x, y - h, w, h);
    }

    @Override
    public void onCollision(ICollidable other) {
        if (other instanceof MousePointer) {
            this.isHovered = true; 
        }
    }

    @Override
    public void renderCustom(ISpriteBatch batch, IShapeRenderer shapeRenderer) {
        if (isHovered) {
            font.setColor(1f, 1f, 0f, 1f); // Yellow highlight
        } else {
            font.setColor(1f, 1f, 1f, 1f); // Default white
        }
        
        super.renderCustom(batch, shapeRenderer);
        font.setColor(1f, 1f, 1f, 1f);
        this.isHovered = false;
    }
}