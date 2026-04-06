package org.hiram;

import javax.swing.*;
import com.formdev.flatlaf.FlatDarculaLaf;
import org.hiram.forms.Management;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        Library library = new Library();

        // Books
        Book book1 = new Book("Roger Penrose", "Physics", "The Road to Reality", "Vintage", "English", 9780679776314L, LocalDate.of(2004, 2, 25), 3);
        Book book2 = new Book("Donald Knuth", "Computer Science", "The Art of Computer Programming", "Addison-Wesley", "English", 9780201896831L, LocalDate.of(1968, 1, 1), 5);
        Book book3 = new Book("Albert Einstein", "Physics", "Relativity: The Special and General Theory", "Henry Holt", "English", 9781514605151L, LocalDate.of(1916, 5, 1), 2);
        Book book4 = new Book("Thomas H. Cormen", "Computer Science", "Introduction to Algorithms", "MIT Press", "English", 9780262033848L, LocalDate.of(2009, 7, 31), 10);
        Book book5 = new Book("Stephen Hawking", "Physics", "A Brief History of Time", "Bantam", "English", 9780553380163L, LocalDate.of(1988, 4, 1), 8);
        Book book6 = new Book("Harold Abelson", "Computer Science", "Structure and Interpretation of Computer Programs", "MIT Press", "English", 9780262510875L, LocalDate.of(1984, 8, 1), 4);
        Book book7 = new Book("Richard Feynman", "Physics", "QED: The Strange Theory of Light and Matter", "Princeton University Press", "English", 9780691024172L, LocalDate.of(1985, 10, 2), 6);
        Book book8 = new Book("Alfred Aho", "Computer Science", "Compilers: Principles, Techniques, and Tools", "Pearson", "English", 9780321486813L, LocalDate.of(1986, 1, 1), 3);
        Book book9 = new Book("Isaac Newton", "Physics", "Philosophiae Naturalis Principia Mathematica", "Royal Society", "Latin", 9781607962403L, LocalDate.of(1687, 7, 5), 1);
        Book book10 = new Book("Robert C. Martin", "Computer Science", "Clean Code", "Prentice Hall", "English", 9780132350884L, LocalDate.of(2008, 8, 1), 12);
        Book book11 = new Book("Carl Sagan", "Astronomy", "Cosmos", "Random House", "English", 9780345331359L, LocalDate.of(1980, 10, 1), 7);
        Book book12 = new Book("Erich Gamma", "Computer Science", "Design Patterns", "Addison-Wesley", "English", 9780201633610L, LocalDate.of(1994, 10, 21), 5);

        // Members
        Member member1 = new Member("Gabriel Licup", 17, 3243421);
        Member member2 = new Member("Ted Altura", 17, 3243421);
        Member member3 = new Member("Alan Turing", 41, 1001010);
        Member member4 = new Member("Grace Hopper", 85, 19061209);
        Member member5 = new Member("Marie Curie", 66, 8823471);
        Member member6 = new Member("Richard Feynman", 69, 5506110);
        Member member7 = new Member("Ada Lovelace", 36, 18151210);
        Member member8 = new Member("Linus Torvalds", 56, 19691228);
        Member member9 = new Member("Nikola Tesla", 86, 18560710);
        Member member10 = new Member("James Gosling", 68, 19550519);

        // library initialization
        library.populateBooks(book1, book2, book3, book4, book5, book6, book7, book8, book9, book10, book11, book12);
        library.populateMembers(member1, member2, member3, member4, member5, member6, member7, member8, member9,
                member10);

        FlatDarculaLaf.setup();

        // Methods
        SwingUtilities.invokeLater(() -> {
            Management manager = new Management(library);

            // maximize program because default size values are not good
            manager.setExtendedState(JFrame.MAXIMIZED_BOTH);
            manager.setVisible(true);

        });
    }
}