package com.example.bai3;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<String> imageUriList;

    public ImageAdapter(Context context, List<String> imageUriList) {
        this.context = context;
        this.imageUriList = imageUriList;
    }

    @Override
    public int getCount() {
        return imageUriList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUriList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_item, parent, false);

            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.textView = convertView.findViewById(R.id.imageName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String uriString = imageUriList.get(position);

        if (uriString.startsWith("drawable://")) {
            String drawableName = uriString.replace("drawable://", "");
            int drawableId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
            holder.imageView.setImageResource(drawableId);
        } else {
            holder.imageView.setImageURI(Uri.parse(uriString));
        }

        // Hiển thị tên ảnh
        String fileName = uriString.substring(uriString.lastIndexOf('/') + 1);
        if (fileName.length() > 10) {
            fileName = fileName.substring(0, 10) + "...";
        }
        holder.textView.setText(fileName);

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}