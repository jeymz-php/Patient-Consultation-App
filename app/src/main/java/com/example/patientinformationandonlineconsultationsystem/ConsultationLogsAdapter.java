package com.example.patientinformationandonlineconsultationsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ConsultationLogsAdapter extends RecyclerView.Adapter<ConsultationLogsAdapter.ViewHolder> {

    private List<ConsultationLog> logs;

    public ConsultationLogsAdapter(List<ConsultationLog> logs) {
        this.logs = logs;
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
        ConsultationLog log = logs.get(position);
        holder.tvDoctor.setText("👨‍⚕️ " + log.getDoctorName() + " (" + log.getDoctorSpecialty() + ")");
        holder.tvDateTime.setText("📅 " + log.getDate() + " ⏰ " + log.getTime());
        holder.tvStatus.setText("Status: " + log.getStatus());
        holder.tvDiagnosis.setText("Diagnosis: " + (log.getDiagnosis().isEmpty() ? "-" : log.getDiagnosis()));
        holder.tvTreatment.setText("Treatment: " + (log.getTreatment().isEmpty() ? "-" : log.getTreatment()));
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctor, tvDateTime, tvStatus, tvDiagnosis, tvTreatment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctor = itemView.findViewById(R.id.tvDoctor);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDiagnosis = itemView.findViewById(R.id.tvDiagnosis);
            tvTreatment = itemView.findViewById(R.id.tvTreatment);
        }
    }
}
