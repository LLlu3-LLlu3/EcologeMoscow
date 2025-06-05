package com.example.ecologemoscow.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecologemoscow.R;
import com.example.ecologemoscow.models.EcoEvent;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.List;

public class EcoEventAdapter extends RecyclerView.Adapter<EcoEventAdapter.ViewHolder> {
    private List<EcoEvent> events;
    private Context context;

    public EcoEventAdapter(Context context, List<EcoEvent> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_eco_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EcoEvent event = events.get(position);
        holder.title.setText(event.getTitle());
        holder.date.setText(event.getDate());
        
        holder.itemView.setOnClickListener(v -> showEventDetails(event));
    }

    private void showEventDetails(EcoEvent event) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_event_details, null);
        
        TextView titleView = dialogView.findViewById(R.id.dialog_event_title);
        TextView dateView = dialogView.findViewById(R.id.dialog_event_date);
        TextView addressView = dialogView.findViewById(R.id.dialog_event_address);
        TextView descriptionView = dialogView.findViewById(R.id.dialog_event_description);
        
        titleView.setText(event.getTitle());
        dateView.setText(event.getDate());

        descriptionView.setText(event.getDescription());
        
        new MaterialAlertDialogBuilder(context)
            .setView(dialogView)
            .setPositiveButton("Закрыть", null)
            .show();
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setEvents(List<EcoEvent> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, address;
        
        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.event_title);
            date = itemView.findViewById(R.id.event_date);
            address = itemView.findViewById(R.id.event_address);
        }
    }
} 