package com.example.improvedpersonalizedlearningapp;

import retrofit2.Call;
import retrofit2.http.GET;

// Define a Retrofit interface for the API
public interface QuestionService {
    @GET("api.php?amount=3")
    Call<QuestionApiResponse> getQuestions();
}

