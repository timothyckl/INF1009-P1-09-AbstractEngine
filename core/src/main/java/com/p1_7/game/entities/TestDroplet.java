package com.p1_7.game.entities;

import com.p1_7.abstractengine.entity.Entity;
import com.p1_7.abstractengine.input.IInputQuery;
import com.p1_7.abstractengine.render.IRenderItem;
import com.p1_7.abstractengine.transform.ITransform;
import com.p1_7.game.core.Transform2D;
import com.p1_7.game.input.MappableActions;

/**
 * A simple test entity that uses the droplet asset and responds to mapped actions.
 */
public class TestDroplet extends Entity implements IRenderItem {

    private final Transform2D transform;
    private static final float SPEED = 300f; // Pixels per second

    public TestDroplet(float x, float y) {
        // Droplet asset is 64x64 pixels
        this.transform = new Transform2D(x, y, 64f, 64f);
    }

    /**
     * Polls the input query for logical actions and moves accordingly.
     */
    public void update(float deltaTime, IInputQuery input) {
        float x = transform.getPosition(0);
        float y = transform.getPosition(1);

        if (input.isActionActive(MappableActions.LEFT)) {
            x -= SPEED * deltaTime;
        }
        if (input.isActionActive(MappableActions.RIGHT)) {
            x += SPEED * deltaTime;
        }
        if (input.isActionActive(MappableActions.UP)) {
            y += SPEED * deltaTime;
        }
        if (input.isActionActive(MappableActions.DOWN)) {
            y -= SPEED * deltaTime;
        }

        transform.setPosition(0, x);
        transform.setPosition(1, y);
    }

    @Override
    public String getAssetPath() {
        return "demo_archive/droplet.png";
    }

    @Override
    public ITransform getTransform() {
        return transform;
    }
}