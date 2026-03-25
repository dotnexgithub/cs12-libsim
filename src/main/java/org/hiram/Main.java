package org.hiram;

import javax.swing.*;

import com.formdev.flatlaf.FlatDarculaLaf;
import org.hiram.forms.Borrower;
import org.hiram.forms.Example;
import org.hiram.forms.Management;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * Hello world!
 *
 */
public class Main
{
    public static void main( String[] args ) {
        // Tests
        List<Book> books = new ArrayList<Book>();
        Book testBook = new Book("Gabriel Licup", "Horror", "Java", "CS12", "English", 4864295693956945L, LocalDate.now(), 2);
        Book testBook2 = new Book("Gabriel Licup", "Horror", "Another one", "CS12", "English", 4864295693956945L, LocalDate.now(), 5);
        Member member = new Member(testBook, "Gabriel Licup", 15, 3243421);
        books.add(testBook);
        books.add(testBook2);
        FlatDarculaLaf.setup();
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                Management manager = new Management(books);
                manager.setVisible(true);
            }
        });
    }
}
