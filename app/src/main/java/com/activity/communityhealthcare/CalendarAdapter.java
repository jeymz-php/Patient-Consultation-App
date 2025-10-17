package com.activity.communityhealthcare;

import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private List<CalendarDate> calendarDateList;
    private OnDateClickListener listener;
    private int selectedPosition = -1;

    public interface OnDateClickListener {
        void onDateClick(CalendarDate calendarDate);
    }

    public CalendarAdapter(List<CalendarDate> calendarDateList, OnDateClickListener listener) {
        this.calendarDateList = calendarDateList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_date, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        CalendarDate calendarDate = calendarDateList.get(position);

        // Add this debug logging
        Log.d("CalendarAdapter", "Position: " + position +
                ", DayNumber: '" + calendarDate.getDayNumber() + "'" +
                ", DayName: '" + calendarDate.getDayName() + "'" +
                ", Selectable: " + calendarDate.isSelectable() +
                ", Today: " + calendarDate.isToday());

        holder.bind(calendarDate, position);
    }

    @Override
    public int getItemCount() {
        return calendarDateList.size();
    }

    public void updateDates(List<CalendarDate> newDates) {
        this.calendarDateList = newDates;
        this.selectedPosition = -1;
        notifyDataSetChanged();
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView tvDate, tvDay;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDay = itemView.findViewById(R.id.tvDay);
        }

        public void bind(CalendarDate calendarDate, int position) {
            tvDate.setText(calendarDate.getDayNumber());
            tvDay.setText(calendarDate.getDayName());

            // Convert dp to pixels for elevation
            float elevation4dp = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    4,
                    itemView.getContext().getResources().getDisplayMetrics()
            );

            float elevation2dp = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    2,
                    itemView.getContext().getResources().getDisplayMetrics()
            );

            // Reset visibility first
            tvDate.setVisibility(View.VISIBLE);
            tvDay.setVisibility(View.VISIBLE);

            if (calendarDate.getDayNumber().isEmpty()) {
                // Empty cell (padding for calendar alignment)
                cardView.setCardBackgroundColor(Color.TRANSPARENT);
                cardView.setCardElevation(0);
                cardView.setClickable(false);
                tvDate.setVisibility(View.INVISIBLE);
                tvDay.setVisibility(View.INVISIBLE);
            } else {
                // Handle non-empty cells based on selectable and today status
                if (position == selectedPosition && calendarDate.isSelectable()) {
                    // Selected date (only if selectable)
                    cardView.setCardBackgroundColor(Color.parseColor("#E87C00"));
                    tvDate.setTextColor(Color.WHITE);
                    tvDay.setTextColor(Color.WHITE);
                    cardView.setCardElevation(elevation4dp);
                    cardView.setClickable(true);
                } else if (calendarDate.isToday()) {
                    // Today's date - show special styling regardless of selectable status
                    cardView.setCardBackgroundColor(Color.parseColor("#E8F5E8"));
                    tvDate.setTextColor(Color.parseColor("#4CAF50"));
                    tvDay.setTextColor(Color.parseColor("#4CAF50"));
                    cardView.setCardElevation(elevation2dp);
                    cardView.setClickable(calendarDate.isSelectable());
                } else if (calendarDate.isSelectable()) {
                    // Normal selectable date
                    cardView.setCardBackgroundColor(Color.WHITE);
                    tvDate.setTextColor(Color.BLACK);
                    tvDay.setTextColor(Color.parseColor("#666666"));
                    cardView.setCardElevation(elevation2dp);
                    cardView.setClickable(true);
                } else {
                    // Unselectable date (past or too far future)
                    cardView.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                    tvDate.setTextColor(Color.parseColor("#9E9E9E"));
                    tvDay.setTextColor(Color.parseColor("#9E9E9E"));
                    cardView.setCardElevation(0);
                    cardView.setClickable(false);
                }
            }

            itemView.setOnClickListener(v -> {
                if (calendarDate.isSelectable() && !calendarDate.getDayNumber().isEmpty()) {
                    int previousPosition = selectedPosition;
                    selectedPosition = position;

                    // Notify changes
                    if (previousPosition != -1) {
                        notifyItemChanged(previousPosition);
                    }
                    notifyItemChanged(selectedPosition);

                    listener.onDateClick(calendarDate);
                }
            });
        }
    }
}