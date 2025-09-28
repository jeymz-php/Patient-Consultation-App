package com.example.patientinformationandonlineconsultationsystem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class CalendarAdapter extends BaseAdapter {

    private Context context;
    private List<Day> days;

    public CalendarAdapter(Context context, List<Day> days) {
        this.context = context;
        this.days = days;
    }

    @Override
    public int getCount() { return days.size(); }

    @Override
    public Object getItem(int position) { return days.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv;
        if (convertView == null) {
            tv = new TextView(context);
            tv.setPadding(16, 16, 16, 16);
            tv.setGravity(android.view.Gravity.CENTER);
        } else {
            tv = (TextView) convertView;
        }

        Day day = days.get(position);
        if (day.getDay() == 0) {
            tv.setText("");
        } else {
            tv.setText(String.valueOf(day.getDay()));
            tv.setBackground(day.isSelected() ?
                    context.getDrawable(R.drawable.selected_day_bg) :
                    context.getDrawable(R.drawable.default_day_bg));
        }

        return tv;
    }

    public void updateDays(List<Day> newDays) {
        this.days = newDays;
        notifyDataSetChanged();
    }
}
