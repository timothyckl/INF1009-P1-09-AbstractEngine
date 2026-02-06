package com.p1_7.abstractengine.render;

/**
 * A single-frame accumulator for items that should be drawn this tick.
 *
 * <p>Scenes submit {@link IRenderItem} instances each frame via
 * {@link #queue(IRenderItem)}.  The {@link RenderManager} consumes
 * the queue during the draw pass and then calls {@link #clear()}.
 * Implementations are provided internally by the render manager.</p>
 */
public interface IRenderQueue {

    /**
     * Adds an item to the queue for drawing this frame.
     *
     * @param item the render item to enqueue
     */
    void queue(IRenderItem item);

    /**
     * Removes all items from the queue.  Called by the render manager
     * after the draw pass completes.
     */
    void clear();

    /**
     * Returns an iterable over every item currently in the queue.
     *
     * @return an iterable of queued render items
     */
    Iterable<IRenderItem> items();
}
