package com.voltazor.bluetooth_test;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

/**
 * Created by Dmitriy Dovbnya on 26.01.14.
 */
public class ControlActivity extends Activity implements KnobProcessor.KnobHorizontalListener, KnobProcessor.KnobVerticalListener {

    private ConnectionThread mCurrentThread;

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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCurrentThread != null) {
            mCurrentThread.disconnect();
        }
    }

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