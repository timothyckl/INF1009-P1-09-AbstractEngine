package com.p1_7.abstractengine.core;

import java.util.Set;

/**
 * Interface for objects that can be tagged with enum-based categories.
 *
 * <p>Provides methods for adding, removing, and querying tags on an object.
 * Tags must be enums that implement the {@link Tag} marker interface.</p>
 */
public interface Taggable {

    /**
     * Adds a tag to this object.
     *
     * @param <T> the enum type that implements Tag
     * @param tag the tag to add
     */
    <T extends Enum<T> & Tag> void addTag(T tag);

    /**
     * Removes a tag from this object.
     *
     * @param <T> the enum type that implements Tag
     * @param tag the tag to remove
     */
    <T extends Enum<T> & Tag> void removeTag(T tag);

    /**
     * Checks whether this object has a specific tag.
     *
     * @param <T> the enum type that implements Tag
     * @param tag the tag to check for
     * @return true if the object has the tag, false otherwise
     */
    <T extends Enum<T> & Tag> boolean hasTag(T tag);

    /**
     * Returns all tags currently applied to this object.
     *
     * @return an unmodifiable set of all tags
     */
    Set<Tag> getTags();

    /**
     * Removes all tags from this object.
     */
    void clearTags();
}
