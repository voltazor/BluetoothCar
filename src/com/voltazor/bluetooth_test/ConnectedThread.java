package com.voltazor.bluetooth_test;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * Created by Dima on 22.01.14.
 */
public class ConnectedThread extends Thread {
    private static BluetoothSocket mmSocket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private Random random = new Random(System.currentTimeMillis());

    private boolean canceled = false;

    public ConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            mmInStream = socket.getInputStream();
            mmOutStream = socket.getOutputStream();
        } catch (IOException e) { }
    }

    public void run() {
        while (!canceled) {
            try {
                mmOutStream.write(random.nextInt(5));
            } catch (IOException e) {
                break;
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
