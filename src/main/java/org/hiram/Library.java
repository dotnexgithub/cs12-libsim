package org.hiram;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Library {
    public List<Book> getBooks() {
        return books;
    }

    public List<Member> getMembers() {
        return members;
    }

    public Member getFocusedMember() {
        return focusedMember;
    }

    public Loan getFocusedLoan() {
        return focusedLoan;
    }

    public Actions getLastAction() {
        return lastAction;
    }

    public int getNumberOfVisits() {
        return numberOfVisits;
    }

    public int getNumberOfLoans() {
        return numberOfLoans;
    }

    public int getNumberOfReturns() {
        return numberOfReturns;
    }

    public int getNumberOfWashroomUses() {
        return numberOfWashroomUses;
    }

    public int getErrorLevel() {
        return errorLevel;
    }

    private List<Book> books = new ArrayList<Book>();
    private List<Member> members = new ArrayList<Member>();
    private List<Loan> loans = new ArrayList<Loan>();

    // Action specific variables updated depending on the actions done
    private Member focusedMember;
    private Loan focusedLoan;
    private Actions lastAction;


    // Statistics variables
    private int numberOfVisits;
    private int numberOfLoans;
    private int numberOfReturns;
    private int numberOfWashroomUses;

    // errorLevel used to determine if a method executed expectedly (inspired by windows batch)
    private int errorLevel;

    public Library() {
    }

    public void populateBooks(Book... book) {
        this.books = new ArrayList<>(Arrays.asList(book));
    }

    public void populateMembers(Member... member) {
        this.members = new ArrayList<>(Arrays.asList(member));
    }

    private Member getAnyRandomMember() {
        return members.get(Rand.randomInt(0, members.size()));
    }

    private Book getRandomAvailableBook() {
        List<Book> availableBooks = new ArrayList<>(books);
        for (Book book : books) {
            if (book.getQuantity() == 0) availableBooks.remove(book);
        }
        if (availableBooks.isEmpty()) {
            return null;
        }
        Book randomBook = availableBooks.get(Rand.randomInt(0, availableBooks.size()));

        // refer back to original books list to update qty
        int bookIndex = books.indexOf(randomBook);
        Book originalBook = books.get(bookIndex);
        originalBook.setQuantity(originalBook.getQuantity() - 1);
        return randomBook;
    }

    private Loan getRandomLoan() {
        if (loans.isEmpty()) {
            return null;
        }
        return loans.get(Rand.randomInt(0, loans.size()));
    }

    // ALL ACTIONS THAT PATRONS CAN DO
    // Actions that need the library to change, any member can visit (assumption)
    public void randomMemberVisited() {
        focusedMember = getAnyRandomMember();
        errorLevel = 0;
        numberOfVisits += 1;
        lastAction = Actions.VISIT;
    }

    // A random member is selected to loan, errorLevel = -1 if no books are found
    public void randomMemberLoans() {
        focusedMember = getAnyRandomMember();
        Book randomBook = getRandomAvailableBook();
        // use codes/errorlevels to determine if a member can still loan a book
        if (focusedMember == null || randomBook == null) {
            errorLevel = -1;
            return;
        }

        Loan tempLoan = new Loan(focusedMember, LocalDate.now(), LocalDate.now(), randomBook);
        focusedLoan = tempLoan;
        loans.add(tempLoan);
        errorLevel = 0;
        numberOfLoans += 1;
        lastAction = Actions.LOAN;
    }

    // A random member is selected to return a book, errorLevel = -1 if no books are found
    public void randomBookReturn() {
        focusedLoan = getRandomLoan();
        if (focusedLoan == null) {
            errorLevel = -1;
            return;
        }
        focusedMember = focusedLoan.getMember();
        // use codes/errorlevels to determine if a member can still loan
        loans.remove(focusedLoan);
        errorLevel = 0;
        focusedLoan.getBook().setQuantity(focusedLoan.getBook().getQuantity() + 1);
        numberOfReturns += 1;
        lastAction = Actions.RETURN;
    }

    // Assumes that anyone can use the washroom
    public void randomMemberWashroom() {
        focusedMember = getAnyRandomMember();
        numberOfWashroomUses += 1;
        errorLevel = 0;
        lastAction = Actions.WASHROOM;
    }
}

