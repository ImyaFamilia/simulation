package org.sim;

abstract public class Entity {
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
