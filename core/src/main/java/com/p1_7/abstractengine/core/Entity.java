package com.p1_7.abstractengine.core;

import java.util.UUID;

import com.badlogic.gdx.utils.Array;
import com.p1_7.abstractengine.core.properties.*;

public class Entity extends AbstractObject {
    private UUID id;

    public Entity() {
        id = UUID.randomUUID();
        active = false;
        properties = new Array<AbstractProperty>();
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void addProperty(AbstractProperty property) {
        properties.add(property);
    }

    @Override
    public void removeProperty(AbstractProperty property) {
        properties.removeValue(property, true);
    }

    @Override
    public <T extends AbstractProperty> T getProperty(Class<T> type) {
        for (AbstractProperty property : properties) {
            if (type.isInstance(property)) {
                return type.cast(property);
            }
        }
        return null;
    }

    public UUID getID() {
        return id;
    }

    /**
     * Initialises default properties. Subclasses can override to add custom properties.
     */
    protected void initialiseProperties(){
        addProperty(new Dimension());
        addProperty(new Position());
        // addProperty(new State());  // requries an initial state. will handle this later
        // addProperty(new Velocity());  // not implmented yet.

    }
}
