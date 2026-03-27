package org.hiram;

import javax.swing.*;

import com.formdev.flatlaf.FlatDarculaLaf;
import org.hiram.forms.Borrower;
import org.hiram.forms.Example;
import org.hiram.forms.Management;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.hiram.Rand;


/**
 * Hello world!
 *
 */
public class Main
{
    public static void main( String[] args ) {
        // Main Library object
        Library library = new Library();

        // Populate fields of library
        Book testBook = new Book("Gabriel Licup", "Horror", "Java", "CS12", "English", 4864295693956945L, LocalDate.now(), 22);
        Book testBook2 = new Book("Gabriel Licup", "Horror", "Another one", "CS12", "English", 4864295693956945L, LocalDate.now(), 5);

        // Populate members
        Member member = new Member("Gabriel Licup", 15, 3243421);
        Member member2 = new Member("Ted Altura", 15, 3243421);


        // library initialization
        library.populateBooks(testBook, testBook2);
        library.populateMembers(member, member2);

        // simulation
        // days

        // Tests
        List<Book> books = new ArrayList<Book>();

        books.add(testBook);
        books.add(testBook2);
        FlatDarculaLaf.setup();
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                Management manager = new Management(library);
                manager.setVisible(true);
                for (int i = 0; i < 10; i++) {
                    String day = "Day " + (i + 1);
                    SwingUtilities.invokeLater(() -> {
                        manager.appendOutput("Day " + day + " =============");
                    });
                    for (int x = 0; x < 10; x++) {

                        int randomAction = Rand.randomInt(0, 3);
                        switch (randomAction) {
                            case 0:
                                library.randomMemberVisited();
                                String visitMessage = library.focusedMember.name + " visited!";
                                SwingUtilities.invokeLater(() -> {
                                    manager.appendOutput(visitMessage);
                                });
                                break;
                            case 1:
                                library.randomMemberLoans();

                                if (library.errorLevel != 0) {

                                    library.randomBookReturn();
                                    String returnedMessage1 = library.focusedMember + " returned book";
                                    SwingUtilities.invokeLater(() -> {
                                    manager.appendOutput(returnedMessage1);
                                    });
                                    break;
                                }
                                String loanedMessage1 = library.focusedMember.name + " loaned book";

                                SwingUtilities.invokeLater(() -> {
                                    manager.appendOutput(loanedMessage1);
                                });
                                break;
                            case 2:
                                library.randomBookReturn();
                                if (library.errorLevel != 0) {
                                    library.randomMemberLoans();
                                    String loanedMessage2 = library.focusedMember.name + " loaned book";
                                    SwingUtilities.invokeLater(() -> {
                                        manager.appendOutput(loanedMessage2);
                                    });
                                    break;
                                }
                                String returnedMessage2 = library.focusedMember.name + " returned book";

                                System.out.println(returnedMessage2);
                                break;
                            case 3:
                                library.randomMemberWashroom();
                                String washroomMessage = library.focusedMember.name + " used the washroom!";
                                System.out.println(washroomMessage);
                                break;
                        }
                    }
                }
            }
        });
    }
}
