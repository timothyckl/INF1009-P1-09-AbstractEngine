package com.p1_7.game.platform;

import com.badlogic.gdx.Gdx;
import com.p1_7.game.input.ICursorSource;

/**
 * libGDX implementation of ICursorSource.
 *
 * applies the Y-flip using Gdx.graphics.getHeight() so the result is
 * always correct even if the window is resized after startup.
 */
public class GdxCursorSource implements ICursorSource {

    /**
     * returns the raw horizontal cursor position in screen pixels.
     *
     * @return cursor x in pixels from the left edge
     */
    @Override
    public float getCursorX() {
        return Gdx.input.getX();
    }

    /**
     * returns the cursor Y position flipped to world space (bottom-left origin).
     *
     * @return cursor y in pixels from the bottom edge
     */
    @Override
    public float getCursorY() {
        return Gdx.graphics.getHeight() - Gdx.input.getY();
    }
}
