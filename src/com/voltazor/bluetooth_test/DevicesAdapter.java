package com.voltazor.bluetooth_test;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.devices_list_item, null);


        }



        
        return null;
    }

    @Override
    public int getCount() {
        return devices.size();
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
