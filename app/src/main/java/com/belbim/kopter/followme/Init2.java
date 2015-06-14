package com.belbim.kopter.followme;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class Init2 extends Activity {

    public GUIUpdateReceiver mGUIUpdateReceiver;
    Intent GUIUpdateIntent;
    Handler mHandler;

    ImageView ivWifi;
    ImageView ivMobilAg;
    ImageView ivMobilAgBaglanti;
    ImageView ivMobileEdgeOr3g;
    ImageView ivServerKayit;
    ImageView ivServerDokunus;
    View parametreAyarLayout;
    Button button;
    TextView tvLabel;
    TextView tvYuzde;
    ProgressBar pb;
    SharedPrefBilgisi sp;
    int registerDeviceState;
    IDeviceServerImpl ids;
    String androidID;
    JSONProvider<MKSession> mJSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init2);

        sp = new SharedPrefBilgisi(Init2.this);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setMax(7);

        mJSP = new JSONProvider<>();
        button = (Button) findViewById(R.id.btnLogKaydet);
        button.setVisibility(View.INVISIBLE);
        sp.cihazIdYaz(-1);
        ids = new IDeviceServerImpl();
        androidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        parametreAyarLayout = findViewById(R.id.parametreAyarLayout);
        ivWifi = (ImageView) findViewById(R.id.ivWifi);
        ivMobilAg = (ImageView) findViewById(R.id.ivMobilAg);
        ivMobilAgBaglanti = (ImageView) findViewById(R.id.ivMobilAgBaglanti);
        ivMobileEdgeOr3g = (ImageView) findViewById(R.id.ivMobileEdgeOr3G);
        ivServerKayit = (ImageView) findViewById(R.id.ivServerKayit);
        ivServerDokunus = (ImageView) findViewById(R.id.ivDokunus);
        tvLabel = (TextView) findViewById(R.id.tvLabel);
        tvLabel.setOnTouchListener(mOnTouchListener);
        tvYuzde = (TextView) findViewById(R.id.tvYuzde);

        mHandler = new Handler();
        mGUIUpdateReceiver = new GUIUpdateReceiver();
    }

    protected void onPause() {
        super.onPause();
        tvLabel.setVisibility(View.VISIBLE);
        mHandler.removeCallbacks(mRun);
        unregisterReceiver(mGUIUpdateReceiver);

    }

    View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mHandler.postDelayed(mRun, 500);
            return true;
        }
    };

    protected void onResume() {
        super.onResume();
        registerReceiver(mGUIUpdateReceiver, new IntentFilter("com.belbim.kopter.followme.intent.action.GUIUpdate"));
        tvLabel.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);
        pb.setProgress(0);
        mHandler.postDelayed(mRun, 500);
        tvYuzde.setText("%0");
    }

    public void sonlandir(View view) {
        InitInfo.getInstance().setInited(true);
        finish();
    }

    private void beklet() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class GUIUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent mIntent) {
            switch (mIntent.getStringExtra("Progress")) {
                case "01": //Mobil ag açık mı kontrolü
                    beklet();
                    if (InitInfo.getInstance().getMobileDataEnabled()) {
                        ivMobilAg.setImageResource(R.drawable.tick);
                        pb.setProgress(1);
                        tvYuzde.setText("%" + pb.getProgress() * 100 / pb.getMax());

                    } else {
                        ivMobilAg.setImageResource(R.drawable.cross);
                    }
                    GUIUpdateIntent.putExtra("Progress", "02");
                    sendBroadcast(GUIUpdateIntent);
                    break;
                case "02": //Mobil ağ Tip Kontrolü
                    beklet();
                    if (InitInfo.getInstance().getMobileEdgeOr3G() == TelephonyManager.NETWORK_TYPE_GPRS || InitInfo.getInstance().getMobileEdgeOr3G() == TelephonyManager.NETWORK_TYPE_EDGE) {
                        ivMobileEdgeOr3g.setImageResource(R.drawable.warning);
                    } else {
                        ivMobileEdgeOr3g.setImageResource(R.drawable.tick);
                    }
                    pb.setProgress(2);
                    tvYuzde.setText("%" + pb.getProgress() * 100 / pb.getMax());
                    GUIUpdateIntent.putExtra("Progress", "03");
                    sendBroadcast(GUIUpdateIntent);
                    break;
                case "03": //Wifi kontrolü
                    beklet();
                    if (!sp.wifiCheckGetir()) {
                        if (InitInfo.getInstance().getWifiAvailable()) {
                            ivWifi.setImageResource(R.drawable.cross);
                        } else {
                            ivWifi.setImageResource(R.drawable.tick);
                            pb.setProgress(3);
                            tvYuzde.setText("%" + pb.getProgress() * 100 / pb.getMax());
                            GUIUpdateIntent.putExtra("Progress", "04");
                            sendBroadcast(GUIUpdateIntent);
                        }
                    } else {
                        if (InitInfo.getInstance().getWifiAvailable()) {
                            ivWifi.setImageResource(R.drawable.warning);
                        } else {
                            ivWifi.setImageResource(R.drawable.tick);
                        }
                        pb.setProgress(3);
                        tvYuzde.setText("%" + pb.getProgress() * 100 / pb.getMax());
                        GUIUpdateIntent.putExtra("Progress", "04");
                        sendBroadcast(GUIUpdateIntent);
                    }
                    break;
                case "04": //Mobil data bağlı mı
                    beklet();
                    if (InitInfo.getInstance().getMobileConnected()) {
                        ivMobilAgBaglanti.setImageResource(R.drawable.tick);
                        pb.setProgress(4);
                        tvYuzde.setText("%" + pb.getProgress() * 100 / pb.getMax());
                        GUIUpdateIntent.putExtra("Progress", "05");
                        sendBroadcast(GUIUpdateIntent);
                    } else if (sp.wifiCheckGetir()) {
                        ivMobilAgBaglanti.setImageResource(R.drawable.warning);
                        pb.setProgress(4);
                        tvYuzde.setText("%" + pb.getProgress() * 100 / pb.getMax());
                        GUIUpdateIntent.putExtra("Progress", "05");
                        sendBroadcast(GUIUpdateIntent);
                    } else {
                        ivMobilAgBaglanti.setImageResource(R.drawable.cross);
                    }
                    break;
                case "05": //Server bağlantısı (ve registerserver)
                    if (InitInfo.getInstance().getMobileConnected() || InitInfo.getInstance().getWifiConnected()) { //İnternet Var mı?
                        String serverDokunus = ids.touchServer(androidID, DeviceType.MOBILE_DEVICE.getCode());
                        if (serverDokunus != null) { // Server a erişilebiliyor mu?
                            try {
                                InitInfo.getInstance().setMkSession(mJSP.jsonToEntity(ids.touchServer(androidID, DeviceType.MOBILE_DEVICE.getCode()), MKSession.class));
                            } // gelen String JSON a dönüşüyor ve MKSession oluyor mu?
                            catch (Exception ex) {
                                MKSession mMKSession = new MKSession();
                                mMKSession.setDeviceId(-4);  //aşağıdaki switch case e değişken gondermek için -4 diye set ettim.
                                InitInfo.getInstance().setMkSession(mMKSession);
                                LogYonet.getInstance().logKaydet(1, "Touch Serverdan gelen JSON  Sessiona dönüşmedi" + serverDokunus);
                                Log.e("TouchServer Hatasi", "Touch Serverdan gelen JSON  Sessiona dönüşmedi" + serverDokunus);
                            }

                            switch (InitInfo.getInstance().getMkSession().getDeviceId()) {
                                case -4:
                                    ivServerDokunus.setImageResource(R.drawable.cross);
                                    Alarm alarm0 = new Alarm("Sunucu Hatası", "Sunucudan gelen yanıt düzgün değil, Oturum açılamıyor.", "", "TAMAM :(", "");
                                    alarm0.showAlarm(Init2.this);
                                    break;
                                case -1:
                                    ivServerDokunus.setImageResource(R.drawable.cross);
                                    ivServerKayit.setImageResource(R.drawable.cross);
                                    Alarm alarm1 = new Alarm("Sunucu Bağlantı", "Veri Tabanı Hatası \r\n Sunucuya Ulaşılamıyor", "", "TAMAM :(", "");
                                    alarm1.showAlarm(Init2.this);
                                    break;
                                case -3:
                                    ivServerDokunus.setImageResource(R.drawable.cross);
                                    ivServerKayit.setImageResource(R.drawable.cross);

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

                                        InitInfo.getInstance().setMkSession(mJSP.jsonToEntity(ids.touchServer(androidID, DeviceType.MOBILE_DEVICE.getCode())));
                                    }
                                    break;
                                default:
                                    ivServerKayit.setImageResource(R.drawable.tick);
                                    beklet();
                                    ivServerDokunus.setImageResource(R.drawable.tick);
                                    sp.cihazIdYaz(InitInfo.getInstance().getMkSession().getDeviceId());
                                    pb.setProgress(5);
                                    tvYuzde.setText("%" + pb.getProgress() * 100 / pb.getMax());
                                    GUIUpdateIntent.putExtra("Progress", "06");
                                    sendBroadcast(GUIUpdateIntent);
                                    break;
                            } //switch bitti...
                        } else {
                            Alarm alarm3 = new Alarm("Sunucu Problemi", "Sunucu adresi yanlış \r\n ya da Sunucuya Ulaşılamıyor", "", "TAMAM :(", "");
                            alarm3.showAlarm(Init2.this);
                            MKSession mMKSession = new MKSession();
                            mMKSession.setDeviceId(-5);  //aşağıdaki buton visibilitileri ne değişken gondermek için -5 diye set ettim.
                            InitInfo.getInstance().setMkSession(mMKSession);
                        }
                    }
                    break;
                case "06":
                    beklet();
                    if (!sp.wifiCheckGetir()) {
                        if (InitInfo.getInstance().getMobileConnected() && InitInfo.getInstance().getMkSession().getDeviceId() > -1) {
                            button.setVisibility(View.VISIBLE);
                            pb.setProgress(7);
                            tvYuzde.setText("%" + pb.getProgress() * 100 / pb.getMax());
                        } else {
                            button.setVisibility(View.INVISIBLE);
                            Alarm wifiAyarAlarm = new Alarm(" Wifi Ayarı", "Lütfen Wifi Kapatınız", "Wifi Ayarları", "İptal", Settings.ACTION_WIFI_SETTINGS);
                            wifiAyarAlarm.showAlarm(Init2.this);
                            pb.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        if ((InitInfo.getInstance().getMobileConnected() || InitInfo.getInstance().getWifiConnected()) && (InitInfo.getInstance().getMkSession() != null) && (InitInfo.getInstance().getMkSession().getDeviceId() > -1)) {
                            button.setVisibility(View.VISIBLE);
                            pb.setProgress(7);
                            tvYuzde.setText("%" + pb.getProgress() * 100 / pb.getMax());
                        } else {
                            button.setVisibility(View.INVISIBLE);
                            Alarm wifiAyarAlarm = new Alarm(" HATA!!!", "Bağlantı Yok yada Sunucudan Düzgün Yanıt Alınamadı,\r\n Uygulama Geliştirici ile irtibat kurunuz.", "", "TAMAM:(", "");
                            wifiAyarAlarm.showAlarm(Init2.this);
                            tvLabel.setVisibility(View.VISIBLE);
                            pb.setVisibility(View.INVISIBLE);

                        }
                    }
                    mHandler.removeCallbacks(mRun);
                    break;
            }
        }
    }



    Runnable mRun = new Runnable() {
        @Override
        public void run() {
            GUIUpdateIntent = new Intent("com.belbim.kopter.followme.intent.action.GUIUpdate");
            GUIUpdateIntent.putExtra("Progress", "01");
            sendBroadcast(GUIUpdateIntent);
            mHandler.postDelayed(mRun, 10000);
        }
    };


}