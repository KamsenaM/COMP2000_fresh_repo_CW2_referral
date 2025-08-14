package com.example.librarymanager;

public class BookRequest {
    public int id;
    public String username;
    public String bookTitle;
    public String issueDate;
    public String returnDate;

    public BookRequest(int id, String username, String bookTitle, String issueDate, String returnDate) {
        this.id = id;
        this.username = username;
        this.bookTitle = bookTitle;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
    }
}
