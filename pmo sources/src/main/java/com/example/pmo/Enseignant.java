package com.example.pmo;

import java.util.List;
public class Enseignant {
    private String lastName;
    private String firstName;
    private String email;
    public String password;
    private List<String> matieres;

    public Enseignant() {
    }

    public Enseignant(String lastName, String firstName, String email, String password, List<String> matieres) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.matieres = matieres;
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

    public List<String> getMatieres() {
        return matieres;
    }
}
