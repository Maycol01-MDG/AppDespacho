package com.example.appdespacho.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdespacho.R;
import com.example.appdespacho.adaptadores.TransferenciaCrcAdapter;
import com.example.appdespacho.database.TransferenciaCrcDAO;
import com.example.appdespacho.modelos.TransferenciaCrc;

import java.util.List;

public class HistorialTransferenciaCrcActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransferenciaCrcAdapter adapter;
    private TransferenciaCrcDAO transferenciaCrcDAO;
    private List<TransferenciaCrc> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_transferencia_crc);

        transferenciaCrcDAO = new TransferenciaCrcDAO(this);
        transferenciaCrcDAO.open();

        recyclerView = findViewById(R.id.recyclerTransferencias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        lista = transferenciaCrcDAO.obtenerTodos();

        adapter = new TransferenciaCrcAdapter(
                this,
                lista,
                new TransferenciaCrcAdapter.OnItemClickListener() {
                    @Override
                    public void onEditarClick(TransferenciaCrc transferencia) {
                        Intent intent = new Intent(HistorialTransferenciaCrcActivity.this, TransferenciaCrcActivity.class);
                        intent.putExtra("modo", "editar");
                        intent.putExtra("id", transferencia.getId());
                        intent.putExtra("fecha", transferencia.getFecha());
                        intent.putExtra("vale", transferencia.getNumeroVale());
                        intent.putExtra("galones", transferencia.getGalones());
                        intent.putExtra("camion", transferencia.getCamionCrc());

                        startActivity(intent);
                    }

                    @Override
                    public void onEliminarClick(TransferenciaCrc transferencia) {
                        confirmarEliminacion(transferencia);
                    }
                }
        );

        recyclerView.setAdapter(adapter);
    }

    private void confirmarEliminacion(TransferenciaCrc transferencia) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Registro")
                .setMessage("¿Seguro que quieres eliminar este registro?")
                .setPositiveButton("Sí", (dialog, which) -> eliminarTransferencia(transferencia))
                .setNegativeButton("No", null)
                .show();
    }

    private void eliminarTransferencia(TransferenciaCrc transferencia) {
        transferenciaCrcDAO.open();
        boolean eliminado = transferenciaCrcDAO.eliminarTransferencia(transferencia.getId());
        transferenciaCrcDAO.close();

        if (eliminado) {
            lista.remove(transferencia);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        transferenciaCrcDAO.open();
        lista.clear();
        lista.addAll(transferenciaCrcDAO.obtenerTodos());
        transferenciaCrcDAO.close();
        adapter.notifyDataSetChanged();
    }
}
