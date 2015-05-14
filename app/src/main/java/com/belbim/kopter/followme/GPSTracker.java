package com.belbim.kopter.followme;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Created by eakbiyik on 26.12.2014.
 */
public class GPSTracker extends Service implements LocationListener {
    private final Context mContext;
    LocationManager locMan=null;
    Location location=null;
    boolean gpsAcikMi = false;

    BroadcastReceiver mGpsReceiver;

    public GPSTracker(Context Context) {
        this.mContext = Context;
        locationGetir();
        gpsAcikMi = locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
        InitInfo.getInstance().setGpsKAcikMi(gpsAcikMi);
    }

    public Location locationGetir() {
        try {
            locMan = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            return location;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location loc) {
        InitInfo.getInstance().setGpsKilitlendiMi(true);
        location= loc;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        gpsAcikMi = locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
        InitInfo.getInstance().setGpsKAcikMi(gpsAcikMi);
        InitInfo.getInstance().setGpsKilitlendiMi(false);
    }

    @Override
    public void onProviderEnabled(String provider) {
        gpsAcikMi = locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
        InitInfo.getInstance().setGpsKAcikMi(gpsAcikMi);

    }

    @Override
    public void onProviderDisabled(String provider) {
        gpsAcikMi = locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
        InitInfo.getInstance().setGpsKAcikMi(gpsAcikMi);
    }

    public void durdur() {
        if (locMan != null) {
            locMan.removeUpdates(GPSTracker.this);
            InitInfo.getInstance().setGpsKilitlendiMi(false);
        }
    }

    public void baslat(int GPSupdatePeriodmilis){
        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPSupdatePeriodmilis, 5, this);
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }
}

