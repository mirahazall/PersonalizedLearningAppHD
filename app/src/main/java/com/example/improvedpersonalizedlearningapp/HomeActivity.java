package com.example.improvedpersonalizedlearningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    TextView textViewDescription;
    TextView textViewYourName;
    private InterestDataSource dataSource;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        textViewYourName = findViewById(R.id.textViewYourName);
        // Retrieve the data from the intent and set the retrieved data to the TextView
        Intent intent = getIntent();
        if (intent != null) {
            String yourName = intent.getStringExtra("username");
            if (yourName != null) {
                textViewYourName.setText(yourName);
            }
        }

        dataSource = new InterestDataSource(this);
        dataSource.open();

        String selectedInterest = dataSource.getSelectedInterest();
        if (selectedInterest != null) {
            textViewDescription = findViewById(R.id.textViewDescription);
            textViewDescription.setText("This task involves 3 multiple choice questions related to one of your interest topics: " + selectedInterest);
        }
    }

    public void onGeneratedTaskClicked(View view) {
        Intent intent = new Intent(this, GenerateTaskActivity.class);
        startActivity(intent);
        finish();
    }
}
