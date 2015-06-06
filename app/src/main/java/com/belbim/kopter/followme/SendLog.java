package com.belbim.kopter.followme;

/**
 * Created by eakbiyik on 22.4.2015.
 */
public class SendLog {
    private static SendLog instance = null;
    LogMessage mLogMessage = new LogMessage();

    public static synchronized SendLog getInstance() {
        if (instance == null) {
            instance = new SendLog();
        }
        return instance;
    }

    public int logla(int logLevel, String logMessage) {
        int result = -1;
        try {
            this.mLogMessage.setDeviceId(InitInfo.getInstance().getMkSession().getDeviceId());
            this.mLogMessage.setDeviceType(DeviceType.MOBILE_DEVICE.getCode());
            this.mLogMessage.setLogLevel(logLevel);
            this.mLogMessage.setLogMessage(logMessage);
            JSONProvider<LogMessage> jsp = new JSONProvider<>();
            IDeviceServerImpl ids = new IDeviceServerImpl();
            result = ids.sendLog(jsp.entityToJson(mLogMessage));
        } catch (Exception ex) {
            result = -2;
        }
        return result;
    }


}
