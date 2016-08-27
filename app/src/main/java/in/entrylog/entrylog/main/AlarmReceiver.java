package in.entrylog.entrylog.main;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import in.entrylog.entrylog.R;

/**
 * Created by Admin on 08-Jul-16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final String PREFS_NAME = "MyPrefsFile";
    SharedPreferences settings;

    @Override
    public void onReceive(Context context, Intent intent) {
        /*createdialog(context);*/
        /*settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        Intent in = new Intent(context, Overnightstay_Visitors.class);
        in.putExtra("VIEW", "OverNightStay");
        PendingIntent pi = PendingIntent.getActivity(context, 0, in, 0);
        //build notification
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_entrylog_icon)
                        .setContentTitle("EntryLog")
                        .setContentText("Check the Visitors still staying in Organization")
                        .setDefaults(Notification.DEFAULT_ALL) // must requires VIBRATE permission
                        .setPriority(NotificationCompat.PRIORITY_HIGH) //must give priority to High, Max which will considered as heads-up notification
                        .setContentIntent(pi)
                        .setAutoCancel(true);

        // Gets an instance of the NotificationManager service
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //to post your notification to the notification bar with a id. If a notification with same id already exists, it will get replaced with updated information.
        notificationManager.notify(0, builder.build());*/
        Intent i=new Intent(context.getApplicationContext(),Overnightstay_Visitors.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
