package com.example.improvedpersonalizedlearningapp;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class ActivityUpgrade extends AppCompatActivity{
        AppCompatButton cardViewPurchaseButton;


        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_upgrade);


            cardViewPurchaseButton = findViewById(R.id.cardViewPurchaseButton);
            cardViewPurchaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PurchaseFragment fragment = new PurchaseFragment();

                    // Begin the fragment transaction
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();

                    // Replace the current fragment/container with the new fragment
                    transaction.replace(R.id.fragment_container, fragment);

                    // Add the transaction to the back stack
                    transaction.addToBackStack(null);

                    // Commit the transaction
                    transaction.commit();
                }
            });


        }
   /* private void sendTokenToServer(Token token) {
        ApiService apiService = RetrofitClient.getClient();
        Call<ApiResponse> call = apiService.processPayment(token.getId(), amount, currency);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    // Handle the response from the server
                } else {
                    // Handle server error
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Handle network error
            }
        });
    }

    */

}




