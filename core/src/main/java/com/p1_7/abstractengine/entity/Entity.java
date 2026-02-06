package com.p1_7.abstractengine.entity;

import java.util.UUID;

/**
 * Abstract base class for every entity managed by the engine.
 *
 * <p>Each entity is assigned a globally unique identifier at
 * construction time and is active by default.  Concrete subclasses
 * (created during the demo phase) extend this class to attach
 * transform, movability, collidability and other capabilities.</p>
 */
public abstract class Entity {

    /** unique identifier assigned at construction */
    private final UUID id;

    /** whether this entity participates in engine updates */
    private boolean active;

    /**
     * Constructs a new entity with a randomly generated UUID and
     * {@code active} set to {@code true}.
     */
    protected Entity() {
        this.id = UUID.randomUUID();
        this.active = true;
    }

    /**
     * Returns the unique identifier of this entity.
     *
     * @return the entity's UUID; never {@code null}
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns whether this entity is currently active.
     *
     * @return {@code true} if the entity participates in updates
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active state of this entity.
     *
     * @param active {@code true} to activate, {@code false} to deactivate
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}
