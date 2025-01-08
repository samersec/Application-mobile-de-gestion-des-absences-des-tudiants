package com.example.pmo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Choix extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix);

        final Button teacher= findViewById(R.id.Button_Teacher);
        final Button student= findViewById(R.id.Button_Student);

        //vers inscri enseignant
        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Choix.this, inscri.class);
                startActivity(intent);
            }
        });

        //vers inscri etudiant:
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Choix.this, inscrietudiant.class);
                startActivity(intent);
            }
        });
    }
}