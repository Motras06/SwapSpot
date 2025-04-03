package com.example.swapspot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class EchangeAdapter extends ArrayAdapter<Echange> {
    private LayoutInflater inflater;
    private int layout;
    private List<Echange> echanges;

    public EchangeAdapter(Context context, int resource, List<Echange> echanges) {
        super(context, resource, echanges);
        this.echanges = echanges;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
            holder = new ViewHolder();
            holder.itemImageView = convertView.findViewById(R.id.item_image);
            holder.nameView = convertView.findViewById(R.id.item_name);
            holder.usernamePostView = convertView.findViewById(R.id.item_name_publishing);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Echange echange = echanges.get(position);
        Glide.with(getContext())
                .load(new File(echange.getImagePath()))
                .placeholder(R.drawable.home)
                .into(holder.itemImageView);

        holder.nameView.setText(echange.getItemName());
        holder.usernamePostView.setText(echange.getUserName());

        return convertView;
    }

    private static class ViewHolder {
        ImageView itemImageView;
        TextView nameView;
        TextView usernamePostView;
    }
}
