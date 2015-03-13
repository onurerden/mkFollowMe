package com.belbim.kopter.followme;
/**
 * Created by asay on 24.12.2014.
 */
public  class OperationConfig {
    public static String host = "mk-onurerden.rhcloud.com";

    public static enum JsonActionList {
        touchServer("/mkWS/TouchServer"),
        registerDevice("/mkWS/registerDevice"),
        sendStatus("/mkWS/SendStatus"),
        getTask("/mkWS/getTask"),
        sendFollowMeData("/mkWS/SendFollowMeData"),
        getKopterStatus("/mkWS/GetKopterStatus"),
        getRegisteredData("mkWS/GetRegisteredData"),
        getRouteId("mkWS/getRouteId");

        private final String text;


        private JsonActionList(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public void SetHost(String hostUrl){
        this.host=hostUrl;
    }
}