package com.example.patientinformationandonlineconsultationsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OptionAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] options;
    private final int[] icons;

    public OptionAdapter(Context context, String[] options, int[] icons) {
        super(context, R.layout.dialog_option_item, options);
        this.context = context;
        this.options = options;
        this.icons = icons;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.dialog_option_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.itemText);
        ImageView imageView = convertView.findViewById(R.id.itemIcon);

        textView.setText(options[position]);
        imageView.setImageResource(icons[position]);

        return convertView;
    }
}
