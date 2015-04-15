package com.belbim.kopter.followme;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by eakbiyik on 26.12.2014.
 */
public class SharedPrefBilgisi {
    private static SharedPreferences mSharedPrefs ;

//    private static SharedPrefBilgisi instance = null;

  /*  public static synchronized SharedPrefBilgisi getInstance(Context mContext) {
        if (instance == null) {
            instance = new SharedPrefBilgisi(mContext);
        }
        return instance;
    }*/

    public SharedPrefBilgisi(Context mContext){
        mSharedPrefs= mContext.getSharedPreferences("Degerler.xml", mContext.MODE_PRIVATE);
        OperationConfig.host = serverUrlGetir();
    }

    public int guncellemePeriyoduGetir(){
        int guncellemePeriyodu;
        guncellemePeriyodu=mSharedPrefs.getInt("guncelleme_periyodu",5000);
        return guncellemePeriyodu;
    }

    public String serverUrlGetir(){
        String serverURL;
        serverURL=mSharedPrefs.getString("server_url", "mk-onurerden.rhcloud.com");
        return serverURL;
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
        OperationConfig.host = uRL;
    }

    public int cihazIdGetir(){
        int cihazId;
        cihazId=mSharedPrefs.getInt("cihazId",-1);
        return cihazId;
    }

    public void cihazIdYaz(int cihazId) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putInt("cihazId", cihazId);
        editor.commit();
    }

    public boolean rotaSecenegiGetir(){
        boolean rotaSecenegi;
        rotaSecenegi=mSharedPrefs.getBoolean("rotaSecenegi",false);
        return rotaSecenegi;
    }

    public void rotaSecenegiYaz(boolean rotaSecenegi){
        SharedPreferences.Editor editor=mSharedPrefs.edit();
        editor.putBoolean("rotaSecenegi",rotaSecenegi);
        editor.commit();
    }



    public int accuarcyGetir(){
        int accuarcy;
        accuarcy=mSharedPrefs.getInt("accuarcy",20);
        return accuarcy;
    }

    public void accuarcyYaz(int accuarcy){
        SharedPreferences.Editor editor=mSharedPrefs.edit();
        editor.putInt("accuarcy",accuarcy);
        editor.commit();
    }

    public boolean wifiCheckGetir(){
        boolean wifiCheck;
        wifiCheck=mSharedPrefs.getBoolean("wifiCheck",false);
        return wifiCheck;
    }

    public void wifiCheckYaz(boolean wifiCheck){
        SharedPreferences.Editor editor=mSharedPrefs.edit();
        editor.putBoolean("wifiCheck",wifiCheck);
        editor.commit();
    }

    public int GPSupdatePeriodmilisGetir(){
        int updatePeriodmilisGetir;
        updatePeriodmilisGetir=mSharedPrefs.getInt("updatePeriodmilis",2000);
        return updatePeriodmilisGetir;
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

    public String kullaniciAdiGetir() {
        String kullaniciAdi;
        kullaniciAdi= mSharedPrefs.getString("kullaniciAdi", "");
        return kullaniciAdi;
    }
}
