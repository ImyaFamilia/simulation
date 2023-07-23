package org.sim;

import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int length, height;
        while (true) {
            System.out.print("Enter length of map (max " + CellMap.LIMIT + ") type n to exit): ");
            String input = scanner.next();

            try {
                length = Integer.parseInt(input);
                if (length > CellMap.LIMIT) {
                    System.out.println("Max length of map is " + CellMap.LIMIT);
                    continue;
                }
                break;
            } catch (NumberFormatException exception) {
                if (input.equalsIgnoreCase("n")) {
                    return;
                }
            }
        }

        while (true) {
            System.out.print("Enter height of map (max " + CellMap.LIMIT + ") type n to exit): ");
            String input = scanner.next();

            try {
                height = Integer.parseInt(input);
                if (height > CellMap.LIMIT) {
                    System.out.println("Max height of map is " + CellMap.LIMIT);
                    continue;
                }
                break;
            } catch (NumberFormatException exception) {
                if (input.equalsIgnoreCase("n")) {
                    return;
                }
            }
        }

        Simulation simulation = new Simulation(new Actions(new CellMap(length, height)));
        simulation.startSimulation();
    }
}
