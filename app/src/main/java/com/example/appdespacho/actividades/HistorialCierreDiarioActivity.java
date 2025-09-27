package com.example.appdespacho.actividades;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appdespacho.R;
import com.example.appdespacho.adaptadores.CierreDiarioAdapter;
import com.example.appdespacho.database.CierreDiarioDAO;
import com.example.appdespacho.modelos.CierreDiario;
import java.util.List;

public class HistorialCierreDiarioActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CierreDiarioAdapter adapter;
    private CierreDiarioDAO cierreDiarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_cierre_diario);

        recyclerView = findViewById(R.id.recyclerHistorialCierreDiario);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cierreDiarioDAO = new CierreDiarioDAO(this);
        cierreDiarioDAO.open();
        List<CierreDiario> lista = cierreDiarioDAO.obtenerTodos();
        cierreDiarioDAO.close();

        adapter = new CierreDiarioAdapter(lista);
        recyclerView.setAdapter(adapter);
    }
}
