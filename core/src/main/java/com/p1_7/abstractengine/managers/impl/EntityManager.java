package com.p1_7.abstractengine.managers.impl;

import java.util.Collection;
import java.util.UUID;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.p1_7.abstractengine.core.Entity;
import com.p1_7.abstractengine.managers.base.AbstractManager;

public class EntityManager extends AbstractManager {
    private ObjectMap<UUID, Entity> entities;

    public EntityManager() {
        entities = new ObjectMap<>();
    }

    @Override
    public void init() {

    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void shutdown() {

    }

    public Entity getEntity(UUID id) {
        return entities.get(id);
    }

    public void addEntity(Entity entity) {
        entities.put(entity.getID(), entity);
    }

    public void removeEntity(UUID id) {
        entities.remove(id);
    }
}
