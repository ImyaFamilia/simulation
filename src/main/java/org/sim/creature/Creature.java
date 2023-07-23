package org.sim.creature;

import org.sim.Entity;

abstract public class Creature extends Entity {

    private int health;
    private int speed;
    private final int MAX_HEALTH;
    private static long nextId = 0;
    private final long ID;

    public Creature() {
        this.MAX_HEALTH = 10;
        this.health = 10;
        this.speed = 1;
        this.ID = nextId++;
    }

    public Creature(int health, int speed) {
        this.ID = nextId++;
        this.health = health;
        this.MAX_HEALTH = health;
        this.speed = speed;
    }

    public long getId() {
        return ID;
    }

    public int getHealth() {
        return health;
    }

    public int getSpeed() {
        return speed;
    }

    public int getMaxHealth() {
        return MAX_HEALTH;
    }

    public void setHealth(int health) {
        if (health > MAX_HEALTH) {
            this.health = MAX_HEALTH;
        } else this.health = Math.max(health, 0);
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void addHealth(int health) {
        setHealth(getHealth() + health);
    }

    public void reduceHealth(int health) {
        setHealth(getHealth() - health);
    }
}
