package com.belbim.kopter.followme;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by eakbiyik on 19.3.2015.
 */
public class Konus extends MediaPlayer implements MediaPlayer.OnCompletionListener{

    MediaPlayer mMediaPlayer;
    private int trackSayisi=0;
    private int gecerliTrack=0;
    Context mContext;
    private int [] mDosyalarDizisi;

    public Konus(Context mContext) {
        mMediaPlayer = new MediaPlayer();
        this.mContext=mContext;
        mMediaPlayer.setOnCompletionListener(this);
    }

    public void listeCal(int[] dosyalarDizisi){
        mDosyalarDizisi =dosyalarDizisi;
        trackSayisi= mDosyalarDizisi.length;
        mMediaPlayer.create(mContext,mDosyalarDizisi[0]);
        mMediaPlayer.start();
    }
    public void tekTrackCal(int dosya){
        mMediaPlayer.create(mContext,dosya);
                    mMediaPlayer.start();

    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        mMediaPlayer.release();
        if (gecerliTrack< trackSayisi){
            gecerliTrack++;
            mMediaPlayer.create(mContext, mDosyalarDizisi[gecerliTrack]);
            mMediaPlayer.start();
        }

    }
}
