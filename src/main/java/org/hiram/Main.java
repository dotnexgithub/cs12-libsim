package org.hiram;

import javax.swing.*;
import com.formdev.flatlaf.FlatDarculaLaf;
import org.hiram.forms.Management;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

public class Main {

    // 1. MOVE SIMULATION STATE HERE (Class Level)
    private static Timer simulationTimer;
    private static int currentDay = 1;
    private static int currentAction = 0;
    private static boolean isRunning = false;
    private static int maxDays = 10;
    private static int actionsPerDay = 20;

    public static void main(String[] args) {
        // Main Library object
        Library library = new Library();

        // Populate fields of library
        Book testBook = new Book("Gabriel Licup", "Horror", "Java", "CS12", "English", 4864295693956945L, LocalDate.now(), 1);
        Book testBook2 = new Book("Gabriel Licup", "Horror", "Another one", "CS12", "English", 4864295693956945L, LocalDate.now(), 1);

        // Populate members
        Member member = new Member("Gabriel Licup", 15, 3243421);
        Member member2 = new Member("Ted Altura", 15, 3243421);

        // library initialization
        library.populateBooks(testBook, testBook2);
        library.populateMembers(member, member2);

        FlatDarculaLaf.setup();

        // Methods
        SwingUtilities.invokeLater(() -> {
            Management manager = new Management(library);
            manager.setVisible(true);

            manager.nextDayButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    isRunning = true;
                }
            });
            // 2. USE A SINGLE ACTION LISTENER AS A TOGGLE SWITCH
            manager.startSimulationButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent a) {

                    // Stop logic
                    if (isRunning) {
                        isRunning = false;
                        if (simulationTimer != null) {
                            simulationTimer.stop();
                        }
                        manager.startSimulationButton.setText("Start simulation");

                        // Clear actions panel


                        manager.appendOutput("Simulation Stopped manually.");
                        return;
                    }

                    // Disable button while running, use it as button to go to next day
                    isRunning = true;
                    manager.startSimulationButton.setText("Running...");
                    manager.startSimulationButton.setEnabled(false);



                    // Create the timer
                    simulationTimer = new Timer(500, e -> {

                        // When to stop naturally (Max days reached)
                        if (currentDay > maxDays) {
                            simulationTimer.stop();
                            isRunning = false;
                            manager.startSimulationButton.setEnabled(true);
                            manager.startSimulationButton.setText("Start simulation"); // Reset button text
                            manager.appendOutput("Simulation Complete!");
                            return; // Exit early
                        } else if (!isRunning) {
                            manager.startSimulationButton.setEnabled(true);
                            manager.startSimulationButton.setText("Go Next Day"); // Reset button text
                            return;
                        }

                        // Current day counter
                        if (currentAction == 0) {
                            manager.addDayLabel(currentDay);
                        }

                        int randomAction = Rand.randomInt(0, 4);
                        switch (randomAction) {
                            case 0:
                                library.randomMemberVisited();
                                SwingUtilities.invokeLater(() -> manager.actionHandler(Actions.VISIT, library));
                                break;
                            case 1:
                                library.randomMemberLoans();
                                if (library.errorLevel != 0) {
                                    library.randomBookReturn();
                                    if (library.errorLevel == 0 && library.focusedLoan != null) {
                                        SwingUtilities.invokeLater(() -> manager.actionHandler(Actions.RETURN, library));
                                    }
                                } else {
                                    SwingUtilities.invokeLater(() -> manager.actionHandler(Actions.LOAN, library));
                                }
                                break;
                            case 2:
                                library.randomBookReturn();
                                if (library.errorLevel != 0) {
                                    library.randomMemberLoans();
                                    SwingUtilities.invokeLater(() -> manager.actionHandler(Actions.LOAN, library));
                                    break;
                                }
                                SwingUtilities.invokeLater(() -> manager.actionHandler(Actions.RETURN, library));
                                break;
                            case 3:
                                library.randomMemberWashroom();
                                SwingUtilities.invokeLater(() -> manager.actionHandler(Actions.WASHROOM, library));
                                break;
                            default:
                                System.out.println("weird");
                        }

                        // --- INCREMENT COUNTERS ---
                        currentAction++;

                        // If we hit 10 actions, reset actions to 0 and move to the next day
                        if (currentAction >= actionsPerDay) {
                            currentAction = 0;
                            isRunning = false;
                            currentDay++;
                        }
                    });

                    // Start the timer
                    simulationTimer.start();
                }
            });

            // Pause till next day

        });
    }
}