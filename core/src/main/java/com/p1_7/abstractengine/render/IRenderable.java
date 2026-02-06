package com.p1_7.abstractengine.render;

/**
 * Base capability interface for any object the render system can draw.
 *
 * <p>If {@link #getAssetPath()} returns {@code null} the entity is
 * drawn procedurally (e.g. as a filled rectangle via
 * {@code ShapeRenderer}).  A non-null path indicates a textured
 * asset that should be loaded and drawn via a {@code SpriteBatch}.</p>
 */
public interface IRenderable {

    /**
     * Returns the asset path for a textured sprite, or {@code null}
     * if the entity should be drawn procedurally.
     *
     * @return the asset path string, or {@code null}
     */
    String getAssetPath();
}
