package com.luxoft.paveltask.app.view.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.luxoft.paveltask.app.R;
import com.luxoft.paveltask.app.model.entity.GeoName;

import java.util.List;

public class GeoNameAdapter extends ArrayAdapter<GeoName> {

    public static final int DEFAULT_RESOURCE = R.layout.geo_name_list_item;

    static class ViewHolder {
        public TextView name;
        public TextView title;
    }

    protected int resource;

    protected ViewHolder viewHolder;

    public GeoNameAdapter(Context context, int resource, List<GeoName> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    public GeoNameAdapter(Context context, List<GeoName> objects) {
        super(context, DEFAULT_RESOURCE, objects);
        resource = DEFAULT_RESOURCE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)view.findViewById(R.id.gnli_name);
            viewHolder.title = (TextView)view.findViewById(R.id.gnli_title);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        System.out.println("View: " + view);
        GeoName item = getItem(position);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(item.getName());
        holder.title.setText("Population: " + item.getPopulation());
        return view;
    }
}
