package com.example.ecologemoscow;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EcoPlaceAdapter extends RecyclerView.Adapter<EcoPlaceAdapter.EcoPlaceViewHolder> {
    private List<EcoPlace> ecoPlaces;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(EcoPlace place);
    }

    public EcoPlaceAdapter(List<EcoPlace> ecoPlaces, OnItemClickListener listener) {
        this.ecoPlaces = ecoPlaces;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EcoPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_eco_place, parent, false);
        return new EcoPlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EcoPlaceViewHolder holder, int position) {
        EcoPlace place = ecoPlaces.get(position);
        holder.nameTextView.setText(place.getName());
        holder.descriptionTextView.setText(place.getDescription());
        holder.addressTextView.setText(place.getAddress());
        holder.workingHoursTextView.setText(place.getWorkingHours());

        // Загрузка изображения
        holder.imageView.setImageResource(R.drawable.default_image_background);
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Обработка нажатия на карточку для открытия места на карте
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(place);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(place);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ecoPlaces.size();
    }

    static class EcoPlaceViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;
        TextView nameTextView;
        TextView descriptionTextView;
        TextView addressTextView;
        TextView workingHoursTextView;

        EcoPlaceViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            imageView = itemView.findViewById(R.id.image_view);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
            addressTextView = itemView.findViewById(R.id.address_text_view);
            workingHoursTextView = itemView.findViewById(R.id.working_hours_text_view);
        }
    }
} 