package com.voltazor.bluetooth_test;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private BluetoothAdapter mBluetoothAdapter;

    private ProgressBar progressBar;
    private DevicesAdapter adapter;

    private List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

    private boolean registered = false;
    private boolean connected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (checkBT()) {
            initUI();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registered) {
            unregisterReceiver(mReceiver);
        }
        setBluetoothEnabled(false);
    }

    private boolean checkBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null;
    }

    private void initUI() {
        ToggleButton toggleState = (ToggleButton) findViewById(R.id.toggleState);
        final Button startSearching = (Button) findViewById(R.id.startSearching);
        ListView foundDevices = (ListView) findViewById(R.id.foundDevices);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        toggleState.setEnabled(true);
        toggleState.setChecked(mBluetoothAdapter.isEnabled());
        toggleState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setBluetoothEnabled(isChecked);
                startSearching.setEnabled(isChecked);
            }
        });

        startSearching.setEnabled(toggleState.isChecked());
        startSearching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearResults();
                startSearching();
            }
        });

        adapter = new DevicesAdapter(this, devices);
        foundDevices.setAdapter(adapter);
        foundDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connectToDevice(position);
            }
        });
    }

    private void setBluetoothEnabled(boolean enabled) {
        if (enabled) {
            mBluetoothAdapter.enable();
        } else {
            mBluetoothAdapter.disable();
            clearResults();
        }
    }

    private void startSearching() {
        mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();
        createReceiver();
    }

    private void createReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
        registered = true;
    }

    private void clearResults() {
        devices.clear();
        adapter.updateContent(devices);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                if (!devices.contains(device)) {
                    devices.add(device);
                    adapter.updateContent(devices);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                progressBar.setVisibility(View.VISIBLE);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                progressBar.setVisibility(View.GONE);
            }
        }
    };


    private void connectToDevice(int position) {
        if (connected) {
//            ConnectedThread.cancel();
            connected = false;
        } else {
            BluetoothDevice device = devices.get(position);
            (new ConnectThread(device)).start();
            connected = true;
        }

    }

}
