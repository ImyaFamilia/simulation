package org.sim;

import org.sim.creature.Creature;
import org.sim.creature.Herbivore;
import org.sim.creature.Predator;
import org.sim.object.Grass;
import org.sim.object.Rock;
import org.sim.object.Tree;

import java.util.*;

public class Actions {
    private final CellMap cellMap;
    private final int MAX_GRASS;
    private final int MAX_ROCK;
    private final int MAX_TREE;
    private final int MAX_PREDATOR;
    private final int MAX_HERBIVORE;

    public Actions(CellMap cellMap) {
        this.cellMap = cellMap;
        this.MAX_GRASS = cellMap.getAmountOfCells() / 8;
        this.MAX_ROCK = cellMap.getAmountOfCells() / 8;
        this.MAX_TREE = cellMap.getAmountOfCells() / 8;
        this.MAX_PREDATOR = cellMap.getAmountOfCells() / 32;
        this.MAX_HERBIVORE = cellMap.getAmountOfCells() / 32;
    }

    public CellMap getCellMap() {
        return cellMap;
    }

    public int turns = 1;

    public void initActions() {
        for (int i = 0; i < MAX_PREDATOR; i++) {
            cellMap.putRandom(new Predator());
        }
        for (int i = 0; i < MAX_HERBIVORE; i++) {
            cellMap.putRandom(new Herbivore());
        }
        for (int i = 0; i < MAX_GRASS; i++) {
            cellMap.putRandom(new Grass());
        }
        for (int i = 0; i < MAX_ROCK; i++) {
            cellMap.putRandom(new Rock());
        }
        for (int i = 0; i < MAX_TREE; i++) {
            cellMap.putRandom(new Tree());
        }
    }

    public void turnActions() {
        turns++;

        Map<String, Integer> amountOfEntities = cellMap.getAmountOfEntities();
        for (int i = amountOfEntities.getOrDefault("Grass", 0); i < MAX_GRASS; i++) {
            cellMap.putRandom(new Grass());
            System.out.println("New grass just grown up!");
        }
        for (int i = amountOfEntities.getOrDefault("Predator", 0); i < MAX_PREDATOR; i++) {
            cellMap.putRandom(new Predator());
            System.out.println("New predator just born!");
        }
        for (int i = amountOfEntities.getOrDefault("Herbivore", 0); i < MAX_HERBIVORE; i++) {
            cellMap.putRandom(new Herbivore());
            System.out.println("New herbivore just born!");
        }

        Map<Long, CellMap.Coordinates> coordinates = new HashMap<>(cellMap.getCreatureCoordinatesById());
        for (Map.Entry<Long, CellMap.Coordinates> c : coordinates.entrySet()) {
            CellMap.Coordinates coords = c.getValue();

            Creature creature;
            if (!cellMap.isCellEmpty(coords)) {
                creature = (Creature) cellMap.get(coords);
            } else {
                return;
            }

            String creatureName = creature.getClass().getSimpleName();
            switch (creatureName) {
                case "Predator":
                    doPredator(coords);
                    break;
                case "Herbivore":
                    doHerbivore(coords);
                    break;
                default:
                    System.out.println("No method found for class " + creatureName);
            }
        }
    }

    private void doPredator(CellMap.Coordinates from) {
        defaultCreaturePursueAndEatEntity(from, Herbivore.class);
    }

    private void doHerbivore(CellMap.Coordinates from) {
        defaultCreaturePursueAndEatEntity(from, Grass.class);
    }

    private void defaultCreaturePursueAndEatEntity(CellMap.Coordinates from, Class<? extends Entity> entity) {
        Optional<Stack<CellMap.Coordinates>> pathQueue = cellMap.createPathToEntity(from, entity);
        Creature creature = (Creature) cellMap.get(from);
        creature.reduceHealth(1);

        if (creature.getHealth() <= 0) {
            cellMap.remove(from);
            System.out.println(creature.getClass().getSimpleName() + " just died!");
        } else if (pathQueue.isPresent()) {
            Stack<CellMap.Coordinates> path = pathQueue.get();
            int steps = creature.getSpeed();

            for (int i = 0; i < steps; i++) {
                CellMap.Coordinates to = path.pop();

                if (cellMap.isCellEmpty(to)) {
                    cellMap.move(from, to);
                    from = to;
                } else if (cellMap.get(to).getClass().equals(entity)) {
                    cellMap.remove(to);
                    cellMap.move(from, to);
                    from = to;
                    System.out.println(creature.getClass().getSimpleName() + " ate " + entity.getSimpleName());
                    creature.addHealth(creature.getMaxHealth() / 2);
                }

                if (path.isEmpty()) {
                    pathQueue = cellMap.createPathToEntity(from, entity);
                    if (pathQueue.isEmpty()) {
                        return;
                    }

                    path = pathQueue.get();
                }
            }

        }
    }

    public int getTurns() {
        return turns;
    }
}
