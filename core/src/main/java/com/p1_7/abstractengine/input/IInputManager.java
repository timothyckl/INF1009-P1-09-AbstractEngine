package com.p1_7.abstractengine.input;

/**
 * unified input interface that combines read-only action queries, write-side
 * key/button remapping, and a registry for game-layer input extensions.
 *
 * this type is intended for configuration and setup code (e.g. game
 * initialisation or a key-rebinding screen) that needs to bind keys and
 * query input state in the same place. scenes can also retrieve registered
 * extensions (e.g. ICursorSource) via getExtension().
 */
public interface IInputManager extends IInputQuery, IInputMapping, IInputExtensionRegistry {
}
