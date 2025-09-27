package com.example.appdespacho.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appdespacho.R;
import com.example.appdespacho.database.CierreDiarioDAO;
import com.example.appdespacho.modelos.CierreDiario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CierreDiarioActivity extends AppCompatActivity {

    private LinearLayout containerBombas;
    private EditText edtNombreOperador;
    private MaterialButton btnCalcular, btnGuardar;
    private BottomNavigationView bottomNavigationView;
    private List<View> listaBombas = new ArrayList<>();
    private String[] bombas = {"Bomba 1", "Bomba 2", "Bomba 3", "Bomba 4"};

    private CierreDiarioDAO cierreDiarioDAO;
    private SharedPreferences prefs;
    private static final String PREF_NAME = "cierre_diario_temp";
    private Button btnVerHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cierre_diario);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnVerHistorial = findViewById(R.id.btnVerHistorial);
        btnVerHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(CierreDiarioActivity.this, HistorialCierreDiarioActivity.class);
            startActivity(intent);
        });

        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        containerBombas = findViewById(R.id.containerBombas);
        btnCalcular = findViewById(R.id.btnCalcular);
        btnGuardar = findViewById(R.id.btnGuardar);
        edtNombreOperador = findViewById(R.id.edtNombreOperador);

        cierreDiarioDAO = new CierreDiarioDAO(this);
        cierreDiarioDAO.open();

        // 🔹 Inflar dinámicamente las bombas y restaurar valores
        for (int i = 0; i < bombas.length; i++) {
            String nombreBomba = bombas[i];
            View bombaView = getLayoutInflater().inflate(R.layout.item_bombas, containerBombas, false);
            TextView titulo = bombaView.findViewById(R.id.txtTituloBomba);
            titulo.setText(nombreBomba);

            TextInputEditText edtIni = bombaView.findViewById(R.id.edtIniB1);
            TextInputEditText edtFin = bombaView.findViewById(R.id.edtFinB1);
            TextInputEditText edtVales = bombaView.findViewById(R.id.edtValesB1);

            // Restaurar datos guardados en SharedPreferences
            edtIni.setText(prefs.getString("ini_" + i, ""));
            edtFin.setText(prefs.getString("fin_" + i, ""));
            edtVales.setText(prefs.getString("vales_" + i, ""));

            // Escuchar cambios y guardarlos en prefs
            final int index = i;
            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    guardarEnPrefs(index, edtIni.getText().toString(),
                            edtFin.getText().toString(),
                            edtVales.getText().toString());
                }
            };
            edtIni.addTextChangedListener(watcher);
            edtFin.addTextChangedListener(watcher);
            edtVales.addTextChangedListener(watcher);

            containerBombas.addView(bombaView);
            listaBombas.add(bombaView);
        }

        btnCalcular.setOnClickListener(v -> calcularTodas());
        btnGuardar.setOnClickListener(v -> guardarTodas());

        bottomNavigationView.setOnItemSelectedListener(this::handleBottomNavigation);
    }

    private void guardarEnPrefs(int index, String ini, String fin, String vales) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ini_" + index, ini);
        editor.putString("fin_" + index, fin);
        editor.putString("vales_" + index, vales);
        editor.apply();
    }

    private void limpiarPrefs() {
        prefs.edit().clear().apply();
    }

    private boolean handleBottomNavigation(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(0, 0);
            return true;
        }
        if (id == R.id.Registro_Consumo) {
            startActivity(new Intent(this, RegistroConsumoActivity.class));
            overridePendingTransition(0, 0);
            return true;
        }
        if (id == R.id.Registro_transferenciaCRC) {
            startActivity(new Intent(this, TransferenciaCrcActivity.class));
            overridePendingTransition(0, 0);
            return true;
        }
        if (id == R.id.logut) {
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void calcularTodas() {
        boolean hayCamposValidos = true;

        for (View bombaView : listaBombas) {
            TextInputEditText edtIni = bombaView.findViewById(R.id.edtIniB1);
            TextInputEditText edtFin = bombaView.findViewById(R.id.edtFinB1);
            TextInputEditText edtVales = bombaView.findViewById(R.id.edtValesB1);
            TextView txtSalida = bombaView.findViewById(R.id.txtSalidaB1);
            TextView txtDiferencia = bombaView.findViewById(R.id.txtDiferenciaB1);

            String iniStr = edtIni.getText() != null ? edtIni.getText().toString().trim() : "";
            String finStr = edtFin.getText() != null ? edtFin.getText().toString().trim() : "";
            String valesStr = edtVales.getText() != null ? edtVales.getText().toString().trim() : "0";

            if (TextUtils.isEmpty(iniStr) || TextUtils.isEmpty(finStr)) {
                hayCamposValidos = false;
                txtSalida.setText("0.00");
                txtDiferencia.setText("0.00");
                continue;
            }

            try {
                double ini = Double.parseDouble(iniStr);
                double fin = Double.parseDouble(finStr);
                double vales = TextUtils.isEmpty(valesStr) ? 0 : Double.parseDouble(valesStr);

                if (fin <= ini) {
                    Toast.makeText(this, "Lectura final debe ser mayor a la inicial", Toast.LENGTH_SHORT).show();
                    hayCamposValidos = false;
                    continue;
                }

                double salidaContometro = fin - ini;
                double diferencia = vales - salidaContometro;

                txtSalida.setText(String.format(Locale.getDefault(), "%.2f", salidaContometro));
                txtDiferencia.setText(String.format(Locale.getDefault(), "%.2f", diferencia));

            } catch (NumberFormatException e) {
                txtSalida.setText("0.00");
                txtDiferencia.setText("0.00");
                hayCamposValidos = false;
            }
        }

        if (!hayCamposValidos) {
            Toast.makeText(this, "Debes completar todas las lecturas antes de calcular", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarTodas() {
        String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String operador = edtNombreOperador.getText().toString().trim();

        // 🔹 Validación: evitar duplicados por fecha
        if (cierreDiarioDAO.existeRegistroPorFecha(fecha)) {
            Toast.makeText(this, "Ya existe un registro de cierre para hoy", Toast.LENGTH_LONG).show();
            return;
        }

        boolean hayDatos = false;

        for (int i = 0; i < listaBombas.size(); i++) {
            View bombaView = listaBombas.get(i);
            TextInputEditText edtIni = bombaView.findViewById(R.id.edtIniB1);
            TextInputEditText edtFin = bombaView.findViewById(R.id.edtFinB1);
            TextInputEditText edtVales = bombaView.findViewById(R.id.edtValesB1);

            String iniStr = edtIni.getText() != null ? edtIni.getText().toString().trim() : "";
            String finStr = edtFin.getText() != null ? edtFin.getText().toString().trim() : "";
            String valesStr = edtVales.getText() != null ? edtVales.getText().toString().trim() : "0";

            if (TextUtils.isEmpty(iniStr) || TextUtils.isEmpty(finStr)) {
                continue;
            }

            hayDatos = true;

            double ini = Double.parseDouble(iniStr);
            double fin = Double.parseDouble(finStr);
            double vales = TextUtils.isEmpty(valesStr) ? 0 : Double.parseDouble(valesStr);
            double salidaContometro = fin - ini;
            double diferencia = vales - salidaContometro;

            CierreDiario cd = new CierreDiario();
            cd.setBomba(bombas[i]);
            cd.setLecturaInicial(ini);
            cd.setLecturaFinal(fin);
            cd.setSalidaVales(vales);
            cd.setSalidaContometro(salidaContometro);
            cd.setDiferencia(diferencia);
            cd.setFecha(fecha);
            cd.setOperador(operador);

            cierreDiarioDAO.insertar(cd);
        }

        if (!hayDatos) {
            Toast.makeText(this, "Debes llenar al menos una bomba para guardar", Toast.LENGTH_SHORT).show();
            return;
        }

        limpiarPrefs();
        limpiarCampos(); // ✅ AHORA SÍ BORRAMOS DESPUÉS DE GUARDAR
        Toast.makeText(this, "Cierres guardados correctamente", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cierreDiarioDAO.close();
    }

    private void limpiarCampos() {
        for (View bombaView : listaBombas) {
            TextInputEditText edtIni = bombaView.findViewById(R.id.edtIniB1);
            TextInputEditText edtFin = bombaView.findViewById(R.id.edtFinB1);
            TextInputEditText edtVales = bombaView.findViewById(R.id.edtValesB1);
            TextView txtSalida = bombaView.findViewById(R.id.txtSalidaB1);
            TextView txtDiferencia = bombaView.findViewById(R.id.txtDiferenciaB1);

            edtIni.setText("");
            edtFin.setText("");
            edtVales.setText("");
            txtSalida.setText("0.00");
            txtDiferencia.setText("0.00");
        }
        edtNombreOperador.setText("");
    }
}
