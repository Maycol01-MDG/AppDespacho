package com.example.appdespacho.actividades;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdespacho.R;
import com.example.appdespacho.database.ConsumoDAO;
import com.example.appdespacho.modelos.Consumo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class RegistroConsumoActivity extends AppCompatActivity {

    private EditText edtFecha, edtPlaca, edtNumeroVale, edtGalones, edtMotivo;
    private AutoCompleteTextView spnTipoConsumo, spnBomba;
    private TextInputLayout tilMotivo;
    private Button btnGuardarConsumo;
    private ConsumoDAO consumoDAO;
    private BottomNavigationView bottomNavigationView;
    private boolean modoEdicion = false;
    private int idConsumo = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_consumo);

        // Inicializar DAO y abrir base de datos
        consumoDAO = new ConsumoDAO(this);
        consumoDAO.open();

        // Referencias de vistas
        edtFecha = findViewById(R.id.edtFecha);
        edtPlaca = findViewById(R.id.edtPlaca);
        edtNumeroVale = findViewById(R.id.edtNumeroVale);
        edtGalones = findViewById(R.id.edtGalones);
        edtMotivo = findViewById(R.id.edtMotivo);
        spnTipoConsumo = findViewById(R.id.spnTipoConsumo);
        spnBomba = findViewById(R.id.spnBomba);
        tilMotivo = findViewById(R.id.tilMotivo);
        btnGuardarConsumo = findViewById(R.id.btnGuardarConsumo);

        // Mostrar calendario
        edtFecha.setOnClickListener(v -> mostrarCalendario());

        // Cargar datos si es edición
        if (getIntent().hasExtra("id")) {
            modoEdicion = true;
            idConsumo = getIntent().getIntExtra("id", -1);
            edtFecha.setText(getIntent().getStringExtra("fecha"));
            spnTipoConsumo.setText(getIntent().getStringExtra("tipo"), false);
            edtPlaca.setText(getIntent().getStringExtra("placa"));
            edtNumeroVale.setText(getIntent().getStringExtra("numeroVale"));
            spnBomba.setText(getIntent().getStringExtra("bomba"), false);
            edtGalones.setText(String.valueOf(getIntent().getDoubleExtra("galones", 0)));
            edtMotivo.setText(getIntent().getStringExtra("motivo"));

            btnGuardarConsumo.setText("Actualizar Consumo");
            btnGuardarConsumo.setOnClickListener(v -> actualizarConsumo(idConsumo));

            // Mostrar campo de motivo si es necesario
            String tipoConsumo = getIntent().getStringExtra("tipo");
            tilMotivo.setVisibility("VALES MANUALES".equals(tipoConsumo) ?
                    LinearLayout.VISIBLE : LinearLayout.GONE);
        } else {
            btnGuardarConsumo.setOnClickListener(v -> guardarConsumo());
        }

        // Configurar listas desplegables
        ArrayAdapter<CharSequence> adapterTipo = ArrayAdapter.createFromResource(
                this, R.array.tipos_consumo, android.R.layout.simple_dropdown_item_1line);
        spnTipoConsumo.setAdapter(adapterTipo);

        ArrayAdapter<CharSequence> adapterBomba = ArrayAdapter.createFromResource(
                this, R.array.lista_bombas, android.R.layout.simple_dropdown_item_1line);
        spnBomba.setAdapter(adapterBomba);

        // Mostrar motivo si selecciona VALES MANUALES
        spnTipoConsumo.setOnItemClickListener((parent, view, position, id) -> {
            String seleccionado = parent.getItemAtPosition(position).toString();
            tilMotivo.setVisibility("VALES MANUALES".equals(seleccionado) ?
                    LinearLayout.VISIBLE : LinearLayout.GONE);
        });

        // Configurar barra de navegación inferior
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.Registro_Consumo);
        bottomNavigationView.setOnItemSelectedListener(item -> handleBottomNavigation(item));
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
                anio, mes, dia
        );
        datePicker.show();
    }

    private void guardarConsumo() {
        String fecha = edtFecha.getText().toString().trim();
        String tipo = spnTipoConsumo.getText().toString().trim();
        String placa = edtPlaca.getText().toString().trim();
        String numeroVale = edtNumeroVale.getText().toString().trim();
        String bomba = spnBomba.getText().toString().trim();
        String galonesStr = edtGalones.getText().toString().trim();
        String motivo = edtMotivo.getText().toString().trim();

        if (fecha.isEmpty() || tipo.isEmpty() || placa.isEmpty() ||
                numeroVale.isEmpty() || bomba.isEmpty() || galonesStr.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double galones;
        try {
            galones = Double.parseDouble(galonesStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese un valor válido para galones", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto Consumo
        Consumo consumo = new Consumo(fecha, tipo, placa, numeroVale, bomba, galones, motivo);

        // Guardar en BD
        long id = consumoDAO.insertarConsumo(consumo);
        if (id > 0) {
            Toast.makeText(this, "Consumo registrado correctamente", Toast.LENGTH_SHORT).show();
            limpiarCampos();
        } else {
            Toast.makeText(this, "Error al guardar consumo", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarConsumo(int id) {
        String fecha = edtFecha.getText().toString().trim();
        String tipo = spnTipoConsumo.getText().toString().trim();
        String placa = edtPlaca.getText().toString().trim();
        String numeroVale = edtNumeroVale.getText().toString().trim();
        String bomba = spnBomba.getText().toString().trim();
        String galonesStr = edtGalones.getText().toString().trim();
        String motivo = edtMotivo.getText().toString().trim();

        if (fecha.isEmpty() || tipo.isEmpty() || placa.isEmpty() ||
                numeroVale.isEmpty() || bomba.isEmpty() || galonesStr.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double galones;
        try {
            galones = Double.parseDouble(galonesStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese un valor válido para galones", Toast.LENGTH_SHORT).show();
            return;
        }
        // Crear objeto Consumo
        Consumo consumo = new Consumo(id, fecha, tipo, placa, numeroVale, bomba, galones, motivo);

        // Actualizar en BD
        int filasAfectadas = consumoDAO.actualizarConsumo(consumo);
        if (filasAfectadas > 0) {
            Toast.makeText(this, "Consumo actualizado correctamente", Toast.LENGTH_SHORT).show();
            finish(); // Cerrar actividad después de actualizar
        } else {
            Toast.makeText(this, "Error al actualizar consumo", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarCampos() {
        edtFecha.setText("");
        spnTipoConsumo.setText("");
        edtPlaca.setText("");
        edtNumeroVale.setText("");
        spnBomba.setText("");
        edtGalones.setText("");
        edtMotivo.setText("");
    }

    private boolean handleBottomNavigation(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(0, 0);
            return true;
        }

        if (id == R.id.Registro_Consumo) {
            return true;
        }

        if (id == R.id.Registro_transferenciaCRC) {
            startActivity(new Intent(this, TransferenciaCrcActivity.class));
            overridePendingTransition(0, 0);
            return true;
        }

        if (id == R.id.logut) {
            // Aquí va lógica de logout
            return true;
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        consumoDAO.close();
        super.onDestroy();
    }
}