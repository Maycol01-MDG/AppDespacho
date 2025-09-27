package com.example.appdespacho.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appdespacho.R;
import com.example.appdespacho.modelos.CierreDiario;
import java.util.List;

public class CierreDiarioAdapter extends RecyclerView.Adapter<CierreDiarioAdapter.ViewHolder> {

    private List<CierreDiario> lista;

    public CierreDiarioAdapter(List<CierreDiario> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial_cierre_diario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CierreDiario item = lista.get(position);

        holder.txtFecha.setText("Fecha: " + item.getFecha());
        holder.txtBomba.setText("Bomba: " + item.getBomba());
        holder.txtLecturas.setText("Lecturas: " + item.getLecturaInicial() + " - " + item.getLecturaFinal());
        holder.txtSalidaVales.setText("Salida vales: " + item.getSalidaVales());
        holder.txtSalidaContometro.setText("Salida contómetro: " + item.getSalidaContometro());
        holder.txtDiferencia.setText("Diferencia: " + item.getDiferencia());
        holder.txtOperador.setText("Operador: " + item.getOperador()); // 🔹 Nuevo campo
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtFecha, txtBomba, txtLecturas, txtSalidaVales, txtSalidaContometro, txtDiferencia, txtOperador;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtBomba = itemView.findViewById(R.id.txtBomba);
            txtLecturas = itemView.findViewById(R.id.txtLecturas);
            txtSalidaVales = itemView.findViewById(R.id.txtSalidaVales);
            txtSalidaContometro = itemView.findViewById(R.id.txtSalidaContometro);
            txtDiferencia = itemView.findViewById(R.id.txtDiferencia);
            txtOperador = itemView.findViewById(R.id.txtOperador);
        }
    }
}
