package com.example.appdespacho.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.appdespacho.modelos.TransferenciaCrc;

import java.util.ArrayList;
import java.util.List;

public class TransferenciaCrcDAO {
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public TransferenciaCrcDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertarTransferencia(TransferenciaCrc transferencia) {
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(DBHelper.COL_FECHA_TCRC, transferencia.getFecha());
        values.put(DBHelper.COL_VALE_TCRC, transferencia.getNumeroVale());
        values.put(DBHelper.COL_GALONES_TCRC, transferencia.getGalones());
        values.put(DBHelper.COL_CAMION_TCRC, transferencia.getCamionCrc());

        return db.insert(DBHelper.TABLE_TRANSFERENCIA_CRC, null, values);
    }

    public List<TransferenciaCrc> buscarTransferenciasPorFecha(String fecha) {
        List<TransferenciaCrc> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DBHelper.TABLE_TRANSFERENCIA_CRC + " WHERE " + DBHelper.COL_FECHA_TCRC + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{fecha});

        if (cursor.moveToFirst()) {
            do {
                TransferenciaCrc t = new TransferenciaCrc();
                t.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID_TCRC)));
                t.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_FECHA_TCRC)));
                t.setNumeroVale(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_VALE_TCRC)));
                t.setGalones(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_GALONES_TCRC)));
                t.setCamionCrc(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CAMION_TCRC)));

                lista.add(t);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    // 🔹 Nuevo método
    public List<TransferenciaCrc> obtenerTodos() {
        List<TransferenciaCrc> lista = new ArrayList<>();
        Cursor cursor = db.query(DBHelper.TABLE_TRANSFERENCIA_CRC,
                null, null, null, null, null,
                DBHelper.COL_FECHA_TCRC + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TransferenciaCrc t = new TransferenciaCrc();
                t.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID_TCRC)));
                t.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_FECHA_TCRC)));
                t.setNumeroVale(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_VALE_TCRC)));
                t.setGalones(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_GALONES_TCRC)));
                t.setCamionCrc(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CAMION_TCRC)));

                lista.add(t);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return lista;
    }
    public double obtenerTotalGalonesTransferencias() {
        double total = 0;
        Cursor cursor = db.rawQuery(
                "SELECT SUM(galones) as total FROM transferencias_crc", null);

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        return total;
    }
    public boolean eliminarTransferencia(int id) {
        int filas = db.delete(
                DBHelper.TABLE_TRANSFERENCIA_CRC,
                DBHelper.COL_ID_TCRC + "=?",
                new String[]{String.valueOf(id)}
        );
        return filas > 0; // Devuelve true si se eliminó al menos 1 fila
    }
    public boolean actualizarTransferencia(TransferenciaCrc transferencia) {
        ContentValues values = new ContentValues();
        values.put("fecha", transferencia.getFecha());
        values.put("numeroVale", transferencia.getNumeroVale());
        values.put("galones", transferencia.getGalones());
        values.put("camionCrc", transferencia.getCamionCrc());

        int filas = db.update("transferencia_crc", values, "id = ?", new String[]{String.valueOf(transferencia.getId())});
        return filas > 0;
    }



}
