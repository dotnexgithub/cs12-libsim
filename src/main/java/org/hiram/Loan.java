package org.hiram;

import java.time.LocalDate;

public class Loan {
    Member member;
    LocalDate borrowedDate, dueDate;
    Book book; // see if you can make this a list instead to that patrons can borrow multiple books

    public Loan(Member member, LocalDate borrowedDate, LocalDate dueDate, Book book) {
        this.member = member;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.book = book;
    }

    public Member getLoaningMembers() {
        return member;
    }
}