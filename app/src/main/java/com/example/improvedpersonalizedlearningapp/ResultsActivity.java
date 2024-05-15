package com.example.improvedpersonalizedlearningapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    RecyclerView resultsRecyclerView;
    ResultsAdapter adapter;
    HistoryDBHelper dbHelper;
    AppCompatButton continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);


        dbHelper = new HistoryDBHelper(this);

        // Retrieve all data from the database
        List<History> historyData = dbHelper.getAllData();

        // Check if data is retrieved successfully
            if (historyData != null && !historyData.isEmpty()) {
                List<Result> results = new ArrayList<>();
                for (History item : historyData) {
                    String selectedAnswer = item.getUserAnswer(); // Retrieve user's answer from History
                    String correctAnswer = item.getCorrectAnswer();
                    boolean isCorrect = selectedAnswer.equals(correctAnswer);

                    results.add(new Result(selectedAnswer, correctAnswer, isCorrect));
            }

            // Set up RecyclerView
            resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
            adapter = new ResultsAdapter(results);
            resultsRecyclerView.setAdapter(adapter);
            resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultsActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }


}


