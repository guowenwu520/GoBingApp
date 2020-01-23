package com.example.salut;

import android.util.Log;

import com.arasthel.asyncjob.AsyncJob;
import com.example.salut.Callbacks.SalutCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import static com.example.salut.Salut.TAG;
//客户端
public class BackgroundClientRegistrationJob implements AsyncJob.OnBackgroundJob{


    private Salut salutInstance;
    private InetSocketAddress hostDeviceAddress;
    private final int BUFFER_SIZE = 65536;
    protected static boolean disableWiFiOnUnregister;
    protected static SalutCallback onRegistered;
    protected static SalutCallback onRegistrationFail;
    protected static SalutCallback onUnregisterSuccess;
    protected static SalutCallback onUnregisterFailure;


    public BackgroundClientRegistrationJob(Salut salutInstance, InetSocketAddress hostDeviceAddress)
    {
        this.hostDeviceAddress = hostDeviceAddress;
        this.salutInstance = salutInstance;
    }


    @Override
    public void doOnBackground() {
        Log.d(TAG, "\nAttempting to transfer registration data with the server...");
        Socket registrationSocket = new Socket();

        try
        {
            registrationSocket.connect(hostDeviceAddress);
            registrationSocket.setReceiveBufferSize(BUFFER_SIZE);
            registrationSocket.setSendBufferSize(BUFFER_SIZE);

            //If this code is reached, we've connected to the server and will transfer data.
            Log.d(TAG, salutInstance.thisDevice.deviceName + " is connected to the server, transferring registration data...");

            OutputStreamWriter we=new OutputStreamWriter(registrationSocket.getOutputStream());
            InputStreamReader reader=new InputStreamReader(registrationSocket.getInputStream());
            BufferedReader bufferedReader=new BufferedReader(reader);
            BufferedWriter bufferedWriter=new BufferedWriter(we);
            //TODO Use buffered streams.
            Log.e(TAG, "Sending client registration data to server...     {"+salutInstance.thisDevice+"}   ");
            String serializedClient = new Gson().toJson(salutInstance.thisDevice);
            Log.e(TAG, "doOnBackground: "+serializedClient );
            serializedClient+="\n";
            bufferedWriter.write(serializedClient);
            bufferedWriter.flush();


            if(!salutInstance.thisDevice.isRegistered)
            {
                    Log.v(TAG, "Receiving server registration data...");
                String serializedServer = "";
                String line = null;
                while((line = bufferedReader.readLine()) != null) {Log.e("erer","3434");
                    serializedServer += line;
                }
                System.out.println(serializedServer);
              //  String serializedServer = bufferedReader.readLine();
                TypeToken<SalutDevice> typeToken=new TypeToken<SalutDevice>(){};
                SalutDevice serverDevice = new Gson().fromJson(serializedServer, typeToken.getType());
                serverDevice.serviceAddress = registrationSocket.getInetAddress().toString().replace("/", "");
                salutInstance.registeredHost = serverDevice;

                Log.d(TAG, "Registered Host | " + salutInstance.registeredHost.deviceName);

                salutInstance.thisDevice.isRegistered = true;
                salutInstance.dataReceiver.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (onRegistered != null)
                            onRegistered.call();
                    }
                });

                salutInstance.startListeningForData();
            }
            else {
                String registrationCode = "";
                String line = null;
                while((line = bufferedReader.readLine()) != null) {
                    registrationCode += line;break;
                }
                System.out.println(registrationCode);
               // String registrationCode = bufferedReader.readLine(); //TODO Use to verify

                salutInstance.thisDevice.isRegistered = false;
                salutInstance.registeredHost = null;
                salutInstance.closeDataSocket();
                salutInstance.disconnectFromDevice();

                if(onUnregisterSuccess != null) //Success Callback.
                {
                    salutInstance.dataReceiver.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onUnregisterSuccess.call();
                        }
                    });
                }

                Log.d(TAG, "This device has successfully been unregistered from the server.");

            }

            bufferedWriter.close();
            bufferedReader.close();
            we.close();reader.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();

            Log.e(TAG, "An error occurred while attempting to register or unregister.");
            salutInstance.dataReceiver.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (onRegistrationFail != null && !salutInstance.thisDevice.isRegistered) //Prevents both callbacks from being called.
                        onRegistrationFail.call();
                    if(onUnregisterFailure != null)
                        onUnregisterFailure.call();

                }
            });


            if(salutInstance.thisDevice.isRegistered && salutInstance.isConnectedToAnotherDevice)
            {
                //Failed to unregister so an outright disconnect is necessary.
                salutInstance.disconnectFromDevice();
            }
        }
        finally {

            if(disableWiFiOnUnregister)
            {
                Salut.disableWiFi(salutInstance.dataReceiver.activity);
            }
            try
            {
                registrationSocket.close();
            }
            catch(Exception ex)
            {
                Log.e(TAG, "Failed to close registration socket.");
            }
        }
    }
}
