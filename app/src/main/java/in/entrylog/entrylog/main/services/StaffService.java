package in.entrylog.entrylog.main.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import java.util.HashSet;

import in.entrylog.entrylog.dataposting.ConnectingTask;
import in.entrylog.entrylog.dataposting.ConnectingTask.StaffFetching;
import in.entrylog.entrylog.values.DetailsValue;

/**
 * Created by Admin on 29-Jul-16.
 */
public class StaffService extends Service {
    public static final String PREFS_NAME = "MyPrefsFile";
    DetailsValue detailsValue;
    ConnectingTask task;
    Thread staffthread;
    SharedPreferences settings;
    String OrganizationID;
    public static HashSet<String> staffset;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        detailsValue = new DetailsValue();
        task = new ConnectingTask();
        staffset = new HashSet<String>();
        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("debug", "Staff Service Started");
        OrganizationID = settings.getString("OrganizationID", "");
        StaffFetching staffFetching = task.new StaffFetching(OrganizationID, detailsValue, staffset);
        staffFetching.execute();
        staffthread = null;
        Runnable runnable = new StaffData();
        staffthread = new Thread(runnable);
        staffthread.start();

        return Service.START_STICKY;
    }

    class StaffData implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Fetchstaff();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    private void Fetchstaff() {
        if (detailsValue.isStaffExists()) {
            detailsValue.setStaffExists(false);
            staffthread.interrupt();
            this.stopSelf();
        }
        if (detailsValue.isNoStaffExist()) {
            detailsValue.setNoStaffExist(false);
            staffthread.interrupt();
            this.stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        if (staffthread.isAlive()) {
            staffthread.interrupt();
            this.stopSelf();
        }
    }
}
