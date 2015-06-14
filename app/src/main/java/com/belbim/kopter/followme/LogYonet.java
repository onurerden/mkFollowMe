package com.belbim.kopter.followme;

import java.util.ArrayList;

/**
 * Created by eakbiyik on 22.4.2015.
 */
public class LogYonet {
    public static ArrayList<LogMessage> diziLogMessage = new ArrayList<LogMessage>();
    private static LogYonet instance = null;

    public static synchronized LogYonet getInstance() {
        if (instance == null) {
            instance = new LogYonet();
        }
        return instance;
    }

    public int[] logGonder() {
        int diziBoyutu = diziLogMessage.size();
        int[] result = new int[diziLogMessage.size()];
        JSONProvider<LogMessage> jsp = new JSONProvider<>();
        IDeviceServerImpl ids = new IDeviceServerImpl();
        for (int i = 1; i < diziBoyutu; i++) {
            try {
                result[i] = ids.sendLog(jsp.entityToJson(diziLogMessage.get(i)));
                if (result[i] == 0) {
                    LogYonet.getInstance().diziLogMessage.remove(i);
                } else result[0] = 254;
            } catch (Exception ex) {
                result[i] = -2;
                result[0] = 254;
            }
        }
        return result;
    }

    public int logKaydet(int logLevel, String logMessage) {
        LogMessage mLogMessage = new LogMessage();
        mLogMessage.setDeviceId(InitInfo.getInstance().getMkSession().getDeviceId());
        mLogMessage.setDeviceType(DeviceType.MOBILE_DEVICE.getCode());
        mLogMessage.setLogLevel(logLevel);
        mLogMessage.setLogMessage(logMessage);

        diziLogMessage.add(mLogMessage);
        return diziLogMessage.size();
    }


}
