package com.pinta.ws_service.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.pinta.ws_service.Consts;
import com.pinta.ws_service.L;
import com.pinta.ws_service.WsManager;
import com.pinta.ws_service.receivers.HeartBeatTaskReceiver;

import de.tavendo.autobahn.Autobahn;
import de.tavendo.autobahn.AutobahnConnection;

public class WsService extends Service {

    private HeartBeatTaskReceiver mHeartBeatTaskReceiver = new HeartBeatTaskReceiver();
    private String mWsUri;
    private AutobahnConnection mConnection = new AutobahnConnection();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(Consts.WsConstant.WS_CONNECT)) {
            mWsUri = intent.getStringExtra(Consts.WsConstant.WS_CONNECT);
            wsConnect();
        } else if (intent.hasExtra(Consts.WsConstant.WS_RECONNECT)) {
            wsConnect();
        } else if (intent.hasExtra(Consts.WsConstant.WS_DISCONNECT)) {
            wsDisconnect();
        } else if (intent.hasExtra(Consts.WsConstant.WS_PUBLISH)) {
            String topicUri = intent.getStringExtra(Consts.WsConstant.WS_PUBLISH_TOPIC);
            String message = intent.getStringExtra(Consts.WsConstant.WS_PUBLISH);
            wsPublish(topicUri, message);
        } else if (intent.hasExtra(Consts.WsConstant.WS_SUBSCRIBE)) {
            String topicUri = intent.getStringExtra(Consts.WsConstant.WS_SUBSCRIBE);
            wsSubscribe(topicUri);
        } else if (intent.hasExtra(Consts.WsConstant.WS_CALL)) {
            String topicUri = intent.getStringExtra(Consts.WsConstant.WS_CALL);
            wsCall(topicUri);
        }
        return START_NOT_STICKY;
    }

    /**
     * Connect to websocket.
     */
    private void wsConnect() {
        mConnection.connect(mWsUri, new Autobahn.SessionHandler() {
            @Override
            public void onOpen() {
                log("open");
                sendOnOpen();
                mHeartBeatTaskReceiver.stopHeartBeatTask(WsService.this);
            }

            @Override
            public void onClose(int i, String s) {
                log(i + " == " + s);
                sendOnClose(s);
                // i=2 - websocket already connected
                if (i != 2 && WsManager.mHeartBeatPeriodInMillis != 0) {
                    mHeartBeatTaskReceiver.restartHeartBeatTask(WsService.this);
                }
            }
        });
    }

    /**
     * Disconnect from websocket.
     */
    private void wsDisconnect() {
        if (mConnection.isConnected()) {
            mConnection.disconnect();
        }
        stopSelf();
        mHeartBeatTaskReceiver.stopHeartBeatTask(WsService.this);
    }

    /**
     * Publish event to websocket.
     */
    private void wsPublish(String topicUri, Object event) {
        if (mConnection.isConnected()) {
            log(event);
            mConnection.publish(topicUri, event);
        }
    }

    /**
     * Subscribe on topic in websocket.
     */
    private void wsSubscribe(String topicUri) {
        if (mConnection.isConnected()) {
            mConnection.subscribe(topicUri,
                    Object.class,
                    new Autobahn.EventHandler() {
                        @Override
                        public void onEvent(String s, Object o) {
                            log(s + " == " + o);
                            sendOnSubscribeEvent(o.toString());
                        }
                    });
        }
    }

    /**
     * Make a call to websocket.
     */
    private void wsCall(String topicUri, Object... argument) {
        if (mConnection.isConnected()) {
            mConnection.call(topicUri,
                    Object.class,
                    new Autobahn.CallHandler() {
                        @Override
                        public void onResult(Object o) {
                            log(o);
                            sendOnCallResult(o.toString());
                        }

                        @Override
                        public void onError(String s, String s1) {
                            log(s + " == " + s1);
                            sendOnCallError(s1);
                        }
                    },
                    argument);
        }
    }

    /**
     * Send response from {@link Service} on websocket open connection.
     */
    private void sendOnOpen() {
        sendBroadcast(new Intent(Consts.BroadcastConstant.BROADCAST_ACTION_WS)
                .putExtra(Consts.WsConstant.WS_CONNECT_OPEN, "")
        );
    }

    /**
     * Send response from {@link Service} on websocket close connection.
     */
    private void sendOnClose(String s) {
        sendBroadcast(new Intent(Consts.BroadcastConstant.BROADCAST_ACTION_WS)
                .putExtra(Consts.WsConstant.WS_CONNECT_CLOSE, s)
        );
    }

    /**
     * Send response from {@link Service} on websocket subscribe event.
     */
    private void sendOnSubscribeEvent(String s) {
        sendBroadcast(new Intent(Consts.BroadcastConstant.BROADCAST_ACTION_WS)
                .putExtra(Consts.WsConstant.WS_SUBSCRIBE, s)
        );
    }

    /**
     * Send response from {@link Service} on websocket call event.
     */
    private void sendOnCallResult(String s) {
        sendBroadcast(new Intent(Consts.BroadcastConstant.BROADCAST_ACTION_WS)
                .putExtra(Consts.WsConstant.WS_CALL, s)
        );
    }

    /**
     * Send response from {@link Service} on websocket call event error.
     */
    private void sendOnCallError(String s) {
        sendBroadcast(new Intent(Consts.BroadcastConstant.BROADCAST_ACTION_WS)
                .putExtra(Consts.WsConstant.WS_CALL, s)
        );
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Log method.
     */
    private void log(Object o) {
        if (WsManager.mIsLogOn) {
            L.d(o);
        }
    }
}
