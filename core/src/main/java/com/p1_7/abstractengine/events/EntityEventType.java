package com.p1_7.abstractengine.events;

/**
 * Event types for entity-related events.
 */
public enum EntityEventType implements EventType {
    ADDED,              // published when an entity is added to EntityManager
    REMOVED,            // published when an entity is removed from EntityManager
    ACTIVE_CHANGED,     // published when an entity's active state changes
    TAG_CHANGED,        // published when an entity's tag changes
    PROPERTY_CHANGED    // published when an entity's properties changes
}
