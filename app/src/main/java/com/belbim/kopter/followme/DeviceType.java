package com.belbim.kopter.followme;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by asay on 24.12.2014.
 */
public enum DeviceType {
    KOPTER_DEVICE("mk", "Kopter"),
    MOBILE_DEVICE("mp", "Mobile");


    private String code;
    private String label;


    /**
     * A mapping between the integer code and its corresponding Status to facilitate lookup by code.
     */
    private static Map<String, DeviceType> codeToStatusMapping;

    private DeviceType(String code, String label) {
        this.code = code;
        this.label = label;

    }

    public static DeviceType getStatus(int i) {
        if (codeToStatusMapping == null) {
            initMapping();
        }
        return codeToStatusMapping.get(i);
    }

    private static void initMapping() {
        codeToStatusMapping = new HashMap<String, DeviceType>();
        for (DeviceType s : values()) {
            codeToStatusMapping.put(s.code, s);
        }
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }
}
