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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

import java.util.Arrays;
import java.util.List;

public class inscri extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscri);

        final EditText lastname= findViewById(R.id.LastName);
        final EditText firstname= findViewById(R.id.FirstName);
        final EditText email= findViewById(R.id.Email);
        final EditText password= findViewById(R.id.Password);
        final EditText mat= findViewById(R.id.Mat);
        final Button submit= findViewById(R.id.Submit);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lastNameText = lastname.getText().toString();
                String firstNameText = firstname.getText().toString();
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();
                String matText = mat.getText().toString();

                if (!isValidName(lastNameText) || !isValidName(firstNameText)) {
                    Toast.makeText(inscri.this, "Le nom et le prénom doivent contenir au moins 3 caractères alphabétiques.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidEmail(emailText)) {
                    Toast.makeText(inscri.this, "Veuillez entrer une adresse e-mail valide.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidMat(matText)) {
                    Toast.makeText(inscri.this, "La matière n'est pas valide.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (passwordText.length() < 6) {
                    Toast.makeText(inscri.this, "Le mot de passe doit contenir au moins 6 caractères.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {          // event listener pour la verification de l'authentication
                                if (task.isSuccessful()) {
                                    List<String> matieresList = Arrays.asList(matText.split("\\s*,\\s*"));
                                    Enseignant enseignant = new Enseignant(lastNameText, firstNameText, emailText, passwordText, matieresList);

                                    FirebaseUser firebaseUser=mAuth.getCurrentUser();
                                    DatabaseReference referenceprofile= FirebaseDatabase.getInstance().getReference("Enseignants");
                                    referenceprofile.child(firebaseUser.getUid()).setValue(enseignant)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {               //event listent pour l'adding in realtimedatabase
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(inscri.this, "Enseignant enregistré avec succès", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        // L'écriture dans la base de données a échoué
                                                        Toast.makeText(inscri.this, "Erreur lors de l'enregistrement des données dans la base de données", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(inscri.this, "Authentication failed: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });
    }

    // Validation du nom et du prénom
    private boolean isValidName(String name) {
        return name.length() > 2 && name.matches("[a-zA-ZÀ-ÖØ-öø-ÿ\\s']+");
    }

    // Validation de l'adresse e-mail
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Validation de la matière
    private boolean isValidMat(String mat) {
        String[] matieresArray = mat.split("\\s*,\\s*");

        for (String matiere : matieresArray) {
            if (!matiere.matches("[a-zA-ZÀ-ÖØ-öø-ÿ\\s']+")) {
                return false;
            }
        }
        return true;
    }
}