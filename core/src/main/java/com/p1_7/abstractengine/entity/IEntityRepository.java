package com.p1_7.abstractengine.entity;

import java.util.UUID;

/**
 * Read-only view of the entity store.
 *
 * <p>Scene and rendering code receives this interface so that it can
 * query entities without being able to mutate the store directly.
 * Write operations are exposed through {@link IEntityMutator}.</p>
 */
public interface IEntityRepository {

    /**
     * Retrieves a single entity by its unique identifier.
     *
     * @param id the UUID of the entity to look up
     * @return the matching {@link Entity}, or {@code null} if none exists
     */
    Entity getEntity(UUID id);

    /**
     * Returns an iterable over the UUIDs of every entity currently in
     * the store.
     *
     * @return an iterable of entity identifiers
     */
    Iterable<UUID> getAllEntityIds();
}
