package com.p1_7.abstractengine.render;

import com.p1_7.abstractengine.transform.ITransformable;

/**
 * Combines renderable and spatial contracts into a single interface
 * that the {@link IRenderQueue} and {@link RenderManager} operate on.
 *
 * <p>No additional methods are declared; the interface exists to give
 * the render pipeline a single type to work with rather than
 * requiring casts.</p>
 */
public interface IRenderItem extends IRenderable, ITransformable {
}
