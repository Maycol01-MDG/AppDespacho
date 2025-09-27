package com.example.appdespacho.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.appdespacho.modelos.CierreDiario;

import java.util.ArrayList;
import java.util.List;

public class CierreDiarioDAO {
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public CierreDiarioDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    // 🔹 Abrir conexión
    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    // 🔹 Cerrar conexión
    public void close() {
        dbHelper.close();
    }

    // 🔹 Verificar si ya existe un registro por fecha
    public boolean existeRegistroPorFecha(String fecha) {
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + DBHelper.TABLE_CIERRE_DIARIO + " WHERE " + DBHelper.COL_FECHA_CD + " = ?",
                new String[]{fecha}
        );
        boolean existe = false;
        if (cursor.moveToFirst()) {
            existe = cursor.getInt(0) > 0;
        }
        cursor.close();
        return existe;
    }

    // 🔹 Insertar nuevo cierre diario
    public boolean insertar(CierreDiario cd) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_BOMBA_CD, cd.getBomba());
        values.put(DBHelper.COL_LECTURA_INICIAL_CD, cd.getLecturaInicial());
        values.put(DBHelper.COL_LECTURA_FINAL_CD, cd.getLecturaFinal());
        values.put(DBHelper.COL_SALIDA_VALES_CD, cd.getSalidaVales());
        values.put(DBHelper.COL_SALIDA_CONTOMETRO_CD, cd.getSalidaContometro());
        values.put(DBHelper.COL_DIFERENCIA_CD, cd.getDiferencia());
        values.put(DBHelper.COL_FECHA_CD, cd.getFecha());
        values.put(DBHelper.COL_OPERADOR_CD, cd.getOperador());

        long result = db.insert(DBHelper.TABLE_CIERRE_DIARIO, null, values);
        return result != -1;
    }

    // 🔹 Obtener todos los cierres diarios
    public List<CierreDiario> obtenerTodos() {
        List<CierreDiario> lista = new ArrayList<>();
        Cursor cursor = db.query(
                DBHelper.TABLE_CIERRE_DIARIO,
                null, null, null, null, null,
                DBHelper.COL_FECHA_CD + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                CierreDiario cd = new CierreDiario();
                cd.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID_CD)));
                cd.setBomba(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_BOMBA_CD)));
                cd.setLecturaInicial(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_LECTURA_INICIAL_CD)));
                cd.setLecturaFinal(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_LECTURA_FINAL_CD)));
                cd.setSalidaVales(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_SALIDA_VALES_CD)));
                cd.setSalidaContometro(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_SALIDA_CONTOMETRO_CD)));
                cd.setDiferencia(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_DIFERENCIA_CD)));
                cd.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_FECHA_CD)));
                cd.setOperador(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_OPERADOR_CD)));

                lista.add(cd);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return lista;
    }
}
