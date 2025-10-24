package com.activity.communityhealthcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ConsultationAdapter extends RecyclerView.Adapter<ConsultationAdapter.ConsultationViewHolder> {

    private List<Consultation> consultationList;
    private List<Consultation> consultationListFull;
    private OnConsultationClickListener listener;

    public interface OnConsultationClickListener {
        void onConsultationClick(Consultation consultation);
    }

    public ConsultationAdapter(List<Consultation> consultationList, OnConsultationClickListener listener) {
        this.consultationList = consultationList;
        this.consultationListFull = new ArrayList<>(consultationList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConsultationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_consultation, parent, false);
        return new ConsultationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultationViewHolder holder, int position) {
        Consultation consultation = consultationList.get(position);
        holder.bind(consultation, listener);
    }

    @Override
    public int getItemCount() {
        return consultationList.size();
    }

    public void filterList(List<Consultation> filteredList) {
        consultationList = filteredList;
        notifyDataSetChanged();
    }

    public void updateConsultations(List<Consultation> newConsultations) {
        consultationList.clear();
        consultationList.addAll(newConsultations);
        consultationListFull.clear();
        consultationListFull.addAll(newConsultations);
        notifyDataSetChanged();
    }

    static class ConsultationViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardConsultation;
        private TextView tvDate, tvTime, tvStatus;

        public ConsultationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardConsultation = itemView.findViewById(R.id.cardConsultation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        public void bind(Consultation consultation, OnConsultationClickListener listener) {
            tvDate.setText(consultation.getAppointmentDate());
            tvTime.setText(consultation.getAppointmentTime());
            tvStatus.setText(consultation.getStatus());

            // Set status color
            switch (consultation.getStatus().toLowerCase()) {
                case "scheduled":
                    tvStatus.setTextColor(0xFF4CAF50); // Green
                    break;
                case "completed":
                    tvStatus.setTextColor(0xFF2196F3); // Blue
                    break;
                case "cancelled":
                    tvStatus.setTextColor(0xFFF44336); // Red
                    break;
                default:
                    tvStatus.setTextColor(0xFF666666); // Gray
            }

            cardConsultation.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onConsultationClick(consultation);
                }
            });
        }
    }
}