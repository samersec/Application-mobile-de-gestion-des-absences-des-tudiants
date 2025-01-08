package com.example.pmo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class AjouterEtudiantActivity extends AppCompatActivity {

    private EditText CIN, lastname, firstname;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_etudiant);

        // Récupérer l'intent
        Intent in = getIntent();

        // Vérifier si l'intent contient la clé "selectedMatiere"
        if (in.hasExtra("selectedMatiere")) {
            // Récupérer la matière
            String selectedMatiere = in.getStringExtra("selectedMatiere");
            // Chemin du fichier CSV
            String filePath = getFilesDir() + "/" + selectedMatiere + ".csv";

            // Vérifier si le fichier existe
            File file = new File(filePath);
            List<String[]> data = readCSV(file);

            // Initialisez les vues manquantes (à définir dans votre fichier XML)
            CIN = findViewById(R.id.CIN);
            lastname = findViewById(R.id.LastName);
            firstname = findViewById(R.id.FirstName);
            submit = findViewById(R.id.Submit);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cinText = CIN.getText().toString();
                    String lastNameText = lastname.getText().toString();
                    String firstNameText = firstname.getText().toString();
                    int cinInt;

                    try {
                        cinInt = Integer.parseInt(cinText);
                    } catch (NumberFormatException e) {
                        // La conversion a échoué, la chaîne n'est pas un nombre valide
                        Toast.makeText(AjouterEtudiantActivity.this, "Le CIN doit être un nombre valide.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Validation du CIN (8 chiffres)
                    if (!isValidCIN(cinText)) {
                        Toast.makeText(AjouterEtudiantActivity.this, "Le CIN doit comporter 8 chiffres.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Validation du nom et du prénom
                    if (!isValidName(lastNameText) || !isValidName(firstNameText)) {
                        Toast.makeText(AjouterEtudiantActivity.this, "Le nom et le prénom doivent contenir au moins 3 caractères alphabétiques.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!isCINUnique(cinText, file)) {
                        Toast.makeText(AjouterEtudiantActivity.this, "Le CIN doit être unique.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Ajouter l'étudiant au fichier CSV
                    String[] newStudent = {cinText, lastNameText, firstNameText};
                    appendToCSV(file, newStudent);
                    // Enregistrez le CIN dans Firebase Realtime Database
                    saveCINToFirebase(selectedMatiere);
                    Toast.makeText(AjouterEtudiantActivity.this, "Ajout réussi.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AjouterEtudiantActivity.this, matiereenseignantcsv.class);
                    intent.putExtra("selectedMatiere", selectedMatiere);
                    startActivity(intent);
                    // Ajouter le reste de votre logique ici (redirection, etc.)
                }
            });

        }
    }

    private List<String[]> readCSV(File file) {
        // Utiliser OpenCSV pour lire les données du fichier CSV
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            return reader.readAll();
        } catch (IOException | CsvException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Validation du CIN (8 chiffres)
    private boolean isValidCIN(String cin) {
        return cin.length() == 8 && cin.matches("\\d{8}");
    }

    // Validation du nom et du prénom
    private boolean isValidName(String name) {
        return name.length() > 2 && name.matches("[a-zA-ZÀ-ÖØ-öø-ÿ\\s']+");
    }


    private boolean isCINUnique(String cin, File file) {
        List<String[]> data = readCSV(file);

        if (data != null) {
            // Parcourir les lignes du fichier CSV et vérifier si le CIN existe déjà
            for (String[] row : data) {
                if (row.length > 0 && row[0].equals(cin)) {
                    // Le CIN existe déjà, il n'est pas unique
                    return false;
                }
            }
        }
        // Le CIN est unique
        return true;
    }

    private void appendToCSV(File file, String[] newStudent) {
        try {
            // Créer un FileWriter avec l'option d'ajouter à la fin du fichier
            FileWriter fileWriter = new FileWriter(file, true);

            // Créer un CSVWriter
            CSVWriter csvWriter = new CSVWriter(fileWriter);

            // Ajouter la nouvelle ligne avec les données de l'étudiant et 16 champs vides
            String[] data = new String[newStudent.length + 16];
            System.arraycopy(newStudent, 0, data, 0, newStudent.length);

            // Remplir les champs vides avec des chaînes vides
            for (int i = newStudent.length; i < data.length; i++) {
                data[i] = "";
            }

            // Écrire la ligne dans le fichier CSV
            csvWriter.writeNext(data,false);

            // Fermer le CSVWriter
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveCINToFirebase(String selectedMatiere) {
        // Obtenez la référence de la base de données Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Construisez le chemin dans la base de données pour enregistrer le CIN
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String path = "Matieres/" + uid +"/" + selectedMatiere ;
    }



}
