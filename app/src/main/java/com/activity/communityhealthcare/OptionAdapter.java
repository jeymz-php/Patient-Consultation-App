package com.activity.communityhealthcare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Arrays;

public class OptionAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] options;
    private int[] icons;

    public OptionAdapter(Context context, String[] options, int[] icons) {
        super(context, R.layout.item_option, options);
        this.context = context;
        this.options = options;
        this.icons = icons;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_option, parent, false);
            holder = new ViewHolder();
            holder.optionIcon = convertView.findViewById(R.id.optionIcon);
            holder.optionText = convertView.findViewById(R.id.optionText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Set option text
        holder.optionText.setText(options[position]);

        // Set option icon
        if (icons != null && position < icons.length) {
            holder.optionIcon.setImageResource(icons[position]);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView optionIcon;
        TextView optionText;
    }
}
