package com.example.patientinformationandonlineconsultationsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;

public class TimeAdapter extends BaseAdapter {

    private Context context;
    private List<String> timeSlots;
    private int selectedPosition = -1;

    public TimeAdapter(Context context, List<String> timeSlots) {
        this.context = context;
        this.timeSlots = timeSlots;
    }

    @Override
    public int getCount() {
        return timeSlots.size();
    }

    @Override
    public Object getItem(int position) {
        return timeSlots.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelected(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    public String getSelectedTime() {
        if (selectedPosition >= 0 && selectedPosition < timeSlots.size()) {
            return timeSlots.get(selectedPosition);
        }
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_time_slot, parent, false);
            holder = new ViewHolder();
            holder.tvTimeSlot = convertView.findViewById(R.id.tvTimeSlot);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvTimeSlot.setText(timeSlots.get(position));

        if (position == selectedPosition) {
            holder.tvTimeSlot.setBackgroundResource(R.drawable.bg_time_selected);
            holder.tvTimeSlot.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.tvTimeSlot.setBackgroundResource(R.drawable.bg_time_normal);
            holder.tvTimeSlot.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvTimeSlot;
    }
}
