package com.activity.communityhealthcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder> {

    private List<Doctor> doctorList;
    private List<Doctor> doctorListFull;
    private OnDoctorClickListener listener;

    public interface OnDoctorClickListener {
        void onDoctorClick(Doctor doctor);
    }

    public DoctorsAdapter(List<Doctor> doctorList, OnDoctorClickListener listener) {
        this.doctorList = doctorList;
        this.doctorListFull = new ArrayList<>(doctorList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.bind(doctor, listener);
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public void filterList(List<Doctor> filteredList) {
        doctorList = filteredList;
        notifyDataSetChanged();
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivDoctorAvatar;
        private TextView tvDoctorName;
        private TextView tvDoctorSpecialty;
        private TextView tvAvailability;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDoctorAvatar = itemView.findViewById(R.id.ivDoctorAvatar);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvDoctorSpecialty = itemView.findViewById(R.id.tvDoctorSpecialty);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);
        }

        public void bind(Doctor doctor, OnDoctorClickListener listener) {
            ivDoctorAvatar.setImageResource(doctor.getAvatarResId());
            tvDoctorName.setText(doctor.getName());
            tvDoctorSpecialty.setText(doctor.getSpecialty());
            tvAvailability.setText(doctor.getAvailability());

            // Set availability color
            if ("Available".equals(doctor.getAvailability())) {
                tvAvailability.setBackgroundResource(R.drawable.availability_bg);
                tvAvailability.setTextColor(itemView.getContext().getColor(android.R.color.holo_green_dark));
            } else {
                tvAvailability.setBackgroundResource(R.drawable.availability_busy_bg);
                tvAvailability.setTextColor(itemView.getContext().getColor(android.R.color.holo_red_dark));
            }

            itemView.setOnClickListener(v -> listener.onDoctorClick(doctor));
        }
    }
}