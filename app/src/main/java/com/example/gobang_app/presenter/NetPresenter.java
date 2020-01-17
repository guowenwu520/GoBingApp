package com.example.gobang_app.presenter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;


import com.example.gobang_app.bean.Message;
import com.example.gobang_app.interator.NetInteractor;
import com.example.gobang_app.interator.WifiInteractor;
import com.example.gobang_app.util.Constants;
import com.example.salut.SalutDevice;

import java.util.List;

/**
 * Created by Xuf on 2016/1/23.
 */
public class NetPresenter implements INetInteratorCallback {
    private INetView mNetView;
    private NetInteractor mNetInteractor;

    private int mGameMode;

    public NetPresenter(Context context, INetView netView, int gameMode) {
        mNetView = netView;
        mGameMode = gameMode;
        if (isWifiMode()) {
            mNetInteractor = new WifiInteractor(context, this);
        }
    }

    private boolean isWifiMode() {
        return mGameMode == Constants.WIFI_MODE;
    }

    public void init() {
        mNetInteractor.init();
    }

    public void unInit() {
        mNetInteractor.unInit();
    }

    public void startService() {
        mNetInteractor.startNetService();
    }

    public void stopService() {
        mNetInteractor.stopNetService();
    }

    public void sendToDevice(Message message, boolean isHost) {
        mNetInteractor.sendToDevice(message, isHost);
    }

    public void findPeers() {
        mNetInteractor.findPeers();
    }

    public void connectToHost(SalutDevice salutHost, BluetoothDevice blueToothHost) {
        mNetInteractor.connectToHost(salutHost, blueToothHost);
    }

    @Override
    public void onWifiDeviceConnected(SalutDevice device) {
        mNetView.onWifiDeviceConnected(device);
    }


    @Override
    public void onStartWifiServiceFailed() {
        mNetView.onStartWifiServiceFailed();
    }

    @Override
    public void onFindWifiPeers(List<SalutDevice> deviceList) {
        mNetView.onFindWifiPeers(deviceList);
    }


    @Override
    public void onPeersNotFound() {
        mNetView.onPeersNotFound();
    }

    @Override
    public void onDataReceived(Object o) {
        mNetView.onDataReceived(o);
    }

    @Override
    public void onSendMessageFailed() {
        mNetView.onSendMessageFailed();
    }

    @Override
    public void onMobileNotSupportDevice() {
        String message = "抱歉，您的设备不支持wifi直连";
        mNetView.onWifiInitFailed(message);
    }
}
