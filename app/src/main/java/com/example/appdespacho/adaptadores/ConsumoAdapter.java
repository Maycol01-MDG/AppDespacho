package com.example.appdespacho.adaptadores;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdespacho.R;
import com.example.appdespacho.modelos.Consumo;

import java.util.List;

public class ConsumoAdapter extends RecyclerView.Adapter<ConsumoAdapter.ConsumoViewHolder> {

    private List<Consumo> lista;
    private final Context context;
    private final OnItemClickListener listener;

    // 🔹 Interfaz para manejar eventos de click
    public interface OnItemClickListener {
        void onEditarClick(Consumo consumo);
        void onEliminarClick(Consumo consumo);
    }

    // 🔹 Constructor
    public ConsumoAdapter(Context context, List<Consumo> listaConsumos, OnItemClickListener listener) {
        this.context = context;
        this.lista = listaConsumos;
        this.listener = listener;
    }

    // 🔹 Actualizar lista
    public void setLista(List<Consumo> lista) {
        this.lista = lista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ConsumoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_consumo, parent, false);
        return new ConsumoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsumoViewHolder holder, int position) {
        Consumo consumo = lista.get(position);

        holder.tvPlaca.setText("Placa: " + consumo.getPlaca());
        holder.tvFecha.setText("Fecha: " + consumo.getFecha());
        holder.tvTipo.setText("Tipo: " + consumo.getTipoConsumo());
        holder.tvGalones.setText("Galones: " + consumo.getGalones());
        holder.tvBomba.setText("Bomba: " + consumo.getBomba());
        holder.tvNumeroVale.setText("Vale: " + consumo.getNumeroVale());

        // 🔹 Botón Editar
        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) listener.onEditarClick(consumo);
        });

        // 🔹 Botón Eliminar con confirmación
        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) {
                new AlertDialog.Builder(context)
                        .setTitle("Confirmar eliminación")
                        .setMessage("¿Seguro que quieres eliminar este registro?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            listener.onEliminarClick(consumo);
                            lista.remove(consumo);
                            notifyDataSetChanged();
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (lista != null) ? lista.size() : 0;
    }

    // 🔹 ViewHolder
    public static class ConsumoViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlaca, tvFecha, tvTipo, tvGalones, tvBomba, tvNumeroVale;
        ImageView btnEditar, btnEliminar;

        public ConsumoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlaca = itemView.findViewById(R.id.tvPlaca);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvGalones = itemView.findViewById(R.id.tvGalones);
            tvBomba = itemView.findViewById(R.id.tvBomba);
            tvNumeroVale = itemView.findViewById(R.id.tvNumeroVale); // 🔹 Inicializamos
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
