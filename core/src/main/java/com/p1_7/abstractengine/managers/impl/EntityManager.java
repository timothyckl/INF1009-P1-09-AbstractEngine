package com.p1_7.abstractengine.managers.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.badlogic.gdx.utils.ObjectMap;
import com.p1_7.abstractengine.core.AbstractProperty;
import com.p1_7.abstractengine.core.Entity;
import com.p1_7.abstractengine.core.Tag;
import com.p1_7.abstractengine.managers.base.AbstractManager;

public class EntityManager extends AbstractManager {
    private ObjectMap<UUID, Entity> entities;

    public EntityManager() {
        entities = new ObjectMap<>();
    }

    @Override
    protected void onInit() {

    }

    @Override
    protected void onUpdate(float deltaTime) {
        // iterate through all entities and update active ones
        for (Entity entity : entities.values()) {
            if (entity.isActive()) {
                entity.update(deltaTime);
            }
        }
    }

    @Override
    protected void onShutdown() {
        // deactivate all entities before clearing
        for (Entity entity : entities.values()) {
            entity.setActive(false);
        }
        entities.clear();
    }

    // retrieves an entity by its unique identifier
    public Entity getEntity(UUID id) {
        if (id == null) {
            return null;
        }
        return entities.get(id);
    }

    // adds an entity to this manager
    public boolean addEntity(Entity entity) {
        if (entity == null) {
            return false;
        }
        entities.put(entity.getID(), entity);
        return true;
    }

    // removes an entity from this manager
    public boolean removeEntity(UUID id) {
        if (id == null) {
            return false;
        }
        return entities.remove(id) != null;
    }

    // returns all entities managed by this manager
    public Iterable<Entity> getAllEntities() {
        return entities.values();
    }

    // tag-based query methods

    // returns all entities with the specified tag
    public <T extends Enum<T> & Tag> List<Entity> getEntitiesByTag(T tag) {
        List<Entity> result = new ArrayList<>();
        if (tag == null) {
            return result;
        }
        for (Entity entity : entities.values()) {
            if (entity.hasTag(tag)) {
                result.add(entity);
            }
        }
        return result;
    }

    // returns all active entities with the specified tag
    public <T extends Enum<T> & Tag> List<Entity> getActiveEntitiesByTag(T tag) {
        List<Entity> result = new ArrayList<>();
        if (tag == null) {
            return result;
        }
        for (Entity entity : entities.values()) {
            if (entity.isActive() && entity.hasTag(tag)) {
                result.add(entity);
            }
        }
        return result;
    }

    // returns all entities that have all specified tags
    public List<Entity> getEntitiesWithAllTags(Tag... tags) {
        List<Entity> result = new ArrayList<>();
        if (tags == null || tags.length == 0) {
            return result;
        }
        for (Entity entity : entities.values()) {
            if (entity.hasAllTags(tags)) {
                result.add(entity);
            }
        }
        return result;
    }

    // returns all entities that have any of the specified tags
    public List<Entity> getEntitiesWithAnyTag(Tag... tags) {
        List<Entity> result = new ArrayList<>();
        if (tags == null || tags.length == 0) {
            return result;
        }
        for (Entity entity : entities.values()) {
            if (entity.hasAnyTag(tags)) {
                result.add(entity);
            }
        }
        return result;
    }

    // counts entities with the specified tag
    public <T extends Enum<T> & Tag> int countEntitiesByTag(T tag) {
        if (tag == null) {
            return 0;
        }
        int count = 0;
        for (Entity entity : entities.values()) {
            if (entity.hasTag(tag)) {
                count++;
            }
        }
        return count;
    }

    // property-based query methods

    // returns all entities with the specified property type
    public List<Entity> getEntitiesByProperty(Class<? extends AbstractProperty> propertyType) {
        List<Entity> result = new ArrayList<>();
        if (propertyType == null) {
            return result;
        }
        for (Entity entity : entities.values()) {
            if (entity.hasProperty(propertyType)) {
                result.add(entity);
            }
        }
        return result;
    }

    // returns all active entities with the specified property type
    public List<Entity> getActiveEntitiesByProperty(Class<? extends AbstractProperty> propertyType) {
        List<Entity> result = new ArrayList<>();
        if (propertyType == null) {
            return result;
        }
        for (Entity entity : entities.values()) {
            if (entity.isActive() && entity.hasProperty(propertyType)) {
                result.add(entity);
            }
        }
        return result;
    }

    // returns all entities that have all specified property types
    @SafeVarargs
    public final List<Entity> getEntitiesWithAllProperties(Class<? extends AbstractProperty>... propertyTypes) {
        List<Entity> result = new ArrayList<>();
        if (propertyTypes == null || propertyTypes.length == 0) {
            return result;
        }
        for (Entity entity : entities.values()) {
            boolean hasAll = true;
            for (Class<? extends AbstractProperty> propertyType : propertyTypes) {
                if (!entity.hasProperty(propertyType)) {
                    hasAll = false;
                    break;
                }
            }
            if (hasAll) {
                result.add(entity);
            }
        }
        return result;
    }

    // returns all entities that have any of the specified property types
    @SafeVarargs
    public final List<Entity> getEntitiesWithAnyProperty(Class<? extends AbstractProperty>... propertyTypes) {
        List<Entity> result = new ArrayList<>();
        if (propertyTypes == null || propertyTypes.length == 0) {
            return result;
        }
        for (Entity entity : entities.values()) {
            for (Class<? extends AbstractProperty> propertyType : propertyTypes) {
                if (entity.hasProperty(propertyType)) {
                    result.add(entity);
                    break;
                }
            }
        }
        return result;
    }

    // counts entities with the specified property type
    public int countEntitiesByProperty(Class<? extends AbstractProperty> propertyType) {
        if (propertyType == null) {
            return 0;
        }
        int count = 0;
        for (Entity entity : entities.values()) {
            if (entity.hasProperty(propertyType)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String toString() {
        return "EntityManager{entities=" + entities.size + "}";
    }
}
