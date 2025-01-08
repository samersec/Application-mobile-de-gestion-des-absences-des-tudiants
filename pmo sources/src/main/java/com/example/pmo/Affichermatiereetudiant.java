package com.example.pmo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.util.List;

public class Affichermatiereetudiant extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matiereenseignantcsv);

        // Récupérer l'intent
        Intent in = getIntent();

        // Vérifier si l'intent contient la clé "selectedMatiere"
        if (in.hasExtra("selectedMatiere")) {
            // Récupérer la matière
            String selectedMatiere = in.getStringExtra("selectedMatiere");

            // Afficher la matière dans un TextView
            TextView matiereTextView = findViewById(R.id.matiereTextView);
            matiereTextView.setText(selectedMatiere);

            // Chemin du fichier CSV
            String filePath = getFilesDir() + "/" + selectedMatiere + ".csv";

            // Vérifier si le fichier existe
            File file = new File(filePath);

            if (file.exists()) {
                // Lire et afficher les données du fichier CSV existant
                List<String[]> data = readCSV(file);
                // ... Afficher les données dans votre interface utilisateur

                // Utiliser TableLayout pour afficher les données dans des TableRow
                TableLayout tableLayout = findViewById(R.id.tableLayout);

                for (String[] row : data) {
                    TableRow tableRow = new TableRow(this);

                    for (String cell : row) {
                        TextView textView = new TextView(this);
                        textView.setText(cell);
                        tableRow.addView(textView);
                    }

                    tableLayout.addView(tableRow);
                }
            }
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

}
