package com.pinta.ws_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.pinta.ws_service.services.WsService;

public class WsManager {

    public static boolean mIsLogOn;
    public static long mHeartBeatPeriodInMillis;
    private static WsManager sInstance;
    private String mPort;
    private WsCallbackListeners mWsCallbackListeners;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            if (intent.hasExtra(Consts.WsConstant.WS_CONNECT_OPEN)) {
                if (mWsCallbackListeners != null) {
                    mWsCallbackListeners.onWsOpenCallbackListener();
                }
            } else if (intent.hasExtra(Consts.WsConstant.WS_CONNECT_CLOSE)) {
                String onCloseMessage = intent.getStringExtra(Consts.WsConstant.WS_CONNECT_CLOSE);
                if (mWsCallbackListeners != null) {
                    mWsCallbackListeners.onWsCloseCallbackListener(onCloseMessage);
                }
            } else if (intent.hasExtra(Consts.WsConstant.WS_SUBSCRIBE)) {
                String onCloseMessage = intent.getStringExtra(Consts.WsConstant.WS_SUBSCRIBE);
                if (mWsCallbackListeners != null) {
                    mWsCallbackListeners.onWsSubscribeCallbackListener(onCloseMessage);
                }
            } else if (intent.hasExtra(Consts.WsConstant.WS_CALL)) {
                String onCloseMessage = intent.getStringExtra(Consts.WsConstant.WS_CALL);
                if (mWsCallbackListeners != null) {
                    mWsCallbackListeners.onWsCallCallbackListener(onCloseMessage);
                }
            }
        }
    };

    private WsManager() {
        sInstance = this;
    }

    public static WsManager getWsManager() {
        if (sInstance == null) {
            sInstance = new WsManager();
        }
        return sInstance;
    }

    /**
     * Send {@code WS_DISCONNECT} command to {@link WsService} to disconnect websocket.
     *
     * @param context a {@link Context} object.
     */
    public void disconnect(Context context) {
        context.startService(new Intent(context, WsService.class)
                .putExtra(Consts.WsConstant.WS_DISCONNECT, ""));
        unregisterCallback(context);
    }

    /**
     * Send {@code WS_SUBSCRIBE} command to {@link WsService} to subscribe to topic in websocket.
     *
     * @param context   a {@link Context} object.
     * @param topicName name of topic
     */
    public void subscribe(Context context, String topicName) {
        context.startService(new Intent(context, WsService.class)
                .putExtra(Consts.WsConstant.WS_SUBSCRIBE, topicName));
    }

    /**
     * Send {@code WS_CALL} command to {@link WsService} to make a call to topic in websocket.
     *
     * @param context   a {@link Context} object.
     * @param topicName name of topic
     */
    public void call(Context context, String topicName) {
        context.startService(new Intent(context, WsService.class)
                .putExtra(Consts.WsConstant.WS_CALL, topicName));
    }

    /**
     * Send {@code WS_PUBLISH} command to {@link WsService} to publish {@code WS_PUBLISH} message
     * to {@code WS_PUBLISH_TOPIC} topic {@code WS_PUBLISH_TOPIC} in websocket.
     *
     * @param context   a {@link Context} object.
     * @param topicName name of topic
     * @param msg       message need to be send
     */
    public void publish(Context context, String topicName, String msg) {
        context.startService(new Intent(context, WsService.class)
                .putExtra(Consts.WsConstant.WS_PUBLISH, msg)
                .putExtra(Consts.WsConstant.WS_PUBLISH_TOPIC, topicName));
    }

    /**
     * Provide host:mPort to connect websocket to.
     *
     * @param port websocket host:mPort (e.g. ws://localhost:9000)
     * @return WsManager
     */
    public WsManager setPort(@NonNull String port) {
        mPort = port;
        return this;
    }

    /**
     * Indicate {@code true\false} to show\not show log messages, default {@code false}.
     *
     * @param isLogOn true - show log, false - do not show
     * @return {@link WsService}
     */
    public WsManager setLog(boolean isLogOn) {
        WsManager.mIsLogOn = isLogOn;
        return this;
    }

    /**
     * Provide interval period for task that reconnect websocket on close connection, default 0.
     *
     * @param heartBeatPeriodInMillis interval in milliseconds, 0 means that reconnection task is off
     * @return WsManager
     */
    public WsManager setHeartBeat(long heartBeatPeriodInMillis) {
        WsManager.mHeartBeatPeriodInMillis = heartBeatPeriodInMillis;
        return this;
    }

    /**
     * Register {@link BroadcastReceiver} to receive callback from websocket in {@link WsService}.
     *
     * @param context             a {@link Context} object.
     * @param wsCallbackListeners callback interface
     */
    public void registerCallback(Context context, WsCallbackListeners wsCallbackListeners) {
        mWsCallbackListeners = wsCallbackListeners;
        context.registerReceiver(mBroadcastReceiver, new IntentFilter(Consts.BroadcastConstant.BROADCAST_ACTION_WS));
    }

    /**
     * Unregister {@link BroadcastReceiver} in activity\fragment.
     *
     * @param context a {@link Context} object.
     */
    public void unregisterCallback(Context context) {
        context.unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * Connect websocket to {@code mPort}.
     *
     * @param context a {@link Context} object.
     */
    public void connect(Context context) {
        if (!TextUtils.isEmpty(mPort)) {
            context.startService(new Intent(context, WsService.class)
                    .putExtra(Consts.WsConstant.WS_CONNECT, mPort));
        } else {
            throw new IllegalArgumentException("You must enter ws mPort to connect to.");
        }
    }

    /**
     * A callback used for sending responses from websocket to activity\fragment.
     */
    public interface WsCallbackListeners {
        void onWsOpenCallbackListener();

        void onWsCloseCallbackListener(String onCloseMessage);

        void onWsSubscribeCallbackListener(String onSubscribeMessage);

        void onWsCallCallbackListener(String onCallMessage);
    }
}
