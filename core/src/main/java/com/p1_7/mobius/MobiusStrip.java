package com.p1_7.mobius;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.p1_7.abstractengine.render.ICustomRenderable;
import com.p1_7.abstractengine.render.IRenderItem;
import com.p1_7.abstractengine.render.IShapeRenderer;
import com.p1_7.abstractengine.render.ISpriteBatch;
import com.p1_7.abstractengine.transform.ITransform;
import com.p1_7.mobius.core.Transform2D;
import com.p1_7.mobius.platform.GdxShapeRenderer;

// renderable möbius strip drawn as a 3D wireframe projected orthographically onto the screen
public class MobiusStrip implements IRenderItem, ICustomRenderable {

    // radius from origin to the centre-line of the strip (model units)
    private static final float R = 1.0f;

    // number of angular divisions around the strip
    private static final int S_STEPS = 80;

    // number of divisions across the strip width
    private static final int T_STEPS = 20;

    // pixels per model unit — controls the apparent size on screen
    private static final float SCALE = 200f;

    // half-width of the strip in model units (t ∈ [-STRIP_WIDTH, +STRIP_WIDTH])
    private static final float STRIP_WIDTH = 0.3f;

    // cumulative rotation around the X-axis, in radians
    private float rotX = 0f;

    // cumulative rotation around the Y-axis, in radians
    private float rotY = 0f;

    // zero-size stub transform; satisfies the ITransformable contract
    private final ITransform stubTransform = new Transform2D(0f, 0f, 0f, 0f);

    /**
     * returns null so the render manager routes this item to the custom/procedural pass.
     *
     * @return always null
     */
    @Override
    public String getAssetPath() {
        return null;
    }

    /**
     * returns a stub transform; actual draw position is computed inside renderCustom.
     *
     * @return the stub ITransform instance
     */
    @Override
    public ITransform getTransform() {
        return stubTransform;
    }

    /**
     * increments the X-axis rotation by the given delta.
     *
     * @param delta angle to add, in radians
     */
    public void rotateX(float delta) {
        rotX += delta;
    }

    /**
     * increments the Y-axis rotation by the given delta.
     *
     * @param delta angle to add, in radians
     */
    public void rotateY(float delta) {
        rotY += delta;
    }

    /**
     * draws the möbius strip wireframe using the libGDX ShapeRenderer.
     *
     * @param batch         the sprite batch (unused; the strip is shape-only)
     * @param shapeRenderer the engine shape renderer; must be a GdxShapeRenderer
     */
    @Override
    public void renderCustom(ISpriteBatch batch, IShapeRenderer shapeRenderer) {
        // obtain the underlying libgdx ShapeRenderer
        ShapeRenderer sr = ((GdxShapeRenderer) shapeRenderer).unwrap();

        // the renderer is active in Filled mode from GdxShapeRenderer.begin();
        // end that session and restart in Line mode for wireframe drawing
        sr.end();
        sr.begin(ShapeType.Line);
        sr.setColor(Color.CYAN);

        // screen centre for orthographic projection
        float cx = Gdx.graphics.getWidth()  / 2f;
        float cy = Gdx.graphics.getHeight() / 2f;

        // generate the full surface grid in model space
        float[][][] grid = MobiusGeometry.generateGrid(S_STEPS, T_STEPS, R, STRIP_WIDTH);

        // precompute projected screen coordinates for every grid vertex
        float[][][] screen = projectGrid(grid, cx, cy);

        // draw horizontal edges (along the s-axis) and vertical edges (along t-axis)
        for (int si = 0; si < S_STEPS; si++) {
            for (int ti = 0; ti < T_STEPS; ti++) {
                // horizontal edge: (si, ti) → (si+1, ti)
                sr.line(
                    screen[si    ][ti][0], screen[si    ][ti][1],
                    screen[si + 1][ti][0], screen[si + 1][ti][1]
                );

                // vertical edge: (si, ti) → (si, ti+1)
                sr.line(
                    screen[si][ti    ][0], screen[si][ti    ][1],
                    screen[si][ti + 1][0], screen[si][ti + 1][1]
                );
            }
        }
    }

    /**
     * applies rotation and orthographic projection to every vertex in the model-space grid.
     *
     * @param grid the model-space grid produced by MobiusGeometry.generateGrid
     * @param cx   screen x-coordinate of the projection centre
     * @param cy   screen y-coordinate of the projection centre
     * @return a [sSteps+1][tSteps+1][2] array of (screenX, screenY) pairs
     */
    private float[][][] projectGrid(float[][][] grid, float cx, float cy) {
        int sSize = grid.length;
        int tSize = grid[0].length;
        float[][][] screen = new float[sSize][tSize][2];

        for (int si = 0; si < sSize; si++) {
            for (int ti = 0; ti < tSize; ti++) {
                // apply Y-axis then X-axis rotation, then project to screen
                float[] rotated = Projection.rotateY(grid[si][ti], rotY);
                rotated         = Projection.rotateX(rotated,      rotX);
                float[] projected = Projection.projectOrtho(rotated, SCALE, cx, cy);

                screen[si][ti][0] = projected[0];
                screen[si][ti][1] = projected[1];
            }
        }

        return screen;
    }
}
