package com.belbim.kopter.followme;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Swipe extends Activity {

    Button btnLogKaydet;
    ImageView m_ivImage;
    float m_lastTouchX, m_dx, m_posX, m_prevX;
    int imageWidth;
    int maxImageX;
    RelativeLayout mRelativeMain;
    RelativeLayout.LayoutParams iVParams;
    View.OnTouchListener m_onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            imageWidth = m_ivImage.getWidth();
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    m_lastTouchX = event.getX();
                    //imajın genişliği ve gidebileceği max mesafe tespit ediliyor.
                    maxImageX = mRelativeMain.getWidth() - imageWidth;
                    break;

                case MotionEvent.ACTION_UP:
                    if (m_posX > mRelativeMain.getWidth() / 2) {
                        iVParams.setMargins(maxImageX, 0, 0, 0);
                        m_ivImage.setImageResource(R.drawable.swipe_left);
                    }
                    if (m_posX < mRelativeMain.getWidth() / 2) {
                        iVParams.setMargins(0, 0, 0, 0);
                        m_ivImage.setImageResource(R.drawable.swipe_right);
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
    EditText etLogVerisi;
    TextView tvLogSonucu;
    EditText etLogSonucu;
    Konus konusucu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        m_ivImage = (ImageView) findViewById(R.id.imageView1);
        m_ivImage.setOnTouchListener(m_onTouchListener);
        mRelativeMain = (RelativeLayout) findViewById(R.id.RelativeMain);
        iVParams = (RelativeLayout.LayoutParams) m_ivImage.getLayoutParams();
        imageWidth = m_ivImage.getWidth();
        konusucu = new Konus(getApplicationContext());
        etLogVerisi = (EditText) findViewById(R.id.etLogVerisi);
        tvLogSonucu = (TextView) findViewById(R.id.tvLogSonucu);
        etLogSonucu = (EditText) findViewById(R.id.etLogSonucu);
        btnLogKaydet = (Button) findViewById(R.id.btnLogKaydet);


    }

    public void logKaydet(View view) {
        tvLogSonucu.setText("");
        int logSayisi = LogYonet.getInstance().logKaydet(Integer.parseInt(etLogSonucu.getText().toString()), etLogVerisi.getText().toString());
        etLogVerisi.setText("");
        btnLogKaydet.setText("LOG KAYDET (" + logSayisi + ")");
    }

    public void logGonder(View v) {
        tvLogSonucu.setText("");
        int[] iLogSonucu = LogYonet.getInstance().logGonder();
        for (int i = 0; i < iLogSonucu.length; i++) {
            tvLogSonucu.setText(tvLogSonucu.getText() + "," + iLogSonucu[i]);
            btnLogKaydet.setText("LOG KAYDET (" + LogYonet.getInstance().diziLogMessage.size() + ")");
        }
    }
}
