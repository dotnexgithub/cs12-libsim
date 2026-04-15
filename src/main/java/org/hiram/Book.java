package org.hiram;

import java.time.LocalDate;

public class Book {


    private String author, genre, title, publisher, language;
    private long ISBN;
    private LocalDate publicationDate;
    private int quantity;

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
    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public String getTitle() {
        return title;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getLanguage() {
        return language;
    }

    public long getISBN() {
        return ISBN;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

