package com.p1_7.abstractengine.transform;

/**
 * Dimension-agnostic spatial state for an entity.
 *
 * <p>All positional and size data is expressed as plain {@code float[]}
 * arrays.  The length of each array is determined by the concrete
 * implementation; the abstract engine does not assume any particular
 * dimensionality.  Demo code that targets 2-D will use arrays of
 * length 2.</p>
 */
public interface ITransform {

    /**
     * Returns the current position as a float array.
     *
     * @return the position vector; length equals the number of dimensions
     */
    float[] getPosition();

    /**
     * Sets the position to the supplied values.
     *
     * @param position the new position vector
     */
    void setPosition(float[] position);

    /**
     * Returns the current size (extent in each dimension) as a float array.
     *
     * @return the size vector; length equals the number of dimensions
     */
    float[] getSize();

    /**
     * Sets the size to the supplied values.
     *
     * @param size the new size vector
     */
    void setSize(float[] size);

    /**
     * Returns the number of spatial dimensions this transform operates in.
     * Concrete implementations decide the value; demo code will typically
     * return 2.
     *
     * @return the dimensionality (length of the position and size arrays)
     */
    int getDimensions();
}
