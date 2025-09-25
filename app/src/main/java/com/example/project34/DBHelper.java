package com.example.project34;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "Project34.db";
    private static final int DB_VERSION = 3;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE," +
                "passwordHash TEXT)");
        db.execSQL("CREATE TABLE transactions (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "type TEXT," +
                "description TEXT," +
                "value REAL," +
                "date TEXT," +
                "category TEXT," +
                "FOREIGN KEY(user_id) REFERENCES users(id))");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS transactions");
        onCreate(db);
    }

    public boolean insertUser(String name, String passwordHash) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("passwordHash", passwordHash);

        long result = db.insert("users", null, values);
        return result != -1;
    }

    public int validateLogin(String name, String passwordHash) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT _id FROM users WHERE name = ? AND passwordHash = ?",
                new String[]{name, passwordHash}
        );
        try {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            }
            return -1;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public boolean userExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT _id FROM users WHERE name = ?",
                new String[]{name}
        );
        try {
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public boolean insertTransaction(int userId, String type, String description, double value, String date, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("type", type);
        values.put("description", description);
        values.put("value", value);
        values.put("date", date);
        values.put("category", category);

        long result = db.insert("transactions", null, values);
        return result != -1;
    }

    public Cursor getTransactionsByUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM transactions WHERE user_id = ? ORDER BY date ASC", new String[]{String.valueOf(userId)});
    }

    public boolean deleteTransaction(int transactionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("transactions", "_id = ?", new String[]{String.valueOf(transactionId)});
        return result > 0;
    }

    public boolean updateTransaction(int transactionId, String type, String description, double value, String date, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("type", type);
        values.put("description", description);
        values.put("value", value);
        values.put("date", date);
        values.put("category", category);

        int result = db.update("transactions", values, "_id = ?", new String[]{String.valueOf(transactionId)});
        return result > 0;
    }
}
