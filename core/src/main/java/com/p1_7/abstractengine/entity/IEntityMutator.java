package com.p1_7.abstractengine.entity;

import java.util.UUID;

/**
 * Write-side contract for the entity store.
 *
 * <p>Code that needs to add, update or remove entities receives this
 * interface.  Read-only access is provided by {@link IEntityRepository}.
 * Ownership of an entity moves to the manager once
 * {@link #createEntity(EntityFactory)} returns.</p>
 */
public interface IEntityMutator {

    /**
     * Creates a new entity via the supplied factory, adds it to the
     * store, and returns it.
     *
     * @param factory the factory that constructs the concrete entity
     * @return the newly created and registered entity
     */
    Entity createEntity(EntityFactory factory);

    /**
     * Updates the active flag on the entity identified by {@code id}.
     *
     * @param id     the UUID of the target entity
     * @param active the desired active state
     */
    void updateEntity(UUID id, boolean active);

    /**
     * Removes the entity identified by {@code id} from the store.
     *
     * @param id the UUID of the entity to remove
     */
    void removeEntity(UUID id);
}
