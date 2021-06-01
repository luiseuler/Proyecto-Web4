package com.lagg.enfriamiento;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MiAdapter extends RecyclerView.Adapter<MiAdapter.ViewHolder> {
    private RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;
    String[][] dataset;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvId, tvUser, tvValor, tvFecha;
        ImageView ivDelete;
        public ViewHolder(@NonNull View itemView, RecyclerViewOnItemClickListener recyclerViewOnItemClickListener) {
            super(itemView);
            tvId = (TextView) itemView.findViewById(R.id.id);
            tvUser = (TextView) itemView.findViewById(R.id.usuario);
            tvValor = (TextView) itemView.findViewById(R.id.valor);
            tvFecha = (TextView) itemView.findViewById(R.id.fecha);
            ivDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewOnItemClickListener.onClick(view, getAdapterPosition());
                }
            });
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewOnItemClickListener.onClickDel(view, getAdapterPosition());
                }
            });
        }
    }

    public MiAdapter(String[][] dataset, RecyclerViewOnItemClickListener recyclerViewOnItemClickListener){
        this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sensor,parent,false);
        return new ViewHolder(view, recyclerViewOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvId.setText(dataset[position][0]);
        holder.tvUser.setText(dataset[position][1]);
        //holder.tvTipo.setText(dataset[position][2]);
        holder.tvValor.setText(dataset[position][3]);
        holder.tvFecha.setText(dataset[position][4]);
    }

    @Override
    public int getItemCount() {
        return dataset.length;
    }
}

