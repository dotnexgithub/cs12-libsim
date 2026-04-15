package org.hiram;

import java.time.LocalDate;

public class Loan {
    private Member member;
    private LocalDate borrowedDate, dueDate;
    private Book book;

    public Loan(Member member, LocalDate borrowedDate, LocalDate dueDate, Book book) {
        this.member = member;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.book = book;
    }

    public Member getMember() {
        return member;
    }

    public Book getBook() {
        return book;
    }

}

