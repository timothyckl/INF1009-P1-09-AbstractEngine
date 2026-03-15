package com.p1_7.mobius.core;

import com.p1_7.abstractengine.transform.ITransform;

// concrete 2D implementation of ITransform — manages position (x, y) and size (width, height)
public class Transform2D implements ITransform {

    // x, y coordinates
    private float[] position = new float[2];

    // width, height dimensions
    private float[] size = new float[2];

    /**
     * constructs a 2D transform with the specified position and size.
     *
     * @param x      the x coordinate
     * @param y      the y coordinate
     * @param width  the width
     * @param height the height
     */
    public Transform2D(float x, float y, float width, float height) {
        this.position[0] = x;
        this.position[1] = y;
        this.size[0] = width;
        this.size[1] = height;
    }

    /**
     * @param axis 0 for x, 1 for y
     * @return the position value on the given axis
     */
    @Override
    public float getPosition(int axis) {
        return position[axis];
    }

    /**
     * @param axis  0 for x, 1 for y
     * @param value the new position value
     */
    @Override
    public void setPosition(int axis, float value) {
        position[axis] = value;
    }

    /**
     * @param axis 0 for width, 1 for height
     * @return the size value on the given axis
     */
    @Override
    public float getSize(int axis) {
        return size[axis];
    }

    /**
     * @param axis  0 for width, 1 for height
     * @param value the new size value
     */
    @Override
    public void setSize(int axis, float value) {
        size[axis] = value;
    }

    @Override
    public int getDimensions() {
        return 2;
    }
}
