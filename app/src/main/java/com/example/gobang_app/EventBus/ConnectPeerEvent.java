package com.example.gobang_app.EventBus;

import android.bluetooth.BluetoothDevice;

import com.example.salut.SalutDevice;

/**
 * Created by Administrator on 2016/1/25.
 */
public class ConnectPeerEvent {

    public SalutDevice mSalutDevice;
    public BluetoothDevice mBlueToothDevice;

    public ConnectPeerEvent(SalutDevice device, BluetoothDevice bluetoothDevice) {
        mSalutDevice = device;
        mBlueToothDevice = bluetoothDevice;
    }
}
