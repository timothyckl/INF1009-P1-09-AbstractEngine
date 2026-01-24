package com.p1_7.abstractengine.core;

import java.util.UUID;

public interface EntityRepository {
    Entity getEntity(UUID id);

    Iterable<Entity> getAllEntities();
}
