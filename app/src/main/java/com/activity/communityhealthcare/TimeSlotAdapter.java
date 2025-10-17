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

    class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView tvTime, tvStatus;
        private View itemLayout;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            itemLayout = itemView;
        }

        public void bind(TimeSlot timeSlot, int position) {
            // Always set the text - this is crucial
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

            if (timeSlot.isAvailable()) {
                tvStatus.setText("Available");

                // Check if this is the selected position
                if (position == selectedPosition) {
                    // Selected state - Orange background, white text
                    cardView.setCardBackgroundColor(Color.parseColor("#E87C00"));
                    tvTime.setTextColor(Color.WHITE);
                    tvStatus.setTextColor(Color.WHITE);
                    cardView.setCardElevation(elevation4dp);
                } else {
                    // Normal available state - White background, colored text
                    cardView.setCardBackgroundColor(Color.WHITE);
                    tvTime.setTextColor(Color.BLACK);
                    tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                    cardView.setCardElevation(elevation2dp);
                }

                itemLayout.setAlpha(1.0f);
                itemLayout.setEnabled(true);
            } else {
                // Unavailable state
                tvStatus.setText("Unavailable");
                cardView.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                tvTime.setTextColor(Color.parseColor("#9E9E9E"));
                tvStatus.setTextColor(Color.parseColor("#F44336"));
                cardView.setCardElevation(0);
                itemLayout.setAlpha(0.7f);
                itemLayout.setEnabled(false);
            }

            itemView.setOnClickListener(v -> {
                if (timeSlot.isAvailable()) {
                    int previousPosition = selectedPosition;
                    selectedPosition = position;

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