
package com.belbim.kopter.followme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;


public class MainActivity extends ActionBarActivity {

    private static long back_pressed;
    TextView _tvGPSPozisyon;
    TextView _tvStatus;
    TextView _tvSayac;
    TextView _tvHiz;
    TextView tvRota;
    Switch swAktivasyon;
    RadioGroup _rgParametre;
    TextView _dogruluk;
    int konumParametre = 1;
    int iterasyonSayisi = 0;
    int gidenVeriSayisi = 0;
    int routeId = 0;
    int gonderimdurumu = -1;
    Intent intentGPSTracker;
    ////////////////////
    FileOutputStream outputStream;
    SharedPrefBilgisi sp;
    GPSTracker gps;
    IDeviceServerImpl ids;
    Handler mHandler = new Handler();
    Konus konusucu;
    FollowMe fm;
    JSONProvider<FollowMe> jsp;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cnnMgr = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

            NetworkInfo wifiInfo = cnnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileInfo = cnnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            InitInfo.getInstance().setMobileDataEnabled(mobileInfo.isAvailable());
            InitInfo.getInstance().setMobileEdgeOr3G(mobileInfo.getSubtype());
            InitInfo.getInstance().setMobileConnected(mobileInfo.isConnected());
            InitInfo.getInstance().setWifiEnabled(wifiInfo.isAvailable());

            if (mobileInfo.isConnected() && wifiInfo.isConnected()) {
                InitInfo.getInstance().setInited(false);
                swAktivasyon.setChecked(false);
            }

            if (!gps.gpsEnabledGetir()) {
                swAktivasyon.setChecked(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = new SharedPrefBilgisi(MainActivity.this); //SharedPref ten okuyan ve oraya yazan Class'ın hazırlanması
        ids = new IDeviceServerImpl();
        konusucu = new Konus(getApplicationContext());

        //GPSTracker ile konum alınıyor, Bunu servis olarak başlatmak gerekiyor(muş)!!!
        intentGPSTracker = new Intent(this, GPSTracker.class);
        this.startService(intentGPSTracker);
        //Servisten bir nesne oluşturuluyor
        /* ************************************************************************** */

        /*Telefonun bağlantısında bir değişiklik olup olmadığını kontrol eden dinleyici*/
        IntentFilter filters = new IntentFilter();
        filters.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filters.addAction("android.location.PROVIDERS_CHANGED");
        super.registerReceiver(mBroadcastReceiver, filters);
    }

    protected void onStart() {
        super.onStart();
        gps = new GPSTracker(this);

        /////////////////////////GUI nin tanımlanması////////////////
        _tvGPSPozisyon = (TextView) findViewById(R.id.tvGPSPozisyon);
        _tvSayac = (TextView) findViewById(R.id.tvSayac);
        swAktivasyon = (Switch) findViewById(R.id._swAktivasyon);
        _tvStatus = (TextView) findViewById(R.id.tvStatus);
        _rgParametre = (RadioGroup) findViewById(R.id.rgRadioGroup);
        _dogruluk = (TextView) findViewById(R.id.tvdogruluk);
        _tvHiz = (TextView) findViewById(R.id.tvHiz);
        tvRota = (TextView) findViewById(R.id.tvRota);

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
                                    break;
                                case -1:
                                    tvRota.setText("DB Erişim Hatası");
                                case -2:
                                    tvRota.setText("Cihaz ID si yanlış:" + sp.cihazIdGetir());
                                default:
                                    tvRota.setText("ROTA:" + routeId);
                            }
                        }
                        /*try {
                            outputStream = openFileOutput(routeId + ".gpx", Context.MODE_APPEND);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                        mHandler.postDelayed(mUpdateTimeTask, 100); //isInited ve cihazID alınmış ise handler başlar

                    } else {
                        _tvStatus.setText("BAĞLANTI HAZIR DEĞİL");

                        Init2 init2 = new Init2();
                        Intent tetik = new Intent(MainActivity.this, init2.getClass());
                        startActivity(tetik);
                    }
                } else { //eğer switch off olursa
                    mHandler.removeCallbacks(mUpdateTimeTask); //Handler i durdurur

                    if (routeId > 0 && gidenVeriSayisi > 0) { //rotayı sonlandırır
                        int endRouteStatus = ids.endRoute(routeId);
                        if (endRouteStatus == 1) {
                            tvRota.setText("Rota Sonlandırıldı");
                        } else {
                            tvRota.setText("Rota Sonlandırmada Hata!");
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
                    gps.durdur(); // gps update lerini durdur
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
        Init2 init2 = new Init2();
        Intent tetik2 = new Intent(MainActivity.this, init2.getClass());

        if (!InitInfo.getInstance().isInited()) {
            startActivity(tetik2);
        }

        //eger kullanıcı adı yok ise Init acalım ve kullanıcı adı oluşturalım
        if (sp.kullaniciAdiGetir().isEmpty()) {
            Init init = new Init();
            Intent tetik = new Intent(MainActivity.this, init.getClass());
            startActivity(tetik);
        }
        if (sp.cihazIdGetir() == 0) {
            startActivity(tetik2);
        }

        jsp = new JSONProvider<>();

    }

    protected void onPause() {
        super.onPause();
        InitInfo.getInstance().setInited(false);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            if (!gps.gpsEnabledGetir()) {
                swAktivasyon.setChecked(false);
                _tvSayac.setText("GPS ETKIN DEGIL");
                _tvSayac.setTextColor(Color.parseColor("#FF0000"));
                mHandler.removeCallbacks(mUpdateTimeTask);

                Alarm gpsAyarGoster = new Alarm("GPS Ayarları", "GPS Aktif Değil Aktif etmek ister misiniz?", "Ayarlar", "İptal", Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                gpsAyarGoster.showAlarm(MainActivity.this);

            } else {
                mHandler.postDelayed(this, sp.guncellemePeriyoduGetir());

                if (!gps.gpsKilitlendiMi()) {
                    _tvStatus.setText("GPS ARANIYOR");

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

                        if (gps.location != null && routeId > 0 && gps.locationGetir().getAccuracy() < sp.accuarcyGetir()) {
                            fm.setLat(gps.locationGetir().getLatitude());
                            fm.setLng(gps.locationGetir().getLongitude());
                            fm.setFollowMeDeviceId(sp.cihazIdGetir());
                            fm.setRouteId(routeId);
                            fm.setSessionId(InitInfo.getInstance().getMkSession().getSessionId());
                            gonderimdurumu = ids.sendFollowMeData(jsp.entityToJson(fm));

                            switch (gonderimdurumu) {
                                case 0:
                                    _tvStatus.setTextColor(Color.parseColor("#FF00AAFF"));
                                    _tvStatus.setText("VERİ GÖNDERİLİYOR");
                                    break;
                                case -1:
                                    _tvStatus.setTextColor(Color.parseColor("#FF0000"));
                                    _tvStatus.setText("DB Erişim Hatası");
                                    konusucu.trackCal(R.raw.warning);
                                    break;
                                case -2:
                                    _tvStatus.setTextColor(Color.parseColor("#FF0000"));
                                    _tvStatus.setText("Hatalı Veri!!!");
                                    konusucu.trackCal(R.raw.warning);
                                    break;
                                case -3:
                                    _tvStatus.setTextColor(Color.parseColor("#FF0000"));
                                    _tvStatus.setText("Async Task Hatası!");
                                    konusucu.trackCal(R.raw.warning);
                                    break;
                            }

                            /*DOSYAYA YAZAN BÖLÜM
                            try {
                                String yazilacakMetin = gps.locationGetir().getTime() + ":" + gps.locationGetir().getLatitude() + ":" + gps.locationGetir().getLongitude() + ":" + gps.locationGetir().getAltitude() + "\r\n";
                                outputStream.write(yazilacakMetin.getBytes());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            /********************/

                            gidenVeriSayisi = gidenVeriSayisi + 1;
                            _tvHiz.setText(String.format("%.1f", gps.location.getSpeed()));
                            _tvGPSPozisyon.setText(String.format("%.6f", gps.location.getLatitude()) + " | " + String.format("%.6f", gps.location.getLongitude()));
                            _tvStatus.setVisibility(View.VISIBLE);

                        } else {
                            _tvStatus.setVisibility(View.VISIBLE);
                            _tvStatus.setText("Yeterli GPS Hassasiyeti Bekleniyor!");
                            konusucu.trackCal(R.raw.warning);
                        }
                        _dogruluk.setText("Doğruluk: " + gps.location.getAccuracy());
                    }
                }
            }
            /////////////////Her koşulda çalışacak olan bölüm////////////////
            iterasyonSayisi++;
            _tvSayac.setText(iterasyonSayisi + "," + gidenVeriSayisi);
            ///////////////////////////////////////////////////////////////////
        }
    };

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
        this.stopService(intentGPSTracker);
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) super.onBackPressed();
        else
            Toast.makeText(getBaseContext(), "Çıkmak istediğinizden emin misiniz?", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }


}