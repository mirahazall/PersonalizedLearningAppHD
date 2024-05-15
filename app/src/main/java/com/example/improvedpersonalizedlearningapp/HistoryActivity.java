package com.example.improvedpersonalizedlearningapp;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    RecyclerView historyRecyclerView;
    private LinearLayoutManager layoutManager;
    HistoryDBHelper dbHelper;
    HistoryAdapter adapter;
    AppCompatButton deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        // Initialize dbHelper
        dbHelper = new HistoryDBHelper(this);

        // Retrieve data from the database after dbHelper initialization
        List<History> data = dbHelper.getAllData();

        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        historyRecyclerView.setLayoutManager(layoutManager);
        // Set up the RecyclerView with the adapter
        adapter = new HistoryAdapter(data);
        historyRecyclerView.setAdapter(adapter);

        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteAllData();
            }
        });

    }
}
