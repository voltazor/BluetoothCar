package com.voltazor.bluetooth_test;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

    private BluetoothAdapter mBluetoothAdapter;

    private ToggleButton toggleState;
    private Button startSearching;
    private ListView foundDevices;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        toggleState = (ToggleButton) findViewById(R.id.toggleState);
        startSearching = (Button) findViewById(R.id.startSearching);
        foundDevices = (ListView) findViewById(R.id.foundDevices);

        if (checkBT()) {
            initUI();
        }
    }

    private boolean checkBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null;
    }

    private void initUI() {
        toggleState = (ToggleButton) findViewById(R.id.toggleState);
        startSearching = (Button) findViewById(R.id.startSearching);
        foundDevices = (ListView) findViewById(R.id.foundDevices);

        toggleState.setChecked(mBluetoothAdapter.isEnabled());



    }

}
