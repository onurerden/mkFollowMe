package com.belbim.kopter.followme;

/**
 * Created by eakbiyik on 29.12.2014.
 */
public class InitInfo {
    private static InitInfo instance = null;
    /*InitInfo değişkenleri*/
    public boolean isMobileDataEnabled;
    public int mobileEdgeOr3G;
    public boolean isMobileConnected;
    public boolean wifiAvailable;
    public boolean isInited = false;
    public MKSession mkSession = null;
    boolean gpsKilitlendiMi = false;
    boolean GPSAcikMi = false;
    boolean wifiConnected = false;

    private InitInfo() {
    }

    public static synchronized InitInfo getInstance() {
        if (instance == null) {
            instance = new InitInfo();
        }
        return instance;
    }

    public boolean getMobileDataEnabled() {
        return isMobileDataEnabled;
    }

    public void setMobileDataEnabled(boolean isMobileDataEnabled) {
        this.isMobileDataEnabled = isMobileDataEnabled;
    }

    public int getMobileEdgeOr3G() {
        return mobileEdgeOr3G;
    }

    public void setMobileEdgeOr3G(int mobileEdgeOr3G) {
        this.mobileEdgeOr3G = mobileEdgeOr3G;
    }

    public boolean getMobileConnected() {
        return isMobileConnected;
    }

    public void setMobileConnected(boolean isMobileConnected) {
        this.isMobileConnected = isMobileConnected;
    }

    public boolean getWifiAvailable() {
        return wifiAvailable;
    }

    public void setWifiAvailable(boolean isWifiAvailable) {
        this.wifiAvailable = isWifiAvailable;
    }

    public boolean isInited() {
        return isInited;
    }

    public void setInited(boolean isInited) {
        this.isInited = isInited;
    }

    public MKSession getMkSession() {
        return mkSession;
    }

    public void setMkSession(MKSession mMkSession) {
        this.mkSession = mMkSession;
    }

    public boolean getGpsKilitlendiMi() {
        return gpsKilitlendiMi;
    }

    public void setGpsKilitlendiMi(boolean mGpsKilitlendiMi) {
        this.gpsKilitlendiMi = mGpsKilitlendiMi;
    }

    public void setGpsKAcikMi(boolean mGpsAcikMi) {
        this.GPSAcikMi = mGpsAcikMi;
    }

    public boolean getGpsAcikMi() {
        return GPSAcikMi;
    }

    public boolean getWifiConnected() {
        return wifiConnected;
    }

    public void setWifiConnected(boolean WifiConnected) {
        this.wifiConnected = WifiConnected;
    }
}
