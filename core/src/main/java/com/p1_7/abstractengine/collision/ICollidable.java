package com.p1_7.abstractengine.collision;

import com.badlogic.gdx.math.Rectangle;

/**
 * Capability interface for any entity that participates in collision
 * detection.
 *
 * <p>The bounding {@link Rectangle} is used by
 * {@link CollisionDetector} to perform overlap tests.  The
 * {@link #onCollision(ICollidable)} callback is invoked by the
 * {@code CollisionManager} when an overlap is detected with another
 * collidable.</p>
 */
public interface ICollidable {

    /**
     * Returns the axis-aligned bounding rectangle that represents
     * this entity's collision shape.
     *
     * @return the bounding rectangle; must not be {@code null}
     */
    Rectangle getBounds();

    /**
     * Called when this entity has been found to overlap with another.
     *
     * @param other the collidable that this entity collided with
     */
    void onCollision(ICollidable other);
}
