package com.p1_7.abstractengine.collision;

import com.badlogic.gdx.utils.Array;

import com.p1_7.abstractengine.engine.UpdatableManager;

/**
 * Per-frame manager that tests all registered {@link ICollidable}
 * entities for pairwise overlap and fires the appropriate callbacks.
 *
 * <p>Entities must be explicitly registered via
 * {@link #registerCollidable(ICollidable)}.  Detection uses a
 * stateless {@link CollisionDetector} and iterates unique pairs so
 * that each collision is reported exactly once (both sides receive
 * their {@link ICollidable#onCollision(ICollidable)} call).</p>
 */
public class CollisionManager extends UpdatableManager {

    /** all collidable entities managed by this manager */
    private final Array<ICollidable> collidables = new Array<>();

    /** stateless detector that performs the overlap test */
    private final CollisionDetector detector = new CollisionDetector();

    // ---------------------------------------------------------------
    // registration
    // ---------------------------------------------------------------

    /**
     * Adds an {@link ICollidable} to the detection list.
     *
     * @param collidable the collidable entity to register
     */
    public void registerCollidable(ICollidable collidable) {
        collidables.add(collidable);
    }

    /**
     * Removes an {@link ICollidable} from the detection list.
     *
     * @param collidable the collidable entity to unregister
     */
    public void unregisterCollidable(ICollidable collidable) {
        collidables.removeValue(collidable, true);
    }

    // ---------------------------------------------------------------
    // UpdatableManager hook
    // ---------------------------------------------------------------

    /**
     * Runs collision detection and resolution for this frame.
     *
     * @param deltaTime seconds elapsed since the previous frame
     */
    @Override
    protected void onUpdate(float deltaTime) {
        detectAndResolve();
    }

    // ---------------------------------------------------------------
    // detection & resolution
    // ---------------------------------------------------------------

    /**
     * Iterates all unique pairs {@code (i, j)} where {@code i < j}.
     * When an overlap is detected both collidables are notified via
     * their {@link ICollidable#onCollision(ICollidable)} callbacks.
     */
    private void detectAndResolve() {
        for (int i = 0; i < collidables.size - 1; i++) {
            ICollidable a = collidables.get(i);
            for (int j = i + 1; j < collidables.size; j++) {
                ICollidable b = collidables.get(j);
                if (detector.checkCollision(a, b)) {
                    a.onCollision(b);
                    b.onCollision(a);
                }
            }
        }
    }
}
