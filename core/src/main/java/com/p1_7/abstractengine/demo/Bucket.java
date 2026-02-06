package com.p1_7.abstractengine.demo;

import com.p1_7.abstractengine.collision.ICollidable;
import com.p1_7.abstractengine.input.IInputQuery;

/**
 * Player-controlled bucket entity that catches falling droplets.
 *
 * <p>Moves horizontally via LEFT/RIGHT input. Registered with
 * MovementManager to receive automatic boundary clamping.</p>
 */
public class Bucket extends SpriteEntity {

    /** bucket sprite width in pixels */
    public static final float BUCKET_WIDTH = 128f;

    /** bucket sprite height in pixels */
    public static final float BUCKET_HEIGHT = 128f;

    /** horizontal movement speed in pixels per second */
    public static final float BUCKET_SPEED = 250f;

    /**
     * Constructs a bucket at the specified position.
     *
     * @param x the initial x position
     * @param y the initial y position
     */
    public Bucket(float x, float y) {
        super("bucket.png", x, y, BUCKET_WIDTH, BUCKET_HEIGHT);
    }

    /**
     * Updates bucket velocity based on player input.
     *
     * <p>Called by the game scene each frame to respond to LEFT/RIGHT
     * actions. Sets velocity directly rather than using acceleration.</p>
     *
     * @param input the input query interface
     */
    public void updateMovement(IInputQuery input) {
        // check left/right input and set horizontal velocity
        if (input.isActionActive(DemoActions.LEFT)) {
            velocity[0] = -BUCKET_SPEED;
        } else if (input.isActionActive(DemoActions.RIGHT)) {
            velocity[0] = BUCKET_SPEED;
        } else {
            velocity[0] = 0f;
        }

        // no vertical movement
        velocity[1] = 0f;
    }

    @Override
    public void onCollision(ICollidable other) {
        // bucket doesn't respond to collisions
        // (droplets detect collision with bucket)
    }
}
