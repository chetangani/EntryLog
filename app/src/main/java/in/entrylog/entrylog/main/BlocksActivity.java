package in.entrylog.entrylog.main;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import in.entrylog.entrylog.R;
import in.entrylog.entrylog.dataposting.ConnectingTask;
import in.entrylog.entrylog.dataposting.ConnectingTask.LogoutUser;
import in.entrylog.entrylog.dataposting.ConnectingTask.CheckUpdatedApk;
import in.entrylog.entrylog.main.bluetooth.AddVisitor_Bluetooth;
import in.entrylog.entrylog.main.el101_102.AddVisitors_EL101;
import in.entrylog.entrylog.main.el201.AddVisitors_EL201;
import in.entrylog.entrylog.main.el201.CheckoutVisitors_EL201;
import in.entrylog.entrylog.main.services.FieldsService;
import in.entrylog.entrylog.main.services.PrintingService;
import in.entrylog.entrylog.main.services.StaffService;
import in.entrylog.entrylog.serialprinter.SerialPrinter;
import in.entrylog.entrylog.values.DetailsValue;
import in.entrylog.entrylog.values.FunctionCalls;
import in.entrylog.entrylog.values.IMEIFunctionCalls;

public class BlocksActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final int DEVICE_DLG = 1;
    private static final int ABOUTUS_DLG = 2;
    private static final int UPDATES_DLG = 3;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    Button AddVisitors_btn, Checkout_btn, Visitors_btn, ManualCheckout_btn;
    String OrganizationID, OrganizationName, GuardID, User, UpdateApkURL="", Apkfile="";
    SerialPrinter printer;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    Context context;
    ConnectingTask task;
    Thread logoutthread, updatethread;
    DetailsValue detailsValue;
    private boolean manualcheckoutbtn = false, addvisitorsbtn = false, visitorsbtn = false, checkoutbtn = false,
            updatefound = false, appdownloaded = false;
    static ProgressDialog dialog = null;
    FunctionCalls functionCalls;
    IMEIFunctionCalls imeiFunctionCalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_entrylog_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_blocks);

        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = settings.edit();

        OrganizationID = settings.getString("OrganizationID", "");
        GuardID = settings.getString("GuardID", "");
        OrganizationName = settings.getString("OrganizationName", "");
        User = settings.getString("User", "");

        task = new ConnectingTask();
        printer = new SerialPrinter();
        detailsValue = new DetailsValue();
        functionCalls = new FunctionCalls();
        imeiFunctionCalls = new IMEIFunctionCalls();

        AddVisitors_btn = (Button) findViewById(R.id.addvisitors_btn);
        Visitors_btn = (Button) findViewById(R.id.visitors_btn);
        Checkout_btn = (Button) findViewById(R.id.checkout_btn);
        ManualCheckout_btn = (Button) findViewById(R.id.manually_checkout_btn);

        Intent service = new Intent(BlocksActivity.this, FieldsService.class);
        startService(service);
        Intent service1 = new Intent(BlocksActivity.this, PrintingService.class);
        startService(service1);
        Intent service2 = new Intent(BlocksActivity.this, StaffService.class);
        startService(service2);

        /*imeiFunctionCalls.OrientationView(BlocksActivity.this);
        Log.d("debug", "MANUFACTURER : "+Build.MANUFACTURER
                +"\nMODEL : "+Build.MODEL
                +"\nPRODUCT : "+Build.PRODUCT);*/

        AddVisitors_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (functionCalls.isInternetOn(BlocksActivity.this)) {
                    if (settings.getString("Printertype", "").equals("")) {
                        addvisitorsbtn = true;
                        showdialog(DEVICE_DLG);
                    } else if (settings.getString("Printertype", "").equals("Bluetooth")) {
                        Toast.makeText(BlocksActivity.this, "Bluetooth with "+settings.getString("Device", ""),
                                Toast.LENGTH_SHORT).show();
                        addVisitors(AddVisitor_Bluetooth.class);
                    } else if (!settings.getString("Printertype", "").equals("Bluetooth")) {
                        if (settings.getString("Device", "").equals("EL101")) {
                            Toast.makeText(BlocksActivity.this, "EL101", Toast.LENGTH_SHORT).show();
                            addVisitors(AddVisitors_EL101.class);
                        } else if (settings.getString("Device", "").equals("EL201")) {
                            Toast.makeText(BlocksActivity.this, "EL201", Toast.LENGTH_SHORT).show();
                            addVisitors(AddVisitors_EL201.class);
                        }/* else {
                            addvisitorsbtn = true;
                            showdialog(DEVICE_DLG);
                        }*/
                    }
                } else {
                    Toast.makeText(BlocksActivity.this, "Please Turn On Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Checkout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (functionCalls.isInternetOn(BlocksActivity.this)) {
                    if (settings.getString("Printertype", "").equals("")) {
                        checkoutbtn = true;
                        showdialog(DEVICE_DLG);
                    } else if (settings.getString("Printertype", "").equals("Bluetooth")) {
                        if (settings.getString("Device", "").equals("EL201")) {
                            checkoutVisitors(CheckoutVisitors_EL201.class);
                        } else {
                            checkoutVisitors(CheckoutVisitors.class);
                        }
                    } else if (!settings.getString("Printertype", "").equals("Bluetooth")) {
                        if (settings.getString("Device", "").equals("EL201")) {
                            checkoutVisitors(CheckoutVisitors_EL201.class);
                        } else {
                            checkoutVisitors(CheckoutVisitors.class);
                        }
                    }
                } else {
                    Toast.makeText(BlocksActivity.this, "Please Turn On Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Visitors_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (functionCalls.isInternetOn(BlocksActivity.this)) {
                    if (settings.getString("Printertype", "").equals("")) {
                        visitorsbtn = true;
                        showdialog(DEVICE_DLG);
                    } else {
                        visitors(Visitors.class, "Visitors");
                    }
                } else {
                    Toast.makeText(BlocksActivity.this, "Please Turn On Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ManualCheckout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (functionCalls.isInternetOn(BlocksActivity.this)) {
                    if (settings.getString("Printertype", "").equals("")) {
                        manualcheckoutbtn = true;
                        showdialog(DEVICE_DLG);
                    } else {
                        visitors(Visitors.class, "Manually Checkout");
                    }
                } else {
                    Toast.makeText(BlocksActivity.this, "Please Turn On Internet", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        MenuItem bedMenuItem = menu.findItem(R.id.menu_user_name);
        bedMenuItem.setTitle(User);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_aboutus:
                showdialog(ABOUTUS_DLG);
                break;

            case R.id.menu_updates:
                if (functionCalls.isInternetOn(BlocksActivity.this)) {
                    CheckUpdatedApk checkUpdatedApk = task.new CheckUpdatedApk(detailsValue);
                    checkUpdatedApk.execute();
                    dialog = ProgressDialog.show(BlocksActivity.this, "", "Checking for App Updates..", true);
                    updatethread = null;
                    Runnable updaterunnable = new Updatetimer();
                    updatethread = new Thread(updaterunnable);
                    updatethread.start();
                } else {
                    Toast.makeText(BlocksActivity.this, "Please Turn On Internet", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.menu_settings:
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                break;

            case R.id.menu_printer:
                showdialog(DEVICE_DLG);
                break;

            case R.id.menu_logout:
                if (functionCalls.isInternetOn(BlocksActivity.this)) {
                    LogoutUser logoutUser = task.new LogoutUser(GuardID, BlocksActivity.this, detailsValue);
                    logoutUser.execute();
                    dialog = ProgressDialog.show(BlocksActivity.this, "", "Logging Out please wait..", true);
                    logoutthread = null;
                    Runnable runnable = new Logouttimer();
                    logoutthread = new Thread(runnable);
                    logoutthread.start();
                } else {
                    Toast.makeText(BlocksActivity.this, "Please Turn On Internet", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showdialog(int id) {
        switch (id) {
            case DEVICE_DLG:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select Device");
                LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.deviceview, null);
                builder.setView(ll);
                final RadioButton bluetooth_radio = (RadioButton) ll.findViewById(R.id.bluetooth_radio);
                final RadioButton el101_radio = (RadioButton) ll.findViewById(R.id.el101_102_radio);
                final RadioButton el201 = (RadioButton) ll.findViewById(R.id.el201_radio);
                if (settings.getString("Device", "").equals("Bluetooth")) {
                    bluetooth_radio.setChecked(true);
                } else if (settings.getString("Device", "").equals("EL101")) {
                    el101_radio.setChecked(true);
                } else if (settings.getString("Device", "").equals("EL201")) {
                    el201.setChecked(true);
                }
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (bluetooth_radio.isChecked() == true) {
                            /*editor.putString("Device", "Bluetooth");
                            editor.commit();*/
                            if (addvisitorsbtn) {
                                addvisitorsbtn = false;
                                addVisitors(AddVisitor_Bluetooth.class);
                            } else if (manualcheckoutbtn) {
                                manualcheckoutbtn = false;
                                visitors(Visitors.class, "Manually Checkout");
                            } else if (visitorsbtn){
                                visitorsbtn = false;
                                visitors(Visitors.class, "Visitors");
                            } else if (checkoutbtn) {
                                checkoutbtn = false;
                                checkoutVisitors(CheckoutVisitors.class);
                            } else {
                                Toast.makeText(BlocksActivity.this, "Bluetooth", Toast.LENGTH_SHORT).show();
                            }
                        } else if (el101_radio.isChecked() == true) {
                            /*editor.putString("Device", "EL101");
                            editor.commit();*/
                            if (addvisitorsbtn) {
                                addvisitorsbtn = false;
                                addVisitors(AddVisitors_EL101.class);
                            } else if (manualcheckoutbtn) {
                                manualcheckoutbtn = false;
                                visitors(Visitors.class, "Manually Checkout");
                            } else if (visitorsbtn){
                                visitorsbtn = false;
                                visitors(Visitors.class, "Visitors");
                            } else if (checkoutbtn) {
                                checkoutbtn = false;
                                checkoutVisitors(CheckoutVisitors.class);
                            } else {
                                Toast.makeText(BlocksActivity.this, "EL101/102", Toast.LENGTH_SHORT).show();
                            }
                        } else if (el201.isChecked() == true) {
                            /*editor.putString("Device", "EL201");
                            editor.commit();*/
                            if (addvisitorsbtn) {
                                addvisitorsbtn = false;
                                addVisitors(AddVisitors_EL201.class);
                            } else if (manualcheckoutbtn) {
                                manualcheckoutbtn = false;
                                visitors(Visitors.class, "Manually Checkout");
                            } else if (visitorsbtn){
                                visitorsbtn = false;
                                visitors(Visitors.class, "Visitors");
                            } else if (checkoutbtn) {
                                checkoutbtn = false;
                                checkoutVisitors(CheckoutVisitors_EL201.class);
                            } else {
                                Toast.makeText(BlocksActivity.this, "EL201", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                AlertDialog devicealert = builder.create();
                devicealert.show();
                break;

            case ABOUTUS_DLG:
                AlertDialog.Builder aboutus = new AlertDialog.Builder(this);
                aboutus.setTitle("About Us");
                aboutus.setMessage("About Us will be implemented soon");
                aboutus.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog aboutusalert = aboutus.create();
                aboutusalert.show();
                break;

            case UPDATES_DLG:
                AlertDialog.Builder appupdate = new AlertDialog.Builder(this);
                appupdate.setTitle("App Updates");
                if (updatefound) {
                    appupdate.setMessage("Newer version of Entrylog Application is available to Update..");
                    appupdate.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DownloadLatestApk downloadLatestApk = new DownloadLatestApk();
                            downloadLatestApk.execute();
                        }
                    });
                } else {
                    appupdate.setMessage("Current Version of Entrylog Application is up to date..");
                    appupdate.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                }
                appupdate.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog appupdatealert = appupdate.create();
                appupdatealert.show();
                if (updatefound) {
                    updatefound = false;
                    ((AlertDialog) appupdatealert).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void addVisitors(Class startclass) {
        Intent intent = new Intent(BlocksActivity.this, startclass);
        intent.putExtra("ID", OrganizationID);
        intent.putExtra("GuardID", GuardID);
        intent.putExtra("OrganizationName", OrganizationName);
        intent.putExtra("User", User);
        startActivity(intent);
    }

    private void checkoutVisitors(Class startclass) {
        Intent checkout = new Intent(BlocksActivity.this, startclass);
        checkout.putExtra("ID", OrganizationID);
        checkout.putExtra("GuardID", GuardID);
        startActivity(checkout);
    }

    private void visitors(Class startclass, String view) {
        Intent visitors = new Intent(BlocksActivity.this, startclass);
        visitors.putExtra("VIEW", view);
        startActivity(visitors);
    }

    class Logouttimer implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    logoutstatus();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    public void logoutstatus() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (detailsValue.isLoginSuccess()) {
                        detailsValue.setLoginSuccess(false);
                        logoutthread.interrupt();
                        dialog.dismiss();
                        editor.putString("Login", "No");
                        editor.putString("Device", "");
                        editor.commit();
                        boolean deleted = functionCalls.deletefolder();
                        if (deleted) {
                            Log.d("debug", "Entrylog Folder deleted");
                        } else {
                            Log.d("debug", "Entrylog Folder not deleted");
                        }
                        Intent logoutIntent = new Intent();
                        setResult(Activity.RESULT_OK, logoutIntent);
                        finish();
                    }
                    if (detailsValue.isLoginFailure()) {
                        detailsValue.setLoginFailure(false);
                        dialog.dismiss();
                        Toast.makeText(BlocksActivity.this, "Logout Failure", Toast.LENGTH_SHORT).show();
                        logoutthread.interrupt();
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    class Updatetimer implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    updatestatus();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    public void updatestatus() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (detailsValue.isApkfilexist()) {
                        detailsValue.setApkfilexist(false);
                        dialog.dismiss();
                        PackageInfo pInfo = null;
                        try {
                            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        String Appversion = pInfo.versionName;
                        Apkfile = detailsValue.getApkfile();
                        UpdateApkURL = detailsValue.getApkdownloadUrl();
                        String Serverapkversion = Apkfile.substring(Apkfile.length()-9, Apkfile.length()-4);
                        File apkFile = new File(android.os.Environment.getExternalStorageDirectory(), "Entrylog"
                                + File.separator + "Apk" + File.separator + Apkfile);
                        if (apkFile.exists()) {
                            updatethread.interrupt();
                            try {
                                functionCalls.LogStatus("Apk file exist so don't need to download apk..");
                                UpdateApp(apkFile);
                            } catch (ActivityNotFoundException e) {
                            }
                        } else {
                            compare(Appversion, Serverapkversion);
                            functionCalls.LogStatus("Apk file doesn't exists downloading necessary");
                        }
                    }
                    if (appdownloaded) {
                        appdownloaded = false;
                        updatethread.interrupt();
                        File apkFile = new File(android.os.Environment.getExternalStorageDirectory(), "Entrylog"
                                + File.separator + "Apk" + File.separator + Apkfile);
                        try {
                            if (apkFile.exists()) {
                                functionCalls.LogStatus("Apk file exist");
                                UpdateApp(apkFile);
                            }
                        } catch (ActivityNotFoundException e) {
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    public void UpdateApp(File Apkfile) {
        Uri path = Uri.fromFile(Apkfile);
        Intent objIntent = new Intent(Intent.ACTION_VIEW);
        objIntent.setDataAndType(path, "application/vnd.android.package-archive");
        objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(objIntent);
    }

    public void compare(String v1, String v2) {
        String s1 = functionCalls.normalisedVersion(v1);
        String s2 = functionCalls.normalisedVersion(v2);
        int cmp = s1.compareTo(s2);
        String cmpStr = cmp < 0 ? "<" : cmp > 0 ? ">" : "==";
        if (cmpStr.equals("<")) {
            updatefound = true;
            showdialog(UPDATES_DLG);
        } else {
            showdialog(UPDATES_DLG);
        }
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Downloading file..");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }

    class DownloadLatestApk extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count = 0;

            try {
                URL url = new URL(UpdateApkURL);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lengthOfFile = conexion.getContentLength();
                Log.d("debug", "Length of file: " + lengthOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                File apkFile = new File(android.os.Environment.getExternalStorageDirectory(), "Entrylog"
                        + File.separator + "Apk" + File.separator + Apkfile);
                if (apkFile.exists()) {
                    apkFile.delete();
                    functionCalls.LogStatus("Apk file exists and deleted it");
                } else {
                    functionCalls.LogStatus("Apk file doesn't exists");
                }
                OutputStream output = new FileOutputStream(functionCalls.filepath("Apk")+ File.separator + Apkfile);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lengthOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {}
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
            appdownloaded = true;
        }
    }
}
