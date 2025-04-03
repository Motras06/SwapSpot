package com.example.swapspot;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private DatabaseHelper databaseHelper;
    private CurentUserDatabase userDatabase;
    private ImageView imageView;
    private EditText editTextName, editTextAddress, editTextDescription, editTextPhoneNumber;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        databaseHelper = new DatabaseHelper(this);
        userDatabase = new CurentUserDatabase(this);

        currentUser = userDatabase.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editTextName = findViewById(R.id.editText1);
        editTextAddress = findViewById(R.id.editText2);
        editTextDescription = findViewById(R.id.editText3);
        editTextPhoneNumber = findViewById(R.id.editTextPhone);
        imageView = findViewById(R.id.image_current);
        Button buttonAddImage = findViewById(R.id.button_add_image);
        Button buttonAddItem = findViewById(R.id.buttonLogin);

        buttonAddImage.setOnClickListener(v -> openGallery());

        buttonAddItem.setOnClickListener(v -> addItemToDatabase());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addItemToDatabase() {
        String itemName = editTextName.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim(); // Получаем номер телефона
        Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap(); // Получаем изображение

        if (itemName.isEmpty() || address.isEmpty() || description.isEmpty() || phoneNumber.isEmpty() || image == null) {
            Toast.makeText(this, "Заполните все поля и добавьте изображение!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInserted = databaseHelper.addItem(itemName, currentUser, phoneNumber, address, description, image);
        if (isInserted) {
            Toast.makeText(this, "Товар добавлен!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddActivity.this, MyExchange.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        } else {
            Toast.makeText(this, "Ошибка при добавлении товара", Toast.LENGTH_SHORT).show();
        }
    }

    public void onTurnBackButtonClick(View view) {
        Intent intent = new Intent(AddActivity.this, MyExchange.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}
