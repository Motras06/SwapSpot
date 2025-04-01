package com.example.swapspot;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
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
    private EditText editTextName, editTextAddress, editTextDescription;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Инициализация базы данных
        databaseHelper = new DatabaseHelper(this);
        userDatabase = new CurentUserDatabase(this);

        // Получение текущего пользователя
        currentUser = userDatabase.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show();
            finish(); // Закрываем активность, если пользователь не найден
            return;
        }

        // Найти элементы интерфейса
        editTextName = findViewById(R.id.editText1);
        editTextAddress = findViewById(R.id.editText2);
        editTextDescription = findViewById(R.id.editText3);
        imageView = findViewById(R.id.image_current); // Добавьте в XML `ImageView`
        Button buttonAddImage = findViewById(R.id.button_add_image);
        Button buttonAddItem = findViewById(R.id.buttonLogin);

        // Кнопка добавления изображения
        buttonAddImage.setOnClickListener(v -> openGallery());

        // Кнопка добавления товара
        buttonAddItem.setOnClickListener(v -> addItemToDatabase());
    }

    // Открываем галерею для выбора изображения
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

    // Добавление товара в БД
    private void addItemToDatabase() {
        String itemName = editTextName.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap(); // Получаем изображение

        // Проверка на пустые поля
        if (itemName.isEmpty() || address.isEmpty() || description.isEmpty() || image == null) {
            Toast.makeText(this, "Заполните все поля и добавьте изображение!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Добавляем данные в БД
        boolean isInserted = databaseHelper.addItem(itemName, currentUser, address, description, image);
        if (isInserted) {
            Toast.makeText(this, "Товар добавлен!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddActivity.this, MyExchange.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish(); // Закрываем активность
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
