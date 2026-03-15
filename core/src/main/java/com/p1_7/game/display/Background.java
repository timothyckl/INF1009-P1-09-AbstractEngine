package com.p1_7.game.display;

import com.p1_7.abstractengine.render.IRenderItem;
import com.p1_7.abstractengine.transform.ITransform;
import com.p1_7.game.core.Transform2D;

/**
 * lightweight background holder for rendering.
 *
 * not an Entity - no UUID, no active flag. submitted first to the
 * render queue so it draws behind all game entities.
 */
public class Background implements IRenderItem {

    /** 2d spatial transform */
    private final Transform2D transform;

    /** path to background texture */
    private String ASSET_PATH;

    /**
     * constructs a background with the specified dimensions.
     *
     * @param width  the background width (typically window width)
     * @param height the background height (typically window height)
     */
    public Background(float width, float height) {
        // position at origin (0, 0)
        this.ASSET_PATH = "menu/background.png";
        this.transform = new Transform2D(0f, 0f, width, height);
    }

    public Background(String assetPath, float width, float height) {
        this.ASSET_PATH = assetPath;
        this.transform = new Transform2D(0f, 0f, width, height);
    }

    @Override
    public String getAssetPath() {
        return ASSET_PATH;
    }

    public void setAssetPath(String path) {
        this.ASSET_PATH = path;
    }

    @Override
    public ITransform getTransform() {
        return transform;
    }
}
