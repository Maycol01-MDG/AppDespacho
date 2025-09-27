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
import com.example.appdespacho.modelos.TransferenciaCrc;

import java.util.List;

public class TransferenciaCrcAdapter extends RecyclerView.Adapter<TransferenciaCrcAdapter.ViewHolder> {

    private final Context context;
    private List<TransferenciaCrc> lista;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditarClick(TransferenciaCrc transferencia);
        void onEliminarClick(TransferenciaCrc transferencia);
    }

    public TransferenciaCrcAdapter(Context context, List<TransferenciaCrc> lista, OnItemClickListener listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransferenciaCrcAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transferencia_crc, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransferenciaCrcAdapter.ViewHolder holder, int position) {
        TransferenciaCrc t = lista.get(position);

        holder.txtId.setText("ID: " + t.getId());
        holder.txtFecha.setText("Fecha: " + t.getFecha());
        holder.txtVale.setText("Vale: " + t.getNumeroVale());
        holder.txtGalones.setText("Galones: " + t.getGalones());
        holder.txtCamion.setText("Camión CRC: " + t.getCamionCrc());

        // 🔹 Botón Editar
        holder.btnEditarcrc.setOnClickListener(v -> {
            if (listener != null) listener.onEditarClick(t);
        });

        // 🔹 Botón Eliminar
        holder.btnEliminarcrc.setOnClickListener(v -> {
            if (listener != null) listener.onEliminarClick(t);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtId, txtFecha, txtVale, txtGalones, txtCamion;
        ImageView btnEditarcrc, btnEliminarcrc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtId = itemView.findViewById(R.id.txtId);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtVale = itemView.findViewById(R.id.txtVale);
            txtGalones = itemView.findViewById(R.id.txtGalones);
            txtCamion = itemView.findViewById(R.id.txtCamion);
            btnEditarcrc = itemView.findViewById(R.id.btnEditarcrc);
            btnEliminarcrc = itemView.findViewById(R.id.btnEliminarcrc);
        }
    }
    public void actualizarLista(List<TransferenciaCrc> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }
}
