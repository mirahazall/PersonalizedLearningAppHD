package com.example.improvedpersonalizedlearningapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class InterestsActivity extends AppCompatActivity {
    private String selectedInterest;
    private InterestDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);
        dataSource = new InterestDataSource(this);
        dataSource.open();
    }

    public void onButtonClick(View view) {
        // Retrieve the text from the clicked button
        selectedInterest = ((AppCompatButton) view).getText().toString();
    }

    public void onNextButtonClick(View view) {
        if (selectedInterest != null) {
            // Save the selected interest to the database
            long result = dataSource.addInterest(selectedInterest);

            if (result != -1) {
                Toast.makeText(this, "Insertion successful", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Insertion failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please select an interest", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }

}
