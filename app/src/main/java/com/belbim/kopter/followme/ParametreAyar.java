package com.belbim.kopter.followme;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by eakbiyik on 21.12.2014.
 */
public class ParametreAyar extends Activity {

    EditText _etServerURL;
    EditText _etGuncellemePeriyodu;
    EditText _etCihazId;
    EditText etAccuarcy;
    EditText etGPSPeriod;
    SharedPrefBilgisi sp;
    CheckBox wifiCheckBox;
    CheckBox rotaSecenegi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parametre_ayar);
        sp=new SharedPrefBilgisi(this);

        // Değişkenleri Resource nesnelerine eşitliyotuz.
        _etServerURL = (EditText) findViewById(R.id.etServerUrl);
        _etGuncellemePeriyodu = (EditText) findViewById(R.id.etGuncellemePeriyodu);
        _etCihazId=(EditText) findViewById(R.id.etcihazId);
        etAccuarcy=(EditText) findViewById(R.id.etAccuarcy);
        wifiCheckBox= (CheckBox) findViewById(R.id.wifiCheckBox);
        rotaSecenegi= (CheckBox) findViewById(R.id.rotaSecenegi);
        etGPSPeriod=(EditText) findViewById(R.id.etGPSPeriod);

        _etServerURL.setText(sp.serverUrlGetir());
        _etGuncellemePeriyodu.setText(""+sp.guncellemePeriyoduGetir());
        _etCihazId.setText(""+sp.cihazIdGetir());
        etAccuarcy.setText(""+sp.accuarcyGetir());
        etGPSPeriod.setText(""+sp.GPSupdatePeriodmilisGetir());
        wifiCheckBox.setChecked(sp.wifiCheckGetir());
        rotaSecenegi.setChecked(sp.rotaSecenegiGetir());
    }

    public void kaydet(View view) {
        sp.serverUrlYaz(_etServerURL.getText().toString());
        if (Integer.parseInt(_etGuncellemePeriyodu.getText().toString())>0 && Integer.parseInt(_etCihazId.getText().toString())>0 && Integer.parseInt(etAccuarcy.getText().toString())>0 && Integer.parseInt(etGPSPeriod.getText().toString())>1000) {
            sp.guncellemePeriyoduYaz(Integer.parseInt(_etGuncellemePeriyodu.getText().toString()));
            sp.cihazIdYaz(Integer.parseInt(_etCihazId.getText().toString()));
            sp.accuarcyYaz((Integer.parseInt(etAccuarcy.getText().toString())));
            sp.wifiCheckYaz(wifiCheckBox.isChecked());
            sp.GPSupdatePeriodmilisYaz(Integer.parseInt(etGPSPeriod.getText().toString()));
            sp.rotaSecenegiYaz(rotaSecenegi.isChecked());
            finish();
        }
        else {
            _etCihazId.setBackgroundColor(Color.parseColor("#ff0000"));
            _etGuncellemePeriyodu.setBackgroundColor(Color.parseColor("#ff0000"));
            etAccuarcy.setBackgroundColor(Color.parseColor("#ff0000"));
        }
    }
}