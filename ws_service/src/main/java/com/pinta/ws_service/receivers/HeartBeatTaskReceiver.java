package com.pinta.ws_service.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.text.TextUtils;

import com.pinta.ws_service.Consts;
import com.pinta.ws_service.WsManager;
import com.pinta.ws_service.services.WsService;

public class HeartBeatTaskReceiver extends BroadcastReceiver {

    // TODO see link https://technology.jana.com/2014/10/28/periodic-background-tasks-in-android/
    // TODO see link https://www.pubnub.com/blog/2015-09-17-tutorial-realtime-android-heart-rate-monitor-and-dashboard/

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        if (!TextUtils.isEmpty(intent.getAction())) {
            String prefsFile = context.getPackageName();
            SharedPreferences sharedPreferences = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);

            if (intent.getAction().equals(Consts.BroadcastConstant.INTENT_ACTION_BATTERY_LOW)) {
                sharedPreferences.edit().putBoolean(Consts.BroadcastConstant.BACKGROUND_SERVICE_BATTERY_CONTROL, false).apply();
                stopHeartBeatTask(context);
            } else if (intent.getAction().equals(Consts.BroadcastConstant.INTENT_ACTION_BATTERY_OKAY)) {
                sharedPreferences.edit().putBoolean(Consts.BroadcastConstant.BACKGROUND_SERVICE_BATTERY_CONTROL, true).apply();
                restartHeartBeatTask(context);
            } else if (intent.getAction().equals(Consts.BroadcastConstant.INTENT_ACTION)) {
                doHeartBeatTask(context);
            }
        }
    }

    /**
     * Method to stop periodical task.
     *
     * @param context a {@link Context} object.
     */
    public void stopHeartBeatTask(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, HeartBeatTaskReceiver.class);
        alarmIntent.setAction(Consts.BroadcastConstant.INTENT_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * Method to start periodical task with {@code mHeartBeatPeriodInMillis} interval.
     *
     * @param context a {@link Context} object.
     */
    public void restartHeartBeatTask(Context context) {
        String prefsFile = context.getPackageName();
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        boolean isBatteryOk = sharedPreferences.getBoolean(Consts.BroadcastConstant.BACKGROUND_SERVICE_BATTERY_CONTROL, true);
        Intent alarmIntent = new Intent(context, HeartBeatTaskReceiver.class);
        boolean isAlarmUp = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null;

        if (isBatteryOk && !isAlarmUp) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmIntent.setAction(Consts.BroadcastConstant.INTENT_ACTION);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), WsManager.mHeartBeatPeriodInMillis, pendingIntent);
        }
    }

    /**
     * Method to make websocket reconnection.
     *
     * @param context a {@link Context} object.
     */
    private void doHeartBeatTask(Context context) {
        context.startService(new Intent(context, WsService.class)
                .putExtra(Consts.WsConstant.WS_RECONNECT, ""));
    }
}
