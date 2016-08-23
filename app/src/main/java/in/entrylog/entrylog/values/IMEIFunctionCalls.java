package in.entrylog.entrylog.values;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by Admin on 03-Aug-16.
 */
public class IMEIFunctionCalls {

    @TargetApi(23)
    public String getIMEI(Activity activity) {
        TelephonyManager telephonyManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED) {
                telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            }
        } else {
            telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        }
        if (!telephonyManager.getDeviceId().equals("")) {
            return telephonyManager.getDeviceId();
        } else {
            return "";
        }
    }

    public void OrientationView(Activity activity) {
        Log.d("debug", "IMEI: "+getIMEI(activity));
        if (!getIMEI(activity).equals("358187072616272")) {
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
    }
}
