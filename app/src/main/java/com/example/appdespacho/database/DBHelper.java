package com.example.appdespacho.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "despacho.db";
    public static final int DATABASE_VERSION = 3; // 🔹 Subimos versión para regenerar

    // TABLA CONSUMO
    public static final String TABLE_CONSUMO = "consumo";
    public static final String COL_ID = "id";
    public static final String COL_FECHA = "fecha";
    public static final String COL_TIPO = "tipo";
    public static final String COL_PLACA = "placa";
    public static final String COL_VALE = "numeroVale";
    public static final String COL_BOMBA = "bomba";
    public static final String COL_GALONES = "galones";
    public static final String COL_MOTIVO = "motivo";

    // TABLA TRANSFERENCIA CRC
    public static final String TABLE_TRANSFERENCIA_CRC = "transferencia_crc";
    public static final String COL_ID_TCRC = "id";
    public static final String COL_FECHA_TCRC = "fecha";
    public static final String COL_VALE_TCRC = "numeroVale";
    public static final String COL_GALONES_TCRC = "galones";
    public static final String COL_CAMION_TCRC = "camionCrc";

    // TABLA CIERRE DIARIO
    public static final String TABLE_CIERRE_DIARIO = "cierre_diario";
    public static final String COL_ID_CD = "id";
    public static final String COL_BOMBA_CD = "bomba";
    public static final String COL_LECTURA_INICIAL_CD = "lecturaInicial";
    public static final String COL_LECTURA_FINAL_CD = "lecturaFinal";
    public static final String COL_SALIDA_VALES_CD = "salidaVales";
    public static final String COL_SALIDA_CONTOMETRO_CD = "salidaContometro";
    public static final String COL_DIFERENCIA_CD = "diferencia";
    public static final String COL_FECHA_CD = "fecha";
    public static final String COL_OPERADOR_CD = "operador";

    private static final String CREATE_TABLE_CONSUMO =
            "CREATE TABLE " + TABLE_CONSUMO + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_FECHA + " TEXT NOT NULL, " +
                    COL_TIPO + " TEXT NOT NULL, " +
                    COL_PLACA + " TEXT NOT NULL, " +
                    COL_VALE + " TEXT, " +
                    COL_BOMBA + " TEXT NOT NULL, " +
                    COL_GALONES + " REAL NOT NULL, " +
                    COL_MOTIVO + " TEXT);";

    private static final String CREATE_TABLE_TRANSFERENCIA_CRC =
            "CREATE TABLE " + TABLE_TRANSFERENCIA_CRC + " (" +
                    COL_ID_TCRC + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_FECHA_TCRC + " TEXT, " +
                    COL_VALE_TCRC + " TEXT, " +
                    COL_GALONES_TCRC + " REAL, " +
                    COL_CAMION_TCRC + " TEXT);";

    private static final String CREATE_TABLE_CIERRE_DIARIO =
            "CREATE TABLE " + TABLE_CIERRE_DIARIO + " (" +
                    COL_ID_CD + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_BOMBA_CD + " TEXT NOT NULL, " +
                    COL_LECTURA_INICIAL_CD + " REAL NOT NULL, " +
                    COL_LECTURA_FINAL_CD + " REAL NOT NULL, " +
                    COL_SALIDA_VALES_CD + " REAL, " +
                    COL_SALIDA_CONTOMETRO_CD + " REAL, " +
                    COL_DIFERENCIA_CD + " REAL, " +
                    COL_FECHA_CD + " TEXT NOT NULL, " +
                    COL_OPERADOR_CD + " TEXT NOT NULL);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CONSUMO);
        db.execSQL(CREATE_TABLE_TRANSFERENCIA_CRC);
        db.execSQL(CREATE_TABLE_CIERRE_DIARIO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONSUMO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSFERENCIA_CRC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CIERRE_DIARIO);
        onCreate(db);
    }

    public boolean insertarCierre(String bomba, double lecturaInicial, double lecturaFinal,
                                  double salidaVales, double salidaContometro,
                                  double diferencia, String fecha, String operador) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOMBA_CD, bomba);
        values.put(COL_LECTURA_INICIAL_CD, lecturaInicial);
        values.put(COL_LECTURA_FINAL_CD, lecturaFinal);
        values.put(COL_SALIDA_VALES_CD, salidaVales);
        values.put(COL_SALIDA_CONTOMETRO_CD, salidaContometro);
        values.put(COL_DIFERENCIA_CD, diferencia);
        values.put(COL_FECHA_CD, fecha);
        values.put(COL_OPERADOR_CD, operador);

        long result = db.insert(TABLE_CIERRE_DIARIO, null, values);
        return result != -1;
    }
}
