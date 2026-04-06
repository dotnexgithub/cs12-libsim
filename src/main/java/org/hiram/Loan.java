package org.hiram;

import java.time.LocalDate;

public class Loan {
    public Member member;
    public LocalDate borrowedDate, dueDate;
    public Book book;

    public Loan(Member member, LocalDate borrowedDate, LocalDate dueDate, Book book) {
        this.member = member;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.book = book;
    }

}