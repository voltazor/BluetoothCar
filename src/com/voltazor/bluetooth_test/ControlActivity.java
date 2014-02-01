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
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Dmitriy Dovbnya on 26.01.14.
 */
public class ControlActivity extends Activity implements KnobProcessor.KnobHorizontalListener, KnobProcessor.KnobVerticalListener {
    private static final String TAG = ControlActivity.class.getSimpleName();

    private ConnectionThread mCurrentThread;
    private boolean registered = false;

    private ImageView mStatus;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_activity);

        BluetoothDevice device = null;
        Bundle extra = getIntent().getExtras();
        if (extra != null && extra.containsKey(Const.EXTRA.DEVICE)) {
            device = extra.getParcelable(Const.EXTRA.DEVICE);
        }
        if (device == null) {
            finish();
            return;
        }
        mCurrentThread = new ConnectionThread(device);
        (new Thread(mCurrentThread)).start();

        KnobProcessor knobProcessor = new KnobProcessor(this, KnobProcessor.VERTICAL);
        knobProcessor.setVerticalListener(this);
        findViewById(R.id.knob_forward_backward).setOnTouchListener(knobProcessor);

        knobProcessor = new KnobProcessor(this, KnobProcessor.HORIZONTAL);
        knobProcessor.setHorizontalListener(this);
        findViewById(R.id.knob_left_right).setOnTouchListener(knobProcessor);

        mStatus = (ImageView) findViewById(R.id.status);
        mStatus.setEnabled(false);

        createReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCurrentThread != null) {
            mCurrentThread.disconnect();
        }
        if (registered) {
            unregisterReceiver(mReceiver);
        }
    }

    private void createReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mReceiver, filter);
        registered = true;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            switch (action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    mStatus.setEnabled(true);
                    Log.d(TAG, "Connected");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    mStatus.setEnabled(false);
                    Log.d(TAG, "Disconnected");
                    break;
            }
        }
    };

    private void applyCommand(ConnectionThread.COMMAND command) {
        if (mCurrentThread != null) {
            mCurrentThread.applyCommand(command);
        }
    }

    @Override
    public void left(int value) {
        if (value != 0) {
            applyCommand(ConnectionThread.COMMAND.LEFT);
        } else {
            applyCommand(ConnectionThread.COMMAND.PAUSE);
        }
    }

    @Override
    public void right(int value) {
        if (value != 0) {
            applyCommand(ConnectionThread.COMMAND.RIGHT);
        } else {
            applyCommand(ConnectionThread.COMMAND.PAUSE);
        }
    }

    @Override
    public void forward(int value) {
        if (value != 0) {
            applyCommand(ConnectionThread.COMMAND.FORWARD);
        } else {
            applyCommand(ConnectionThread.COMMAND.PAUSE);
        }
    }

    @Override
    public void backward(int value) {
        if (value != 0) {
            applyCommand(ConnectionThread.COMMAND.BACKWARD);
        } else {
            applyCommand(ConnectionThread.COMMAND.PAUSE);
        }
    }

}