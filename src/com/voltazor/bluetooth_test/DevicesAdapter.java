package com.voltazor.bluetooth_test;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Dmitriy Dovbnya
 * <br>Date: 11.01.14 16:31
 */
public class DevicesAdapter extends BaseAdapter {

    private Context context;
    private List<BluetoothDevice> devices;

    public DevicesAdapter(Context context, List<BluetoothDevice> devices) {
        this.context = context;
        this.devices = devices;
    }

    public synchronized void updateContent(List<BluetoothDevice> devices) {
        if (devices == null) {
            devices = new ArrayList<BluetoothDevice>();
        }
        this.devices = devices;
        notifyDataSetChanged();
    }

    @Override
    public BluetoothDevice getItem(int position) {
        return devices.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        BluetoothDevice item;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.devices_list_item, null);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.address = (TextView) convertView.findViewById(R.id.address);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if ((item = getItem(position)) != null) {
            holder.name.setText(item.getName());
            holder.address.setText(item.getAddress());
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return devices != null ? devices.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return devices.get(position).hashCode();
    }

    private class ViewHolder {
        TextView name;
        TextView address;
    }

}
