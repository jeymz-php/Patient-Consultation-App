package com.example.patientinformationandonlineconsultationsystem;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
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

        if (day.getDay() == 0) {
            // Placeholder → blank, light gray, not clickable
            holder.tvDay.setText("");
            holder.itemView.setBackgroundColor(Color.parseColor("#EEEEEE"));
            holder.itemView.setEnabled(false);
            return;
        }

        holder.tvDay.setText(String.valueOf(day.getDay()));

        // Calendar instances for today & current day
        Calendar today = Calendar.getInstance();
        Calendar dayCalendar = Calendar.getInstance();
        dayCalendar.set(day.getYear(), day.getMonth(), day.getDay());

        boolean isToday = (day.getYear() == today.get(Calendar.YEAR) &&
                day.getMonth() == today.get(Calendar.MONTH) &&
                day.getDay() == today.get(Calendar.DAY_OF_MONTH));

        boolean isPast = dayCalendar.before(today);

        if (isPast) {
            // Past days: gray text, default bg, disabled
            holder.tvDay.setTextColor(Color.parseColor("#999999"));
            holder.itemView.setBackground(ContextCompat.getDrawable(
                    holder.itemView.getContext(), R.drawable.day_background));
            holder.itemView.setEnabled(false);

        } else {
            // Future & today are clickable
            holder.itemView.setEnabled(true);

            if (day.isSelected()) {
                // Selected → Orange filled
                holder.tvDay.setTextColor(Color.WHITE);
                holder.itemView.setBackground(ContextCompat.getDrawable(
                        holder.itemView.getContext(), R.drawable.selected_day_bg));

            } else if (isToday) {
                // Today → Orange border, white background
                holder.tvDay.setTextColor(Color.parseColor("#E87C00"));
                holder.itemView.setBackground(ContextCompat.getDrawable(
                        holder.itemView.getContext(), R.drawable.today_day_bg));

            } else {
                // Normal future days
                holder.tvDay.setTextColor(Color.parseColor("#222222"));
                holder.itemView.setBackground(ContextCompat.getDrawable(
                        holder.itemView.getContext(), R.drawable.day_background));
            }
        }

        // Handle click only if not past
        holder.itemView.setOnClickListener(v -> {
            if (!isPast) {
                for (Day d : days) d.setSelected(false);
                day.setSelected(true);
                notifyDataSetChanged();
                listener.onDayClick(day);
            }
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
        }
    }
}
