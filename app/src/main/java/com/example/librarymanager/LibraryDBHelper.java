package com.example.librarymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LibraryDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "LibraryDBHelper";

    public static final String DATABASE_NAME = "library.db";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_BOOKS = "books";
    public static final String COL_ID = "id";
    public static final String COL_TITLE = "title";
    public static final String COL_AUTHOR = "author";
    public static final String COL_GENRE = "genre";

    public static final String TABLE_USERS = "users";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_ROLE = "role";

    private static LibraryDBHelper instance;

    public static synchronized LibraryDBHelper getInstance(Context ctx) {
        if (instance == null) {
            instance = new LibraryDBHelper(ctx.getApplicationContext());
        }
        return instance;
    }

    private LibraryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createBooks = "CREATE TABLE " + TABLE_BOOKS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT, "
                + COL_AUTHOR + " TEXT, "
                + COL_GENRE + " TEXT)";
        db.execSQL(createBooks);

        String createUsers = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USERNAME + " TEXT PRIMARY KEY, "
                + COL_PASSWORD + " TEXT, "
                + COL_ROLE + " TEXT)";
        db.execSQL(createUsers);

        insertSampleBooks(db);
        insertSampleUsers(db);
    }

    private void insertSampleBooks(SQLiteDatabase db) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(COL_TITLE, "1984");
            cv.put(COL_AUTHOR, "George Orwell");
            cv.put(COL_GENRE, "Dystopian");
            db.insert(TABLE_BOOKS, null, cv);

            cv.clear();
            cv.put(COL_TITLE, "The Great Gatsby");
            cv.put(COL_AUTHOR, "F. Scott Fitzgerald");
            cv.put(COL_GENRE, "Classic");
            db.insert(TABLE_BOOKS, null, cv);

            cv.clear();
            cv.put(COL_TITLE, "To Kill a Mockingbird");
            cv.put(COL_AUTHOR, "Harper Lee");
            cv.put(COL_GENRE, "Literature");
            db.insert(TABLE_BOOKS, null, cv);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting sample books", e);
        }
    }

    private void insertSampleUsers(SQLiteDatabase db) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(COL_USERNAME, "staff1");
            cv.put(COL_PASSWORD, "pass123");
            cv.put(COL_ROLE, "staff");
            db.insert(TABLE_USERS, null, cv);

            cv.clear();
            cv.put(COL_USERNAME, "member1");
            cv.put(COL_PASSWORD, "pass123");
            cv.put(COL_ROLE, "member");
            db.insert(TABLE_USERS, null, cv);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting sample users", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        } catch (Exception e) {
            Log.e(TAG, "DB upgrade error", e);
        }
    }

    public void addBook(String title, String author, String genre) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put(COL_TITLE, title);
            cv.put(COL_AUTHOR, author);
            cv.put(COL_GENRE, genre);
            db.insert(TABLE_BOOKS, null, cv);
        } catch (Exception e) {
            Log.e(TAG, "DB insert book error", e);
        }
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor c = db.query(
                     TABLE_BOOKS,
                     new String[]{COL_TITLE, COL_AUTHOR, COL_GENRE},
                     null, null, null, null, null)) {

            int titleIndex = c.getColumnIndexOrThrow(COL_TITLE);
            int authorIndex = c.getColumnIndexOrThrow(COL_AUTHOR);
            int genreIndex = c.getColumnIndexOrThrow(COL_GENRE);

            while (c.moveToNext()) {
                books.add(new Book(
                        c.getString(titleIndex),
                        c.getString(authorIndex),
                        c.getString(genreIndex)
                ));
            }
        } catch (Exception e) {
            Log.e(TAG, "DB read error in getAllBooks()", e);
        }
        return books;
    }

    public boolean validateUser(String username, String password) {
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor c = db.query(TABLE_USERS,
                     new String[]{COL_USERNAME},
                     COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                     new String[]{username, password},
                     null, null, null)) {
            return c.moveToFirst();
        } catch (Exception e) {
            Log.e(TAG, "Error validating user", e);
            return false;
        }
    }

    public String getUserRole(String username) {
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor c = db.query(TABLE_USERS,
                     new String[]{COL_ROLE},
                     COL_USERNAME + "=?",
                     new String[]{username},
                     null, null, null)) {
            if (c.moveToFirst()) {
                return c.getString(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user role", e);
        }
        return null;
    }
}
