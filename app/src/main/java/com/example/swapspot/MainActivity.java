package com.example.swapspot;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private CurentUserDatabase curentUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        curentUserDatabase = new CurentUserDatabase(this);
        String currentUser = curentUserDatabase.getCurrentUser();

        Intent intent;
        if (currentUser != null && !currentUser.isEmpty()) {
            intent = new Intent(this, HomeActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
