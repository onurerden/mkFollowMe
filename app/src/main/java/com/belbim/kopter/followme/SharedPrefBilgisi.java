package com.belbim.kopter.followme;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by eakbiyik on 26.12.2014.
 */
public class SharedPrefBilgisi {
    static SharedPreferences mSharedPrefs;
    static int mGuncellemePeriyodu;
    static int mCihazID;
    static boolean mRotaSecenegi;
    static int mAccuarcy;
    static boolean mWifiCheck;
    static int mGPSupdatePeriodmilis;
    static String mKullaniciAdi;
    static OperationConfig mOperationConfig;

    public SharedPrefBilgisi(Context mContext){
        mSharedPrefs= mContext.getSharedPreferences("Degerler.xml", mContext.MODE_PRIVATE);
        mOperationConfig = new OperationConfig();
        populate();
    }

    public void populate() {
        this.mGuncellemePeriyodu = mSharedPrefs.getInt("guncelleme_periyodu", 5000);
        this.mOperationConfig.SetHost(mSharedPrefs.getString("server_url", "mk-onurerden.rhcloud.com"));
        this.mCihazID = mSharedPrefs.getInt("cihazId", -1);
        this.mRotaSecenegi = mSharedPrefs.getBoolean("rotaSecenegi", false);
        this.mAccuarcy = mSharedPrefs.getInt("accuarcy", 20);
        this.mWifiCheck = mSharedPrefs.getBoolean("wifiCheck", false);
        this.mGPSupdatePeriodmilis = mSharedPrefs.getInt("updatePeriodmilis", 2000);
        this.mKullaniciAdi = mSharedPrefs.getString("kullaniciAdi", "");

    }

    public int guncellemePeriyoduGetir(){
        return this.mGuncellemePeriyodu;
    }

    public int cihazIdGetir() {
        return this.mCihazID;
    }

    public boolean rotaSecenegiGetir() {
        return this.mRotaSecenegi;
    }

    public int accuarcyGetir() {
        return this.mAccuarcy;
    }

    public boolean wifiCheckGetir() {
        return this.mWifiCheck;
    }

    public int GPSupdatePeriodmilisGetir() {
        return this.mGPSupdatePeriodmilis;
    }

    public String kullaniciAdiGetir() {
        return this.mKullaniciAdi;
    }

    public void guncellemePeriyoduYaz(int periyod) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putInt("guncelleme_periyodu", periyod);
        editor.commit();
    }

    public void serverUrlYaz(String uRL) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putString("server_url", uRL);
        editor.commit();
        mOperationConfig.SetHost(uRL);
    }

    public String serverUrlGetir() {
        return mOperationConfig.getHost();
    }

    public void cihazIdYaz(int cihazId) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putInt("cihazId", cihazId);
        editor.commit();
    }

    public void rotaSecenegiYaz(boolean rotaSecenegi){
        SharedPreferences.Editor editor=mSharedPrefs.edit();
        editor.putBoolean("rotaSecenegi",rotaSecenegi);
        editor.commit();
    }

    public void accuarcyYaz(int accuarcy){
        SharedPreferences.Editor editor=mSharedPrefs.edit();
        editor.putInt("accuarcy",accuarcy);
        editor.commit();
    }

    public void wifiCheckYaz(boolean wifiCheck){
        SharedPreferences.Editor editor=mSharedPrefs.edit();
        editor.putBoolean("wifiCheck",wifiCheck);
        editor.commit();
    }

    public void GPSupdatePeriodmilisYaz(int updatePeriodmilisGetir){
        SharedPreferences.Editor editor=mSharedPrefs.edit();
        editor.putInt("updatePeriodmilis",updatePeriodmilisGetir);
        editor.commit();
    }

    public void kullaniciAdiYaz(String mKullaniciAdi) {
        SharedPreferences.Editor editor=mSharedPrefs.edit();
        editor.putString("kullaniciAdi", mKullaniciAdi);
        editor.commit();
    }


}
