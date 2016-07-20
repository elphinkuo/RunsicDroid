package com.runningmusic.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.runningmusic.utils.Constants;
import com.runningmusic.utils.Util;
import com.runningmusiclib.amap.search.AmapSearch;
import com.runningmusiclib.cppwrapper.Activity;
import com.runningmusiclib.cppwrapper.ActivityManagerWrapper;
import com.runningmusiclib.cppwrapper.ReportWrapper;

import java.util.ArrayList;

//
//import com.runningmusic.SplashScreenActivity;
//import com.runningmusic.application.WearelfApplication;
//import com.runningmusic.sns.DailyStatsUploader;

/**
 * Created by guofuming on 18/1/16.
 */
public class ReportHandler {

    private static String TAG = ReportHandler.class.getName();
    public static void handleReport() {
        if (ReportWrapper.isActivityUpdated(Util.context())) {
            updateView();
            ReportWrapper.setIsActivityUpdated(Util.context(), false);
        }

        if (ReportWrapper.isActivityWaitingForPOI(Util.context())) {
            long pointer = ReportWrapper.getPOIActivities(Util.context());
            ArrayList<Activity> activities = ActivityManagerWrapper.cactivitiesToActivities(pointer, false);
            // @JackeyLin 改动
            for (Activity activity : activities) {
                if (activity.getPlace().getName().equals("")) {
                    AmapSearch.getInstance().getPlaceFromAmap(activity);
                }
            }

            ReportWrapper.clearActivityWaitingForPOI(Util.context());
        }

        if (ReportWrapper.isDailyStatsUpdated(Util.context())) {
            updateView();
            ReportWrapper.setIsDailyStatsUpdated(Util.context(), false);
        }

        /**
         * 位置相关
         * LocationTracker   LocationTrackerService.java in eclipse java file
         */

//        if (ReportWrapper.isFineLocation(Util.context())) {
//            LocationTracker.getInstance().startFineTrack();
//        } else {
//            LocationTracker.getInstance().startCoarseTrack();
//        }

        if (ReportWrapper.stepNotificationType() != StepMessage.NOTIFICATION_NONE) {
            StepMessage message = new StepMessage();
            message.notificationType = ReportWrapper.stepNotificationType();
            message.fakeStep = ReportWrapper.fakeStep();
            message.bpm = ReportWrapper.bpm();
            sendMessage(message);
            ReportWrapper.reinitStepStatus();
        }

        if (ReportWrapper.firstRGMTime() != 0) {
            double firstRGMTime = ReportWrapper.firstRGMTime();
            SharedPreferences sPreferences = Util.getUserPreferences();
            Editor editor = sPreferences.edit();
            editor.putLong(Constants.FIRST_RGM_TIME, Double.doubleToLongBits(firstRGMTime));
            editor.commit();
            ReportWrapper.setFirstRGMTime(0);


//            if (WearelfApplication.isBackground()) {
//                showNotification("我刚刚帮您记录了一段运动，快去看看吧！");
//            }
        }

        if (ReportWrapper.runningActivityTime() != 0) {
            double runningActivityTime = ReportWrapper.runningActivityTime();
            SharedPreferences sPreferences = Util.getUserPreferences();
            Editor editor = sPreferences.edit();
            editor.putLong(Constants.FIRST_RGM_TIME, Double.doubleToLongBits(runningActivityTime));
            editor.commit();
            /**
             * 显示通知
             */
//            if (WearelfApplication.isBackground()) {
//                showNotification("我刚刚帮您记录了一段跑步，快去看看吧！");
//            }
            ReportWrapper.setRunningActivityTime(0);
        }
        if (ReportWrapper.isRGMEnd()) {
            /**
             * 每天上传RGM数据
             */
//            DailyStatsUploader.getInstance().startUploadingDaily(null);
            ReportWrapper.setIsRGMEnd(false);
        }
    }

    public static void sendMessage(StepMessage message) {
//        if (WearelfApplication.isBackground()) {
//            return;
//        }

        try {
            Intent intent = new Intent(Constants.STEPSTATE);
            intent.putExtra("stepstate", message);
            Util.context().sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateView() {
        SharedPreferences sPreferences = Util.getUserPreferences();
        boolean isWidgetEnable = sPreferences.getBoolean(Constants.WIDGET_ENABLE, false);
//        if (WearelfApplication.isBackground() && !isWidgetEnable) {
//            return;
//        }

        try {
            Intent intent = new Intent("cn.ledongli.pedometer.broadcast.stats");
            intent.putExtra("dailyStats", "");
            Util.context().sendBroadcast(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showNotification(String text) {
        try {

            /**
             * 位置记录好之后  显示通知
             */
//            int notifyIcon = R.mipmap.ic_launcher;
//            String notifyTitle = "Running Music";
//
//            Context app = Util.context();
//            NotificationManager nm = (NotificationManager) app.getSystemService(Context.NOTIFICATION_SERVICE);
//            final int id = Integer.MAX_VALUE / 13 + 1;
//            nm.cancel(id);
//
//            Intent activityIntent = new Intent(Util.context(), RunningMusicActivity.class);
//            activityIntent.setAction(Intent.ACTION_MAIN);
//            activityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//            // activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pi = PendingIntent.getActivity(app, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(Util.context()).setWhen(System.currentTimeMillis())
//                    .setContentText(text).setContentTitle(notifyTitle).setSmallIcon(R.drawable.ic_notification_info)
//                    .setLargeIcon(BitmapFactory.decodeResource(Util.context().getResources(), notifyIcon)).setAutoCancel(true).setTicker(notifyTitle)
//                    .setNumber(10).setContentIntent(pi).setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
//            /* Create notification with builder */
//            Notification notification = notificationBuilder.build();
//            nm.notify(id, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
