package com.belbim.kopter.followme;

/**
 * Created by eakbiyik on 22.4.2015.
 */
public class LogMessage {

    int deviceId;
    String deviceType; //MK or MP
    int logLevel; //hata mesajı 1, bilgi mesajı 2, diğer 99
    String logMessage;

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

}
