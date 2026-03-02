package com.p1_7.demo.platform;

import com.p1_7.abstractengine.render.IAssetStore;
import com.p1_7.abstractengine.render.IShapeRenderer;
import com.p1_7.abstractengine.render.ISpriteBatch;
import com.p1_7.abstractengine.render.RenderManager;

/**
 * libgdx-specific render manager that provides concrete drawing resources.
 */
public class GdxRenderManager extends RenderManager {

    /** {@inheritDoc} */
    @Override
    protected ISpriteBatch createSpriteBatch() {
        return new GdxSpriteBatch();
    }

    /** {@inheritDoc} */
    @Override
    protected IShapeRenderer createShapeRenderer() {
        return new GdxShapeRenderer();
    }

    /** {@inheritDoc} */
    @Override
    protected IAssetStore createAssetStore() {
        return new GdxAssetStore();
    }
}
