package com.example.patientinformationandonlineconsultationsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ConsultationAdapter extends RecyclerView.Adapter<ConsultationAdapter.ViewHolder> {

    public interface OnConsultationActionListener {
        void onDeleteSchedule(Consultation consultation);
        void onJoinFeed(Consultation consultation);
    }

    private List<Consultation> consultations;
    private OnConsultationActionListener listener;

    public ConsultationAdapter(List<Consultation> consultations, OnConsultationActionListener listener) {
        this.consultations = consultations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_consultation_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Consultation c = consultations.get(position);
        holder.tvDoctor.setText("👨‍⚕️ " + c.getDoctor().getName() + " (" + c.getDoctor().getSpecialization() + ")");
        holder.tvDateTime.setText("📅 " + c.getDate() + " ⏰ " + c.getTime());
        holder.tvStatus.setText("Status: " + c.getStatus());
        holder.tvDiagnosis.setText("Diagnosis: " + (c.getDiagnosis().isEmpty() ? "-" : c.getDiagnosis()));
        holder.tvTreatment.setText("Treatment: " + (c.getTreatment().isEmpty() ? "-" : c.getTreatment()));

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteSchedule(c));
        holder.btnJoin.setOnClickListener(v -> listener.onJoinFeed(c));
    }

    @Override
    public int getItemCount() {
        return consultations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctor, tvDateTime, tvStatus, tvDiagnosis, tvTreatment;
        Button btnDelete, btnJoin;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctor = itemView.findViewById(R.id.tvDoctor);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDiagnosis = itemView.findViewById(R.id.tvDiagnosis);
            tvTreatment = itemView.findViewById(R.id.tvTreatment);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnJoin = itemView.findViewById(R.id.btnJoin);
        }
    }
}
