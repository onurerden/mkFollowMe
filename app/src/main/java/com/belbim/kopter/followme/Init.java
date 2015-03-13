package com.belbim.kopter.followme;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Init extends Activity {

    ProgressDialog progressBar;
    int maxProgress = 5;
    int state = 0;
    RelativeLayout parametreAyarLayout;
    Button button;
    String androidID;
    AccountManager manager;
    Account[] accounts;
    SharedPrefBilgisi sp;


    ImageView ivWifi;
    ImageView ivMobilAg;
    ImageView ivMobilAgBaglanti;
    ImageView ivMobileEdgeOr3g;
    ImageView ivServerKayit;
    ImageView ivServerDokunus;
    IDeviceServerImpl ids;
    int touchServerState;
    int registerDeviceState;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        button = (Button) findViewById(R.id.button);
        button.setVisibility(View.INVISIBLE);

        ivWifi = (ImageView) findViewById(R.id.ivWifi);
        ivMobilAg = (ImageView) findViewById(R.id.ivMobilAg);
        ivMobilAgBaglanti = (ImageView) findViewById(R.id.ivMobilAgBaglanti);
        ivMobileEdgeOr3g = (ImageView) findViewById(R.id.ivMobileEdgeOr3G);
        ivServerKayit = (ImageView) findViewById(R.id.ivServerKayit);
        ivServerDokunus = (ImageView) findViewById(R.id.ivDokunus);

        progressBar = new ProgressDialog(this);
        ids = new IDeviceServerImpl();
        androidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        manager = AccountManager.get(Init.this);
        accounts = manager.getAccountsByType("com.google");
        sp=new SharedPrefBilgisi(Init.this);
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onResume() {
        super.onResume();
        button.setVisibility(View.INVISIBLE);
        startTimer();
        sp.cihazIdYaz(-1);
        parametreAyarLayout = (RelativeLayout) findViewById(R.id.parametreAyarLayout);
        parametreAyarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });
    }

    private Handler mHandler = new Handler();

    private void startTimer() {
        progressBar.setMax(maxProgress);
        progressBar.setMessage("Bağlantılar Hazırlanıyor!!!");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.show();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 100);

        /*tüm imajları yok edelim*/
        ivWifi.setImageResource(0);
        ivMobilAg.setImageResource(0);
        ivMobilAgBaglanti.setImageResource(0);
        ivMobileEdgeOr3g.setImageResource(0);
        ivServerKayit.setImageResource(0);
        ivServerDokunus.setImageResource(0);

    }

    public void devamEt(View view) {
        if (state >= maxProgress) {
            finish();
        } else {
            startTimer();
        }
    }

    private Runnable mUpdateTimeTask;

    {
        mUpdateTimeTask = new Runnable() {
            public void run() {
                state = 0;
                ConnectivityManager cnnMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiInfo = cnnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobileInfo = cnnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                InitInfo.getInstance().setMobileDataEnabled(mobileInfo.isAvailable());
                InitInfo.getInstance().setMobileEdgeOr3G(mobileInfo.getSubtype());
                InitInfo.getInstance().setMobileConnected(mobileInfo.isConnected());
                InitInfo.getInstance().setWifiEnabled(wifiInfo.isAvailable());

                ////Wifi etkin mi?
                if (InitInfo.getInstance().isWifiEnabled()) {
                    progressBar.setMessage("Wifi Kapatınız!");
                    progressBar.setProgress(0);
                    ivWifi.setImageResource(R.drawable.cross);
                    Alarm wifiAyarAlarm = new Alarm(" Wifi Ayarı", "Lütfen Wifi Kapatınız", "Wifi Ayarları", "İptal", Settings.ACTION_WIFI_SETTINGS);
                    wifiAyarAlarm.showAlarm(Init.this);

                } else {
                    ivWifi.setImageResource(R.drawable.tick);
                    progressBar.setMessage("Wifi Kapalı.");
                    progressBar.setProgress(1);
                    state += 1;

                    ////Mobil data açık mı?
                    if (!InitInfo.getInstance().isMobileDataEnabled()) {
                        progressBar.setMessage("Mobil ağ kontrol ediliyor!");
                        ivMobilAg.setImageResource(R.drawable.cross);
                        Alarm mobilDataAlarm = new Alarm(" Mobil Ağ Ayarı", "Lütfen Mobil Ağı Açınız", "Mobil Ağ Ayarları", "İptal", Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                        mobilDataAlarm.showAlarm(Init.this);
                    } else {
                        ////Mobil bağlantı var mı?
                        progressBar.setMessage("Mobil veri açık.");
                        ivMobilAg.setImageResource(R.drawable.tick);
                        progressBar.setProgress(2);
                        state += 1;

                        if (!InitInfo.getInstance().isMobileConnected()) {
                            progressBar.setMessage("Mobil veri bağlantısı yok, bağlantı gereklidir. \r\n (5 sn sonra tekrar denenecek!)");
                            ivMobilAgBaglanti.setImageResource(R.drawable.cross);
                            InitInfo.getInstance().setInited(false); //Mobil veri bağlantısı connected değilse bağlantı yok diyip 5 sn daha bekliyoruz.
                            mHandler.postDelayed(mUpdateTimeTask, 5000);
                        } else {
                            progressBar.setMessage("Mobil veri bağlı.");
                            ivMobilAgBaglanti.setImageResource(R.drawable.tick); //Ekranda Mobil veriye tick atıyoruz
                            progressBar.setProgress(3);
                            state += 1;

                            //// Kullanıcı 3G yi tercih etmiş mi?
                            if (InitInfo.getInstance().getMobileEdgeOr3G() == TelephonyManager.NETWORK_TYPE_GPRS || InitInfo.getInstance().getMobileEdgeOr3G() == TelephonyManager.NETWORK_TYPE_EDGE) {
                                progressBar.setMessage("3G kullanmanız tavsiye edilir!!!");
                                ivMobileEdgeOr3g.setImageResource(R.drawable.cross);
                            } else {
                                progressBar.setMessage("3G tercih edilmiş :)");
                                ivMobileEdgeOr3g.setImageResource(R.drawable.tick);

                            } //3g tercihi

                            progressBar.setProgress(4);
                            state += 1;
                            touchServerState = ids.touchServer(androidID, DeviceType.MOBILE_DEVICE.getCode());
                        } // !mobileconnected
                    }//!mobildataenabled

                    switch (touchServerState) {
                        case -1:
                            ivServerDokunus.setImageResource(R.drawable.cross);
                            ivServerKayit.setImageResource(R.drawable.cross);
                            Alarm alarm1 = new Alarm("Sunucu Bağlantı", "Veri Tabanı Hatası \r\n Sunucuya Ulaşılamıyor", "", "TAMAM :(", "");
                            alarm1.showAlarm(Init.this);
                            break;
                        case -3:
                            ivServerDokunus.setImageResource(R.drawable.cross);
                            ivServerKayit.setImageResource(R.drawable.cross);
                            Alarm alarm = new Alarm("Sunucu Bağlantı", "Cihaz Kaydı Yok, \r\n Kayıt işlemi deneniyor", "", "TAMAM :)", "");
                            alarm.showAlarm(Init.this);

                            registerDeviceState = ids.registerDevice(androidID, accounts[0].name.toLowerCase(), DeviceType.MOBILE_DEVICE.getCode());
                            if (registerDeviceState < -1) {
                                ivServerDokunus.setImageResource(R.drawable.cross);
                                ivServerKayit.setImageResource(R.drawable.cross);
                                Alarm alarm2 = new Alarm("Sunucu Kayıt", "Sunucu erişimi var ama kayıt yapılamadı!!!", "", "TAMAM :(", "");
                                alarm2.showAlarm(Init.this);
                            } else if (registerDeviceState == -1) {
                                ivServerDokunus.setImageResource(R.drawable.cross);
                                ivServerKayit.setImageResource(R.drawable.cross);
                                Alarm alarm2 = new Alarm("Sunucu Kayıt", "Sunucu erişimi yok !!!", "", "TAMAM :(", "");
                                alarm2.showAlarm(Init.this);
                            } else {
                                ivServerKayit.setImageResource(R.drawable.tick);
                            }
                            break;
                        default:
                            ivServerKayit.setImageResource(R.drawable.tick);
                            ivServerDokunus.setImageResource(R.drawable.tick);
                            state += 1;
                            progressBar.setProgress(5);
                            sp.cihazIdYaz(touchServerState);
                            break;
                    }
                }//wifienabled

                button.setVisibility(View.VISIBLE);
                if (state < maxProgress) button.setText("BAŞTAN BAŞLA");
                else button.setText("DEVAM");

                if (progressBar.getProgress() >= maxProgress) {

                    InitInfo.getInstance().setInited(true); //Artık herşey Inited :)
                    progressBar.dismiss();
                }
            }
        };
    }
}
