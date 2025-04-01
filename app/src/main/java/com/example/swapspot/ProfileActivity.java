package com.example.swapspot;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private CurentUserDatabase currentUserDb;
    private DatabaseUserHelper userDbHelper;
    private DatabaseHelper databaseHelper;
    private TextView currentUserName;
    private EditText editUserName, editUserPassword;
    private Button saveUserChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Инициализация баз данных
        currentUserDb = new CurentUserDatabase(this);
        userDbHelper = new DatabaseUserHelper(this);
        databaseHelper = new DatabaseHelper(this);

        // Привязка элементов UI
        currentUserName = findViewById(R.id.currentUserName);
        editUserName = findViewById(R.id.editUserName);
        editUserPassword = findViewById(R.id.editUserPassword);
        saveUserChanges = findViewById(R.id.saveUserChanges);

        // Загрузка текущего пользователя
        loadCurrentUser();

        // Обработчик кнопки сохранения изменений
        saveUserChanges.setOnClickListener(v -> updateUser());
    }

    private void loadCurrentUser() {
        String currentUser = currentUserDb.getCurrentUser();
        if (currentUser != null) {
            currentUserName.setText(currentUser);
        } else {
            currentUserName.setText("Не найден");
        }
    }

    private void updateUser() {
        String newUsername = editUserName.getText().toString().trim();
        String newPassword = editUserPassword.getText().toString().trim();

        if (newUsername.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Введите новое имя и пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        // Получаем текущего пользователя
        String oldUsername = currentUserDb.getCurrentUser();
        if (oldUsername == null) {
            Toast.makeText(this, "Ошибка: текущий пользователь не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        // Удаляем старого пользователя и добавляем нового в CurrentUserDatabase
        currentUserDb.addUser(newUsername, newPassword);

        // Обновляем данные в DatabaseUserHelper
        if (userDbHelper.isUserExists(oldUsername)) {
            SQLiteDatabase db = userDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", newUsername);
            values.put("password", newPassword);
            db.update("users", values, "username=?", new String[]{oldUsername});
            db.close();
        } else {
            // Если пользователь не найден в основной БД, просто регистрируем его
            userDbHelper.registerUser(newUsername, newPassword);
        }

        // Обновляем все элементы в таблице items, принадлежащие пользователю
        SQLiteDatabase dbItems = databaseHelper.getWritableDatabase();
        ContentValues itemValues = new ContentValues();
        itemValues.put("user_name", newUsername);
        dbItems.update("items", itemValues, "user_name=?", new String[]{oldUsername});
        dbItems.close();

        Toast.makeText(this, "Данные обновлены!", Toast.LENGTH_SHORT).show();
        loadCurrentUser();
    }

    public void onHomeButtonClick(View view) {
        Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    public void onFavoriteButtonClick(View view) {
        Intent intent = new Intent(ProfileActivity.this, MyExchange.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    public void onProfileButtonClick(View view) {
        Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}
