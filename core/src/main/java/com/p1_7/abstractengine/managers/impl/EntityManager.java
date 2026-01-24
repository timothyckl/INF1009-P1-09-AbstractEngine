package com.p1_7.abstractengine.managers.impl;

import java.util.UUID;

import com.badlogic.gdx.utils.ObjectMap;
import com.p1_7.abstractengine.core.AbstractProperty;
import com.p1_7.abstractengine.core.Entity;
import com.p1_7.abstractengine.core.EntityRepository;
import com.p1_7.abstractengine.core.Tag;
import com.p1_7.abstractengine.events.EntityActiveChangedEvent;
import com.p1_7.abstractengine.events.EntityAddedEvent;
import com.p1_7.abstractengine.events.EntityPropertyChangedEvent;
import com.p1_7.abstractengine.events.EntityRemovedEvent;
import com.p1_7.abstractengine.events.EntityTagChangedEvent;
import com.p1_7.abstractengine.events.Event;
import com.p1_7.abstractengine.managers.base.AbstractManager;

public class EntityManager extends AbstractManager implements EntityRepository {
    private ObjectMap<UUID, Entity> entities;
    private EventManager eventManager;

    public EntityManager() {
        entities = new ObjectMap<>();
    }

    /**
     * Sets the EventManager for publishing entity events.
     *
     * @param eventManager the event manager to use
     */
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    /**
     * Returns the current EventManager.
     *
     * @return the event manager, or null if not set
     */
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    protected void onInit() {
        // no special initialisation required
    }

    @Override
    protected void onShutdown() {
        // deactivate all entities before clearing
        for (Entity entity : entities.values()) {
            setEntityActive(entity, false);
        }
        entities.clear();
    }

    /**
     * Retrieves an entity by ID.
     *
     * @param id entity identifier
     * @return the entity, or null if not found
     */
    public Entity getEntity(UUID id) {
        if (id == null) {
            return null;
        }
        return entities.get(id);
    }

    /**
     * Adds an entity.
     *
     * @param entity the entity to add
     * @return true if added, false if null
     */
    public boolean addEntity(Entity entity) {
        if (entity == null) {
            return false;
        }
        entities.put(entity.getID(), entity);
        publish(new EntityAddedEvent(entity));
        return true;
    }

    /**
     * Removes an entity.
     *
     * @param id entity identifier
     * @return true if removed, false if not found
     */
    public boolean removeEntity(UUID id) {
        if (id == null) {
            return false;
        }
        Entity removed = entities.remove(id);
        if (removed != null) {
            publish(new EntityRemovedEvent(removed));
            return true;
        }
        return false;
    }

    /**
     * Sets the active state of an entity and publishes an event if changed.
     * Only operates on entities managed by this EntityManager.
     *
     * @param entity the entity to modify
     * @param active the new active state
     */
    public void setEntityActive(Entity entity, boolean active) {
        if (entity == null || !entities.containsKey(entity.getID())) {
            return;
        }

        boolean previousState = entity.isActive();
        if (previousState == active) {
            return; // no change, no event
        }

        entity.setActive(active);
        publish(new EntityActiveChangedEvent(entity, active));
    }

    /**
     * Sets the active state of an entity by ID.
     *
     * @param id     the entity identifier
     * @param active the new active state
     */
    public void setEntityActive(UUID id, boolean active) {
        Entity entity = getEntity(id);
        setEntityActive(entity, active);
    }

    // ========================================================================
    // tag operations
    // ========================================================================

    /**
     * Adds a tag to an entity and publishes a TAG_CHANGED event.
     * Only operates on entities managed by this EntityManager.
     *
     * @param <T>    the tag enum type
     * @param entity the entity to modify
     * @param tag    the tag to add
     */
    public <T extends Enum<T> & Tag> void addTag(Entity entity, T tag) {
        if (entity == null || tag == null || !entities.containsKey(entity.getID())) {
            return;
        }

        entity.addTag(tag);
        publish(new EntityTagChangedEvent(entity, tag, true));
    }

    /**
     * Removes a tag from an entity and publishes a TAG_CHANGED event.
     * Only operates on entities managed by this EntityManager.
     *
     * @param <T>    the tag enum type
     * @param entity the entity to modify
     * @param tag    the tag to remove
     */
    public <T extends Enum<T> & Tag> void removeTag(Entity entity, T tag) {
        if (entity == null || tag == null || !entities.containsKey(entity.getID())) {
            return;
        }

        entity.removeTag(tag);
        publish(new EntityTagChangedEvent(entity, tag, false));
    }

    // ========================================================================
    // property operations
    // ========================================================================

    /**
     * Adds a property to an entity and publishes a PROPERTY_CHANGED event.
     * Only operates on entities managed by this EntityManager.
     *
     * @param entity   the entity to modify
     * @param property the property to add
     */
    public void addProperty(Entity entity, AbstractProperty property) {
        if (entity == null || property == null || !entities.containsKey(entity.getID())) {
            return;
        }

        entity.addProperty(property);
        publish(new EntityPropertyChangedEvent(entity, property, true));
    }

    /**
     * Removes a property from an entity and publishes a PROPERTY_CHANGED event.
     * Only operates on entities managed by this EntityManager.
     *
     * @param entity   the entity to modify
     * @param property the property to remove
     */
    public void removeProperty(Entity entity, AbstractProperty property) {
        if (entity == null || property == null || !entities.containsKey(entity.getID())) {
            return;
        }

        entity.removeProperty(property);
        publish(new EntityPropertyChangedEvent(entity, property, false));
    }

    /**
     * publishes an event through the EventManager if available.
     *
     * @param event the event to publish
     */
    private void publish(Event event) {
        if (eventManager != null) {
            eventManager.publish(event);
        }
    }

    /** Returns all managed entities. */
    public Iterable<Entity> getAllEntities() {
        return entities.values();
    }

    @Override
    public String toString() {
        return "EntityManager{entities=" + entities.size + "}";
    }
}
