package com.belbim.kopter.followme;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Swipe extends Activity {

    ImageView m_ivImage ;
    float m_lastTouchX, m_dx,m_posX,m_prevX ;
    int imageWidth;
    int maxImageX;
    double actionDownPosition = 0;
    double actionMoveDistance = 0;
    double actionInitialPosition = 0;
    double actionNewPosition=0;
    RelativeLayout mRelativeMain;
    RelativeLayout.LayoutParams iVParams;
    Konus konusucu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        m_ivImage = (ImageView) findViewById(R.id.imageView1);
        m_ivImage.setOnTouchListener(m_onTouchListener);
        mRelativeMain = (RelativeLayout) findViewById(R.id.RelativeMain);
        iVParams= (RelativeLayout.LayoutParams)m_ivImage.getLayoutParams();
        imageWidth = m_ivImage.getWidth();
        konusucu = new Konus(this);
    }

    View.OnTouchListener m_onTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            imageWidth = m_ivImage.getWidth();
            switch (event.getAction()){

                case MotionEvent.ACTION_DOWN:
                    m_lastTouchX = event.getX();
                    //imajın genişliği ve gidebileceği max mesafe tespit ediliyor.
                    maxImageX= mRelativeMain.getWidth()-imageWidth;
                    break;

                case MotionEvent.ACTION_UP:
                    MediaPlayer mMediaPlayer = new MediaPlayer();
                    mMediaPlayer.create(Swipe.this,R.raw.all_ready);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mMediaPlayer.start();

                    if (m_posX>mRelativeMain.getWidth()/2){
                        iVParams.setMargins(maxImageX,0,0,0);
                        m_ivImage.setImageResource(R.drawable.swipe_left);
                        konusucu.tekTrackCal(R.raw.searching);
                    }

                    if (m_posX<mRelativeMain.getWidth()/2 ){
                        iVParams.setMargins(0,0,0,0);
                        m_ivImage.setImageResource(R.drawable.swipe_right);
                        konusucu.tekTrackCal(R.raw.all_ready);

                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    m_dx = event.getX() - m_lastTouchX;
                    m_posX = m_prevX + m_dx;

                        if (m_posX < maxImageX) {
                            iVParams.setMargins((int) m_posX, 0, 0, 0);
                        }
                    v.setLayoutParams(iVParams);
                    m_prevX = m_posX;

                break;
            }
            return true;
        }
    };
}
