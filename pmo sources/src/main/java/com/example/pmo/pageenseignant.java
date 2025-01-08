package com.example.pmo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class pageenseignant extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spin;
    private Button validateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pageenseignant);

        spin = findViewById(R.id.spin);
        validateButton=findViewById(R.id.validateButton);
        // Récupérer l'utilisateur actuellement authentifié
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Référence à la base de données
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Enseignants").child(uid);

            // Ajouter un écouteur pour les changements de données
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Récupérer les données de l'enseignant
                        String firstName = snapshot.child("firstName").getValue(String.class);
                        String lastName = snapshot.child("lastName").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String password = snapshot.child("password").getValue(String.class);

                        // Récupérer la liste de matières
                        List<String> matieresList = new ArrayList<>();
                        for (DataSnapshot matiereSnapshot : snapshot.child("matieres").getChildren()) {
                            String matiere = matiereSnapshot.getValue(String.class);
                            matieresList.add(matiere);

                            // Créer un adaptateur pour le Spinner
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(pageenseignant.this, android.R.layout.simple_spinner_item, matieresList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // Appliquer l'adaptateur au Spinner
                            spin.setAdapter(adapter);
                        }
                    Enseignant enseignant= new Enseignant(lastName,firstName,email,password,matieresList);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Gérer les erreurs de lecture depuis la base de données
                }
            });

            validateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        // Récupérer la matière sélectionnée
                        String selectedMatiere = spin.getSelectedItem().toString();
                        // Passer à la nouvelle activité avec la matière sélectionnée
                        Intent intent = new Intent(pageenseignant.this, matiereenseignantcsv.class);
                        intent.putExtra("selectedMatiere", selectedMatiere);
                        startActivity(intent);

                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}