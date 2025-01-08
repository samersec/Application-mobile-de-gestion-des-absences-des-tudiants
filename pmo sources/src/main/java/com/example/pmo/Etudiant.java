package com.example.pmo;
public class Etudiant {
    private int CIN;
    private String lastName;
    private String firstName;
    private String email;
    public String password;

    public Etudiant() {
    }

    public Etudiant(int CIN, String lastName, String firstName, String email, String password) {
        this.CIN=CIN;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
    }
    public int getCIN() {
        return CIN;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

