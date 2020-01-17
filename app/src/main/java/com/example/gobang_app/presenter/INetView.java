package com.example.gobang_app.presenter;



import com.example.salut.SalutDevice;

import java.util.List;

/**
 * Created by Xuf on 2016/1/23.
 */
public interface INetView {
    void onWifiInitFailed(String message);

    void onWifiDeviceConnected(SalutDevice device);


    void onStartWifiServiceFailed();

    void onFindWifiPeers(List<SalutDevice> deviceList);


    void onPeersNotFound();

    void onDataReceived(Object o);

    void onSendMessageFailed();
}
