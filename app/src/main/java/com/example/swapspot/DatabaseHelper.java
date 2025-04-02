package com.example.swapspot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BaseOfService.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_ITEMS = "items";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ITEM_NAME = "item_name";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGE_PATH = "image_path";

    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_ITEMS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + COLUMN_USER_NAME + " TEXT NOT NULL, "
                + COLUMN_PHONE_NUMBER + " TEXT NOT NULL, "
                + COLUMN_ADDRESS + " TEXT NOT NULL, "
                + COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + COLUMN_IMAGE_PATH + " TEXT);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_ITEMS + " ADD COLUMN " + COLUMN_PHONE_NUMBER + " TEXT NOT NULL DEFAULT '';");
        }
    }

    public boolean addItem(String itemName, String userName, String phoneNumber, String address, String description, Bitmap image) {
        SQLiteDatabase db = null;
        long result = -1;
        String imagePath = saveImageToStorage(image, itemName);

        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_ITEM_NAME, itemName);
            values.put(COLUMN_USER_NAME, userName);
            values.put(COLUMN_PHONE_NUMBER, phoneNumber);
            values.put(COLUMN_ADDRESS, address);
            values.put(COLUMN_DESCRIPTION, description);
            values.put(COLUMN_IMAGE_PATH, imagePath);

            result = db.insert(TABLE_ITEMS, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (db != null) db.close();
        }

        return result != -1;
    }

    private String saveImageToStorage(Bitmap bitmap, String itemName) {
        File directory = context.getFilesDir();
        File imageFile = new File(directory, itemName + ".png");

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, fos);
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e("DatabaseHelper", "Error saving image", e);
            return null;
        }
    }

    public Bitmap getItemImage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, new String[]{COLUMN_IMAGE_PATH}, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        Bitmap image = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String imagePath = cursor.getString(0);
                image = BitmapFactory.decodeFile(imagePath);
            }
            cursor.close();
        }
        db.close();
        return image;
    }

    public Cursor getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ITEMS, null);
    }

    public boolean deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Получаем путь к изображению перед удалением
        Cursor cursor = db.query(TABLE_ITEMS, new String[]{COLUMN_IMAGE_PATH}, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        String imagePath = null;
        if (cursor != null && cursor.moveToFirst()) {
            imagePath = cursor.getString(0);
            cursor.close(); // Закрываем курсор после использования
        }

        // Удаляем элемент из базы данных
        int result = db.delete(TABLE_ITEMS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        // Если элемент успешно удалён, удаляем изображение
        if (result > 0 && imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                imageFile.delete(); // Физически удаляем изображение с устройства
            }
        }

        return result > 0; // Возвращаем true, если удаление прошло успешно
    }
}
