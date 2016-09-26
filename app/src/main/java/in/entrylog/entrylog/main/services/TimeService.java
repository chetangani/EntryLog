package in.entrylog.entrylog.main.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;

import in.entrylog.entrylog.dataposting.ConnectingTask;
import in.entrylog.entrylog.dataposting.ConnectingTask.GetTime;
import in.entrylog.entrylog.values.DetailsValue;
import in.entrylog.entrylog.values.FunctionCalls;

/**
 * Created by cgani on 26-Sep-16.
 */
public class TimeService extends Service {
    public static final String PREFS_NAME = "MyPrefsFile";
    DetailsValue detailsValue;
    ConnectingTask task;
    Thread timethread, posttimethread;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    FunctionCalls functionCalls;
    boolean timetest = false, posttime = false;
    public static boolean Timeservice = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        detailsValue = new DetailsValue();
        task = new ConnectingTask();
        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = settings.edit();
        functionCalls = new FunctionCalls();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        functionCalls.LogStatus("Time Service Started");
        Timeservice = true;
        getpostTimedata();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                gettimedata();
            }
        }, 5000);
        return Service.START_STICKY;
    }

    private void gettimedata() {
        timethread = null;
        Runnable runnable = new TimeData();
        timethread = new Thread(runnable);
        timethread.start();
        timetest = true;
    }

    private void getpostTimedata() {
        posttimethread = null;
        Runnable runnable = new PostTimeData();
        posttimethread = new Thread(runnable);
        posttimethread.start();
        posttime = true;
    }

    class TimeData implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Timedetails();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    private void Timedetails() {
        if (detailsValue.isGotTime()) {
            detailsValue.setGotTime(false);
            editor.putString("ServerTime", detailsValue.getServerTime());
            editor.commit();
        }
        if (detailsValue.isNoTime()) {
            detailsValue.setNoTime(false);
            editor.putString("ServerTime", "");
            editor.commit();
        }
    }

    class PostTimeData implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    GetTime getTime = task.new GetTime(detailsValue);
                    getTime.execute();
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        functionCalls.LogStatus("Destroying Time Service");
        if (timetest) {
            if (timethread.isAlive()) {
                timethread.interrupt();
            }
        }
        if (posttime) {
            if (posttimethread.isAlive()) {
                posttimethread.interrupt();
            }
        }
        this.stopSelf();
    }
}
