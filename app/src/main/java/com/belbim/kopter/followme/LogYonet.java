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
        int[] result = new int[diziLogMessage.size()];
        JSONProvider<LogMessage> jsp = new JSONProvider<>();
        IDeviceServerImpl ids = new IDeviceServerImpl();
        for (int i = 0; i < diziLogMessage.size(); i++) {
            try {
                result[i] = ids.sendLog(jsp.entityToJson(diziLogMessage.get(i)));
            } catch (Exception ex) {
                result[i] = -2;
            }
            System.out.println(result[i]);
        }

        for (int i = 0; i < diziLogMessage.size(); i++) {
            if (result[i] == 0) {
                LogYonet.getInstance().diziLogMessage.remove(i);
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
