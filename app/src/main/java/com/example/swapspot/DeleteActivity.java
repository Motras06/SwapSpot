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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class DeleteActivity extends AppCompatActivity {

    ArrayList<Echange> echanges;
    EchangeAdapter echangeAdapter;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        dbHelper = new DatabaseHelper(this);

        setInitialData();

        GridView gridView = findViewById(R.id.countriesGrid);
        echangeAdapter = new EchangeAdapter(this, R.layout.list_item, echanges);
        gridView.setAdapter(echangeAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Echange selectedItem = echanges.get(position);
                boolean deleted = dbHelper.deleteItem(selectedItem.getId());

                if (deleted) {
                    echanges.remove(position);
                    echangeAdapter.notifyDataSetChanged();
                    Toast.makeText(DeleteActivity.this, "Товар удален", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DeleteActivity.this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setInitialData() {
        echanges = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        CurentUserDatabase userDbHelper = new CurentUserDatabase(this);

        String currentUser = userDbHelper.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        if ("admin".equals(currentUser)) {
            cursor = db.rawQuery("SELECT * FROM items", null);
        } else {
            cursor = db.rawQuery("SELECT * FROM items WHERE user_name = ?", new String[]{currentUser});
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow("item_name"));
                String userName = cursor.getString(cursor.getColumnIndexOrThrow("user_name"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));

                echanges.add(new Echange(id, itemName, userName, imagePath));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        dbHelper.close();
        userDbHelper.close();
    }

    public void onTurnBackButtonClick(View view) {
        Intent intent = new Intent(DeleteActivity.this, MyExchange.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}
