package com.belbim.kopter.followme;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

/**
 * Created by asay on 23.12.2014.
 */
public abstract class HttpConnectionProvider {

    private String WebServiceUrl = "";
    private int connectionTimeout = 4500; // sn
    private HttpParams connectionParameters;
    private HttpClient client;
    private HttpPost httppost;

    private boolean connect() {

        try {
            connectionParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(connectionParameters, getConnectionTimeout());
            HttpConnectionParams.setSoTimeout(connectionParameters, getConnectionTimeout());
            client = new DefaultHttpClient(connectionParameters);

            return true;
        } catch (Exception ex) {
            return false;
        }

    }

    public String send() {
        connect();

        try {
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            httppost = new HttpPost(getWebServiceUrl());
            String responseBody = client.execute(httppost, responseHandler);
            return responseBody;

        } catch (Exception ex) {

            return null;
        }
    }
    public String send(String json) {
        connect();

        try {
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            httppost = new HttpPost(getWebServiceUrl());
            StringEntity se = new StringEntity(json);
            se.setContentType("application/json;charset=UTF-8");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
            httppost.setEntity(se);

            String responseBody = client.execute(httppost, responseHandler);
            return responseBody;

        } catch (Exception ex) {

            return null;
        }
    }


    public String getWebServiceUrl() {
        return WebServiceUrl;
    }

    public void setWebServiceUrl(String webServiceUrl) {
        WebServiceUrl = webServiceUrl;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
}
