package com.p1_7.mobius;

/**
 * Stateless helper that generates a parametric surface vertex grid for a
 * Möbius strip in model space.
 *
 * <p>Parametric equations (R = strip radius, s ∈ [0, 2π], t ∈ [-1, 1]):</p>
 * <pre>
 *   x = (R + t·cos(s/2))·cos(s)
 *   y = (R + t·cos(s/2))·sin(s)
 *   z =  t·sin(s/2)
 * </pre>
 */
public final class MobiusGeometry {

    // prevent instantiation — all methods are static
    private MobiusGeometry() {}

    /**
     * Generates a (sSteps+1) × (tSteps+1) grid of 3-D points that
     * sample the Möbius strip surface.
     *
     * <p>The extra row/column closes the strip along the s-axis so that
     * wireframe edges can be drawn without a special case at the seam.</p>
     *
     * @param sSteps  number of angular divisions around the strip (≥ 1)
     * @param tSteps  number of divisions across the strip width (≥ 1)
     * @param radius  distance from the origin to the centre of the strip
     * @param width   half-width of the strip in model units (t ∈ [-width, width])
     * @return a 3-D array [sSteps+1][tSteps+1][3] where the last axis
     *         holds (x, y, z) in model space
     */
    public static float[][][] generateGrid(int sSteps, int tSteps, float radius, float width) {
        // allocate grid with one extra sample in each axis to close the mesh
        float[][][] grid = new float[sSteps + 1][tSteps + 1][3];

        for (int si = 0; si <= sSteps; si++) {
            // angular parameter — wraps back to 0 at si == sSteps
            float s = (float) (2.0 * Math.PI * si / sSteps);

            // precompute trig values shared across the t-strip
            float halfS  = s / 2f;
            float cosHalf = (float) Math.cos(halfS);
            float sinHalf = (float) Math.sin(halfS);
            float cosS    = (float) Math.cos(s);
            float sinS    = (float) Math.sin(s);

            for (int ti = 0; ti <= tSteps; ti++) {
                // width parameter mapped from [-width, +width]
                float t = -width + 2f * width * ti / tSteps;

                // möbius strip parametric equations
                float offset = radius + t * cosHalf;
                grid[si][ti][0] = offset * cosS;   // x
                grid[si][ti][1] = offset * sinS;   // y
                grid[si][ti][2] = t * sinHalf;      // z
            }
        }

        return grid;
    }
}
