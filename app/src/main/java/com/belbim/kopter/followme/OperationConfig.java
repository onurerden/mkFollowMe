package com.belbim.kopter.followme;
/**
 * Created by asay on 24.12.2014.
 */
public  class OperationConfig {
    public static String host = "mk-onurerden.rhcloud.com";

    public void SetHost(String hostUrl){
        this.host = hostUrl;
    }

    public String getHost() {
        return this.host;
    }

    public static enum JsonActionList {
       /* touchServer("/mkWS/TouchServer"),
        registerDevice("/mkWS/RegisterDevice"),
        sendStatus("/mkWS/SendStatus"),
        getTask("/mkWS/getTask"),
        sendFollowMeData("/mkWS/SendFollowMeData"),
        getKopterStatus("/mkWS/GetKopterStatus"),
        getRegisteredData("mkWS/GetRegisteredData"),
        getRouteId("mkWS/getRouteId"),
        sendLog("mkWS/SendLog"),
        endRoute("mkWS/EndRoute");
        */

        touchServer("/TouchServer"),
        registerDevice("/RegisterDevice"),
        sendStatus("/SendStatus"),
        getTask("/getTask"),
        sendFollowMeData("/SendFollowMeData"),
        getKopterStatus("/GetKopterStatus"),
        getRegisteredData("/GetRegisteredData"),
        getRouteId("/getRouteId"),
        sendLog("/SendLog"),
        endRoute("/EndRoute");

        private final String text;


        private JsonActionList(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
