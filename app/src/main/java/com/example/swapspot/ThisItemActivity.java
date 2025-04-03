package com.example.swapspot;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ThisItemActivity extends AppCompatActivity {
    private ImageView itemImage;
    private TextView itemName, itemUserName, itemAddress, itemDescription, itemPhoneNumber;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_this_item);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        itemImage = findViewById(R.id.item_image);
        itemName = findViewById(R.id.item_name);
        itemUserName = findViewById(R.id.item_user_name);
        itemAddress = findViewById(R.id.item_address);
        itemDescription = findViewById(R.id.item_description);
        itemPhoneNumber = findViewById(R.id.item_phone_number);
        dbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("ITEM_ID")) {
            int itemId = intent.getIntExtra("ITEM_ID", -1);
            if (itemId != -1) {
                loadItemData(itemId);
            }
        }
    }

    private void loadItemData(int itemId) {
        Cursor cursor = dbHelper.getReadableDatabase().query(
                "items", null, "id = ?", new String[]{String.valueOf(itemId)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("item_name"));
            String userName = cursor.getString(cursor.getColumnIndexOrThrow("user_name"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phone_number"));

            itemName.setText(name);
            itemUserName.setText("Пользователь: " + userName);
            itemAddress.setText("Адрес: " + address);
            itemDescription.setText(description);
            itemPhoneNumber.setText("Телефон: " + phoneNumber);

            if (imagePath != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                itemImage.setImageBitmap(bitmap);
            }

            cursor.close();
        }
    }

    public void onTurnBackButtonClick(View view) {
        finish();
    }
}
