package com.example.swapspot;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private CurentUserDatabase curentUserDatabase;
    private DatabaseUserHelper databaseUserHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        curentUserDatabase = new CurentUserDatabase(this);
        databaseUserHelper = new DatabaseUserHelper(this);
        Button btnAboutDeveloper = findViewById(R.id.btn_about_developer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        btnAboutDeveloper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutDialog();
            }
        });

        Button logoutButton = findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(v -> logoutUser());

        Button deleteAccountButton = findViewById(R.id.btn_delete_account);
        deleteAccountButton.setOnClickListener(v -> deleteAccount());
    }

    private void logoutUser() {
        SQLiteDatabase db = curentUserDatabase.getWritableDatabase();
        db.delete("users", null, null);
        db.close();

        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void deleteAccount() {
        String currentUser = curentUserDatabase.getCurrentUser();
        if (currentUser != null) {
            SQLiteDatabase dbUsers = databaseUserHelper.getWritableDatabase();
            dbUsers.delete("users", "username = ?", new String[]{currentUser});
            dbUsers.close();

            logoutUser();
            Toast.makeText(this, "Аккаунт удалён", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("О разработчике")
                .setMessage("Разработчик: Шпак Костя\nВерсия приложения: 1.0")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void onHomeButtonClick(View view) {
        Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    public void onFavoriteButtonClick(View view) {
        Intent intent = new Intent(SettingsActivity.this, MyExchange.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    public void onBasketButtonClick(View view) {
        Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}
