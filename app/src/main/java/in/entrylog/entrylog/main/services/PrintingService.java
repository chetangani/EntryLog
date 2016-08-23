package in.entrylog.entrylog.main.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import java.util.HashSet;

import in.entrylog.entrylog.dataposting.ConnectingTask;
import in.entrylog.entrylog.dataposting.ConnectingTask.Printing_Fields;
import in.entrylog.entrylog.values.DetailsValue;

/**
 * Created by Admin on 09-Aug-16.
 */
public class PrintingService extends Service {
    public static final String PREFS_NAME = "MyPrefsFile";
    DetailsValue detailsValue;
    ConnectingTask task;
    Thread printingthread;
    SharedPreferences settings;
    String OrganizationID;
    public static HashSet<String> printingset, printingorder;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        detailsValue = new DetailsValue();
        task = new ConnectingTask();
        printingset = new HashSet<String>();
        printingorder = new HashSet<String >();
        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("debug", "Printing Order Service Started");
        OrganizationID = settings.getString("OrganizationID", "");
        Printing_Fields printing_fields = task.new Printing_Fields(OrganizationID, detailsValue, printingorder, printingset);
        printing_fields.execute();
        printingthread = null;
        Runnable runnable = new PrinitingData();
        printingthread = new Thread(runnable);
        printingthread.start();

        return Service.START_STICKY;
    }

    class PrinitingData implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    PrintingOrderStatus();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    private void PrintingOrderStatus() {
        if (detailsValue.isPrinterOrderSuccess()) {
            detailsValue.setPrinterOrderSuccess(false);
            printingthread.interrupt();
            this.stopSelf();
        }
        if (detailsValue.isPrinterOrderNoData()) {
            detailsValue.setPrinterOrderNoData(false);
            printingthread.interrupt();
            this.stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        if (printingthread.isAlive()) {
            printingthread.interrupt();
            this.stopSelf();
        }
    }
}
