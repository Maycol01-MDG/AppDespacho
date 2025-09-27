package com.example.appdespacho.actividades;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdespacho.R;
import com.example.appdespacho.database.TransferenciaCrcDAO;
import com.example.appdespacho.modelos.TransferenciaCrc;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class TransferenciaCrcActivity extends AppCompatActivity {

    private AutoCompleteTextView spnCamionCrc;
    private EditText edtFecha, edtNumeroValeCrc, edtGalonesCrc;
    private MaterialButton btnGuardarTransferencia, btnVerHistorial;
    private TransferenciaCrcDAO dao;
    private BottomNavigationView bottomNavigationView;

    private String modo = "crear";
    private int idEditar = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferencia_crc);

        spnCamionCrc = findViewById(R.id.spnCamionCrc);
        edtFecha = findViewById(R.id.edtFecha);
        edtNumeroValeCrc = findViewById(R.id.edtNumeroValeCrc);
        edtGalonesCrc = findViewById(R.id.edtGalonesCrc);
        btnGuardarTransferencia = findViewById(R.id.btnGuardarTransferencia);
        btnVerHistorial = findViewById(R.id.btnVerHistorial);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        ArrayAdapter<CharSequence> adapterCamiones = ArrayAdapter.createFromResource(
                this, R.array.lista_camiones_crc, android.R.layout.simple_dropdown_item_1line);
        spnCamionCrc.setAdapter(adapterCamiones);

        dao = new TransferenciaCrcDAO(this);
        dao.open();

        // Verificar si venimos de editar
        Intent intent = getIntent();
        if (intent != null && "editar".equals(intent.getStringExtra("modo"))) {
            modo = "editar";
            idEditar = intent.getIntExtra("id", -1);
            edtFecha.setText(intent.getStringExtra("fecha"));
            edtNumeroValeCrc.setText(intent.getStringExtra("vale"));
            edtGalonesCrc.setText(String.valueOf(intent.getDoubleExtra("galones", 0)));
            spnCamionCrc.setText(intent.getStringExtra("camion"), false);
        }

        edtFecha.setOnClickListener(v -> mostrarCalendario());

        btnGuardarTransferencia.setOnClickListener(v -> {
            if (modo.equals("editar")) {
                actualizarTransferencia();
            } else {
                guardarTransferencia();
            }
        });

        btnVerHistorial.setOnClickListener(v -> {
            startActivity(new Intent(TransferenciaCrcActivity.this, HistorialTransferenciaCrcActivity.class));
        });
        // Inicializar barra inferior
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // marcar item por defecto (si existe)
        bottomNavigationView.setSelectedItemId(R.id.Registro_transferenciaCRC);

        // Listener (forma robusta, evita problemas con firmas del listener)
        bottomNavigationView.setOnItemSelectedListener(item -> handleBottomNavigation(item));

    }
    private boolean handleBottomNavigation(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            startActivity(new Intent(this, RegistroConsumoActivity.class));
            overridePendingTransition(0, 0);
            return true;
        }

        if (id == R.id.Registro_Consumo) {
            startActivity(new Intent(this, RegistroConsumoActivity.class));
            overridePendingTransition(0, 0);
            return true;
        }

        if (id == R.id.Registro_transferenciaCRC) {
            return true;
        }

        if (id == R.id.logut) {
            // cerrar sesión: ejemplo
            // Intent intent = new Intent(this, LoginActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // startActivity(intent);
            // finish();
            return true;
        }

        return false;
    }

    private void mostrarCalendario() {
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
                    edtFecha.setText(fecha);
                },
                anio, mes, dia);
        datePicker.show();
    }

    private void guardarTransferencia() {
        if (!validarCampos()) return;

        TransferenciaCrc t = new TransferenciaCrc(
                edtFecha.getText().toString(),
                edtNumeroValeCrc.getText().toString(),
                Double.parseDouble(edtGalonesCrc.getText().toString()),
                spnCamionCrc.getText().toString()
        );

        long id = dao.insertarTransferencia(t);
        if (id > 0) {
            Toast.makeText(this, "Transferencia guardada", Toast.LENGTH_SHORT).show();
            limpiarCampos();
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarTransferencia() {
        if (!validarCampos()) return;

        TransferenciaCrc t = new TransferenciaCrc(
                edtFecha.getText().toString(),
                edtNumeroValeCrc.getText().toString(),
                Double.parseDouble(edtGalonesCrc.getText().toString()),
                spnCamionCrc.getText().toString()
        );
        t.setId(idEditar);

        boolean actualizado = dao.actualizarTransferencia(t);
        if (actualizado) {
            Toast.makeText(this, "Transferencia actualizada", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validarCampos() {
        if (edtFecha.getText().toString().trim().isEmpty() ||
                edtNumeroValeCrc.getText().toString().trim().isEmpty() ||
                edtGalonesCrc.getText().toString().trim().isEmpty() ||
                spnCamionCrc.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void limpiarCampos() {
        edtFecha.setText("");
        edtNumeroValeCrc.setText("");
        edtGalonesCrc.setText("");
        spnCamionCrc.setText("");
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        dao.close();
    }
}
