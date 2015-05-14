package com.belbim.kopter.followme;

/**
 * Created by eakbiyik on 22.4.2015.
 */
public class SendLog {
    private static SendLog instance = null;
    LogMessage mLogMessage = new LogMessage();

    private SendLog() {
    }

    public static synchronized SendLog getInstance() {
        if (instance == null) {
            instance = new SendLog();
        }
        return instance;
    }

    public int send(int logLevel, String logMessage) {
        this.mLogMessage.setDeviceId(InitInfo.getInstance().getMkSession().getDeviceId());
        this.mLogMessage.setDeviceType(DeviceType.MOBILE_DEVICE.getCode());
        this.mLogMessage.setLogLevel(logLevel);
        this.mLogMessage.setLogMessage(logMessage);
        JSONProvider<LogMessage> jsp = new JSONProvider<>();
        IDeviceServerImpl ids = new IDeviceServerImpl();
        return ids.sendLog(jsp.entityToJson(mLogMessage));
    }
}
