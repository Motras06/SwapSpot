package com.example.swapspot;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MyExchange extends AppCompatActivity {
    ArrayList<Echange> echanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_exchange);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setInitialData();
        // получаем элемент ListView
        GridView gridView = findViewById(R.id.countriesGrid);
        // создаем адаптер
        EchangeAdapter echangeAdapter = new EchangeAdapter(this, R.layout.list_item, echanges);
        // устанавливаем адаптер
        gridView.setAdapter(echangeAdapter);
        // слушатель выбора в списке
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Echange selectedItem = echanges.get(position);
                Intent intent = new Intent(MyExchange.this, ThisItemActivity.class);
                intent.putExtra("ITEM_ID", selectedItem.getId()); // Передача ID
                startActivity(intent);
            }
        };
        gridView.setOnItemClickListener(itemListener);
    }

    public void onHomeButtonClick(View view) {
        Intent intent = new Intent(MyExchange.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    public void onBasketButtonClick(View view) {
        Intent intent = new Intent(MyExchange.this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    public void onProfileButtonClick(View view) {
        Intent intent = new Intent(MyExchange.this, SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void setInitialData() {
        echanges = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        CurentUserDatabase userDbHelper = new CurentUserDatabase(this);

        String currentUser = userDbHelper.getCurrentUser();
        if (currentUser == null) {
            return;  // Если нет текущего пользователя, просто выходим
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM items WHERE user_name = ?", new String[]{currentUser});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id")); // Получаем ID
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow("item_name"));
                String userName = cursor.getString(cursor.getColumnIndexOrThrow("user_name"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));

                echanges.add(new Echange(id, itemName, userName, imagePath)); // Передаем ID в конструктор
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        dbHelper.close();
        userDbHelper.close();
    }



    public void onAddButtonClick(View view) {
        Intent intent = new Intent(MyExchange.this, AddActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    public void onDeleteButtonClick(View view) {
        Intent intent = new Intent(MyExchange.this, DeleteActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}