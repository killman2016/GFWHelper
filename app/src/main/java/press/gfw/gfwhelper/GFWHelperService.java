package press.gfw.gfwhelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class GFWHelperService extends Service {

    GFWHelperApplication app;
    Notification notification = null;
    NotificationManager notificationManager = null;

    @Override
    public void onCreate() {
        super.onCreate();
        app = (GFWHelperApplication) getApplication();
        postCreate();
    }

    public void postCreate() {
        app.service = this;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        updateNotification();
        startForeground(0, notification);
    }

    @SuppressWarnings("deprecation")
    public void updateNotification() {
        Context context = app;
        CharSequence contentTitle = "GFW Proxy is on " + app.proxyPort;
        CharSequence contentText = "GFW Server Running " + app.serverHost + ":" + app.serverPort;
        Intent nIntent = new Intent(context, GFWHelperActivity.class);
        nIntent.setAction(Intent.ACTION_VIEW);
        nIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, nIntent, 0);
        if (notification == null) {
            int icon = R.mipmap.ic_launcher_round;
            Notification.Builder nb = new Notification.Builder(context);
            nb.setContentIntent(pIntent);
            nb.setContentText(contentText);
            nb.setContentTitle(contentTitle);
            nb.setWhen(System.currentTimeMillis());
            nb.setSmallIcon(icon);
            nb.setOngoing(true);
            nb.setAutoCancel(true);
            notification = nb.build();
        }
        notificationManager.notify(0, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.i("LocalService", "Received start id " + startId + ": " + intent);
        app.startClient();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        app.stopClient();
        app.service = null;
        //Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

}
