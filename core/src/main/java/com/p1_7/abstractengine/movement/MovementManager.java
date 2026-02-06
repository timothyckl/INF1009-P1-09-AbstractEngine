package com.p1_7.abstractengine.movement;

import com.badlogic.gdx.utils.Array;

import com.p1_7.abstractengine.engine.UpdatableManager;
import com.p1_7.abstractengine.transform.ITransform;
import com.p1_7.abstractengine.transform.ITransformable;

/**
 * Per-frame manager that advances all registered {@link IMovable}
 * entities and, optionally, clamps their positions to a set of
 * world-space boundaries.
 *
 * <p>Entities must be explicitly registered via
 * {@link #registerMovable(IMovable)} before they will be updated.
 * Boundary clamping is dimension-agnostic: the demo calls
 * {@link #setWorldBounds(float[], float[])} with arrays whose length
 * matches the entities' {@link ITransform#getDimensions()}.</p>
 */
public class MovementManager extends UpdatableManager {

    /** all movable entities managed by this manager */
    private final Array<IMovable> movables = new Array<>();

    /** whether boundary clamping is applied after movement */
    private boolean boundariesEnabled = true;

    /** per-dimension lower bound (inclusive); null until set */
    private float[] boundsMin;

    /** per-dimension upper bound (exclusive before size offset); null until set */
    private float[] boundsMax;

    // ---------------------------------------------------------------
    // registration
    // ---------------------------------------------------------------

    /**
     * Adds an {@link IMovable} to the update list.
     *
     * @param movable the movable entity to register
     */
    public void registerMovable(IMovable movable) {
        movables.add(movable);
    }

    /**
     * Removes an {@link IMovable} from the update list.
     *
     * @param movable the movable entity to unregister
     */
    public void unregisterMovable(IMovable movable) {
        movables.removeValue(movable, true);
    }

    // ---------------------------------------------------------------
    // configuration
    // ---------------------------------------------------------------

    /**
     * Enables or disables boundary clamping after movement.
     *
     * @param enabled {@code true} to clamp positions to world bounds
     */
    public void setBoundariesEnabled(boolean enabled) {
        this.boundariesEnabled = enabled;
    }

    /**
     * Sets the per-dimension world-space boundaries.  The demo
     * typically passes arrays derived from {@link com.p1_7.abstractengine.engine.Settings}
     * (e.g. {@code min = {0, 0}}, {@code max = {WINDOW_WIDTH, WINDOW_HEIGHT}}).
     * Both arrays must be the same length; the engine does not enforce
     * a specific dimensionality.
     *
     * @param min the lower bounds per dimension
     * @param max the upper bounds per dimension
     */
    public void setWorldBounds(float[] min, float[] max) {
        this.boundsMin = min;
        this.boundsMax = max;
    }

    // ---------------------------------------------------------------
    // UpdatableManager hook
    // ---------------------------------------------------------------

    /**
     * Advances every registered movable by one physics step, then
     * clamps positions to the world bounds if enabled.
     *
     * @param deltaTime seconds elapsed since the previous frame
     */
    @Override
    protected void onUpdate(float deltaTime) {
        for (int i = 0; i < movables.size; i++) {
            IMovable movable = movables.get(i);

            // step the physics
            movable.move(deltaTime);

            // clamp to world bounds if all conditions are met
            if (boundariesEnabled && boundsMin != null && boundsMax != null
                    && movable instanceof ITransformable) {
                clampToBounds((ITransformable) movable);
            }
        }
    }

    // ---------------------------------------------------------------
    // private helpers
    // ---------------------------------------------------------------

    /**
     * Clamps each dimension of the entity's position so that the
     * entire body (position + size) stays within {@link #boundsMin}
     * and {@link #boundsMax}.
     *
     * @param transformable the entity whose position will be clamped
     */
    private void clampToBounds(ITransformable transformable) {
        ITransform transform = transformable.getTransform();
        float[] position = transform.getPosition();
        float[] size = transform.getSize();
        int dimensions = transform.getDimensions();

        for (int i = 0; i < dimensions; i++) {
            // ensure position does not fall below the lower bound
            if (position[i] < boundsMin[i]) {
                position[i] = boundsMin[i];
            }
            // ensure position + size does not exceed the upper bound
            if (position[i] + size[i] > boundsMax[i]) {
                position[i] = boundsMax[i] - size[i];
            }
        }

        // write the clamped values back
        transform.setPosition(position);
    }
}
