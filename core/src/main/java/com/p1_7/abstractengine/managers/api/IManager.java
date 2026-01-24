package com.p1_7.abstractengine.managers.api;

// NOTE: this is a blueprint manager interface
// methods defined here don't need an implementation,
// but classes that use this interface must define an implementation
public interface IManager {
    void init();
    void shutdown();
}
