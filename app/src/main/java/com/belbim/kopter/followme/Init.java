package com.belbim.kopter.followme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Init extends Activity {

    EditText etKullaniciAdi;
    SharedPrefBilgisi sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        sp = new SharedPrefBilgisi(this);
        etKullaniciAdi = (EditText) findViewById(R.id.etKullaniciAdi1);
        etKullaniciAdi.setText(sp.kullaniciAdiGetir());
    }

    public void kaydetKullanici(View view) {
        if (etKullaniciAdi.getText().length() > 5) {
            sp.kullaniciAdiYaz(etKullaniciAdi.getText().toString());
            finish();
        } else {
            etKullaniciAdi.setText("5 karakterden büyük bir Kullanıcı Adı Giriniz!");
        }
    }

}
