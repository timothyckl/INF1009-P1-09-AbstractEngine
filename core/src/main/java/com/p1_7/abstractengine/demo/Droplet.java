package com.p1_7.abstractengine.demo;

import com.p1_7.abstractengine.collision.ICollidable;

/**
 * Falling droplet entity that the player must catch.
 *
 * <p>Falls at constant speed. Sets a {@code caught} flag when colliding
 * with the bucket. NOT registered with MovementManager (scene calls
 * {@link #move(float)} manually) so it can fall below y=0 to trigger
 * a miss.</p>
 */
public class Droplet extends SpriteEntity {

    /** droplet sprite width in pixels */
    public static final float DROPLET_WIDTH = 64f;

    /** droplet sprite height in pixels */
    public static final float DROPLET_HEIGHT = 64f;

    /** fall speed in pixels per second (negative y direction) */
    public static final float FALL_SPEED = 180f;

    /** flag indicating this droplet was caught by the bucket */
    private boolean caught = false;

    /**
     * Constructs a droplet at the specified position.
     *
     * @param x the initial x position
     * @param y the initial y position (typically top of screen)
     */
    public Droplet(float x, float y) {
        super("droplet.png", x, y, DROPLET_WIDTH, DROPLET_HEIGHT);
        // set downward velocity
        velocity[1] = -FALL_SPEED;
    }

    @Override
    public void onCollision(ICollidable other) {
        // check if collided with bucket
        if (other instanceof Bucket) {
            caught = true;
        }
    }

    /**
     * Returns whether this droplet was caught by the bucket.
     *
     * @return {@code true} if caught, {@code false} otherwise
     */
    public boolean isCaught() {
        return caught;
    }

    /**
     * Resets the caught flag to {@code false}.
     * Used when respawning a droplet.
     */
    public void resetCaught() {
        caught = false;
    }
}
