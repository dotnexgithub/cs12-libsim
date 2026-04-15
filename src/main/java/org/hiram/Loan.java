

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

    public void setMember(Member member) {
        this.member = member;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(LocalDate borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}

