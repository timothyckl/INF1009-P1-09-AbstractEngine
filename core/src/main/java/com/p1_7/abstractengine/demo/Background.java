package com.p1_7.abstractengine.demo;

import com.p1_7.abstractengine.render.IRenderItem;
import com.p1_7.abstractengine.transform.ITransform;

/**
 * Lightweight background holder for rendering.
 *
 * <p>Not an Entity — no UUID, no active flag. Submitted first to the
 * render queue so it draws behind all game entities.</p>
 */
public class Background implements IRenderItem {

    /** 2d spatial transform */
    private final Transform2D transform;

    /** path to background texture */
    private static final String ASSET_PATH = "background.png";

    /**
     * Constructs a background with the specified dimensions.
     *
     * @param width  the background width (typically window width)
     * @param height the background height (typically window height)
     */
    public Background(float width, float height) {
        // position at origin (0, 0)
        this.transform = new Transform2D(0f, 0f, width, height);
    }

    @Override
    public String getAssetPath() {
        return ASSET_PATH;
    }

    @Override
    public ITransform getTransform() {
        return transform;
    }
}
