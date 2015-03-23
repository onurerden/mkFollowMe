package com.belbim.kopter.followme;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.ArrayList;


/**
 * Created by eakbiyik on 19.3.2015.
 */
public class Konus implements MediaPlayer.OnCompletionListener {

    MediaPlayer mp;
    Context cntx;
    private ArrayList<Integer> liste;
    private int gecerliTrack = 0;
    private int trackSayisi = 0;

    public Konus(Context ctx) {
        this.cntx = ctx;
        this.liste = new ArrayList<Integer>();
    }


    public void trackCal(int dosya) {

        mp = MediaPlayer.create(cntx, dosya);
        mp.setOnCompletionListener(this);
        mp.start();


    }

    public void trackCal() {

        if (mp != null && mp.isPlaying()) {
            return;
        } else {
            mp = MediaPlayer.create(cntx, liste.get(0));
            gecerliTrack = 1;
            mp.setOnCompletionListener(this);
            mp.start();
        }

    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.reset();

        if (gecerliTrack < liste.size()) {

            trackCal(liste.get(gecerliTrack));
            gecerliTrack++;
        }


    }

    public void setListe(ArrayList<Integer> liste) {
        this.liste = liste;
    }

    public void addListe(Integer i){
        this.liste.add(i);
    }

    public void clearListe(){
        this.liste.clear();
    }
}





