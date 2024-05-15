package com.example.improvedpersonalizedlearningapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GenerateTaskActivity extends AppCompatActivity {
    AppCompatButton submitButton;
    AppCompatButton translateButton;
    TextView textViewQuestions;
    RecyclerView questionsRecyclerView;
    private QuestionsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private QuestionService questionService;
    private Retrofit retrofit;
   HistoryDBHelper dbHelper;
   TextView textViewQuestionNumber;


    private static List<String> selectedAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_task);
        selectedAnswers = new ArrayList<>();

        dbHelper = new HistoryDBHelper(this);

        questionsRecyclerView = findViewById(R.id.questionsRecyclerView);
        submitButton = findViewById(R.id.submitButton);
        translateButton = findViewById(R.id.translateButton);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        questionsRecyclerView.setLayoutManager(layoutManager);

        fetchQuestions();


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Iterate through selectedAnswers list to get all selected answers
                for (String selectedAnswer : selectedAnswers) {
                    // Get the corresponding question from the adapter
                    int position = selectedAnswers.indexOf(selectedAnswer);
                    Question question = adapter.getQuestion(position);
                    if (question != null) {
                        // Extract question details
                        String questionText = question.getQuestion();
                        String correctAnswer = question.getCorrectAnswer();
                        List<String> incorrectAnswers = question.getIncorrectAnswers();

                        Log.d("Data to be inserted", "Question: " + questionText);
                        Log.d("Data to be inserted", "Correct Answer: " + correctAnswer);
                        Log.d("Data to be inserted", "Incorrect Answers: " + incorrectAnswers);
                        Log.d("Data to be inserted", "User Answer: " + selectedAnswer);

                        // Insert the question along with answers into the database
                        long newRowId = dbHelper.insertHistoryRecord(questionText, correctAnswer, incorrectAnswers, selectedAnswer);
                        if (newRowId == -1) {
                            Toast.makeText(getApplicationContext(), "Error: Failed to insert data into database", Toast.LENGTH_SHORT).show();
                        } else {
                            // Log data for successful insertion
                            Log.d("HistoryData", "Inserted data for Question: " + questionText);
                            Log.d("HistoryData", "\tCorrect Answer: " + correctAnswer);
                            Log.d("HistoryData", "\tIncorrect Answers: " + incorrectAnswers);
                            Log.d("HistoryData", "\tUser Answer: " + selectedAnswer);
                        }
                    }
                }

                Intent intent = new Intent(GenerateTaskActivity.this, ResultsActivity.class);
                startActivity(intent);
            }
        });

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an English-German translator:
                TranslatorOptions options =
                        new TranslatorOptions.Builder()
                                .setSourceLanguage(TranslateLanguage.ENGLISH)
                                .setTargetLanguage(TranslateLanguage.FRENCH)
                                .build();
                    final Translator englishFrenchTranslator =
                        Translation.getClient(options);
                getLifecycle().addObserver(englishFrenchTranslator);

                DownloadConditions conditions = new DownloadConditions.Builder()
                        .requireWifi()
                        .build();
                englishFrenchTranslator.downloadModelIfNeeded(conditions)
                        .addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        // Translation model downloaded successfully, now translate questions and answers
                                        List<Question> questions = adapter.getQuestions();
                                        if (questions != null) {
                                            for (int i = 0; i < questions.size(); i++) {
                                                Question question = questions.get(i); // Retrieve question at position i
                                                String questionNumber = adapter.getQuestionNumberText(i);
                                                String questionText = question.getQuestion();
                                                String correctAnswer = question.getCorrectAnswer();
                                                List<String> incorrectAnswers = question.getIncorrectAnswers();

                                                // Translate question text
                                                englishFrenchTranslator.translate(questionText)
                                                        .addOnSuccessListener(new OnSuccessListener<String>() {
                                                            @Override
                                                            public void onSuccess(String translatedQuestionText) {
                                                                // Update question text with translated text
                                                                question.setQuestion(translatedQuestionText);

                                                                // Translate correct answer
                                                                englishFrenchTranslator.translate(correctAnswer)
                                                                        .addOnSuccessListener(new OnSuccessListener<String>() {
                                                                            @Override
                                                                            public void onSuccess(String translatedCorrectAnswer) {
                                                                                // Update correct answer with translated text
                                                                                question.setCorrectAnswer(translatedCorrectAnswer);

                                                                                // Translate each incorrect answer
                                                                                for (int j = 0; j < incorrectAnswers.size(); j++) {
                                                                                    String incorrectAnswer = incorrectAnswers.get(j);
                                                                                    final int index = j; // final or effectively final variable required for use inside lambda
                                                                                    englishFrenchTranslator.translate(incorrectAnswer)
                                                                                            .addOnSuccessListener(new OnSuccessListener<String>() {
                                                                                                @Override
                                                                                                public void onSuccess(String translatedIncorrectAnswer) {
                                                                                                    // Update incorrect answer with translated text
                                                                                                    question.getIncorrectAnswers().set(index, translatedIncorrectAnswer);
                                                                                                    adapter.notifyDataSetChanged(); // Notify adapter of data change
                                                                                                }
                                                                                            })
                                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                                @Override
                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                    // Translation of incorrect answer failed, handle error
                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                // Translation of correct answer failed, handle error
                                                                            }
                                                                        });
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // Translation of question failed, handle error
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model download failed, handle the error
                            }
                        });


                RemoteModelManager modelManager = RemoteModelManager.getInstance();

// Get translation models stored on the device.
                modelManager.getDownloadedModels(TranslateRemoteModel.class)
                        .addOnSuccessListener(new OnSuccessListener<Set<TranslateRemoteModel>>() {
                            @Override
                            public void onSuccess(Set<TranslateRemoteModel> translateRemoteModels) {
                                // Handle the downloaded translation models
                            }
                        });
            }
        });
    }

    public static void onAnswerClicked(View view) {
        // Cast the clicked view to a RadioButton
        RadioButton radioButton = (RadioButton) view;

        // Check if the RadioButton is checked
        if (radioButton.isChecked()) {
            // Get the text of the selected answer
            String selectedAnswer = radioButton.getText().toString();
            // Add the selected answer to the list of selected answers
            selectedAnswers.add(selectedAnswer);
        }
    }

    public void fetchQuestions() {
        // Create Retrofit instance
        retrofit = new Retrofit.Builder()
                .baseUrl("https://opentdb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create QuestionService instance
        questionService = retrofit.create(QuestionService.class);

        // Make API call to get questions
        Call<QuestionApiResponse> call = questionService.getQuestions();
        call.enqueue(new Callback<QuestionApiResponse>() {
            @Override
            public void onResponse(Call<QuestionApiResponse> call, Response<QuestionApiResponse> response) {
                if (response.isSuccessful()) {
                    QuestionApiResponse questionApiResponse = response.body();
                    if (questionApiResponse != null) {
                        List<Question> questions = questionApiResponse.getResults();
                        adapter = new QuestionsAdapter(questions, GenerateTaskActivity.this);
                        questionsRecyclerView.setAdapter(adapter);

                        // Log out each question along with its incorrect answers
                        for (Question question : questions) {
                            Log.d("Question fetched from the API", question.getQuestion());
                            List<String> incorrectAnswers = question.getIncorrectAnswers();
                            Log.d("Incorrect Answers fetched from the API", incorrectAnswers.toString());
                        }
                    } else {
                        textViewQuestions.setText("Response body is null.");
                    }
                } else {
                    textViewQuestions.setText("Failed to fetch questions. Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<QuestionApiResponse> call, Throwable t) {
                textViewQuestions.setText("Failed to fetch questions. Error: " + t.getMessage());
            }
        });
    }
}
