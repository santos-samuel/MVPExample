package com.example.mvpexample.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.example.mvpexample.R;
import com.example.mvpexample.view.AppListenerActivity;

public class AppListenerService extends Service {

    private BroadcastReceiver appListener;

    public AppListenerService() {
    }

    public void notifyAppInstalled(String installedPackageName) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(getApplicationContext(), AppListenerService.class);
        notificationIntent.putExtra("mytype", "simple" + 10); //not required, but used in this example.
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 10, notificationIntent, 0);
        //Create a new notification. The construction Notification(int icon, CharSequence tickerText, long when) is deprecated.
        //If you target API level 11 or above, use Notification.Builder instead
        //With the second parameter, it would show a marquee
        Notification noti = new NotificationCompat.Builder(getApplicationContext(), "test_channel_01")
                .setSmallIcon(R.drawable.aptoide_icon)
                //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setWhen(System.currentTimeMillis())  //When the event occurred, now, since noti are stored by time.
                .setContentTitle("Marquee or Title")   //Title message top row.
                .setContentText("Message, this has only a small icon.")  //message when looking at the notification, second row
                .setContentIntent(contentIntent)  //what activity to open.
                .setAutoCancel(true)   //allow auto cancel when pressed.
                .setChannelId("test_channel_02")
                .build();  //finally build and return a Notification.

        //Show the notification
        nm.notify(10, noti);




        Intent intent = new Intent(this, AppListenerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("installedPackageName", installedPackageName);
        startActivity(intent);

        /*// Delete installed apk file
        Log.d("INSTALLED APP", installedPackageName);

        if (installedPackageName.equals("com.google.android.gms")) {
            //requestManager.deleteApkFileOnStorage();
        }

        else { // A new app has been installed
            try {
                boolean gpsAvailable = requestManager.checkIfGooglePlayServicesIsAvailable();

                if (!gpsAvailable) {

                    boolean requiresGPS = requestManager.doesThisAppRequireGooglePlayServices(installedPackageName);

                    if (requiresGPS)
                        showAskUserIfHeWantsToDownloadDialog();
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (DeviceNotSupportedException e) {
                showDeviceNotSupportedDialog();
            } catch (GooglePlayServicesIsDisabledException e) {
                showGooglePlayServicesIsDisabledDialog();
            }
        }*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startListening();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("EXIT", "ondestroy!");

        Intent broadcastIntent = new Intent(this, AppListenerRestarter.class);
        sendBroadcast(broadcastIntent);

        stopListening();

    }

    private void stopListening() {
        unregisterReceiver(appListener);
        this.appListener = null;
    }

    public void startListening() {
        this.appListener = new AppListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addDataScheme("package");
        registerReceiver(appListener, intentFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
