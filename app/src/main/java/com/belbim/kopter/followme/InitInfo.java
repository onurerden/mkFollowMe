package com.belbim.kopter.followme;

/**
 * Created by eakbiyik on 29.12.2014.
 */
public class InitInfo {
    /*InitInfo değişkenleri*/
    public boolean isMobileDataEnabled;
    public int mobileEdgeOr3G ;
    public boolean isMobileConnected;
    public boolean isWifiEnabled;
    public boolean isInited=false;
    public MKSession mkSession=null;

    private static InitInfo instance = null;

    private InitInfo() {
    }

    public static synchronized InitInfo getInstance() {
        if (instance == null) {
            instance = new InitInfo();
        }
        return instance;
    }

    public boolean isMobileDataEnabled() {
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

    public boolean isMobileConnected() {
        return isMobileConnected;
    }

    public void setMobileConnected(boolean isMobileConnected) {
        this.isMobileConnected = isMobileConnected;
    }

    public boolean isWifiEnabled() {
        return isWifiEnabled;
    }

    public void setWifiEnabled(boolean isWifiEnabled) {
        this.isWifiEnabled = isWifiEnabled;
    }

    public boolean isInited() {
        return isInited;
    }

    public void setInited(boolean isInited) {
        this.isInited = isInited;
    }

    public void setMkSession(MKSession mMkSession){
        this.mkSession=mMkSession;
    }

    public MKSession getMkSession(){
        return mkSession;
    }
}
