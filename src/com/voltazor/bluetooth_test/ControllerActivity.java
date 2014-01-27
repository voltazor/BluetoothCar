package com.voltazor.bluetooth_test;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

/**
 * Created by Dmitriy Dovbnya on 25.01.14.
 */
public class ControllerActivity extends Activity{
    private static final String TAG = ControllerActivity.class.getSimpleName();

    private ConnectionThread mCurrentThread;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controller_activity);

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

        ((ToggleButton) findViewById(R.id.forward)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    forward();
                } else {
                    pause();
                }
            }
        });

        ((ToggleButton) findViewById(R.id.left)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    left();
                } else {
                    pause();
                }
            }
        });

        ((ToggleButton) findViewById(R.id.right)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    right();
                } else {
                    pause();
                }
            }
        });

        ((ToggleButton) findViewById(R.id.back)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    backward();
                } else {
                    pause();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCurrentThread != null) {
            mCurrentThread.disconnect();
        }
    }

    private void forward() {
        applyCommand(ConnectionThread.COMMAND.FORWARD);
    }

    private void backward() {
        applyCommand(ConnectionThread.COMMAND.BACKWARD);
    }

    private void left() {
        applyCommand(ConnectionThread.COMMAND.LEFT);
    }

    private void right() {
        applyCommand(ConnectionThread.COMMAND.RIGHT);
    }

    private void pause() {
        applyCommand(ConnectionThread.COMMAND.PAUSE);
    }

    private void applyCommand(ConnectionThread.COMMAND command) {
        if (mCurrentThread != null) {
            mCurrentThread.applyCommand(command);
        }
    }

}
