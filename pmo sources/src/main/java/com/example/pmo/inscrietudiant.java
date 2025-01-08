package com.example.pmo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class inscrietudiant extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscrietudiant);

        final EditText CIN = findViewById(R.id.CIN);
        final EditText lastname = findViewById(R.id.LastName);
        final EditText firstname = findViewById(R.id.FirstName);
        final EditText email = findViewById(R.id.Email);
        final EditText password = findViewById(R.id.Password);
        final Button submit = findViewById(R.id.Submit);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cinText = CIN.getText().toString();
                String lastNameText = lastname.getText().toString();
                String firstNameText = firstname.getText().toString();
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();

                int cinInt;
                try {
                    cinInt = Integer.parseInt(cinText);
                } catch (NumberFormatException e) {
                    // La conversion a échoué, la chaîne n'est pas un nombre valide
                    Toast.makeText(inscrietudiant.this, "Le CIN doit être un nombre valide.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validation du CIN (8 chiffres)
                if (!isValidCIN(cinText)) {
                    Toast.makeText(inscrietudiant.this, "Le CIN doit comporter 8 chiffres.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validation du nom et du prénom
                if (!isValidName(lastNameText) || !isValidName(firstNameText)) {
                    Toast.makeText(inscrietudiant.this, "Le nom et le prénom doivent contenir au moins 3 caractères alphabétiques.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validation de l'adresse e-mail
                if (!isValidEmail(emailText)) {
                    Toast.makeText(inscrietudiant.this, "Veuillez entrer une adresse e-mail valide.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validation de la longueur du mot de passe
                if (passwordText.length() < 6) {
                    Toast.makeText(inscrietudiant.this, "Le mot de passe doit contenir au moins 6 caractères.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Vérification si le CIN existe déjà dans la base de données
                                    DatabaseReference referenceCINCheck = FirebaseDatabase.getInstance().getReference("Etudiants");
                                    referenceCINCheck.orderByChild("cin").equalTo(cinInt).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                // Le CIN existe déjà, annuler l'inscription
                                                Toast.makeText(inscrietudiant.this, "Le CIN existe déjà, veuillez choisir un autre CIN.", Toast.LENGTH_SHORT).show();
                                                // Supprimer l'utilisateur créé
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                if (user != null) {
                                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> deleteTask) {
                                                            if (!deleteTask.isSuccessful()) {
                                                                // La suppression de l'utilisateur a échoué, gestion de l'erreur ici
                                                                Toast.makeText(inscrietudiant.this, "Erreur lors de la suppression de l'utilisateur", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }

                                            } else {
                                                // Le CIN est unique, ajoutez l'étudiant à la base de données
                                                Etudiant etudiant = new Etudiant(cinInt, lastNameText, firstNameText, emailText, passwordText);
                                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                                DatabaseReference referenceprofile = FirebaseDatabase.getInstance().getReference("Etudiants");
                                                referenceprofile.child(firebaseUser.getUid()).setValue(etudiant)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(inscrietudiant.this, "Etudiant enregistré avec succès", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(inscrietudiant.this, "Erreur lors de l'enregistrement des données dans la base de données", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Gestion des erreurs
                                            Toast.makeText(inscrietudiant.this, "Erreur lors de la vérification du CIN dans la base de données", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(inscrietudiant.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });



            }
        });
    }

    // Validation du CIN (8 chiffres)
    private boolean isValidCIN(String cin) {
        return cin.length() == 8 && cin.matches("\\d{8}");
    }

    // Validation du nom et du prénom
    private boolean isValidName(String name) {
        return name.length() > 2 && name.matches("[a-zA-ZÀ-ÖØ-öø-ÿ\\s']+");
    }

    // Validation de l'adresse e-mail
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
