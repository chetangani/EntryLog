package in.entrylog.entrylog.main.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import in.entrylog.entrylog.dataposting.ConnectingTask;
import in.entrylog.entrylog.dataposting.ConnectingTask.OrganizationPermissions;
import in.entrylog.entrylog.values.DetailsValue;

/**
 * Created by Admin on 12-Aug-16.
 */
public class PermissionService extends Service {
    public static final String PREFS_NAME = "MyPrefsFile";
    DetailsValue detailsValue;
    ConnectingTask task;
    Thread permissionthread;
    SharedPreferences settings;
    String OrganizationID;
    public static String OTPAccess, ImageAccess, Printertype, Scannertype, RfidStatus, DeviceModel, Cameratype;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        detailsValue = new DetailsValue();
        task = new ConnectingTask();
        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("debug", "Checking Permission Service Started");
        OrganizationID = settings.getString("OrganizationID", "");
        OrganizationPermissions organizationPermissions = task.new OrganizationPermissions(OrganizationID, detailsValue);
        organizationPermissions.execute();
        permissionthread = null;
        Runnable runnable = new PermissionData();
        permissionthread = new Thread(runnable);
        permissionthread.start();
        return Service.START_STICKY;
    }

    class PermissionData implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    PermissionStatus();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    private void PermissionStatus() {
        if (detailsValue.isPermissionSuccess()) {
            detailsValue.setPermissionSuccess(false);
            permissionthread.interrupt();
            OTPAccess = detailsValue.getOTPAccess();
            ImageAccess = detailsValue.getImageAccess();
            Printertype = detailsValue.getPrintertype();
            Scannertype = detailsValue.getScannertype();
            RfidStatus = detailsValue.getRfidStatus();
            DeviceModel = detailsValue.getDeviceModel();
            Cameratype = detailsValue.getCameratype();
            this.stopSelf();
        }
        if (detailsValue.isPermissionFailure()) {
            detailsValue.setPermissionFailure(false);
            permissionthread.interrupt();
            this.stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        if (permissionthread.isAlive()) {
            permissionthread.interrupt();
            this.stopSelf();
        }
    }
}
