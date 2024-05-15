package com.example.improvedpersonalizedlearningapp;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.QuestionsViewHolder> {

    private List<Question> questions;

    private GenerateTaskActivity activity; // Instance of GenerateTaskActivity



    public QuestionsAdapter(List<Question> questions, GenerateTaskActivity activity)

    {
        this.questions = questions;
        this.activity = activity;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    @NonNull
    @Override
    public QuestionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionsViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.textViewQuestionNumber.setText("Question " + (position + 1));
        holder.textViewQuestion.setText(question.getQuestion());
        holder.radioGroupAnswers.removeAllViews();

        // Log out correct and incorrect answers
        Log.d("Correct Answer from adapter", question.getCorrectAnswer());
        Log.d("Incorrect Answers from adapter", "Incorrect Answers from adapter:");
        for (String incorrectAnswer : question.getIncorrectAnswers()) {
            Log.d("Incorrect Answers from adapter", incorrectAnswer);
        }

        // Add RadioButtons for incorrect answers
        for (String incorrectAnswer : question.getIncorrectAnswers()) {
            RadioButton radioButton = new RadioButton(holder.itemView.getContext());
            radioButton.setText(incorrectAnswer); // Set the text for the RadioButton
            radioButton.setTextSize(16); // Set text size
            radioButton.setTextColor(Color.WHITE); // Set text color
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onAnswerClicked(v);  // Call onAnswerClicked method on the instance of GenerateTaskActivity
                }
            });
            radioButton.setTag(holder.getAdapterPosition()); // Set the tag to the current position
            holder.radioGroupAnswers.addView(radioButton); // Add RadioButton to the RadioGroup
        }

        // Add RadioButton for correct answer
        RadioButton correctAnswerRadioButton = new RadioButton(holder.itemView.getContext());
        correctAnswerRadioButton.setText(question.getCorrectAnswer()); // Set the text for the RadioButton
        correctAnswerRadioButton.setTextSize(16); // Set text size
        correctAnswerRadioButton.setTextColor(Color.WHITE); // Set text color
        correctAnswerRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onAnswerClicked(v); // Call onAnswerClicked method on the instance of GenerateTaskActivity
            }
        });
        correctAnswerRadioButton.setTag(holder.getAdapterPosition()); // Set the tag to the current position
        holder.radioGroupAnswers.addView(correctAnswerRadioButton); // Add RadioButton to the RadioGroup

    }


    @Override
    public int getItemCount() {
        return (questions.size());
    }

    // Method to get a question at a specific position
    public Question getQuestion(int position) {
        if (position >= 0 && position < questions.size()) {
            return questions.get(position);
        } else {
            return null;
        }
    }

    public String getQuestionNumberText(int position) {
        if (position >= 0 && position < questions.size()) {
            return "Question " + (position + 1) + ": " + questions.get(position).getQuestion();
        } else {
            return "";
        }
    }



    static class QuestionsViewHolder extends RecyclerView.ViewHolder{
        RadioGroup radioGroupAnswers;
        CardView cardViewQuestion;
        TextView textViewQuestionNumber;
        TextView textViewQuestion;
        RadioButton radioButtonAnswer1;


        QuestionsViewHolder(@NonNull View itemView){
            super(itemView);
            radioGroupAnswers = itemView.findViewById(R.id.radioGroupAnswers);
            cardViewQuestion = itemView.findViewById(R.id.cardViewQuestion);
            textViewQuestionNumber = itemView.findViewById(R.id.textViewQuestionNumber);
            textViewQuestion = itemView.findViewById(R.id.textViewQuestion);
            radioButtonAnswer1 = itemView.findViewById(R.id.radioButtonAnswer1);

        }
    }
}