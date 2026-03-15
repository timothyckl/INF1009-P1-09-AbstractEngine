package com.p1_7.game.platform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.p1_7.abstractengine.render.IShapeRenderer;

/**
 * libgdx implementation of IShapeRenderer that wraps a ShapeRenderer.
 */
public class GdxShapeRenderer implements IShapeRenderer {

    /** the underlying libgdx shape renderer */
    private final ShapeRenderer renderer = new ShapeRenderer();
    private final Matrix4 projection = new Matrix4();

    @Override
    public void begin() {
        // Dynamically sync projection matrix to current window bounds
        projection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderer.setProjectionMatrix(projection);
        renderer.begin(ShapeType.Filled);
    }

    @Override
    public void end() {
        renderer.end();
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }

    /**
     * exposes the underlying libgdx ShapeRenderer for game code that
     * requires direct access (e.g. setColor, rect).
     *
     * @return the wrapped ShapeRenderer
     */
    public ShapeRenderer unwrap() {
        return renderer;
    }
}