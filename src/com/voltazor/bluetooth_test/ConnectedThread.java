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
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Random random = new Random(System.currentTimeMillis());

    private static boolean canceled = false;

    public ConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        while (!canceled) {
            try {
                mmOutStream.write(random.nextInt(5));
            } catch (IOException e) {
                break;
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public static void cancel() {
        canceled = true;
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
