package com.voltazor.bluetooth_test;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;

    private DevicesAdapter adapter;
    private ProgressBar progressBar;

    private List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

    private boolean registered = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (checkBT()) {
            initUI();
            createReceiver();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registered) {
            unregisterReceiver(mReceiver);
        }
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
                if (!isChecked) {
                    devices.clear();
                    adapter.updateContent(devices);
                }
            }
        });

        startSearching.setEnabled(toggleState.isChecked());
        startSearching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devices.clear();
                adapter.updateContent(devices);
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
        }
    }

    private void startSearching() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
        progressBar.setVisibility(View.VISIBLE);
    }

    // Create a BroadcastReceiver
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    if (!devices.contains(device)) {
                        devices.add(device);
                        adapter.updateContent(devices);
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    progressBar.setVisibility(View.GONE);
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Connected");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Disconnected");
                    break;
            }
        }
    };

    private void createReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mReceiver, filter);
        registered = true;
    }

    private void connectToDevice(int position) {
        mBluetoothAdapter.cancelDiscovery();
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(this, ControlActivity.class);
        intent.putExtra(Const.EXTRA.DEVICE, devices.get(position));
        startActivity(intent);
    }

}
