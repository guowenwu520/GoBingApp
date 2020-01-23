package com.example.salut;

import android.util.Log;

import com.arasthel.asyncjob.AsyncJob;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
//服务端
public class BackgroundServerRegistrationJob implements AsyncJob.OnBackgroundJob{

    private Salut salutInstance;
    private Socket clientSocket;

    public BackgroundServerRegistrationJob(Salut salutInstance, Socket clientSocket)
    {
        this.clientSocket = clientSocket;
        this.salutInstance = salutInstance;
    }

    @Override
    public void doOnBackground() {
        try {
            //If this code is reached, a client has connected and transferred data.
            Log.d(Salut.TAG, "A device has connected to the server, transferring data...");
        //    DataInputStream fromClient = new DataInputStream(clientSocket.getInputStream());
           OutputStreamWriter toClient=new OutputStreamWriter(clientSocket.getOutputStream());
            InputStreamReader reader=new InputStreamReader(clientSocket.getInputStream());
            BufferedReader bufferedReader=new BufferedReader(reader);
            BufferedWriter bufferedWriter=new BufferedWriter(toClient);
            Log.v(Salut.TAG, "Receiving client registration data...");
            String serializedClient = "";
            String line = null;
            while((line = bufferedReader.readLine()) != null) {
                serializedClient += line;
                break;
            }
            System.out.println(serializedClient);
           // String serializedClient = bufferedReader.readLine();
            Log.e("sd", "doOnBackground: "+serializedClient );
            TypeToken<SalutDevice> typeToken=new TypeToken<SalutDevice>(){};
            SalutDevice clientDevice = new Gson().fromJson(serializedClient, typeToken.getType());
            clientDevice.serviceAddress = clientSocket.getInetAddress().toString().replace("/", "");


            if (!clientDevice.isRegistered) {

                Log.v(Salut.TAG, "Sending server registration data...");
                String serializedServer = new Gson().toJson(salutInstance.thisDevice)+"\n";
                bufferedWriter.write(serializedServer);
                bufferedWriter.flush();

                Log.d(Salut.TAG, "Registered device and user: " + clientDevice);
                clientDevice.isRegistered = true;
                final SalutDevice finalDevice = clientDevice; //Allows us to get around having to add the final modifier earlier.
                if (salutInstance.registeredClients.isEmpty()) {
                    salutInstance.startListeningForData();
                }
                salutInstance.registeredClients.add(clientDevice);

                if (salutInstance.onDeviceRegisteredWithHost != null) {
                    salutInstance.dataReceiver.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            salutInstance.onDeviceRegisteredWithHost.call(finalDevice);
                        }
                    });
                }

            } else {
                Log.d(Salut.TAG, "\nReceived request to unregister device.\n");

                Log.v(Salut.TAG, "Sending registration code...");
                bufferedWriter.write(Salut.UNREGISTER_CODE);
                bufferedWriter.flush();

                for(SalutDevice registered : salutInstance.registeredClients)
                {
                    if(registered.serviceAddress.equals(clientSocket.getInetAddress().toString().replace("/", "")))
                    {
                        salutInstance.registeredClients.remove(registered);
                        Log.d(Salut.TAG, "\nSuccesfully unregistered device.\n");
                    }
                }
            }

            bufferedReader.close();
            toClient.close();
            bufferedWriter.close();
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(Salut.TAG, "An error occurred while dealing with registration for a client.");
        }
        finally {
            try
            {
                clientSocket.close();
            }
            catch (Exception ex)
            {
                Log.e(Salut.TAG, "Failed to close registration socket.");
            }
        }
    }
}
