package com.pinta.ws_service;

public class Consts {

    public class WsConstant {
        public static final String WS_CONNECT = "ws_connect";
        public static final String WS_RECONNECT = "ws_reconnect";
        public static final String WS_DISCONNECT = "ws_disconnect";
        public static final String WS_PUBLISH = "ws_send";
        public static final String WS_PUBLISH_TOPIC = "ws_send_topic";
        public static final String WS_SUBSCRIBE = "ws_subscribe";
        public static final String WS_CALL = "ws_call";
        public static final String WS_CONNECT_OPEN = "ws_connect_open";
        public static final String WS_CONNECT_CLOSE = "ws_connect_close";
    }

    public class BroadcastConstant {
        public static final String BROADCAST_ACTION_WS = "broadcast_action_ws";
        public static final String BACKGROUND_SERVICE_BATTERY_CONTROL = "BACKGROUND_SERVICE_BATTERY_CONTROL";
        public static final String INTENT_ACTION = "com.pinta.ws_service.PERIODIC_TASK_HEART_BEAT";
        public static final String INTENT_ACTION_BATTERY_LOW = "android.intent.action.BATTERY_LOW";
        public static final String INTENT_ACTION_BATTERY_OKAY = "android.intent.action.BATTERY_OKAY";
    }
}
