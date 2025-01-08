package com.example.pmo;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity {

    TextView SignUpLink;
    RadioButton RB_Teacher;
    RadioButton RB_Student;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SignUpLink = findViewById(R.id.SignUpLink);
        RB_Teacher = findViewById(R.id.RB_Teacher);
        RB_Student = findViewById(R.id.RB_Student);
        final EditText email= findViewById(R.id.Email);
        final EditText password= findViewById(R.id.Password);
        final Button submit= findViewById(R.id.Submit);

        mAuth = FirebaseAuth.getInstance();

        SignUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Choix.class);
                startActivity(intent);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();

                if (!isValidEmail(emailText)) {
                    Toast.makeText(MainActivity.this, "Veuillez entrer une adresse e-mail valide.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Récupérer l'utilisateur actuellement connecté
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        DatabaseReference reference;
                                        // Vérifier le rôle de l'utilisateur en fonction du chemin (Enseignants ou Etudiants)
                                        if (RB_Teacher.isChecked()) {
                                            reference = FirebaseDatabase.getInstance().getReference("Enseignants").child(firebaseUser.getUid());
                                        } else if (RB_Student.isChecked()) {
                                            reference = FirebaseDatabase.getInstance().getReference("Etudiants").child(firebaseUser.getUid());
                                        } else {
                                            // Si aucun rôle n'est sélectionné, affichez un message d'erreur
                                            Toast.makeText(MainActivity.this, "Veuillez sélectionner un rôle.", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        // Lire les données de l'utilisateur pour déterminer le rôle
                                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    // L'utilisateur existe dans le chemin spécifié (Enseignants ou Etudiants)
                                                    if (RB_Teacher.isChecked()) {
                                                        // Redirection vers la page enseignant
                                                        Intent intent = new Intent(MainActivity.this, pageenseignant.class);
                                                        startActivity(intent);
                                                    } else if (RB_Student.isChecked()) {
                                                        // Redirection vers la page étudiant
                                                        Intent intent = new Intent(MainActivity.this, pageetudiant.class);
                                                        startActivity(intent);
                                                    }
                                                } else {
                                                    // L'utilisateur n'existe pas dans le chemin spécifié
                                                    Toast.makeText(MainActivity.this, "Vous n'avez pas accès à ce rôle.", Toast.LENGTH_SHORT).show();
                                                    // Déconnecter l'utilisateur
                                                    mAuth.signOut();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                // Gestion des erreurs de lecture depuis la base de données
                                                Toast.makeText(MainActivity.this, "Erreur de lecture depuis la base de données.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } else {
                                    // Si la connexion échoue, afficher un message d'erreur
                                    Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });


    }

    // Validation de l'adresse e-mail
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}