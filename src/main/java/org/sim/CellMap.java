package org.sim;

import org.sim.creature.Creature;

import java.util.*;

public class CellMap {
    private final int LENGTH;
    private final int HEIGHT;
    public static final int LIMIT = 40;
    private final Map<Coordinates, Entity> entities = new HashMap<>();

    private final Map<Long, Coordinates> creaturesCoordinatesById = new HashMap<>();

    public class Coordinates {
        private final int LENGTH;
        private final int HEIGHT;
        private Coordinates previous;

        public Coordinates getPrevious() {
            return previous;
        }

        public Coordinates getAndRemovePrevious() {
            Coordinates coordinates = getPrevious();
            setPrevious(null);

            return coordinates;
        }

        public void setPrevious(Coordinates previous) {
            this.previous = previous;
        }

        private Coordinates(int LENGTH, int HEIGHT) {
            if (!validateCoordinates(LENGTH, HEIGHT))
                throw new IndexOutOfBoundsException("Coordinates are out of bounds");

            this.LENGTH = LENGTH;
            this.HEIGHT = HEIGHT;
        }

        public int getLength() {
            return LENGTH;
        }

        public int getHeight() {
            return HEIGHT;
        }

        public List<Coordinates> getNeighborCells() {
            return new LinkedList<>() {{
                int length = getLength(), height = getHeight();

                if (validateCoordinates(length, height + 1)) add(getCoordinates(length, height + 1)); // up
                if (validateCoordinates(length + 1, height + 1))
                    add(getCoordinates(length + 1, height + 1)); // up right
                if (validateCoordinates(length + 1, height)) add(getCoordinates(length + 1, height)); // right
                if (validateCoordinates(length + 1, height - 1))
                    add(getCoordinates(length + 1, height - 1)); // down right
                if (validateCoordinates(length, height - 1)) add(getCoordinates(length, height - 1)); // down
                if (validateCoordinates(length - 1, height - 1))
                    add(getCoordinates(length - 1, height - 1)); // down left
                if (validateCoordinates(length - 1, height)) add(getCoordinates(length - 1, height)); // left
                if (validateCoordinates(length - 1, height + 1))
                    add(getCoordinates(length - 1, height + 1)); // up right
            }};
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Coordinates that)) return false;
            return LENGTH == that.LENGTH && HEIGHT == that.HEIGHT;
        }

        @Override
        public int hashCode() {
            return Objects.hash(LENGTH, HEIGHT);
        }

        @Override
        public String toString() {
            return "{L=" + LENGTH + ", H=" + HEIGHT + '}';
        }
    }

    public CellMap(int Length, int Height) {
        if (Length > LIMIT || Height > LIMIT)
            throw new RuntimeException("CellMap boundaries should be not higher than " + LIMIT);
        if (Length < 1 || Height < 1) throw new RuntimeException("CellMap boundaries should be higher than zero");

        this.LENGTH = Length;
        this.HEIGHT = Height;
    }

    public Coordinates getCoordinates(int length, int height) {
        return new Coordinates(length, height);
    }

    public int getAmountOfCells() {
        return HEIGHT * LENGTH;
    }

    public Map<Long, Coordinates> getCreatureCoordinatesById() {
        return creaturesCoordinatesById;
    }

    public Map<Coordinates, Entity> getEntities() {
        return entities;
    }

    public Entity get(Coordinates coordinates) {
        if (isCellEmpty(coordinates)) throw new RuntimeException("Accessed empty cell at " + coordinates);

        return entities.get(coordinates);
    }

    public Entity get(int length, int height) {
        return get(getCoordinates(length, height));
    }

    public int getLength() {
        return LENGTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public Map<String, Integer> getAmountOfEntities() {
        Map<String, Integer> amountOfEntities = new HashMap<>();
        for (Map.Entry<CellMap.Coordinates, Entity> entityEntry : entities.entrySet()) {
            String entity = entityEntry.getValue().getClass().getSimpleName();
            amountOfEntities.put(entity, amountOfEntities.getOrDefault(entity, 0) + 1);
        }

        return amountOfEntities;
    }

    public void put(Coordinates coordinates, Entity entity) {
        if (!isCellEmpty(coordinates)) throw new RuntimeException("Cell is already occupied at " + coordinates + " by "
                + entities.get(coordinates));

        putEntity(coordinates, entity);
    }

    public void put(int length, int height, Entity entity) {
        put(getCoordinates(length, height), entity);
    }

    public void putRandom(Entity entity) {
        if (isCellMapFull()) throw new RuntimeException("Nowhere to put a new entity, all cells are occupied");

        int randomLength, randomHeight;

        do {
            randomLength = (int) (Math.random() * LENGTH);
            randomHeight = (int) (Math.random() * HEIGHT);
        } while (!isCellEmpty(getCoordinates(randomLength, randomHeight)));

        put(getCoordinates(randomLength, randomHeight), entity);
    }

    private void putEntity(Coordinates coordinates, Entity entity) {
        int length = coordinates.getLength();
        int height = coordinates.getHeight();

        if (!validateCoordinates(length, height)) throw new IndexOutOfBoundsException("Coordinates are out of bounds");

        entities.put(coordinates, entity);
        if (entity instanceof Creature) creaturesCoordinatesById.put(((Creature) entity).getId(), coordinates);
    }

    public void move(Coordinates from, Coordinates to) {
        if (!isCellEmpty(to)) throw new RuntimeException("Cannot move to occupied cell " + to);

        Entity entity = entities.get(from);

        this.remove(from);
        this.put(to, entity);
    }

    public void move(int length, int height, int newLength, int newHeight) {
        move(getCoordinates(length, height), getCoordinates(newLength, newHeight));
    }

    public void remove(Coordinates coordinates) {
        if (isCellEmpty(coordinates)) throw new RuntimeException("Nothing to remove at " + coordinates);
        Entity entity = get(coordinates);
        if (entity instanceof Creature) {
            long id = ((Creature) entity).getId();
            creaturesCoordinatesById.remove(id);
        }
        entities.remove(coordinates);
    }

    public void remove(int length, int height) {
        remove(getCoordinates(length, height));
    }

    public Optional<Stack<Coordinates>> createPathToEntity(Coordinates from, Class<? extends Entity> targetEntity) {
        return bfs(from, c -> {
            if (isCellEmpty(c)) return false;
            return get(c).getClass().equals(targetEntity);
        });
    }

    private Optional<Stack<Coordinates>> bfs(Coordinates start, PathfindingTargetCondition condition) {
        Queue<Coordinates> cells = new LinkedList<>() {{
            add(start);
        }};
        Set<Coordinates> knownCoordinates = new HashSet<>();

        while (!cells.isEmpty()) {
            Coordinates currentCoordinates = cells.poll();
            knownCoordinates.add(currentCoordinates);

            for (Coordinates coordinates : currentCoordinates.getNeighborCells()) {
                coordinates.setPrevious(currentCoordinates);

                if (condition.isTarget(coordinates)) {
                    Stack<Coordinates> path = new Stack<>();
                    while (coordinates.getPrevious() != null) {
                        path.add(coordinates);
                        coordinates = coordinates.getAndRemovePrevious();
                    }

                    return Optional.of(path);
                }

                if (!knownCoordinates.contains(coordinates) && isCellEmpty(coordinates)) {
                    cells.add(coordinates);
                }
            }
        }

        return Optional.empty();
    }

    public boolean isCellMapFull() {
        return entities.size() >= (HEIGHT * LENGTH);
    }

    private boolean validateCoordinates(int length, int height) {
        if (length > LENGTH - 1 || length < 0) return false;
        return height <= HEIGHT - 1 && height >= 0;
    }

    public boolean isCellEmpty(Coordinates coordinates) {
        return !entities.containsKey(coordinates);
    }
}
