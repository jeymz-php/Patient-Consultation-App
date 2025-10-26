package com.activity.communityhealthcare;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    private List<TimeSlot> timeSlotList;
    private OnTimeSlotClickListener listener;
    private int selectedPosition = -1;
    private String selectedTime = ""; // Added field for string-based selection

    public interface OnTimeSlotClickListener {
        void onTimeSlotClick(TimeSlot timeSlot);
    }

    public TimeSlotAdapter(List<TimeSlot> timeSlotList, OnTimeSlotClickListener listener) {
        this.timeSlotList = timeSlotList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlot timeSlot = timeSlotList.get(position);
        holder.bind(timeSlot, position);
    }

    @Override
    public int getItemCount() {
        return timeSlotList.size();
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    // Added method for string-based selection
    public void setSelectedTime(String selectedTime) {
        this.selectedTime = selectedTime;
        // Find the position for this time
        selectedPosition = -1;
        for (int i = 0; i < timeSlotList.size(); i++) {
            if (timeSlotList.get(i).getTime().equals(selectedTime)) {
                selectedPosition = i;
                break;
            }
        }
        notifyDataSetChanged();
    }

    class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView tvTime;
        private View itemLayout;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvTime = itemView.findViewById(R.id.tvTime);
            itemLayout = itemView;
        }

        public void bind(TimeSlot timeSlot, int position) {
            tvTime.setText(timeSlot.getTime());

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

            // Check both position-based and string-based selection
            boolean isSelected = (position == selectedPosition) ||
                    timeSlot.getTime().equals(selectedTime);

            if (isSelected) {
                // Selected state - Orange background, white text
                cardView.setCardBackgroundColor(Color.parseColor("#E87C00"));
                tvTime.setTextColor(Color.WHITE);
                cardView.setCardElevation(elevation4dp);
            } else {
                // Normal state - White background, black text
                cardView.setCardBackgroundColor(Color.WHITE);
                tvTime.setTextColor(Color.BLACK);
                cardView.setCardElevation(elevation2dp);
            }

            itemView.setOnClickListener(v -> {
                if (timeSlot.isAvailable()) {
                    int previousPosition = selectedPosition;
                    selectedPosition = position;
                    selectedTime = timeSlot.getTime(); // Update string selection

                    // Notify changes
                    if (previousPosition != -1) {
                        notifyItemChanged(previousPosition);
                    }
                    notifyItemChanged(selectedPosition);

                    listener.onTimeSlotClick(timeSlot);
                }
            });
        }
    }
}