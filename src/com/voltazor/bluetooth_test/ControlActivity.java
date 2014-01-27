package com.voltazor.bluetooth_test;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

/**
 * Created by Dmitriy Dovbnya on 26.01.14.
 */
public class ControlActivity extends Activity {

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

        findViewById(R.id.knob_forward_backward).setOnTouchListener(new KnobProcessor(this, KnobProcessor.VERTICAL));

        findViewById(R.id.knob_left_right).setOnTouchListener(new KnobProcessor(this, KnobProcessor.HORIZONTAL));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCurrentThread != null) {
            mCurrentThread.disconnect();
        }
    }

}