package com.p1_7.game.managers;

import java.util.List;

import com.p1_7.abstractengine.collision.CollisionManager;
import com.p1_7.abstractengine.collision.CollisionPair;

/**
 * game-specific collision manager.
 *
 * resolves collisions by invoking onCollision() on both entities in each pair.
 * each entity is responsible for its own response (e.g. Player reverts position
 * on Wall collision; AnswerTile notifies the scene via its handler).
 */
public class GameCollisionManager extends CollisionManager {

    @Override
    protected void resolve(List<CollisionPair> collisions) {
        for (CollisionPair pair : collisions) {
            pair.getEntityA().onCollision(pair.getEntityB());
            pair.getEntityB().onCollision(pair.getEntityA());
        }
    }
}
