package com.example.patientinformationandonlineconsultationsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.DayViewHolder> {

    private List<Day> days;
    private OnDayClickListener listener;

    public interface OnDayClickListener {
        void onDayClick(Day day);
    }

    public DaysAdapter(List<Day> days, OnDayClickListener listener) {
        this.days = days;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Day day = days.get(position);
        holder.tvDay.setText(String.valueOf(day.getDay()));
        holder.tvMonthYear.setText((day.getMonth() + 1) + "/" + day.getYear());

        holder.itemView.setBackground(day.isSelected() ?
                ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.selected_day_bg)
                : ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.default_day_bg));

        holder.itemView.setOnClickListener(v -> {
            for (Day d : days) d.setSelected(false);
            day.setSelected(true);
            notifyDataSetChanged();
            listener.onDayClick(day);
        });
    }

    @Override
    public int getItemCount() { return days.size(); }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvMonthYear;
        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvMonthYear = itemView.findViewById(R.id.tvMonthYear);
        }
    }
}