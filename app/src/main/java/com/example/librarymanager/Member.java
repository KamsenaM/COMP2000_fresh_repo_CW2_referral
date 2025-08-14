package com.example.librarymanager;

public class Member {
    public String username;
    public String firstname;
    public String lastname;
    public String email;
    public String contact;

    public Member(String username, String firstname, String lastname, String email, String contact) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.contact = contact;
    }
}
