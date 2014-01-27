package com.voltazor.bluetooth_test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Dmitriy Dovbnya on 25.01.14.
 */
public class ConnectionThread implements Runnable {
    private static final String TAG = ConnectionThread.class.getSimpleName();

    private BluetoothSocket mSocket;

    private InputStream inStream;
    private OutputStream outStream;

    private boolean canceled = false;

    private volatile int curCommand = 0;

    public ConnectionThread(BluetoothDevice device) {
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            mSocket = device.createRfcommSocketToServiceRecord(Const.MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "createRfcommSocketToServiceRecord", e);
        }
    }

    @Override
    public void run() {
        // Cancel discovery because it will slow down the connection
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mSocket.connect();
        } catch (IOException e) {
            Log.e(TAG, "connect failed", e);
            // Unable to connect; close the socket and get out
            disconnect();
            return;
        }

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            inStream = mSocket.getInputStream();
            outStream = mSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "IO streams init failed", e);
            disconnect();
            return;
        }

        while (!canceled) {
            if (mSocket.isConnected()) {
                if (curCommand != COMMAND.PAUSE.value) {
                    write(curCommand);
                    Log.d(TAG, "Command: " + curCommand);
                }
            }

            read();

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Log.e(TAG, "sleep failed", e);
            }
        }

    }

    public void applyCommand(COMMAND command) {
//        if ((curCommand & command.value) == 0) {
//            curCommand &= command.value;
//        }
        curCommand = command.value;
        Log.d(TAG, "apply: " + command.name());
    }

    private void write(int msg) {
        try {
            outStream.write(msg);
        } catch (IOException e) {
            Log.e(TAG, "writing to out stream failed", e);
            disconnect();
        }
    }

    private void read() {
        byte[] buffer = new byte[128];
        // Keep listening to the InputStream while connected
        try {
            // Read from the InputStream
            if (inStream.available() > 0) {
                int bytes = inStream.read(buffer);
                if (bytes > 0) {
                    Log.d(TAG, "Response: " + new String(buffer, 0, bytes));
                }
            }
            // Send the obtained bytes to the UI Activity
//          mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
        } catch (IOException e) {
            Log.e(TAG, "reading from input stream failed", e);
            disconnect();
        }
    }

    public void disconnect() {
        try {
            if (outStream != null) {
                outStream.close();
            }
            if (inStream != null) {
                inStream.close();
            }
            mSocket.close();
            canceled = true;
        } catch (IOException e) {
            Log.e(TAG, "close socket", e);
        }
    }

    public enum COMMAND {

        PAUSE(0b0000_0000), FORWARD(0b0000_0011), LEFT(0b0000_1100), RIGHT(0b0011_0000), BACKWARD(0b1100_0000);

        public final int value;

        COMMAND(int value) {
            this.value = value;
        }

    }

}
