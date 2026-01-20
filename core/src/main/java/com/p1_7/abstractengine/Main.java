package com.p1_7.abstractengine;

import java.util.List;
import java.util.UUID;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.ScreenUtils;
import com.p1_7.abstractengine.core.Entity;
import com.p1_7.abstractengine.core.Tag;
import com.p1_7.abstractengine.managers.impl.EntityManager;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main extends ApplicationAdapter {
    EntityManager entityManager;

    // example tag enum for demonstration
    private enum SimulationTag implements Tag {
        DYNAMIC, STATIC
    }

    @Override
    public void create() {
        // create and initialise the entity manager
        entityManager = new EntityManager();
        entityManager.init();

        // create entity with tag
        Entity entityA = new Entity();
        entityA.setActive(true);
        entityA.addTag(SimulationTag.DYNAMIC);
        entityManager.addEntity(entityA);

        // create entity without tag
        Entity entityB = new Entity();
        entityB.setActive(true);
        entityManager.addEntity(entityB);

        // retrieve entity by id
        UUID entityId = entityA.getID();
        Entity retrieved = entityManager.getEntity(entityId);
        System.out.println("Retrieved: " + retrieved);

        // query entities by tag
        List<Entity> dynamicEntities = entityManager.getEntitiesByTag(SimulationTag.DYNAMIC);
        System.out.println("Dynamic entities: " + dynamicEntities.size());

        // check tag count
        int dynamicCount = entityManager.countEntitiesByTag(SimulationTag.DYNAMIC);
        int staticCount = entityManager.countEntitiesByTag(SimulationTag.STATIC);
        System.out.println("Dynamic: " + dynamicCount + ", Static: " + staticCount);

        // iterate all entities
        for (Entity entity : entityManager.getAllEntities()) {
            System.out.println(entity);
        }

        // remove an entity
        entityManager.removeEntity(entityB.getID());
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
    }

    @Override
    public void dispose() {
        // cleanup the entity manager
        entityManager.shutdown();
    }
}
