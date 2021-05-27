package com.mig.firebase;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private ArrayList<dataMahasiswa> listMahasiswa;
    private Context context;

    public interface dataListener{
        void onDeleteData(dataMahasiswa data, int position);
    }

    dataListener listener;

    public Adapter(ArrayList<dataMahasiswa> listMahasiswa, Context context) {
        this.listMahasiswa = listMahasiswa;
        this.context = context;
        listener = (ListDataActivity) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_design, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String nim = listMahasiswa.get(position).getNim();
        final String nama = listMahasiswa.get(position).getNama();
        final String jurusan = listMahasiswa.get(position).getJurusan();

        holder.nim.setText("NIM : "+nim);
        holder.nama.setText("Nama : "+nama);
        holder.jurusan.setText("Jurusan : "+jurusan);

        holder.listItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final String[] action = {"Update","Delete"};
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setItems(action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                Bundle bundle = new Bundle();
                                bundle.putString("dataNIM", listMahasiswa.get(position).getNim());
                                bundle.putString("dataNama", listMahasiswa.get(position).getNama());
                                bundle.putString("dataJurusan", listMahasiswa.get(position).getJurusan());
                                bundle.putString("dataKey", listMahasiswa.get(position).getKey());
                                Intent intent = new Intent(v.getContext(), UpdateDataActivity.class);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                                break;
                            case 1:
                                listener.onDeleteData(listMahasiswa.get(position), position);
                                break;
                        }
                    }
                });
                alert.create();
                alert.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMahasiswa.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nim;
        private final TextView nama;
        private final TextView jurusan;
        private final LinearLayout listItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nim = itemView.findViewById(R.id.nim);
            nama = itemView.findViewById(R.id.nama);
            jurusan = itemView.findViewById(R.id.jurusan);
            listItem = itemView.findViewById(R.id.list_item);
        }
    }
}
