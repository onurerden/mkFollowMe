package com.belbim.kopter.followme;

import android.os.AsyncTask;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;

import java.net.URI;

/**
 * Created by asay on 24.12.2014.
 */
public class AsyncTaskClass extends AsyncTask<String, String, String> {

    @Override
    protected void onPreExecute() {
        //uzun islem oncesi yapilacaklar
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            URI uri = URIUtils.createURI("http", OperationConfig.host, -1, strings[0], strings[1], null);
            HttpGet httpget = new HttpGet(uri);
            System.out.println(httpget.getURI());

            IDeviceServerImpl ids = new IDeviceServerImpl();
            ids.setWebServiceUrl(httpget.getURI().toString());
            String send = ids.send();
            send = send.replace("\n", "").replace("\r", "");

            return send;
        } catch (Exception e) {
            SendLog.getInstance().logla(1, "AsyncTask Hatası (doInBackGround):" + e.getMessage().toString());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        //uzun islem bitince yapilacaklar
    }
}
