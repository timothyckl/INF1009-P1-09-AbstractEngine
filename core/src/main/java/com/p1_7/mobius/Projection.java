package com.p1_7.mobius;

/**
 * Stateless helper that applies 3-D rotation matrices and an orthographic
 * projection to map model-space points onto 2-D screen coordinates.
 *
 * <p>All methods accept and return plain float arrays so there is no libGDX
 * dependency; the class can be tested in isolation.</p>
 */
public final class Projection {

    // prevent instantiation — all methods are static
    private Projection() {}

    /**
     * Rotates a 3-D point around the Y-axis by the specified angle.
     *
     * <p>Y-axis rotation matrix:</p>
     * <pre>
     *   [ cos(a)  0  sin(a) ]   [x]
     *   [   0     1    0    ] · [y]
     *   [-sin(a)  0  cos(a) ]   [z]
     * </pre>
     *
     * @param point a float[3] array representing (x, y, z)
     * @param angle rotation angle in radians
     * @return a new float[3] with the rotated coordinates
     */
    public static float[] rotateY(float[] point, float angle) {
        float cosA = (float) Math.cos(angle);
        float sinA = (float) Math.sin(angle);

        return new float[]{
            cosA * point[0] + sinA * point[2],  // rotated x
            point[1],                            // y is unchanged
           -sinA * point[0] + cosA * point[2]   // rotated z
        };
    }

    /**
     * Rotates a 3-D point around the X-axis by the specified angle.
     *
     * <p>X-axis rotation matrix:</p>
     * <pre>
     *   [ 1    0       0   ]   [x]
     *   [ 0  cos(a)  -sin(a)] · [y]
     *   [ 0  sin(a)   cos(a)]   [z]
     * </pre>
     *
     * @param point a float[3] array representing (x, y, z)
     * @param angle rotation angle in radians
     * @return a new float[3] with the rotated coordinates
     */
    public static float[] rotateX(float[] point, float angle) {
        float cosA = (float) Math.cos(angle);
        float sinA = (float) Math.sin(angle);

        return new float[]{
            point[0],                            // x is unchanged
            cosA * point[1] - sinA * point[2],  // rotated y
            sinA * point[1] + cosA * point[2]   // rotated z
        };
    }

    /**
     * Projects a rotated 3-D point to 2-D screen space using a simple
     * orthographic (parallel) projection.
     *
     * <p>The z-component is discarded; x and y are scaled and offset to the
     * screen centre.</p>
     *
     * @param point a float[3] array representing the rotated (x, y, z)
     * @param scale pixels per model unit (controls apparent size)
     * @param cx    screen x-coordinate of the projection centre
     * @param cy    screen y-coordinate of the projection centre
     * @return a float[2] with the resulting (screenX, screenY)
     */
    public static float[] projectOrtho(float[] point, float scale, float cx, float cy) {
        return new float[]{
            cx + point[0] * scale,  // screen x
            cy + point[1] * scale   // screen y
        };
    }
}
