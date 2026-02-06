package com.p1_7.abstractengine.entity;

/**
 * Functional interface that decouples the {@link EntityManager} from
 * any concrete {@link Entity} subclass.
 *
 * <p>The demo phase supplies a concrete implementation — typically a
 * lambda or method reference — that constructs the desired entity
 * type.  The manager calls {@link #create()} and takes ownership of
 * the returned instance.</p>
 */
@FunctionalInterface
public interface EntityFactory {

    /**
     * Constructs and returns a new entity instance.
     *
     * @return a freshly created {@link Entity}; must not be {@code null}
     */
    Entity create();
}
