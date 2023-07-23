package org.sim;

import java.util.Scanner;

public class Simulation {
    private final Actions actions;
    private final int MAX_TURNS = 100;
    private final Scanner scanner = new Scanner(System.in);

    public Simulation(Actions actions) {
        this.actions = actions;
        this.actions.initActions();
    }

    public void startSimulation() {
        System.out.println("Turn: " + actions.getTurns());
        Renderer.renderCellMap(actions.getCellMap());

        while (true) {
            System.out.print("Enter amount of turns to simulate (max " + MAX_TURNS + ", type n to exit): ");
            String input = scanner.next();

            int turns = 0;
            try {
                turns = Integer.parseInt(input);
                if (turns > 100) {
                    System.out.println("Max turns to simulate is 100");
                    continue;
                }
            } catch (NumberFormatException exception) {
                if (input.equalsIgnoreCase("n")) {
                    break;
                }
            }

            for (int i = 0; i < turns; i++) {
                nextTurn();
            }
        }
    }

    private void nextTurn() {
        actions.turnActions();
        System.out.println("Turn: " + actions.getTurns());
        Renderer.renderCellMap(actions.getCellMap());
    }
}
