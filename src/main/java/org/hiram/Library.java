package org.hiram;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hiram.Rand;

public class Library {
    public List<Book> books = new ArrayList<Book>();
    public List<Book> availableBooks = new ArrayList<Book>();
    public List<Member> members = new ArrayList<Member>();
    public List<Loan> loans = new ArrayList<Loan>();

    // Action specific variables updated depending on the actions done
    public Member focusedMember;
    public Loan focusedLoan;
    public Book focusedBorrowedBook;

    // Class errorlevel for determining if action could have been done
    public int errorLevel;
    public Library() {
    }

    public void populateBooks(Book... book) {
        this.books = new ArrayList<>(Arrays.asList(book));
        this.availableBooks = new ArrayList<>(Arrays.asList(book));
    }

    public void populateMembers(Member... member) {
        this.members = new ArrayList<>(Arrays.asList(member));
    }

    public void populateLoans(Loan... loan) {
        this.loans = new ArrayList<>(Arrays.asList(loan));
    }

//    public void updateLoaningMembers() {
//        this.loaningMembers = loans.stream().map(Loan::getLoaningMembers).toList();
//    }

    private Member getAnyRandomMember() {
        return members.get(Rand.randomInt(0, members.size()));
    }
    private Book getRandomAvailableBook() {
        List<Book> availableBooks = new ArrayList<>(books);
        for (Book book : books) {
            if (book.quantity == 0) availableBooks.remove(book);
        }
        if (availableBooks.isEmpty()) {
            return null;
        }
        Book randomBook = availableBooks.get(Rand.randomInt(0, availableBooks.size()));
        randomBook.quantity -= 1;
        return randomBook;
    }

//    // returns null if none found
//    private Member getRandomAvailableMember() {
//        List<Member> availableMembers = new ArrayList<Member>(members);
//        availableMembers.remove(loaningMembers);
//        if (availableMembers.isEmpty()) {
//            return null;
//        }
//        return availableMembers.get(Rand.randomInt(0, availableMembers.size()));
//    }

    // returns null if none found
    private Loan getRandomLoan() {
        if (loans.isEmpty()) {
            return null;
        }
        return loans.get(Rand.randomInt(0, loans.size()));
    }

//    private void addFocusedMemberAsLoaning() {
//        for (Member member : loaningMembers) {
//            if (member.id == focusedMember.id) {
//                return;
//            }
//        loaningMembers.add(focusedMember);
//        }
//    }

    private Loan getFocusedMemberLoan() {
        // use a for loop to see if any loans have members that have matching IDs with focusedMember
        for (Loan loan : loans) {
            if (loan.member.id == focusedMember.id) return loan;
        }
        return null;
    }

    // ALL ACTIONS THAT PATRONS CAN DO
    // Actions that need the library to change, any member can visit (assumption)
    public void randomMemberVisited() {
        focusedMember = getAnyRandomMember();
        errorLevel = 0;

    }

    public void randomMemberLoans() {
        focusedMember = getAnyRandomMember();
        Book randomBook = getRandomAvailableBook();
        // use codes/errorlevels to determine if a member can still loan a book
        if (focusedMember == null || randomBook == null) {
            errorLevel = -1;
            return;
        }

        Loan tempLoan = new Loan(focusedMember, LocalDate.now(), LocalDate.now(), getRandomAvailableBook());
        loans.add(tempLoan);
        errorLevel = 0;

    }

    public void randomBookReturn() {
        focusedLoan = getRandomLoan();
        if (focusedLoan == null) {
            errorLevel = -1;
            return;
        }
        focusedMember = focusedLoan.member;
        // use codes/errorlevels to determine if a member can still loan
        errorLevel = 0;
        loans.remove(focusedLoan);
    }

    // Assumes that anyone can use the washroom
    public void randomMemberWashroom() {
        focusedMember = getAnyRandomMember();
        errorLevel = 0;
    }



    // Actions
//    private void checkAvailableMembers(List<Member> members) {
//        borrowing
//        for (Member member : members) {
//            if this.
//        }
//    }

//    public void randomMemberLoan() {
//        // Create a temp list that
//        Member member = this.availableMembers.get(randInt(0, this.availableMembers.size()));
//        this.availableMembers.remove(member);
//    }
    public void runSimulation() { // take arguments later

    }
}
