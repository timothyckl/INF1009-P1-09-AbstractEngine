package com.p1_7.abstractengine.demo;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.p1_7.abstractengine.entity.Entity;
import com.p1_7.abstractengine.render.IRenderItem;
import com.p1_7.abstractengine.transform.ITransform;

/**
 * Reusable text rendering entity for displaying arbitrary text.
 *
 * <p>Uses libGDX BitmapFont for text rendering. Returns {@code null}
 * from {@link #getAssetPath()} to signal to RenderManager that this
 * requires text rendering instead of texture drawing.</p>
 *
 * <p>Font scale can be adjusted at construction time to create
 * titles, subtitles, or body text.</p>
 */
public class TextDisplay extends Entity implements IRenderItem {

    /** 2d spatial transform */
    private final Transform2D transform;

    /** font used for rendering the text */
    private final BitmapFont font;

    /** current text content */
    private String text;

    /**
     * Constructs a text display with the specified text, position, and scale.
     *
     * @param text the text to display
     * @param x the x position (left edge)
     * @param y the y position (baseline)
     * @param scale the font scale multiplier (1.0f = normal size)
     */
    public TextDisplay(String text, float x, float y, float scale) {
        super();
        this.text = text;
        this.font = new BitmapFont(); // libgdx default font
        this.font.getData().setScale(scale);

        // transform width/height are not used for text, but required by interface
        this.transform = new Transform2D(x, y, 0f, 0f);
    }

    /**
     * Sets the text content to display.
     *
     * @param text the new text value
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the current text content.
     *
     * @return the text string
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the font used for rendering.
     *
     * @return the BitmapFont instance
     */
    public BitmapFont getFont() {
        return font;
    }

    /**
     * Disposes the font resource.
     * Should be called when the text display is no longer needed.
     */
    public void dispose() {
        font.dispose();
    }

    @Override
    public String getAssetPath() {
        // null signals text rendering instead of texture
        return null;
    }

    @Override
    public ITransform getTransform() {
        return transform;
    }
}
