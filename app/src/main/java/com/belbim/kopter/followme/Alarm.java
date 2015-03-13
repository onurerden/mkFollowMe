package com.belbim.kopter.followme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;


/**
 * Created by eakbiyik on 23.12.2014.
 */
public class Alarm {
    private Context aContext;
    String baslik="";
    String mesaj="";
    String OK_ButonMetni="";
    String Cancel_ButonMetni="";
    String ayar;
    boolean sonuc;

    public Alarm(String baslik, String mesaj, String OK_ButonMetni, String Cancel_ButonMetni, String SecilecekAyar){

        this.baslik=baslik;
        this.mesaj=mesaj;
        this.OK_ButonMetni=OK_ButonMetni;
        this.Cancel_ButonMetni=Cancel_ButonMetni;
        ayar=SecilecekAyar;

    }
    public boolean showAlarm(Context mContext) { // sanırım buraya bir listener eklemek gerekiyor.
        this.aContext = mContext;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(aContext);
        alertDialog.setTitle(baslik);
        alertDialog.setMessage(mesaj);

        alertDialog.setPositiveButton(OK_ButonMetni, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ayar);
                aContext.startActivity(intent);
                sonuc=true;
            }
        });

        alertDialog.setNegativeButton(Cancel_ButonMetni, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                sonuc=false;
            }
        });
        alertDialog.show();
        return sonuc;
    }
}



