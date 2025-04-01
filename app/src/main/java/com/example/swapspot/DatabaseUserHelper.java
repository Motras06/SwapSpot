package com.example.swapspot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseUserHelper extends SQLiteOpenHelper {

    // Имя базы данных
    private static final String DATABASE_NAME = "BaseOfUsers.db";
    // Версия базы данных
    private static final int DATABASE_VERSION = 1;
    // Название таблицы пользователей
    private static final String TABLE_USERS = "users";
    // Название столбцов
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Конструктор, который вызывает конструктор родительского класса
    public DatabaseUserHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Метод для создания базы данных и таблицы
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Строка для создания таблицы пользователей
        String createTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT);";
        // Выполнение SQL-запроса для создания таблицы
        db.execSQL(createTable);
    }

    // Метод для обновления базы данных (в случае изменений версий)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Удаляем старую таблицу, если она существует
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        // Создаем новую таблицу
        onCreate(db);
    }

    // Метод для регистрации нового пользователя
    public boolean registerUser(String username, String password) {
        // Проверка, существует ли уже пользователь с таким именем
        if (isUserExists(username)) {
            return false; // Если существует, возвращаем false
        }
        // Получаем объект базы данных для записи
        SQLiteDatabase db = this.getWritableDatabase();
        // Создаем объект ContentValues для вставки данных
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);  // Добавляем имя пользователя
        values.put(COLUMN_PASSWORD, password);  // Добавляем пароль

        try {
            // Пытаемся вставить данные в таблицу, возвращаем true, если все прошло успешно
            long result = db.insert(TABLE_USERS, null, values);
            return result != -1;
        } catch (SQLException e) {
            return false; // Возвращаем false в случае ошибки
        }
    }

    // Метод для аутентификации пользователя
    public boolean authenticateUser(String username, String password) {
        // Получаем объект базы данных для чтения
        SQLiteDatabase db = this.getReadableDatabase();
        // Выполняем запрос для поиска пользователя с таким именем и паролем
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID}, COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);

        // Если пользователь найден, возвращаем true, иначе false
        boolean authenticated = cursor.getCount() > 0;
        cursor.close(); // Закрываем курсор
        return authenticated;
    }

    // Метод для логина пользователя (просто вызывает метод аутентификации)
    public boolean loginUser(String username, String password) {
        return authenticateUser(username, password);
    }

    // Метод для проверки, существует ли пользователь с таким именем
    public boolean isUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID}, COLUMN_USERNAME + "=?",
                new String[]{username}, null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
    }
}
