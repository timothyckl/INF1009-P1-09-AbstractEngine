package com.p1_7.abstractengine.transform;

/**
 * Marker contract for any object that carries spatial state via an
 * {@link ITransform}.
 *
 * <p>Managers such as {@code MovementManager} cast registered objects
 * to this interface when they need to read or write position and size
 * data.</p>
 */
public interface ITransformable {

    /**
     * Returns the spatial transform attached to this object.
     *
     * @return the transform; must not be {@code null}
     */
    ITransform getTransform();
}
