package com.belbim.kopter.followme;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    boolean gpsKilitlendiMi =false;
    BroadcastReceiver mGpsReceiver;

    public GPSTracker(Context Context) {
        this.mContext = Context;
        locationGetir();

        mGpsReceiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                String str=intent.getAction();
                if (str.equals("android.location.GPS_FIX_CHANGE")) gpsKilitlendiMi=false;
            }
        };
        IntentFilter gpsIntentFilter =new IntentFilter();
        gpsIntentFilter.addAction("android.location.GPS_FIX_CHANGE");
        mContext.registerReceiver(mGpsReceiver,gpsIntentFilter);
    }

    public Location locationGetir() {

try {
    locMan = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    //locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, this); //yerine GPS başlatı yazdım.
    return location;

}catch (Exception ex){

    ex.printStackTrace();
    return null;
}
    }

    @Override
    public void onLocationChanged(Location loc) {
        gpsKilitlendiMi =true;
        location= loc;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

/*
if (status != LocationProvider.AVAILABLE){
    statusDegistiMi = true;
    gpsKilitlendiMi=false;
}
        //int x = extras.getInt("satellites");
       // Toast.makeText(getApplicationContext(),"Uydu Sayısı:"+x,Toast.LENGTH_SHORT).show();
*/
    }

    @Override
    public void onProviderEnabled(String provider) {
        gpsAcikMi=true;
    }

    @Override
    public void onProviderDisabled(String provider) {
        gpsAcikMi=false;
    }

    public void durdur() {
        if (locMan != null) {
            locMan.removeUpdates(GPSTracker.this);
            gpsKilitlendiMi=false;
        }
    }

    public void baslat(int GPSupdatePeriodmilis){
        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPSupdatePeriodmilis, 5, this);
    }

    public boolean gpsEnabledGetir(){
        boolean gpsAcikMi;
        gpsAcikMi= locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return gpsAcikMi;
    }

    public boolean gpsKilitlendiMi(){
        return this.gpsKilitlendiMi;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }
}

