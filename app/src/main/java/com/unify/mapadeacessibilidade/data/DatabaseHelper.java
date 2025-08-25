package com.unify.mapadeacessibilidade.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;
import com.unify.mapadeacessibilidade.model.Lugar;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "acessibilidade.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_LUGARES = "lugares";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOME = "nome";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_DESCRICAO = "descricao";
    public static final String COLUMN_CATEGORIA = "categoria";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LUGARES_TABLE = "CREATE TABLE " + TABLE_LUGARES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOME + " TEXT,"
                + COLUMN_LATITUDE + " REAL,"
                + COLUMN_LONGITUDE + " REAL,"
                + COLUMN_DESCRICAO + " TEXT,"
                + COLUMN_CATEGORIA + " TEXT" + ")";
        db.execSQL(CREATE_LUGARES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LUGARES);
        onCreate(db);
    }

    public void addLugar(Lugar lugar) {
    }

    public List<Lugar> getAllLugares() {
        return null;
    }
}