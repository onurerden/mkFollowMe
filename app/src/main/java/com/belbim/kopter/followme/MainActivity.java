
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
import java.io.IOException;


public class MainActivity extends ActionBarActivity{

    TextView _tvGPSPozisyon;
    TextView _tvStatus;
    TextView _tvSayac;
    TextView _tvHiz;
    TextView tvRota;
    Switch  swAktivasyon;
    RadioGroup _rgParametre;
    TextView _dogruluk;
    int konumParametre=1;
    int iterasyonSayisi =0;
    int gidenVeriSayisi =0;
    int routeId=0;
    Intent intentGPSTracker;
    FileOutputStream outputStream;
    ////////////////////

    SharedPrefBilgisi sp;
    GPSTracker gps ;
    IDeviceServerImpl ids;
    Handler mHandler = new Handler();
    private static long back_pressed;
    Konus konusucu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = new SharedPrefBilgisi(MainActivity.this); //SharedPref ten okuyan ve oraya yazan Class'ın hazırlanması
        ids = new IDeviceServerImpl();
        konusucu =new Konus(MainActivity.this);

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

    protected void onStart(){
        super.onStart();
        gps =new GPSTracker(this);

        /////////////////////////GUI nin tanımlanması////////////////
        _tvGPSPozisyon = (TextView) findViewById(R.id.tvGPSPozisyon);
        _tvSayac = (TextView)findViewById(R.id.tvSayac);
        swAktivasyon = (Switch)findViewById(R.id._swAktivasyon);
        _tvStatus = (TextView)findViewById(R.id.tvStatus);
        _rgParametre=(RadioGroup)findViewById(R.id.rgRadioGroup);
        _dogruluk=(TextView)findViewById(R.id.tvdogruluk);
        _tvHiz=(TextView)findViewById(R.id.tvHiz);
        tvRota=(TextView)findViewById(R.id.tvRota);

        swAktivasyon.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { //Eğer switch on olursa
                    if (InitInfo.getInstance().isInited() && sp.cihazIdGetir() > 0) {
                        gps.baslat(sp.GPSupdatePeriodmilisGetir()); // GPS provider update leri durmuş ise tekrar başlat

                        _tvStatus.setText("BAĞLANTI HAZIR");
                        konusucu.tekTrackCal(R.raw.all_ready);


                        if (routeId==0 || sp.rotaSecenegiGetir()){routeId=ids.getRouteId(sp.cihazIdGetir());}
                        tvRota.setText("ROTA:"+routeId);
                        try{
                            outputStream = openFileOutput(routeId+".gpx", Context.MODE_APPEND);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mHandler.postDelayed(mUpdateTimeTask, 100); //isInited ve cihazID alınmış ise handler başlar
                    } else {
                        swAktivasyon.setChecked(false);
                        _tvStatus.setText("BAĞLANTI HAZIR DEĞİL");
                        konusucu.tekTrackCal(R.raw.preparing_connection);

                        Init2 init2 = new Init2();
                        Intent tetik = new Intent(MainActivity.this, init2.getClass());
                        startActivity(tetik);
                    }
                } else { //eğer switch off olursa
                    mHandler.removeCallbacks(mUpdateTimeTask); //Handler i durdurur

                    /*Veri gonderimi kapatıldığında dosyayı kapatır.*/
                    //if (outputStream!=null){
                        try {outputStream.close();}
                        catch (IOException e) {e.printStackTrace();}//}
                    /************************************************/

                    iterasyonSayisi = 0; // iterasyonları sıfırla
                    gidenVeriSayisi = 0; // iterasyonları sıfırla
                    _tvSayac.setText("0,0");
                    _tvGPSPozisyon.setText("");
                    _tvHiz.setText("---");
                    _tvStatus.clearAnimation();
                    _tvStatus.setText("GÖNDERİM DURDURULDU");
                    konusucu.tekTrackCal(R.raw.all_ready);

                    tvRota.setText("---");
                    gps.durdur(); // gps update lerini durdur
                }
            }
        });
       swAktivasyon.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { }}); //boş metod
    }

    protected  void onResume (){
        super.onResume();
        if (InitInfo.getInstance().isInited() ){swAktivasyon.setChecked(true);}
        //eger kullanıcı adı yok ise Init acalım ve kullanıcı adı oluşturalım
        if (sp.kullaniciAdiGetir().isEmpty()){
            Init init = new Init();
            Intent tetik = new Intent(MainActivity.this, init.getClass());
            startActivity(tetik);
        }

    }

    protected void onPause (){
        super.onPause();
        swAktivasyon.setChecked(false);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            if (!gps.gpsEnabledGetir()) {
//                /////////////////Butonu Kapatır////////////////
                swAktivasyon.setChecked(false);
//                //////////////////////////////////////////////

                _tvSayac.setText("GPS ETKIN DEGIL");
                _tvSayac.setTextColor(Color.parseColor("#FF0000"));
                mHandler.removeCallbacks(mUpdateTimeTask);

                Alarm gpsAyarGoster=new Alarm("GPS Ayarları", "GPS Aktif Değil Aktif etmek ister misiniz?","Ayarlar", "İptal",Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                gpsAyarGoster.showAlarm(MainActivity.this);

            } else {
                mHandler.postDelayed(this,sp.guncellemePeriyoduGetir());

                if (!gps.gpsKilitlendiMi()) {  //(gps.location.getAccuracy() > 0.01) && (gps.location.getAccuracy() < 5)
                    _tvStatus.setText("GPS ARANIYOR");
                    konusucu.tekTrackCal(R.raw.searching);

                    if (_tvStatus.getVisibility()==View.VISIBLE){_tvStatus.setVisibility(View.INVISIBLE);} else {_tvStatus.setVisibility(View.VISIBLE);}
                }
                else {
                    if (InitInfo.getInstance().isInited() && sp.cihazIdGetir() > 0){
                        if (_rgParametre.getCheckedRadioButtonId() == R.id.rbFollowMe) konumParametre = 1; else konumParametre = 0;
                        JSONProvider<FollowMe> jsp = new JSONProvider<>();
                        FollowMe fm = new FollowMe();
                        fm.setEvent(konumParametre);
                        if (gps.location != null && routeId>0 && gps.locationGetir().getAccuracy()<sp.accuarcyGetir()) {
                            fm.setLat(gps.locationGetir().getLatitude());
                            fm.setLng(gps.locationGetir().getLongitude());
                            fm.setFollowMeDeviceId(sp.cihazIdGetir());
                            fm.setRouteId(routeId);


                            String json = jsp.entityToJson(fm);
                            ids.sendFollowMeData(json);

                            /*DOSYAYA YAZAN BÖLÜM*/
                            try {
                                String yazilacakMetin=gps.locationGetir().getTime() + ":" + gps.locationGetir().getLatitude() + ":" + gps.locationGetir().getLongitude() + ":" + gps.locationGetir().getAltitude()+"\r\n";
                                outputStream.write(yazilacakMetin.getBytes());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            /********************/

                            _dogruluk.setText("Doğruluk: " + gps.location.getAccuracy());
                            gidenVeriSayisi = gidenVeriSayisi + 1;
                            _tvHiz.setText(String.format("%.1f", gps.location.getSpeed())); //((double)(gps.locationGetir().getSpeed())*(5/18))
                            _tvGPSPozisyon.setText(String.format("%.3f",gps.location.getLatitude())+" | "+String.format("%.3f", gps.location.getLongitude()));
                            _tvStatus.setVisibility(View.VISIBLE);
                            _tvStatus.setText("VERİ GÖNDERİLİYOR");
                        } else if  (routeId<0){ _tvStatus.setText("Rota Bilgisi Alınamıyor");
                        } else if (routeId==0){_tvStatus.setText("Rota Bilgisi İçin Gerekli Bilgiler Hatalı");}
                    }
                }
            }
            /////////////////Her koşulda çalışacak olan bölüm////////////////
            iterasyonSayisi = iterasyonSayisi + 1;
            _tvSayac.setText(iterasyonSayisi+","+ gidenVeriSayisi);
            ///////////////////////////////////////////////////////////////////
        }
    };

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

            if (mobileInfo.isConnected() && wifiInfo.isConnected()){
                InitInfo.getInstance().setInited(false);
                swAktivasyon.setChecked(false);
            }

            if (!gps.gpsEnabledGetir()){
                swAktivasyon.setChecked(false);
            }
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
            Intent tetik = new Intent(MainActivity.this,ParametreAyar.class);
            startActivity(tetik);
            return true;
        }
        if (id == R.id.action_swipe) {
            Intent tetik = new Intent(MainActivity.this,Swipe.class);
            startActivity(tetik);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        gps.durdur();
    }

    @Override
    public void onBackPressed()
    {
        if (back_pressed + 2000 > System.currentTimeMillis()) super.onBackPressed();
        else Toast.makeText(getBaseContext(), "Çıkmak istediğinizden emin misiniz?", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
}