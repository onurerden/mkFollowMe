
package com.belbim.kopter.followme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;


public class MainActivity extends ActionBarActivity {

    private static long back_pressed;
    public FollowMeDataSentUpdateReceiver mFollowMeDataSentUpdateReceiver;
    TextView _tvGPSPozisyon;
    TextView _tvStatus;
    TextView _tvSayac;
    TextView _tvHiz;
    TextView tvRota;
    TextView tvSure;
    Button btnMinus;
    TextView tvPeriod;
    Button btnPlus;

    Switch swAktivasyon;
    RadioGroup _rgParametre;
    TextView _dogruluk;
    int konumParametre = 1;
    int iterasyonSayisi = 0;
    int gidenVeriSayisi = 0;
    int hataliVeriSayisi = 0;
    int routeId = 0;
    int gonderimDurumu = -1;

    Intent intentGPSTracker;
    Intent intentKulak;
    Intent intentBroadCast;

    FileOutputStream outputStream;
    SharedPrefBilgisi sp;
    GPSTracker gps;
    IDeviceServerImpl ids;
    Handler mHandler = new Handler();
    Handler periodGuncellemeHandler = new Handler();
    Konus konusucu;
    FollowMe fm;
    Runnable followMeGonder = new Runnable() {
        @Override
        public void run() {
            gonderimDurumu = ids.sendFollowMeData(jsp.entityToJson(fm));
            Intent followMeSend = new Intent("com.belbim.kopter.followme.intent.action.FollowMeSent");
            followMeSend.putExtra("gonderimDurumu", gonderimDurumu);
            sendBroadcast(followMeSend);
        }
    };
    JSONProvider<FollowMe> jsp;
    Kulak mKulak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = new SharedPrefBilgisi(MainActivity.this); //SharedPref ten okuyan ve oraya yazan Class'ın hazırlanması
        ids = new IDeviceServerImpl();
        konusucu = new Konus(getApplicationContext());

        intentGPSTracker = new Intent(this, GPSTracker.class);
        this.startService(intentGPSTracker);

        intentBroadCast = new Intent(this, Kulak.class);
        this.startService(intentBroadCast);

        mFollowMeDataSentUpdateReceiver = new FollowMeDataSentUpdateReceiver();
    }

    protected void onStart() {
        super.onStart();

        mKulak = new Kulak(getApplicationContext(), intentKulak);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.location.PROVIDERS_CHANGED");

        registerReceiver(mKulak, intentFilter);
        registerReceiver(mFollowMeDataSentUpdateReceiver, new IntentFilter("com.belbim.kopter.followme.intent.action.FollowMeSent"));
        gps = new GPSTracker(this);

        /////////////////////////GUI nin tanımlanması////////////////
        tvSure = (TextView) findViewById(R.id.tvSure);
        _tvGPSPozisyon = (TextView) findViewById(R.id.tvGPSPozisyon);
        _tvSayac = (TextView) findViewById(R.id.tvSayac);
        swAktivasyon = (Switch) findViewById(R.id._swAktivasyon);
        _tvStatus = (TextView) findViewById(R.id.tvStatus);
        _rgParametre = (RadioGroup) findViewById(R.id.rgRadioGroup);
        _dogruluk = (TextView) findViewById(R.id.tvdogruluk);
        _tvHiz = (TextView) findViewById(R.id.tvHiz);
        tvRota = (TextView) findViewById(R.id.tvRota);
        btnMinus = (Button) findViewById(R.id.btnMinus);
        btnPlus = (Button) findViewById(R.id.btnPlus);
        tvPeriod = (TextView) findViewById(R.id.tvPeriod);

        btnMinus.setOnTouchListener(listenerPeriodGuncelle);
        btnPlus.setOnTouchListener(listenerPeriodGuncelle);

        _rgParametre.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (_rgParametre.getCheckedRadioButtonId() == R.id.rbFollowMe)
                    konusucu.trackCal(R.raw.follow_me);
                else konusucu.trackCal(R.raw.look_at_me);
            }
        });

        swAktivasyon.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { //Eğer switch on olursa
                    if (InitInfo.getInstance().isInited() && sp.cihazIdGetir() > 0) {
                        gps.baslat(sp.GPSupdatePeriodmilisGetir()); // GPS provider update leri durmuş ise tekrar başlat
                        _tvStatus.setText("BAĞLANTI HAZIR");

                        if (routeId == 0 || sp.rotaSecenegiGetir()) {
                            routeId = ids.getRouteId(sp.cihazIdGetir());
                            switch (routeId) {
                                case 0:
                                    tvRota.setText("Async Task Hatası");
                                    LogYonet.getInstance().logKaydet(1, "Rota Alınamıyor, Değer:" + routeId);
                                    break;
                                case -1:
                                    tvRota.setText("DB Erişim Hatası");
                                    LogYonet.getInstance().logKaydet(1, "DB Erişim Hatası, Değer:" + routeId);
                                    break;
                                case -2:
                                    tvRota.setText("Cihaz ID si yanlış:" + sp.cihazIdGetir());
                                    LogYonet.getInstance().logKaydet(1, "Cihaz ID Yanlış, Değer:" + routeId);
                                    break;
                                default:
                                    tvRota.setText("ROTA:" + routeId);
                            }
                        } else {
                            tvRota.setText("ROTA:" + routeId);
                        }
                        /*try {
                            outputStream = openFileOutput(routeId + ".gpx", Context.MODE_APPEND);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                        mHandler.postDelayed(mUpdateTimeTask, sp.guncellemePeriyoduGetir()); //isInited ve cihazID alınmış ise handler başlar

                    } else {
                        _tvStatus.setText("BAĞLANTI HAZIR DEĞİL");

                        Init2 init2 = new Init2();
                        Intent tetik2 = new Intent(MainActivity.this, init2.getClass());
                        startActivity(tetik2);
                        swAktivasyon.setChecked(false);
                    }
                } else { //eğer switch off olursa
                    mHandler.removeCallbacks(mUpdateTimeTask); //Handler i durdurur

                    if (routeId > 0 && gidenVeriSayisi > 0) { //rotayı sonlandırır
                        int endRouteStatus = ids.endRoute(routeId);
                        if (endRouteStatus == 0) {
                            tvRota.setText("Rota Sonlandırıldı");
                        } else {
                            tvRota.setText("Rota Sonlandırmada Hata!");
                            LogYonet.getInstance().logKaydet(1, "Rota sonlandirmada hata, RotaID:" + routeId);
                        }
                    }
/*
                    try {                       // dosyayı sonlandırır
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
*/
                    iterasyonSayisi = 0; // iterasyonları sıfırla
                    gidenVeriSayisi = 0; // iterasyonları sıfırla
                    _tvSayac.setText("0,0");
                    _tvGPSPozisyon.setText("");
                    _tvHiz.setText("---");
                    _tvStatus.setTextColor(Color.parseColor("#000000"));
                    _tvStatus.setText("GÖNDERİM DURDURULDU");
                }
            }
        });
        swAktivasyon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        }); //boş metod
    }

    protected void onResume() {
        super.onResume();
        sp.populate();
        tvPeriod.setText("" + (double) sp.guncellemePeriyoduGetir() / 1000);

        //eger kullanıcı adı yok ise Init acalım ve kullanıcı adı oluşturalım
        if (sp.kullaniciAdiGetir().isEmpty()) {
            Init init = new Init();
            Intent tetik = new Intent(MainActivity.this, init.getClass());
            startActivity(tetik);
        }
        jsp = new JSONProvider<>();
        if (InitInfo.getInstance().isInited()) {
            swAktivasyon.setChecked(true);
        }
    }

    protected void onPause() {
        super.onPause();
        InitInfo.getInstance().setInited(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent tetik = new Intent(MainActivity.this, ParametreAyar.class);
            startActivity(tetik);
            return true;
        }
        if (id == R.id.action_swipe) {
            Intent tetik = new Intent(MainActivity.this, Swipe.class);
            startActivity(tetik);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        gps.durdur();
        unregisterReceiver(mKulak);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.stopService(intentGPSTracker);
        this.stopService(intentBroadCast);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            if (!InitInfo.getInstance().getGpsAcikMi()) {
                swAktivasyon.setChecked(false);
                _tvSayac.setText("GPS ETKIN DEGIL");
                _tvSayac.setTextColor(Color.parseColor("#FF0000"));
                mHandler.removeCallbacks(mUpdateTimeTask);

                Alarm gpsAyarGoster = new Alarm("GPS Ayarları", "GPS Aktif Değil Aktif etmek ister misiniz?", "Ayarlar", "İptal", Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                gpsAyarGoster.showAlarm(MainActivity.this);

            } else {
                //mHandler.postDelayed(this, sp.guncellemePeriyoduGetir()); //bunun yerine ilk mesajın sonucu gelince başlatıyorum.gonderim durumu na taşıyorum

                if (!InitInfo.getInstance().getGpsKilitlendiMi()) {
                    _tvStatus.setText("GPS ARANIYOR");
                    mHandler.postDelayed(this, sp.guncellemePeriyoduGetir());

                    if (_tvStatus.getVisibility() == View.VISIBLE) {
                        _tvStatus.setVisibility(View.INVISIBLE);
                    } else {
                        _tvStatus.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (InitInfo.getInstance().isInited() && sp.cihazIdGetir() > 0) {

                        if (_rgParametre.getCheckedRadioButtonId() == R.id.rbFollowMe)
                            konumParametre = 1;
                        else konumParametre = 2;
                        fm = new FollowMe();
                        fm.setEvent(konumParametre);

                        if (gps.locationGetir() != null && routeId > 0 && gps.locationGetir().getAccuracy() < sp.accuarcyGetir()) {
                            fm.setLat(gps.locationGetir().getLatitude());
                            fm.setLng(gps.locationGetir().getLongitude());
                            fm.setFollowMeDeviceId(sp.cihazIdGetir());
                            fm.setRouteId(routeId);
                            fm.setSessionId(InitInfo.getInstance().getMkSession().getSessionId());

                            FireAndForgetExecutor FFE = new FireAndForgetExecutor();
                            FFE.exec(followMeGonder);

                            /*DOSYAYA YAZAN BÖLÜM
                            try {
                                String yazilacakMetin = gps.locationGetir().getTime() + ":" + gps.locationGetir().getLatitude() + ":" + gps.locationGetir().getLongitude() + ":" + gps.locationGetir().getAltitude() + "\r\n";
                                outputStream.write(yazilacakMetin.getBytes());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            /********************/

                            _tvHiz.setText(String.format("%.1f", gps.location.getSpeed()));
                            _tvGPSPozisyon.setText(String.format("%.6f", gps.location.getLatitude()) + " | " + String.format("%.6f", gps.location.getLongitude()));
                            _tvStatus.setVisibility(View.VISIBLE);
                            mHandler.postDelayed(this, sp.guncellemePeriyoduGetir());

                        } else {
                            _tvStatus.setVisibility(View.VISIBLE);
                            _tvStatus.setText("Yeterli GPS Hassasiyeti Bekleniyor!");
                            LogYonet.getInstance().logKaydet(2, "GPS Dogrulugu dusuk:" + gps.location.getAccuracy());
                            konusucu.trackCal(R.raw.warning);
                            mHandler.postDelayed(this, sp.guncellemePeriyoduGetir());
                        }
                        _dogruluk.setText("Doğruluk: " + gps.location.getAccuracy());
                    }
                }
            }
            /////////////////Her koşulda çalışacak olan bölüm////////////////
            iterasyonSayisi++;
            _tvSayac.setText(iterasyonSayisi + "," + gidenVeriSayisi + " - " + hataliVeriSayisi);
            ///////////////////////////////////////////////////////////////////
        }
    };

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) super.onBackPressed();
        else
            Toast.makeText(getBaseContext(), "Çıkmak için tekrar dokunun!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

    public class FollowMeDataSentUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {


            switch (intent.getIntExtra("gonderimDurumu", -2)) {
                case 0:
                    _tvStatus.setText("VERİ GÖNDERİLİYOR");
                    gidenVeriSayisi++;

                    break;
                case -1:
                    _tvStatus.setTextColor(Color.parseColor("#FF0000"));
                    _tvStatus.setText("DB Erişim Hatası");
                    LogYonet.getInstance().logKaydet(1, "Mesaj goderim durumu -1, DB Erisim Hatasi");
                    konusucu.trackCal(R.raw.warning);
                    hataliVeriSayisi++;
                    break;
                case -2:
                    _tvStatus.setTextColor(Color.parseColor("#FF0000"));
                    _tvStatus.setText("Hatalı Veri!!!");
                    LogYonet.getInstance().logKaydet(1, "Mesaj gonderim durumu -2, Hatali Veri");
                    konusucu.trackCal(R.raw.warning);
                    hataliVeriSayisi++;
                    break;
                case -3:
                    _tvStatus.setTextColor(Color.parseColor("#FF0000"));
                    _tvStatus.setText("Async Task Hatası!");
                    LogYonet.getInstance().logKaydet(1, "Mesaj gonderim durumu -3, Async Task Exception");
                    konusucu.trackCal(R.raw.warning);
                    hataliVeriSayisi++;

                    break;
            }
        }
    }

    View.OnTouchListener listenerPeriodGuncelle = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    switch (v.getId()) {
                        case R.id.btnPlus:
                            periodGuncellemeHandler.post(periodArtir);
                            break;
                        case R.id.btnMinus:
                            periodGuncellemeHandler.post(periodAzalt);
                            break;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    periodGuncellemeHandler.removeCallbacks(periodArtir);
                    periodGuncellemeHandler.removeCallbacks(periodAzalt);
                    break;
            }
            return false;
        }
    };

    Runnable periodAzalt = new Runnable() {
        @Override
        public void run() {
            if (sp.guncellemePeriyoduGetir() - 100 > 400)
                sp.guncellemePeriyoduGuncelle(sp.guncellemePeriyoduGetir() - 100);
            periodGuncellemeHandler.postDelayed(periodAzalt, 100);
            tvPeriod.setText("" + (double) sp.guncellemePeriyoduGetir() / 1000);
        }
    };

    Runnable periodArtir = new Runnable() {
        @Override
        public void run() {
            sp.guncellemePeriyoduGuncelle(sp.guncellemePeriyoduGetir() + 100);
            periodGuncellemeHandler.postDelayed(periodArtir, 100);
            tvPeriod.setText("" + (double) sp.guncellemePeriyoduGetir() / 1000);
        }
    };


    ;
}