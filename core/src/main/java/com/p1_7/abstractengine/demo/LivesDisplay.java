package com.p1_7.abstractengine.demo;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.p1_7.abstractengine.engine.Settings;
import com.p1_7.abstractengine.entity.Entity;
import com.p1_7.abstractengine.render.IRenderItem;
import com.p1_7.abstractengine.transform.ITransform;

/**
 * Text entity displaying remaining lives count.
 *
 * <p>Uses libGDX BitmapFont for text rendering. Returns {@code null}
 * from {@link #getAssetPath()} to signal to RenderManager that this
 * requires text rendering instead of texture drawing.</p>
 */
public class LivesDisplay extends Entity implements IRenderItem {

    /** 2d spatial transform */
    private final Transform2D transform;

    /** font used for rendering the lives text */
    private final BitmapFont font;

    /** current lives count */
    private int lives;

    /**
     * Constructs a lives display with the specified initial lives.
     *
     * @param initialLives the starting number of lives
     */
    public LivesDisplay(int initialLives) {
        super();
        this.lives = initialLives;
        this.font = new BitmapFont(); // libgdx default font

        // position at top-left corner
        float x = 10f;
        float y = Settings.WINDOW_HEIGHT - 10f;
        this.transform = new Transform2D(x, y, 100f, 20f);
    }

    /**
     * Sets the current lives count.
     *
     * @param lives the new lives value
     */
    public void setLives(int lives) {
        this.lives = lives;
    }

    /**
     * Returns the current lives count.
     *
     * @return the number of lives remaining
     */
    public int getLives() {
        return lives;
    }

    /**
     * Returns the formatted text to display.
     *
     * @return the lives text string
     */
    public String getText() {
        return "Lives: " + lives;
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
     * Should be called when the scene exits.
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
