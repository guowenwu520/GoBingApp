package com.example.salut;

import android.util.Log;

import com.arasthel.asyncjob.AsyncJob;
import com.example.salut.Callbacks.SalutCallback;
import com.google.common.base.Charsets;
import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by markrjr on 6/8/15.
 */
public class BackgroundDataSendJob implements AsyncJob.OnBackgroundJob{

    private final int BUFFER_SIZE = 65536;
    private Salut salutInstance;
    private String data;
    private SalutCallback onFailure;
    private SalutDevice device;

    public BackgroundDataSendJob(SalutDevice device, Salut salutInstance, String data, SalutCallback onFailure)
    {
        this.data = data;
        this.device = device;
        this.salutInstance = salutInstance;
        this.onFailure = onFailure;
    }

    @Override
    public void doOnBackground() {

        Log.d(Salut.TAG, "\nAttempting to send data to a device.");
        Socket dataSocket = new Socket();

        try {
            dataSocket.connect(new InetSocketAddress(device.serviceAddress, device.servicePort));
            dataSocket.setReceiveBufferSize(BUFFER_SIZE);
            dataSocket.setSendBufferSize(BUFFER_SIZE);

            //If this code is reached, a client has connected and transferred data.
            Log.e(Salut.TAG, "Connected, transferring data...");
            BufferedOutputStream dataStreamToOtherDevice = new BufferedOutputStream(dataSocket.getOutputStream());

            String dataToSend = data;

            dataStreamToOtherDevice.write(dataToSend.getBytes(Charsets.UTF_8));
            dataStreamToOtherDevice.flush();
            dataStreamToOtherDevice.close();

            Log.e(Salut.TAG, "Successfully sent data.");
            Log.e("senddata",dataToSend);
        } catch (Exception ex) {
            Log.d(Salut.TAG, "An error occurred while sending data to a device.");
            if (onFailure != null)
                onFailure.call();
            ex.printStackTrace();
        } finally {
            try
            {
                dataSocket.close();
            }
            catch(Exception ex)
            {
                Log.e(Salut.TAG, "Failed to close data socket.");
            }

        }
    }
}
