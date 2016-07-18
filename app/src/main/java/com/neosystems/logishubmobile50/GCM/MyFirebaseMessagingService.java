package com.neosystems.logishubmobile50.GCM;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.neosystems.logishubmobile50.MainActivity;
import com.neosystems.logishubmobile50.R;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    final static String packageName = "com.neosystems.logishubmobile50";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        Map<String, String> data = message.getData();
        String title = data.get("title");
        String msg = data.get("message");

        //if (isRunningProcess(this, packageName)) {
        //    showMessage(this, title, msg);
        //}
        //else {
            sendNotification(title, msg);
        //}
    }

    public static boolean isRunningProcess(Context context, String packageName) {

        boolean isRunning = false;

        ActivityManager actMng = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        List<RunningAppProcessInfo> list = actMng.getRunningAppProcesses();

        for(RunningAppProcessInfo rap : list)
        {
            if(rap.processName.equals(packageName))
            {
                isRunning = true;
                break;
            }
        }

        return isRunning;
    }

    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000,2000})
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    public void showMessage(final Context context, final String title, final String message){
        new Runnable() {
            @Override
            public void run() {
                Map<String, Object> map= new Hashtable<String, Object>();
                Message msg = new Message();
                msg.what = 0;
                map.put("title", title);
                map.put("message", message);
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
            String title = (String)map.get("title");
            String message = (String)map.get("message");
            Context context = (Context)map.get("context");
            Toast.makeText(context, "제목 : " + title + " / 수신 메시지 : " + message, Toast.LENGTH_LONG).show();
        }
    };
}