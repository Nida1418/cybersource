package com.concord.catalogservice.event;

public class BookAddedEvent {

    private int bookId;
    private String title;
    private String isbn;

    public BookAddedEvent() {
    }

    public BookAddedEvent(int bookId, String title, String isbn) {
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}