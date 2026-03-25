package org.hiram;

import java.time.LocalDate;

public class Book {
    public String author, genre, title, publisher, language;
    public long ISBN;
    public LocalDate publicationDate;
    public int quantity;

    public Book(String author, String genre, String title, String publisher, String language, long ISBN, LocalDate publicationDate, int quantity) {
        this.author = author;
        this.genre = genre;
        this.title = title;
        this.publisher = publisher;
        this.language = language;
        this.ISBN = ISBN;
        this.publicationDate = publicationDate;
        this.quantity = quantity;
    }
}