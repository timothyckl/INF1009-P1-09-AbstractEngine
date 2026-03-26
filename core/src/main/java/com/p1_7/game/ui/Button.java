package com.p1_7.game.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.p1_7.abstractengine.entity.Entity;
import com.p1_7.abstractengine.entity.IDisposable;
import com.p1_7.abstractengine.input.IInputQuery;
import com.p1_7.abstractengine.input.InputState;
import com.p1_7.abstractengine.render.IDrawContext;
import com.p1_7.abstractengine.render.IRenderable;
import com.p1_7.abstractengine.transform.ITransform;
import com.p1_7.game.core.Transform2D;
import com.p1_7.game.input.GameActions;
import com.p1_7.game.input.ICursorSource;

/**
 * Abstract base class for all button-style UI entities.
 * Manages transform layout, hit-testing, and click detection so that subclasses
 * only need to supply rendering and resource cleanup.
 *
 * Call updateInput() once per frame, then check isClicked().
 * Call resetClick() after handling the action so it fires only once.
 */
public abstract class Button extends Entity implements IRenderable, IDisposable {

    /** Rendered button size */
    public static final float BUTTON_WIDTH = 390f;
    public static final float BUTTON_HEIGHT = 180f;

    /** Smaller interaction box inside the rendered button */
    public static final float HITBOX_WIDTH = 320f;
    public static final float HITBOX_HEIGHT = 72f;

    protected final Transform2D transform;
    protected final String label;
    protected final BitmapFont font;
    protected final GlyphLayout layout;

    /** True while the cursor is inside the button bounds */
    protected boolean hovered = false;

    private boolean clicked = false;

    protected Button(String label, float centreX, float centreY, BitmapFont font) {
        float x = centreX - BUTTON_WIDTH / 2f;
        float y = centreY - BUTTON_HEIGHT / 2f;

        this.transform = new Transform2D(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        this.label = label;
        this.font = font;
        this.layout = new GlyphLayout(font, label);
    }

    public void updateInput(ICursorSource cursor, IInputQuery inputQuery) {
        float mx = cursor.getCursorX();
        float my = cursor.getCursorY();

        float bx = transform.getPosition(0);
        float by = transform.getPosition(1);

        // Centre the smaller hitbox inside the large rendered button
        float hitboxX = bx + (BUTTON_WIDTH - HITBOX_WIDTH) / 2f;
        float hitboxY = by + (BUTTON_HEIGHT - HITBOX_HEIGHT) / 2f;

        hovered =
            mx >= hitboxX && mx <= hitboxX + HITBOX_WIDTH &&
            my >= hitboxY && my <= hitboxY + HITBOX_HEIGHT;

        if (hovered &&
            inputQuery.getActionState(GameActions.POINTER_PRIMARY) == InputState.PRESSED) {
            clicked = true;
        }
    }

    public boolean isClicked() {
        return clicked;
    }

    public void resetClick() {
        clicked = false;
    }

    @Override
    public String getAssetPath() {
        return null;
    }

    @Override
    public ITransform getTransform() {
        return transform;
    }

    @Override
    public abstract void render(IDrawContext ctx);

    @Override
    public abstract void dispose();
}