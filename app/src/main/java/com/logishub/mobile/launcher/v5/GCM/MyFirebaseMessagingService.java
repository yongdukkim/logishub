package com.logishub.mobile.launcher.v5.GCM;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.Common.Func;
import com.logishub.mobile.launcher.v5.DATA.ConfigData;
import com.logishub.mobile.launcher.v5.DB.ConfigAdapter;
import com.logishub.mobile.launcher.v5.LoginActivity;
import com.logishub.mobile.launcher.v5.MainActivity;
import com.logishub.mobile.launcher.v5.R;
import com.logishub.mobile.launcher.v5.ShowMsgActivity;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    final static String packageName = "com.logishub.mobile.launcher.v5";
    private ConfigAdapter mConfigDb = null;
    private ConfigData mConfigData = null;

    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        Map<String, String> data = message.getData();
        String title = Func.checkStringNull(data.get(Define.LOGISHUB_PUSH_TITLE));
        String msg = Func.checkStringNull(data.get(Define.LOGISHUB_PUSH_MESSAGE));
        String link = Func.checkStringNull(data.get(Define.LOGISHUB_PUSH_LINK));
        String type = Func.checkStringNull(data.get(Define.LOGISHUB_PUSH_TYPE));

        try {
            mConfigDb = new ConfigAdapter(this);
            mConfigDb.open();

            mConfigData = mConfigDb.GetConfigData();

            if (mConfigData.getPushConfig().equals("Y")) {
                if (isRunningProcess(this, packageName)) {
                    showMessage(this, title, msg, link, type);
                    //sendNotification(title, msg, link, type);
                }
                else {
                    sendNotification(title, msg, link, type);
                }
            }
        } catch (Exception e) { }
    }

    public static boolean isRunningProcess(Context context, String packageName) {

        boolean isRunning = false;

        ActivityManager actMng = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> list = actMng.getRunningAppProcesses();

        for(RunningAppProcessInfo rap : list)
        {
            if(rap.processName.equals(packageName) && rap.importance == rap.IMPORTANCE_FOREGROUND)
            {
                isRunning = true;
                break;
            }
        }

        return isRunning;
    }

    private void sendNotification(String title, String message, String link, String type) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Intent intent = new Intent(this, SplashActivity.class);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Define.ACT_PUT_REQ_PUSH_LINK, link);
        intent.putExtra(Define.ACT_PUT_REQ_PUSH_TYPE, type);
        intent.putExtra(Define.ACT_PUT_REQ_DEVICE_TOKEN, "");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder mBuilder = new Notification.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_notification_s);
        mBuilder.setTicker(Define.APP_NAME);
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);

        Notification.BigTextStyle style = new Notification.BigTextStyle(mBuilder);
        style.setBigContentTitle(title);
        style.bigText(message);

        mBuilder.setStyle(style);

        nm.notify(0, mBuilder.build());
    }

    public void showMessage(final Context context, final String title, final String message, final  String link, final String type){
        new Runnable() {
            @Override
            public void run() {
                Map<String, Object> map= new Hashtable<String, Object>();
                Message msg = new Message();
                msg.what = 0;
                map.put(Define.LOGISHUB_PUSH_TITLE, title);
                map.put(Define.LOGISHUB_PUSH_MESSAGE, message);
                map.put(Define.LOGISHUB_PUSH_LINK, link);
                map.put(Define.LOGISHUB_PUSH_TYPE, type);
                map.put("context", context);
                msg.obj = map;
                handler.sendMessage(msg);
            }
        }.run();
    }

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Map<String, Object> map = (Hashtable<String, Object>)msg.obj;
            String title = (String)map.get(Define.LOGISHUB_PUSH_TITLE);
            String message = (String)map.get(Define.LOGISHUB_PUSH_MESSAGE);
            String link = (String)map.get(Define.LOGISHUB_PUSH_LINK);
            String type = (String)map.get(Define.LOGISHUB_PUSH_TYPE);
            Context context = (Context)map.get("context");

            Intent intent = new Intent(context, ShowMsgActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Define.ACT_PUT_REQ_PUSH_TITLE, title);
            intent.putExtra(Define.ACT_PUT_REQ_PUSH_MESSAGE, message);
            intent.putExtra(Define.ACT_PUT_REQ_PUSH_LINK, link);
            intent.putExtra(Define.ACT_PUT_REQ_PUSH_TYPE, type);
            intent.putExtra(Define.ACT_PUT_REQ_USER_LIST, MainActivity.mArrUserList);

            context.startActivity(intent);
        }
    };
}