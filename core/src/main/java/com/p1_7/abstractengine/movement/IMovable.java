package com.p1_7.abstractengine.movement;

/**
 * Capability interface for any entity that can move under the
 * influence of acceleration and velocity.
 *
 * <p>All vectors are plain {@code float[]} arrays whose length matches
 * the dimensionality of the owning entity's
 * {@link com.p1_7.abstractengine.transform.ITransform}.  The abstract
 * engine does not mandate 2-D; concrete implementations decide the
 * array length.</p>
 */
public interface IMovable {

    /**
     * Returns the current acceleration vector.
     *
     * @return the acceleration as a float array
     */
    float[] getAcceleration();

    /**
     * Sets the acceleration to the supplied values.
     *
     * @param acceleration the new acceleration vector
     */
    void setAcceleration(float[] acceleration);

    /**
     * Returns the current velocity vector.
     *
     * @return the velocity as a float array
     */
    float[] getVelocity();

    /**
     * Sets the velocity to the supplied values.
     *
     * @param velocity the new velocity vector
     */
    void setVelocity(float[] velocity);

    /**
     * Advances the entity by one physics step.  Concrete
     * implementations apply acceleration to velocity, then velocity
     * to position.
     *
     * @param deltaTime seconds elapsed since the previous frame
     */
    void move(float deltaTime);
}
