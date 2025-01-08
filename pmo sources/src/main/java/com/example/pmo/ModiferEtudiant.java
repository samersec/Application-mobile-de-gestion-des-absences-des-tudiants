package com.example.pmo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ModiferEtudiant extends AppCompatActivity {

    private EditText CIN;
    private Button submit, rechercher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifer_etudiant); // Assurez-vous que le fichier XML est correctement référencé

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

            CIN = findViewById(R.id.CIN);
            submit = findViewById(R.id.Submit);
            rechercher = findViewById(R.id.rechercherButton);

            rechercher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cinText = CIN.getText().toString();

                    // Validation du CIN (8 chiffres)
                    if (!isValidCIN(cinText)) {
                        Toast.makeText(ModiferEtudiant.this, "Le CIN doit comporter 8 chiffres.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Vérifier si l'étudiant avec le CIN spécifié existe dans le fichier CSV
                    String[] studentData = findStudentByCIN(cinText, data);

                    if (studentData == null) {
                        // Étudiant introuvable, afficher le message
                        Toast.makeText(ModiferEtudiant.this, "Étudiant introuvable. Veuillez saisir un CIN correct.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Remplir les TextView avec les données de l'étudiant
                    TextView nomTextView = findViewById(R.id.LastName);
                    TextView prenomTextView = findViewById(R.id.Prenom);

                    nomTextView.setText(studentData[1]); // L'index 1 correspond à la colonne "Nom" dans le fichier CSV
                    prenomTextView.setText(studentData[2]); // L'index 2 correspond à la colonne "Prenom" dans le fichier CSV

                    // Remplir les EditText avec les données des notes de l'étudiant
                    for (int i = 0; i < 14; i++) {
                        int editTextId = getResources().getIdentifier("S" + (i + 1), "id", getPackageName());
                        EditText editText = findViewById(editTextId);
                        editText.setText(studentData[i + 3]); // Les index 3 à 16 correspondent aux colonnes "S1" à "S14" dans le fichier CSV
                    }
                }
            });

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Ajouter la logique pour soumettre les modifications de l'étudiant
                    String cinText = CIN.getText().toString();

                    // Validation du CIN (8 chiffres)
                    if (!isValidCIN(cinText)) {
                        Toast.makeText(ModiferEtudiant.this, "Le CIN doit comporter 8 chiffres.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Vérifier si l'étudiant avec le CIN spécifié existe dans le fichier CSV
                    String[] studentData = findStudentByCIN(cinText, data);

                    if (studentData == null) {
                        // Étudiant introuvable, afficher le message
                        Toast.makeText(ModiferEtudiant.this, "Étudiant introuvable. Veuillez saisir un CIN correct.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Mettre à jour les notes de l'étudiant avec les nouvelles valeurs
                    for (int i = 0; i < 14; i++) {
                        int editTextId = getResources().getIdentifier("S" + (i + 1), "id", getPackageName());
                        EditText editText = findViewById(editTextId);
                        studentData[i + 3] = editText.getText().toString(); // Mettre à jour les notes dans le tableau des données de l'étudiant
                    }

                    // Calculer le nombre d'absences
                    int nbrAbsences = calculateNbrAbsences(studentData);

                    // Mettre à jour le tableau des données de l'étudiant avec le nombre d'absences
                    studentData[17] = String.valueOf(nbrAbsences);

                    // Mettre à jour l'état de l'étudiant
                    updateStudentState(studentData);

                    // Mettre à jour le fichier CSV avec les nouvelles données
                    updateCSV(file, data);

                    // Afficher un message de succès
                    Toast.makeText(ModiferEtudiant.this, "Modifications enregistrées avec succès.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ModiferEtudiant.this, matiereenseignantcsv.class);
                    intent.putExtra("selectedMatiere", selectedMatiere);
                    startActivity(intent);
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

    // Méthode pour trouver l'étudiant par CIN dans la liste des données du fichier CSV
    private String[] findStudentByCIN(String cin, List<String[]> data) {
        if (data != null) {
            for (String[] row : data) {
                if (row.length > 0 && row[0].equals(cin)) {
                    return row; // Retourner les données de l'étudiant s'il est trouvé
                }
            }
        }
        return null; // Retourner null si l'étudiant n'est pas trouvé
    }

    // Méthode pour mettre à jour le fichier CSV avec les nouvelles données
    private void updateCSV(File file, List<String[]> data) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            writer.writeAll(data); // Écrire toutes les données dans le fichier CSV
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour calculer le nombre d'absences
    private int calculateNbrAbsences(String[] studentData) {
        int nbrAbsences = 0;
        for (int i = 3; i <= 16; i++) {
            // Si la note est égale à "A", incrémenter le nombre d'absences
            if (studentData[i].equalsIgnoreCase("A")) {
                nbrAbsences++;
            }
        }
        return nbrAbsences;
    }

    // Méthode pour mettre à jour l'état de l'étudiant en fonction des règles spécifiées
    private void updateStudentState(String[] studentData) {
        int nbrAbsences = Integer.parseInt(studentData[17]);
        int semaines=14;

        // Calculer le pourcentage d'absences
        double pourcentageAbsences = ((double) nbrAbsences / semaines) * 100;
        // Mettre à jour l'état en fonction des règles spécifiées
        if (pourcentageAbsences > 50) {
            studentData[18] = "Exclu des deux sessions";
        } else if (pourcentageAbsences > 20) {
            studentData[18] = "Exclu de la session principale";
        } else {
            studentData[18] = "Admis";
        }
    }
}
