package com.belbim.kopter.followme;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by asay on 23.12.2014.
 */
public class IDeviceServerImpl extends HttpConnectionProvider implements IDeviceServer {

    @Override
    public int getRouteId(int deviceId) {
        try{
            List<NameValuePair> qparams= new ArrayList<NameValuePair>();
            qparams.add((new BasicNameValuePair("deviceId", ""+deviceId)));

            String queryParameters = URLEncodedUtils.format(qparams,"UTF-8");

            AsyncTask<String,String,String> execute = new AsyncTaskClass().execute(OperationConfig.JsonActionList.getRouteId.toString(),queryParameters);

            return Integer.valueOf(execute.get());
        } catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int sendLog(String logJson) {
        return 0;
    }

    @Override
    public int endRoute(int routeId) {
        return 0;
    }

    @Override
    public String touchServer(String uid, String deviceType) {

        try {
            List<NameValuePair> qparams = new ArrayList<NameValuePair>();
            qparams.add(new BasicNameValuePair("uid", uid));
            qparams.add(new BasicNameValuePair("deviceType", deviceType));

            String queryParamters = URLEncodedUtils.format(qparams, "UTF-8");

            AsyncTask<String, String, String> execute = new AsyncTaskClass().execute(OperationConfig.JsonActionList.touchServer.toString(), queryParamters);
            return execute.get();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public int registerDevice(String uid, String name, String deviceType) {

        try {
            List<NameValuePair> qparams = new ArrayList<NameValuePair>();
            qparams.add(new BasicNameValuePair("uid", uid));
            qparams.add(new BasicNameValuePair("name", name));
            qparams.add(new BasicNameValuePair("deviceType", deviceType));

            String queryParamters = URLEncodedUtils.format(qparams, "UTF-8");

            AsyncTask<String, String, String> execute = new AsyncTaskClass().execute(OperationConfig.JsonActionList.registerDevice.toString(), queryParamters);

            return Integer.valueOf(execute.get());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int sendStatus(String jsonStatus) {

        try {
            List<NameValuePair> qparams = new ArrayList<NameValuePair>();
            qparams.add(new BasicNameValuePair("jsonstatus", jsonStatus));

            String queryParamters = URLEncodedUtils.format(qparams, "UTF-8");

            AsyncTask<String, String, String> execute = new AsyncTaskClass().execute(OperationConfig.JsonActionList.sendStatus.toString(), queryParamters);

            return Integer.valueOf(execute.get());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

    @Override
    public String getKopterStatus(int deviceId) {
        return null;
    }

    @Override
    public String getTask(int deviceID) {
        try {
            List<NameValuePair> qparams = new ArrayList<NameValuePair>();
            qparams.add(new BasicNameValuePair("deviceId", String.valueOf(deviceID)));

            String queryParamters = URLEncodedUtils.format(qparams, "UTF-8");

            AsyncTask<String, String, String> execute = new AsyncTaskClass().execute(OperationConfig.JsonActionList.getTask.toString(), queryParamters);

            return execute.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int setTask(int kopterId, int followMeDeviceId) {
        return 0;
    }

    @Override
    public int sendFollowMeData(String jsonfollowme) {
        try {
            List<NameValuePair> qparams = new ArrayList<NameValuePair>();
            qparams.add(new BasicNameValuePair("jsonfollowme", jsonfollowme));

            String queryParamters = URLEncodedUtils.format(qparams, "UTF-8");

            AsyncTask<String, String, String> execute = new AsyncTaskClass().execute(OperationConfig.JsonActionList.sendFollowMeData.toString(), queryParamters);

            String ss = execute.get();

            return Integer.valueOf(ss);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public String getFollowMeData(int deviceId) {
        return null;
    }

    public Collection<Kopter> getRegisteredData(DeviceType type) {
        try {
            AsyncTask<String, String, String> execute = new AsyncTaskClass().execute(OperationConfig.JsonActionList.getRegisteredData.toString());
            Collection<Kopter> kopters = new JSONProvider<Kopter>().fromJSonToEntityList(execute.get());
            return kopters;
        } catch (Exception ex) {
            return null;
        }


    }
}
