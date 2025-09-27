package com.example.appdespacho.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appdespacho.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // Declarar las tarjetas
    private CardView cardRegistroConsumo, cardCierreDiario, cardTransferenciaCrc,
            cardHistorial, cardReporteDiario, cardCerrarSesion;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ajuste de insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referenciar las tarjetas
        cardRegistroConsumo = findViewById(R.id.cardRegistroConsumo);
        cardCierreDiario = findViewById(R.id.cardCierreDiario);
        cardTransferenciaCrc = findViewById(R.id.cardTransferenciaCrc);
        cardHistorial = findViewById(R.id.cardHistorial);
        cardReporteDiario = findViewById(R.id.cardReporteDiario);
        cardCerrarSesion = findViewById(R.id.cardCerrarSesion);

        // Listeners para las tarjetas
        cardRegistroConsumo.setOnClickListener(v -> startActivity(new Intent(this, RegistroConsumoActivity.class)));
        cardCierreDiario.setOnClickListener(v -> startActivity(new Intent(this, CierreDiarioActivity.class)));
        cardTransferenciaCrc.setOnClickListener(v -> startActivity(new Intent(this, TransferenciaCrcActivity.class)));
        cardHistorial.setOnClickListener(v -> startActivity(new Intent(this, HistorialActivity.class)));
        cardReporteDiario.setOnClickListener(v -> startActivity(new Intent(this, ReporteDiarioActivity.class)));
        cardCerrarSesion.setOnClickListener(v -> {
            // lógica de cerrar sesión (opcional)
        });

        // Inicializar barra inferior
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // marcar item por defecto (si existe)
        bottomNavigationView.setSelectedItemId(R.id.home);

        // Listener (forma robusta, evita problemas con firmas del listener)
        bottomNavigationView.setOnItemSelectedListener(item -> handleBottomNavigation(item));
    }

    // Método separado para la navegación (más claro y testeable)
    private boolean handleBottomNavigation(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            return true; // ya estamos aquí
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
            // cerrar sesión: ejemplo
            // Intent intent = new Intent(this, LoginActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // startActivity(intent);
            // finish();
            return true;
        }

        return false;
    }
}
