package com.example.swapspot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private DatabaseUserHelper databaseHelper;
    private CurentUserDatabase curentUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseUserHelper(this);
        curentUserDatabase = new CurentUserDatabase(this);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonRegister = findViewById(R.id.buttonRegister);

        String dbPath = getDatabasePath("BaseOfUsers.db").getAbsolutePath();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Пожалуйста, введите имя пользователя и пароль", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (databaseHelper.loginUser(username, password)) {
                    Toast.makeText(LoginActivity.this, "Вход выполнен успешно", Toast.LENGTH_SHORT).show();
                    curentUserDatabase.addUser(username, password);
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Ошибка входа! Проверьте данные.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Пожалуйста, введите имя пользователя и пароль", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isRegistered = databaseHelper.registerUser(username, password);

                if (isRegistered) {
                    curentUserDatabase.addUser(username, password);
                    Toast.makeText(LoginActivity.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Ошибка регистрации! Пользователь с таким именем уже существует.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}