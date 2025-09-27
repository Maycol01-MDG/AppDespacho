package com.example.appdespacho.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.appdespacho.modelos.Consumo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsumoDAO {
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public ConsumoDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // 🔹 Insertar
    public long insertarConsumo(Consumo consumo) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_FECHA, consumo.getFecha());
        values.put(DBHelper.COL_TIPO, consumo.getTipoConsumo());
        values.put(DBHelper.COL_PLACA, consumo.getPlaca());
        values.put(DBHelper.COL_VALE, consumo.getNumeroVale());
        values.put(DBHelper.COL_BOMBA, consumo.getBomba());
        values.put(DBHelper.COL_GALONES, consumo.getGalones());
        values.put(DBHelper.COL_MOTIVO, consumo.getMotivo());

        return db.insert(DBHelper.TABLE_CONSUMO, null, values);
    }

    // 🔹 Obtener todos los consumos
    public List<Consumo> obtenerTodos() {
        List<Consumo> lista = new ArrayList<>();
        Cursor cursor = db.query(DBHelper.TABLE_CONSUMO, null, null, null, null, null, DBHelper.COL_FECHA + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Consumo c = new Consumo();
                c.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID)));
                c.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_FECHA)));
                c.setTipoConsumo(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_TIPO)));
                c.setPlaca(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PLACA)));
                c.setNumeroVale(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_VALE))); // 🔹 Vale
                c.setBomba(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_BOMBA)));     // 🔹 Bomba
                c.setGalones(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_GALONES))); // 🔹 Galones
                c.setMotivo(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_MOTIVO)));

                lista.add(c);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return lista;
    }

    // 🔹 Buscar por fecha
    public List<Consumo> buscarConsumosPorFecha(String fecha) {
        List<Consumo> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DBHelper.TABLE_CONSUMO + " WHERE " + DBHelper.COL_FECHA + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{fecha});

        if (cursor.moveToFirst()) {
            do {
                Consumo c = new Consumo();
                c.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID)));
                c.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_FECHA)));
                c.setTipoConsumo(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_TIPO)));
                c.setPlaca(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PLACA)));
                c.setNumeroVale(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_VALE)));
                c.setBomba(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_BOMBA)));
                c.setGalones(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_GALONES)));
                c.setMotivo(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_MOTIVO)));

                lista.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
    public Map<String, Double> obtenerGalonesPorBomba() {
        Map<String, Double> resumen = new HashMap<>();
        Cursor cursor = db.rawQuery(
                "SELECT bomba, SUM(galones) as total FROM consumos GROUP BY bomba", null);

        if (cursor.moveToFirst()) {
            do {
                String bomba = cursor.getString(cursor.getColumnIndexOrThrow("bomba"));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
                resumen.put(bomba, total);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return resumen;
    }

    // 🔹 Buscar con filtros
    public List<Consumo> buscarConsumos(String placa, String fechaDesde, String fechaHasta, String turno) {
        List<Consumo> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder query = new StringBuilder("SELECT * FROM " + DBHelper.TABLE_CONSUMO + " WHERE 1=1 ");
        List<String> args = new ArrayList<>();

        if (!placa.isEmpty()) {
            query.append("AND ").append(DBHelper.COL_PLACA).append(" LIKE ? ");
            args.add("%" + placa + "%");
        }
        if (!fechaDesde.isEmpty()) {
            query.append("AND ").append(DBHelper.COL_FECHA).append(" >= ? ");
            args.add(fechaDesde);
        }
        if (!fechaHasta.isEmpty()) {
            query.append("AND ").append(DBHelper.COL_FECHA).append(" <= ? ");
            args.add(fechaHasta);
        }
        if (!turno.isEmpty() && !turno.equals("Seleccione opción")) {
            query.append("AND turno = ? ");
        }

        Cursor cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));
        if (cursor.moveToFirst()) {
            do {
                Consumo c = new Consumo();
                c.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID)));
                c.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_FECHA)));
                c.setTipoConsumo(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_TIPO)));
                c.setPlaca(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PLACA)));
                c.setNumeroVale(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_VALE)));
                c.setBomba(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_BOMBA)));
                c.setGalones(cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_GALONES)));
                lista.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    // 🔹 Actualizar
    public int actualizarConsumo(Consumo consumo) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_FECHA, consumo.getFecha());
        values.put(DBHelper.COL_TIPO, consumo.getTipoConsumo());
        values.put(DBHelper.COL_PLACA, consumo.getPlaca());
        values.put(DBHelper.COL_VALE, consumo.getNumeroVale());
        values.put(DBHelper.COL_BOMBA, consumo.getBomba());
        values.put(DBHelper.COL_GALONES, consumo.getGalones());
        values.put(DBHelper.COL_MOTIVO, consumo.getMotivo());

        return db.update(DBHelper.TABLE_CONSUMO, values, DBHelper.COL_ID + " = ?", new String[]{String.valueOf(consumo.getId())});
    }

    // 🔹 Eliminar
    public void eliminarConsumo(int id) {
        db.delete(DBHelper.TABLE_CONSUMO, DBHelper.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }
}
