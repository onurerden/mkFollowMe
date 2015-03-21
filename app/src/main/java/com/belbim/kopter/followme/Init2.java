package com.belbim.kopter.followme;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class Init2 extends Activity {

    ImageView ivWifi;
    ImageView ivMobilAg;
    ImageView ivMobilAgBaglanti;
    ImageView ivMobileEdgeOr3g;
    ImageView ivServerKayit;
    ImageView ivServerDokunus;
    Button button;
    SharedPrefBilgisi sp;
    int touchServerState;
    int registerDeviceState;
    IDeviceServerImpl ids;
    String androidID;
    Account[] accounts;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init2);
        sp=new SharedPrefBilgisi(Init2.this);



        button = (Button) findViewById(R.id.button);
        button.setVisibility(View.INVISIBLE);
        sp.cihazIdYaz(-1);
        ids = new IDeviceServerImpl();
        androidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        ivWifi = (ImageView) findViewById(R.id.ivWifi);
        ivMobilAg = (ImageView) findViewById(R.id.ivMobilAg);
        ivMobilAgBaglanti = (ImageView) findViewById(R.id.ivMobilAgBaglanti);
        ivMobileEdgeOr3g = (ImageView) findViewById(R.id.ivMobileEdgeOr3G);
        ivServerKayit = (ImageView) findViewById(R.id.ivServerKayit);
        ivServerDokunus = (ImageView) findViewById(R.id.ivDokunus);

        IntentFilter filters = new IntentFilter();
        filters.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        super.registerReceiver(mBroadcastReceiver, filters);

    }

    protected void onResume(){
        super.onResume();
    }

    public void devamEt(View view) {
        InitInfo.getInstance().setInited(true);
        finish();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ekranKontrolleriniAyarla();
        }
    };

    public void ekranKontrolleriniAyarla(){

        ConnectivityManager cnnMgr = (ConnectivityManager)this.getSystemService((this.CONNECTIVITY_SERVICE));

        NetworkInfo wifiInfo = cnnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = cnnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        InitInfo.getInstance().setMobileDataEnabled(mobileInfo.isAvailable());
        InitInfo.getInstance().setMobileEdgeOr3G(mobileInfo.getSubtype());
        InitInfo.getInstance().setMobileConnected(mobileInfo.isConnected());
        InitInfo.getInstance().setWifiEnabled(wifiInfo.isAvailable());

        if (mobileInfo.isAvailable()){ ivMobilAg.setImageResource(R.drawable.tick); }else { ivMobilAg.setImageResource(R.drawable.cross); }
        if (mobileInfo.getSubtype()== TelephonyManager.NETWORK_TYPE_GPRS || mobileInfo.getSubtype()== TelephonyManager.NETWORK_TYPE_EDGE){ivMobileEdgeOr3g.setImageResource(R.drawable.warning);}else {ivMobileEdgeOr3g.setImageResource(R.drawable.tick);}

        if (!sp.wifiCheckGetir()){
            if(wifiInfo.isAvailable()){ivWifi.setImageResource(R.drawable.cross);}else {ivWifi.setImageResource(R.drawable.tick);}
        }else {
            if (wifiInfo.isAvailable()) {ivWifi.setImageResource(R.drawable.warning);} else {ivWifi.setImageResource(R.drawable.tick);}
        }

        if (mobileInfo.isConnected()){ivMobilAgBaglanti.setImageResource(R.drawable.tick);}else if(sp.wifiCheckGetir()){ivMobilAgBaglanti.setImageResource(R.drawable.warning);} else {ivMobilAgBaglanti.setImageResource(R.drawable.cross);}

        if (mobileInfo.isConnected() || wifiInfo.isConnected()  ){
            touchServerState = ids.touchServer(androidID, DeviceType.MOBILE_DEVICE.getCode());
            switch (touchServerState) {
                case -1:
                    ivServerDokunus.setImageResource(R.drawable.cross);
                    ivServerKayit.setImageResource(R.drawable.cross);
                    Alarm alarm1 = new Alarm("Sunucu Bağlantı", "Veri Tabanı Hatası \r\n Sunucuya Ulaşılamıyor", "", "TAMAM :(", "");
                    alarm1.showAlarm(Init2.this);
                    break;
                case -3:
                    ivServerDokunus.setImageResource(R.drawable.cross);
                    ivServerKayit.setImageResource(R.drawable.cross);
                    Alarm alarm = new Alarm("Sunucu Bağlantı", "Cihaz Kaydı Yok, \r\n Kayıt işlemi deneniyor", "", "TAMAM :)", "");
                    alarm.showAlarm(Init2.this);

                    registerDeviceState = ids.registerDevice(androidID, sp.kullaniciAdiGetir(), DeviceType.MOBILE_DEVICE.getCode());
                    if (registerDeviceState < -1) {
                        ivServerDokunus.setImageResource(R.drawable.cross);
                        ivServerKayit.setImageResource(R.drawable.cross);
                        Alarm alarm2 = new Alarm("Sunucu Kayıt", "Sunucu erişimi var ama kayıt yapılamadı!!!", "", "TAMAM :(", "");
                        alarm2.showAlarm(Init2.this);
                    } else if (registerDeviceState == -1) {
                        ivServerDokunus.setImageResource(R.drawable.cross);
                        ivServerKayit.setImageResource(R.drawable.cross);
                        Alarm alarm2 = new Alarm("Sunucu Kayıt", "Sunucu erişimi yok !!!", "", "TAMAM :(", "");
                        alarm2.showAlarm(Init2.this);
                    } else {
                        ivServerKayit.setImageResource(R.drawable.tick);
                        touchServerState = ids.touchServer(androidID, DeviceType.MOBILE_DEVICE.getCode());
                    }
                    break;
                default:
                    ivServerKayit.setImageResource(R.drawable.tick);
                    ivServerDokunus.setImageResource(R.drawable.tick);
                    sp.cihazIdYaz(touchServerState);
                    break;
            }
        }

        if (!sp.wifiCheckGetir()){
            if (mobileInfo.isConnected() && touchServerState>-1){button.setVisibility(View.VISIBLE);}
            else {
                button.setVisibility(View.INVISIBLE);
                Alarm wifiAyarAlarm = new Alarm(" Wifi Ayarı", "Lütfen Wifi Kapatınız", "Wifi Ayarları", "İptal", Settings.ACTION_WIFI_SETTINGS);
                wifiAyarAlarm.showAlarm(Init2.this);
            }
        }

        else {
            if ((mobileInfo.isConnected() || wifiInfo.isConnected())&& touchServerState>-1 ){button.setVisibility(View.VISIBLE);}
            else {
                button.setVisibility(View.INVISIBLE);
                Alarm wifiAyarAlarm = new Alarm(" Mobil Ağ Bağlantı", "Lütfen Mobil ağ bağlantınızı kontrol ediniz", "", "TAMAM","");
                wifiAyarAlarm.showAlarm(Init2.this);
            }
        }

    }
}
