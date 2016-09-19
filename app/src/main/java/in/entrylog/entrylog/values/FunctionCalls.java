package in.entrylog.entrylog.values;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import in.entrylog.entrylog.util.FileUtil;

/**
 * Created by Admin on 25-Jul-16.
 */
public class FunctionCalls {
    public static final String PREFS_NAME = "MyPrefsFile";

    public String filepath(String value) {
        File dir = new File(android.os.Environment.getExternalStorageDirectory(), Appfolder()
                + File.separator + value);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String pathname = "" + dir;
        return pathname;
    }

    public String apkfilepath() {
        File dir = new File(android.os.Environment.getExternalStorageDirectory(), "EntrylogApk");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String pathname = "" + dir;
        return pathname;
    }

    public String checkapkfilepath(String value) {
        File dir = new File(android.os.Environment.getExternalStorageDirectory(), "EntrylogApk"
                + File.separator + value);
        String pathname = "" + dir;
        return pathname;
    }

    public String setBarCodeStatus(String code, String OrganizationID) {
        int digits = 4;
        String format = String.format("%%0%dd", digits);
        int codevalue = Integer.parseInt(code);
        String value = String.format(format, codevalue);
        String Barcodevalue = value + OrganizationID;
        return Barcodevalue;
    }

    public void deleteDataBasefile() {
        File dir = new File(android.os.Environment.getExternalStorageDirectory(), Appfolder()
                + File.separator + "Database" + File.separator + "entrylog.db");
        if (dir.exists()) {
            dir.delete();
            Log.d("debug", "DataBase File deleted");
        } else {
            Log.d("debug", "DataBase File doesn't exist");
        }
    }

    public void deleteTextfile(String textfile) {
        File dir = new File(android.os.Environment.getExternalStorageDirectory(), Appfolder()
                + File.separator + "Textfile" + File.separator + textfile);
        if (dir.exists()) {
            dir.delete();
            Log.d("debug", textfile+" File deleted");
        } else {
            Log.d("debug", textfile+" File doesn't exist");
        }
    }

    public void deleteImagefile(String imagefile) {
        File dir = new File(imagefile);
        if (dir.exists()) {
            dir.delete();
            Log.d("debug", imagefile+" File deleted");
        } else {
            Log.d("debug", imagefile+" File doesn't exist");
        }
    }

    public boolean deletefolder() {
        File dir = new File(android.os.Environment.getExternalStorageDirectory(), Appfolder());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return deleteDir(dir);
    }

    private String Appfolder() {
        String folder = "Entrylog";
        return folder;
    }

    // For to Delete the directory inside list of files and inner Directory
    public boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public void OrientationView(Activity activity) {
        int screenSize = activity.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;

            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
        }
    }

    public String Convertdate(String date) {
        String result="";
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
            final Date dateObj = sdf.parse(date);
            result = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        Log.d("debug", "Time Result: "+result);
        if (result.length() > 22) {
            String d1 = result.substring(result.length()-4, result.length());
            String d2 = result.substring(0, result.length()-4);
            String d3 = "";
            if (d1.equals("p.m.")) {
                d3 = "PM";
            } else if (d1.equals("a.m.")) {
                d3 = "AM";
            }
            result = d2 + d3;
        }
        return result;
    }

    public void checkimage_and_delete(String foldername, String MobileNo, String presentfile) {
        String folderpath = filepath(foldername);
        int mobilenolength = MobileNo.length();
        File imagefiledir = new File(folderpath);
        File[] files = imagefiledir.listFiles();
        for (int i = 0; i < files.length; i++) {
            String filename = files[i].getName();
            String filepath = files[i].getPath();
            String findimage = filename.substring(0, mobilenolength);
            if (findimage.equals(MobileNo)) {
                if (!filepath.equals(presentfile)) {
                    File file = new File(folderpath + File.separator + filename);
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
    }

    public void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void LogStatus(String message) {
        Log.d("debug", message);
    }

    public String normalisedVersion(String version) {
        return normalisedVersion(version, ".", 4);
    }

    public String normalisedVersion(String version, String sep, int maxWidth) {
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
    }

    public void setSharedPreferencesvalues(Activity activity) {
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Login", "No");
        editor.putString("Device", "");
        editor.putString("BarCode", "");
        editor.putString("Orgid", "");
        editor.putString("OrganizationID", "");
        editor.putString("OrganizationName", "");
        editor.putString("User", "");
        editor.putString("GuardID", "");
        editor.putString("CurrentDate", "");
        editor.putString("OverNightTime", "");
        editor.putString("OTPAccess", "");
        editor.putString("ImageAccess", "");
        editor.putString("Printertype", "");
        editor.putString("Scannertype", "");
        editor.putString("RFID", "");
        editor.putString("Cameratype", "");
        editor.putString("OTP", "");
        editor.putString("UpdateData", "");
        editor.commit();
    }

    public final boolean isInternetOn(Activity activity) {
        ConnectivityManager connect = (ConnectivityManager) activity.getSystemService(activity.getBaseContext().CONNECTIVITY_SERVICE);
        if (connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
                connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
                connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else if (connect.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
                connect.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

    public void ringtone(Context context){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String CurrentDate() {
        Calendar cal = Calendar.getInstance();
        int curyear = cal.get(Calendar.YEAR);
        int curmonth = cal.get(Calendar.MONTH);
        int curdate = cal.get(Calendar.DAY_OF_MONTH);
        String Currentdate = "" + curdate + "-" + "" + (curmonth + 1) + "-" + curyear;
        Date Starttime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Starttime = new SimpleDateFormat("dd-MM-yyyy").parse(Currentdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String Date = sdf.format(Starttime);
        return Date;
    }

    public String CurrentTime() {
        Calendar cal = Calendar.getInstance();
        int curhour = cal.get(Calendar.HOUR_OF_DAY);
        int curminute = cal.get(Calendar.MINUTE);
        String minute = "" + curminute;
        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        String Currenttime = "" + curhour + ":" + minute;
        Date Starttime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        try {
            Starttime = new SimpleDateFormat("HH:mm").parse(Currenttime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String Time = sdf.format(Starttime);
        return Time;
    }
}
