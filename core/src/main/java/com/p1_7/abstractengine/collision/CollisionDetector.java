package com.p1_7.abstractengine.collision;

/**
 * Stateless utility that checks whether two {@link ICollidable}
 * objects overlap.
 *
 * <p>The check is performed by comparing the axis-aligned bounding
 * rectangles returned by each collidable via
 * {@link com.badlogic.gdx.math.Rectangle#overlaps(com.badlogic.gdx.math.Rectangle)}.</p>
 */
public class CollisionDetector {

    /**
     * Determines whether the bounding rectangles of the two
     * collidables overlap.
     *
     * @param a the first collidable
     * @param b the second collidable
     * @return {@code true} if their bounds overlap
     */
    public boolean checkCollision(ICollidable a, ICollidable b) {
        return a.getBounds().overlaps(b.getBounds());
    }
}
