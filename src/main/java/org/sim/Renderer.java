package org.sim;

public class Renderer {
    private static final String ERROR_CELL = " ⭕";
    private static final String EMPTY_CELL = " \uD83D\uDFE9";

    public static void renderCellMap(CellMap cellMap) {
        int length = cellMap.getLength();
        int height = cellMap.getHeight();

        for (int l = 0; l < length; l++) {
            System.out.print(getNumberForCellMap(l));
        }
        System.out.println();

        for (int h = 0; h < height; h++) {
            for (int l = 0; l < length; l++) {
                if (cellMap.isCellEmpty(cellMap.getCoordinates(l, h))) {
                    System.out.print(EMPTY_CELL);
                } else {
                    System.out.print(getEntityRepresentation(cellMap.get(cellMap.getCoordinates(l, h))));
                }
            }
            System.out.println(getNumberForCellMap(h));
        }
    }

    public static String getEntityRepresentation(Entity entity) {
        return switch (entity.getClass().getSimpleName()) {
            case "Herbivore" -> " \uD83E\uDD8C";
            case "Predator" -> " \uD83E\uDD81";
            case "Grass" -> " \uD83C\uDF31";
            case "Rock" -> " \uD83E\uDEA8";
            case "Tree" -> " \uD83C\uDF32";
            default -> ERROR_CELL;
        };
    }

    public static String getNumberForCellMap(int number) {
        if (number >= 0 && number <= 9) return " " + number + " ";
        if (number >= 10 && number <= 99) return " " + number;
        if (number >= 100 && number <= 999) return String.valueOf(number);

        return ERROR_CELL;
    }
}
