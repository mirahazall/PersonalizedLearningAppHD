package com.example.improvedpersonalizedlearningapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HistoryDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "history.db";
    private static final int DATABASE_VERSION = 9;

    // Table name and column names for the history table
    public static final String TABLE_HISTORY = "history";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_QUESTION = "question";
    public static final String COLUMN_CORRECT_ANSWER = "correct_answer";
    public static final String COLUMN_INCORRECT_ANSWER = "incorrect_answer";
    private static final String COLUMN_USER_ANSWER = "user_answer";

    // SQL statement to create the history table
    private static final String SQL_CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_QUESTION + " TEXT," +
            COLUMN_CORRECT_ANSWER + " TEXT," +
            COLUMN_INCORRECT_ANSWER + " TEXT," +
            COLUMN_USER_ANSWER + " TEXT)";


    public HistoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create history table
        db.execSQL(SQL_CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        // Recreate the tables
        onCreate(db);
    }

    // Method to insert a new history record into the database
    public long insertHistoryRecord(String question, String correctAnswer, List<String> incorrectAnswers, String userAnswer) {
        SQLiteDatabase db = this.getWritableDatabase();
        long newRowId = -1; // Initialize newRowId to indicate insertion failure

        try {
            // Begin transaction
            db.beginTransaction();

            // Concatenate incorrect answers into a single comma-separated string
            String concatenatedIncorrectAnswers = TextUtils.join(",", incorrectAnswers);

            // Insert the record for the question into the database
            ContentValues values = new ContentValues();
            values.put(COLUMN_QUESTION, question);
            values.put(COLUMN_CORRECT_ANSWER, correctAnswer);
            values.put(COLUMN_INCORRECT_ANSWER, concatenatedIncorrectAnswers); // Add incorrect answers
            values.put(COLUMN_USER_ANSWER, userAnswer);
            newRowId = db.insert(TABLE_HISTORY, null, values);
            // Log the inserted data
            Log.d("InsertedData", "New Row ID: " + newRowId +
                    ", Question: " + question +
                    ", Correct Answer: " + correctAnswer +
                    ", Incorrect Answers: " + Arrays.toString(incorrectAnswers.toArray()));

            // Set transaction successful and end transaction
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("HistoryDBHelper", "Error inserting history record: " + e.getMessage());
        } finally {
            // End transaction
            db.endTransaction();
            // Close the database connection
            db.close();
        }

        return newRowId;
    }

    // Method to retrieve all history data from the database
    public List<History> getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HISTORY, null);
        List<History> data = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int questionTextIndex = cursor.getColumnIndex(COLUMN_QUESTION);
                int correctAnswerIndex = cursor.getColumnIndex(COLUMN_CORRECT_ANSWER);
                int incorrectAnswerIndex = cursor.getColumnIndex(COLUMN_INCORRECT_ANSWER); // Index for incorrect answers
                int userAnswerIndex = cursor.getColumnIndex(COLUMN_USER_ANSWER); // Index for user answers

                if (questionTextIndex != -1 && correctAnswerIndex != -1 && incorrectAnswerIndex != -1 && userAnswerIndex != -1) {
                    String questionText = cursor.getString(questionTextIndex);
                    String correctAnswer = cursor.getString(correctAnswerIndex);
                    String incorrectAnswersString = cursor.getString(incorrectAnswerIndex); // Retrieve incorrect answers string
                    List<String> incorrectAnswers = Arrays.asList(incorrectAnswersString.split(",")); // Convert comma-separated string to list
                    String userAnswer = cursor.getString(userAnswerIndex); // Retrieve user answer
                    History history = new History(questionText, correctAnswer, incorrectAnswers, userAnswer);
                    data.add(history);
                    // Log the saved history data
                    Log.d("GetAllData", "Question Text: " + history.getQuestionText() +
                            ", Correct Answer: " + history.getCorrectAnswer() +
                            ", Incorrect Answers: " + Arrays.toString(incorrectAnswers.toArray()) +
                            ", User Answer: " + userAnswer);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return data;
    }

    // Method to delete all data from the history table
    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, null, null); // Delete all rows from the history table
        db.close();
        Log.d("HistoryDBHelper", "All data deleted from history table.");
    }
}



