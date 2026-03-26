package org.hiram;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Library {
    public List<Book> books;
    public List<Book> availableBooks;
    public List<Member> members;
    public List<Member> loaningMembers;
    public List<Loan> loans;

    // Adaptive field
    public Member focusedMember;
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

    public void updateLoaningMembers() {
        this.loaningMembers = loans.stream().map(Loan::getLoaningMembers).toList();
    }

    private Book getRandomAvailableBook() {
        List<Book> availableBooks = new ArrayList<>(books);
        for (Book book : books) {
            if (book.quantity == 0) availableBooks.remove(book);
        }
        Book randomBook = availableBooks.get(randInt(0, availableBooks.size()));
        randomBook.quantity -= 1;
        return randomBook;
    }

    private Member getRandomAvailableMember() {
        List<Member> availableMembers = new ArrayList<Member>(members);
        availableMembers.remove(loaningMembers);
        return availableMembers.get(randInt(0, availableMembers.size()));
    }
    // Actions that need the library to change
    public void randomMemberVisited() {
        this.focusedMember = getRandomAvailableMember();
    }
    public Loan randomMemberLoans() {
        this.focusedMember = getRandomAvailableMember();
        loans.add(new Loan(focusedMember, LocalDate.now(), LocalDate.now(), getRandomAvailableBook()));
        return ;
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
