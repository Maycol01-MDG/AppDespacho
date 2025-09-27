package com.example.appdespacho.actividades;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdespacho.R;
import com.example.appdespacho.adaptadores.ConsumoAdapter;
import com.example.appdespacho.database.ConsumoDAO;
import com.example.appdespacho.modelos.Consumo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private EditText edtPlaca, edtFechaDesde, edtFechaHasta;
    private AutoCompleteTextView spnTurno;
    private Button btnBuscar;
    private RecyclerView recyclerView;
    private ConsumoAdapter adapter;
    private ConsumoDAO consumoDAO;
    private List<Consumo> listaConsumos;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);


        consumoDAO = new ConsumoDAO(this);
        consumoDAO.open();

        edtPlaca = findViewById(R.id.edtPlacaHistorial);
        edtFechaDesde = findViewById(R.id.edtFechaDesde);
        edtFechaHasta = findViewById(R.id.edtFechaHasta);
        spnTurno = findViewById(R.id.spnTurnoHistorial);
        btnBuscar = findViewById(R.id.btnBuscarHistorial);
        recyclerView = findViewById(R.id.recyclerHistorial);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 🔹 Configurar calendario
        edtFechaDesde.setOnClickListener(v -> mostrarCalendario(edtFechaDesde));
        edtFechaHasta.setOnClickListener(v -> mostrarCalendario(edtFechaHasta));

        // 🔹 Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaConsumos = new ArrayList<>();
        adapter = new ConsumoAdapter(this, listaConsumos, new ConsumoAdapter.OnItemClickListener() {
            @Override
            public void onEditarClick(Consumo registro) {
                Intent intent = new Intent(HistorialActivity.this, RegistroConsumoActivity.class);
                intent.putExtra("id", registro.getId());
                intent.putExtra("fecha", registro.getFecha());
                intent.putExtra("tipo", registro.getTipoConsumo());
                intent.putExtra("placa", registro.getPlaca());
                intent.putExtra("numeroVale", registro.getNumeroVale());
                intent.putExtra("bomba", registro.getBomba());
                intent.putExtra("galones", registro.getGalones());
                intent.putExtra("motivo", registro.getMotivo());
                startActivity(intent);
            }

            @Override
            public void onEliminarClick(Consumo registro) {
                consumoDAO.eliminarConsumo(registro.getId());
                listaConsumos.remove(registro);
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Registro eliminado", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);

        btnBuscar.setOnClickListener(v -> buscar());
        cargarTodo();
        bottomNavigationView.setOnItemSelectedListener(this::handleBottomNavigation);
    }

    private boolean handleBottomNavigation(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.Registro_Consumo) {
            startActivity(new Intent(this, RegistroConsumoActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.Registro_transferenciaCRC) {
            startActivity(new Intent(this, TransferenciaCrcActivity.class));
            overridePendingTransition(0, 0);
            return true;
        } else if (id == R.id.logut) {
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void mostrarCalendario(EditText campo) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog picker = new DatePickerDialog(this, (view, y, m, d) -> {
            campo.setText(d + "/" + (m + 1) + "/" + y);
        }, year, month, day);
        picker.show();
    }

    private void buscar() {
        String placa = edtPlaca.getText().toString().trim();
        String fechaDesde = edtFechaDesde.getText().toString().trim();
        String fechaHasta = edtFechaHasta.getText().toString().trim();
        String turno = spnTurno.getText().toString().trim();

        List<Consumo> resultados = consumoDAO.buscarConsumos(placa, fechaDesde, fechaHasta, turno);
        if (resultados.isEmpty()) {
            Toast.makeText(this, "No se encontraron registros", Toast.LENGTH_SHORT).show();
        }
        adapter.setLista(resultados);
    }

    private void cargarTodo() {
        List<Consumo> todos = consumoDAO.buscarConsumos("", "", "", "");
        adapter.setLista(todos);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTodo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (consumoDAO != null) {
            consumoDAO.close();
        }
    }
}
