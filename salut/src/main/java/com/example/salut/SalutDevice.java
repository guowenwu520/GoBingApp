package com.example.salut;

import android.net.wifi.p2p.WifiP2pDevice;



import java.util.Map;


public class SalutDevice {


    public Map<String, String> txtRecord;

    public String deviceName;

    public String serviceName;

    public String instanceName;

    public String readableName;

    public boolean isRegistered;

    protected int servicePort;
    
    protected String TTP = "._tcp.";

    protected String macAddress;
    
    protected String serviceAddress;

    public Map<String, String> getTxtRecord() {
        return txtRecord;
    }

    public void setTxtRecord(Map<String, String> txtRecord) {
        this.txtRecord = txtRecord;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getReadableName() {
        return readableName;
    }

    public void setReadableName(String readableName) {
        this.readableName = readableName;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public String getTTP() {
        return TTP;
    }

    public void setTTP(String TTP) {
        this.TTP = TTP;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public SalutDevice(){}

    public SalutDevice(WifiP2pDevice device, Map<String, String> txtRecord) {
        this.serviceName = txtRecord.get("SERVICE_NAME");
        this.readableName = txtRecord.get("INSTANCE_NAME");
        this.instanceName = txtRecord.get("INSTANCE_NAME");
        this.deviceName = device.deviceName;
        this.macAddress = device.deviceAddress;
        this.txtRecord = txtRecord;

    }


//    @Override
//    public String toString()
//    {
//        return String.format("Salut Device | Service Name: %s TTP: %s Human-Readable Name: %s", instanceName, TTP, readableName);
//    }


    @Override
    public String toString() {
        return "SalutDevice{" +
                "txtRecord=" + txtRecord +
                ", deviceName='" + deviceName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", instanceName='" + instanceName + '\'' +
                ", readableName='" + readableName + '\'' +
                ", isRegistered=" + isRegistered +
                ", servicePort=" + servicePort +
                ", TTP='" + TTP + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", serviceAddress='" + serviceAddress + '\'' +
                '}';
    }
}
