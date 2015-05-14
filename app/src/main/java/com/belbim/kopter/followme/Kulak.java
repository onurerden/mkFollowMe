package com.belbim.kopter.followme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by eakbiyik on 6.5.2015.
 */
public class Kulak extends BroadcastReceiver {

    public Kulak(Context mContext, Intent mIntent) {
        InitInfo.getInstance().setGpsKilitlendiMi(true);
        onReceive(mContext, mIntent);
    }

    @Override
    public void onReceive(Context mContext, Intent intent) {
        //Toast.makeText(mContext, "BroadCast Receiver Event Aldı", Toast.LENGTH_SHORT).show();
        Log.i("Info", "BroadCast Receiver Event Aldı");

        ConnectivityManager cnnMgr = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);

        NetworkInfo wifiInfo = cnnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = cnnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        InitInfo.getInstance().setMobileDataEnabled(mobileInfo.isAvailable());
        InitInfo.getInstance().setMobileEdgeOr3G(mobileInfo.getSubtype());
        InitInfo.getInstance().setMobileConnected(mobileInfo.isConnected());
        InitInfo.getInstance().setWifiAvailable(wifiInfo.isAvailable());
        if (mobileInfo.isConnected() && wifiInfo.isConnected()) {
            InitInfo.getInstance().setInited(false);
        }
        InitInfo.getInstance().setWifiConnected(wifiInfo.isConnected());
    }
}


