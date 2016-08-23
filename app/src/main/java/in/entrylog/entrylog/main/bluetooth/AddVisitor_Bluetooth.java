package in.entrylog.entrylog.main.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import in.entrylog.entrylog.R;
import in.entrylog.entrylog.database.DataBase;
import in.entrylog.entrylog.dataposting.ConnectingTask;
import in.entrylog.entrylog.dataposting.ConnectingTask.MobileAutoSuggest;
import in.entrylog.entrylog.dataposting.ConnectingTask.SMSOTP;
import in.entrylog.entrylog.main.services.FieldsService;
import in.entrylog.entrylog.main.services.PrintingService;
import in.entrylog.entrylog.main.services.StaffService;
import in.entrylog.entrylog.main.services.Updatedata;
import in.entrylog.entrylog.myprinter.BTPrinting;
import in.entrylog.entrylog.myprinter.Global;
import in.entrylog.entrylog.myprinter.WorkService;
import in.entrylog.entrylog.values.DetailsValue;
import in.entrylog.entrylog.values.FunctionCalls;

public class AddVisitor_Bluetooth extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final int START_DLG = 5;
    private static final int END_DLG = 6;
    private static final int MOBILE_DLG = 7;
    private static final int OTP_DLG = 8;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

    private static Uri fileUri; // file url to store image/video
    static File mediaFile;
    private static Handler mHandler = null;

    EditText name_et, email_et, mobile_et, address_et, vehicle_et;
    AutoCompleteTextView tomeet_et;
    ImageView photo_img;
    LinearLayout addvisitorslayout;
    Button submit_btn;
    String Name, Email="", FromAddress, ToMeet, Vehicleno = "", Organizationid, OrganizationName, UpdateVisitorImage="",
            Visitors_ImagefileName = "", GuardID, User, DataPath, DateTime="", BarCodeValue="", format, Visitor_Designation="",
            Department="", Purpose="", House_number="", Flat_number="", Block="", No_Visitor="", aClass="", Section="",
            Student_Name="", ID_Card="", Visitor_Entry="";
    int codevalue, digits;
    static String Mobile = "", Visitors_id;
    ConnectingTask task;
    DetailsValue details;
    ArrayList<String> fieldslist, fieldvalues;
    Thread mythread, bluetooththread, scanningthread, mobilesuggestthread;
    static ProgressDialog dialog = null;
    boolean Visitorsimage = false, connetedsocket = false, textfileready = false, submitpressed = false, devicefound = false,
            scanningstarted = false, connectingdevice = false, devicenamenotfound = false, pairingstarted = false, reprint = false,
            scanningregistered = false, otpcheck = false, manualcheck = false, otpresent = false;
    static boolean qrcodeprinted = false, btconnected = false, completed = false, deviceconnected = false, devicenotconnected = false;
    static BluetoothAdapter mBluetoothAdapter;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    static ArrayList<String> arrayListpaired, stafflist, printingorder, printingdisplay;
    static ArrayList<BluetoothDevice> arrayListPairedBluetoothDevices;
    BroadcastReceiver mReceiver, mPairing;
    View mProgressBar;
    DataBase dataBase;
    IntentFilter scanningdevice;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    FunctionCalls functionCalls;
    FieldsService fieldsService;
    StaffService staffService;
    PrintingService printingService;
    TextInputLayout Til_field1, Til_field2, Til_field3, Til_field4, Til_field5, Til_field6, Til_field7, Til_field8,
            Til_field9, Til_field10, Til_field11, emailLayout;
    EditText Et_field1, Et_field2, Et_field3, Et_field4, Et_field5, Et_field6, Et_field7, Et_field8, Et_field9,
            Et_field10, Et_field11;
    ArrayAdapter<String> Staffadapter;
    int otpcount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_entrylog_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_add_visitors);

        details = new DetailsValue();
        task = new ConnectingTask();
        functionCalls = new FunctionCalls();

        fieldsService = new FieldsService();
        staffService = new StaffService();
        printingService = new PrintingService();

        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = settings.edit();

        dataBase = new DataBase(this);
        dataBase.open();

        digits = 4;
        format = String.format("%%0%dd", digits);

        name_et = (EditText) findViewById(R.id.name_EtTxt);
        email_et = (EditText) findViewById(R.id.email_EtTxt);
        mobile_et = (EditText) findViewById(R.id.mobile_EtTxt);
        address_et = (EditText) findViewById(R.id.address_EtTxt);
        tomeet_et = (AutoCompleteTextView) findViewById(R.id.tomeet_EtTxt);
        vehicle_et = (EditText) findViewById(R.id.vehicle_EtTxt);
        photo_img = (ImageView) findViewById(R.id.cameraimage);
        if (settings.getString("ImageAccess","").equals("No")) {
            photo_img.setVisibility(View.GONE);
        }
        submit_btn = (Button) findViewById(R.id.submit_btn);

        functionCalls.OrientationView(AddVisitor_Bluetooth.this);

        emailLayout = (TextInputLayout) findViewById(R.id.email_Til);
        Til_field1 = (TextInputLayout) findViewById(R.id.field1_Til);
        Til_field2 = (TextInputLayout) findViewById(R.id.field2_Til);
        Til_field3 = (TextInputLayout) findViewById(R.id.field3_Til);
        Til_field4 = (TextInputLayout) findViewById(R.id.field4_Til);
        Til_field5 = (TextInputLayout) findViewById(R.id.field5_Til);
        Til_field6 = (TextInputLayout) findViewById(R.id.field6_Til);
        Til_field7 = (TextInputLayout) findViewById(R.id.field7_Til);
        Til_field8 = (TextInputLayout) findViewById(R.id.field8_Til);
        Til_field9 = (TextInputLayout) findViewById(R.id.field9_Til);
        Til_field10 = (TextInputLayout) findViewById(R.id.field10_Til);
        Til_field11 = (TextInputLayout) findViewById(R.id.field11_Til);

        Et_field1 = (EditText) findViewById(R.id.field1_EtTxt);
        Et_field2 = (EditText) findViewById(R.id.field2_EtTxt);
        Et_field3 = (EditText) findViewById(R.id.field3_EtTxt);
        Et_field4 = (EditText) findViewById(R.id.field4_EtTxt);
        Et_field5 = (EditText) findViewById(R.id.field5_EtTxt);
        Et_field6 = (EditText) findViewById(R.id.field6_EtTxt);
        Et_field7 = (EditText) findViewById(R.id.field7_EtTxt);
        Et_field8 = (EditText) findViewById(R.id.field8_EtTxt);
        Et_field9 = (EditText) findViewById(R.id.field9_EtTxt);
        Et_field10 = (EditText) findViewById(R.id.field10_EtTxt);
        Et_field11 = (EditText) findViewById(R.id.field11_EtTxt);

        addvisitorslayout = (LinearLayout) findViewById(R.id.addvisitors_layout);

        mProgressBar = findViewById(R.id.addvisitors_progress);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
        arrayListpaired = new ArrayList<String>();
        arrayListPairedBluetoothDevices = new ArrayList<BluetoothDevice>();

        mHandler = new MHandler(this);
        WorkService.addHandler(mHandler);

        if (null == WorkService.workThread) {
            Intent intent = new Intent(this, WorkService.class);
            startService(intent);
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mBluetoothAdapter.isEnabled()) {
                    getPairedDevices();
                }
            }
        }, 5000);

        CheckScanning();
        BluetoothTimerThread();
        MobileNoSuggestThread();

        DataPath = functionCalls.filepath("Textfile") + File.separator + "Data.txt";

        Organizationid = settings.getString("OrganizationID", "");
        GuardID = settings.getString("GuardID", "");
        OrganizationName = settings.getString("OrganizationName", "");
        User = settings.getString("User", "");

        if (settings.getString("BarCode", "").equals("")) {
            codevalue = 1;
            String value = String.format(format, codevalue);
            BarCodeValue = value + Organizationid;
            editor.putString("BarCode", BarCodeValue);
            editor.commit();
        } else {
            String code = settings.getString("BarCode", "");
            String barvalue = code.substring(0, 4);
            codevalue = Integer.parseInt(barvalue);
            if (codevalue == 9999) {
                codevalue = 1;
                String value = String.format(format, codevalue);
                BarCodeValue = value + Organizationid;
                editor.putString("BarCode", BarCodeValue);
                editor.commit();
            } else {
                codevalue = codevalue + 1;
                String value = String.format(format, codevalue);
                BarCodeValue = value + Organizationid;
                editor.putString("BarCode", BarCodeValue);
                editor.commit();
            }
        }

        showdialog(START_DLG);

        photo_img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Checking camera availability
                if (!isDeviceSupportCamera()) {
                    Toast.makeText(getApplicationContext(), "Sorry! Your device doesn't support camera", Toast.LENGTH_LONG).show();
                    // will close the app if the device does't have camera
                    finish();
                } else {
                    // capture picture
                    captureImage();
                }
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settings.getString("ImageAccess","").equals("Yes")) {
                    if (Visitorsimage) {
                        if (btconnected) {
                            btconnected = false;
                            CheckInDetails();
                            Log.d("debug", "Image Path: "+fileUri.getPath());
                        } else {
                            Toast.makeText(AddVisitor_Bluetooth.this, "Please turn On Bluetooth Device", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddVisitor_Bluetooth.this, "Please take Visitors Photo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (btconnected) {
                        btconnected = false;
                        CheckInDetails();
                        Log.d("debug", "Image Path: "+fileUri.getPath());
                    } else {
                        Toast.makeText(AddVisitor_Bluetooth.this, "Please turn On Bluetooth Device", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void CheckInDetails() {
        Name = name_et.getText().toString();
        if (!name_et.getText().toString().equals("")) {
            Email = email_et.getText().toString();
            Mobile = mobile_et.getText().toString();
            if (!mobile_et.getText().toString().equals("")) {
                if (Mobile.length() == 10) {
                    FromAddress = address_et.getText().toString();
                    if (!address_et.getText().toString().equals("")) {
                        ToMeet = tomeet_et.getText().toString();
                        if (!tomeet_et.getText().toString().equals("")) {
                            Vehicleno = vehicle_et.getText().toString();
                            Random rand = new Random();
                            int num = rand.nextInt(9000) + 1000;
                            Visitors_ImagefileName = Mobile+""+num + ".jpg";
                            if (!Visitors_ImagefileName.equals("")) {
                                getExtraFields();
                                if (UpdateVisitorImage.equals("Yes")) {
                                    dataBase.insertentrylogdata(Name, Email, Mobile, FromAddress, ToMeet, Vehicleno,
                                            Visitors_ImagefileName, fileUri.getPath(), BarCodeValue, Organizationid, GuardID,
                                            UpdateVisitorImage, Visitor_Designation, Department, Purpose, House_number,
                                            Flat_number, Block, No_Visitor, aClass, Section, Student_Name, ID_Card,
                                            settings.getString("Device", ""), Visitor_Entry);
                                } else {
                                    dataBase.insertentrylogdata(Name, Email, Mobile, FromAddress, ToMeet, Vehicleno,
                                            Visitors_ImagefileName, "", BarCodeValue, Organizationid, GuardID,
                                            UpdateVisitorImage, Visitor_Designation, Department, Purpose, House_number,
                                            Flat_number, Block, No_Visitor, aClass, Section, Student_Name, ID_Card,
                                            settings.getString("Device", ""), Visitor_Entry);
                                }
                                if (!settings.getString("UpdateData", "").equals("Running")) {
                                    Log.d("debug", "Service Started");
                                    Intent intent = new Intent(AddVisitor_Bluetooth.this, Updatedata.class);
                                    startService(intent);
                                }
                                PrintData();
                            } else {
                                Toast.makeText(AddVisitor_Bluetooth.this, "Please take a Photo of Visitor", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            tomeet_et.setError("Please Enter To Meet Person");
                        }
                    } else {
                        address_et.setError("Please Enter From Address");
                    }
                } else {
                    mobile_et.setError("Please Enter Correct Mobile Number");
                }
            } else {
                mobile_et.setError("Please Enter Mobile Number");
            }
        } else {
            name_et.setError("Please Enter Name");
        }
    }
    
    private void PrintData() {
        dialog = ProgressDialog.show(AddVisitor_Bluetooth.this, "", "Updating file...", true);
        SaveData();
        if (deviceconnected) {
            deviceconnected = false;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    try {
                        makeConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 2000);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    dialog.dismiss();
                    showdialog(END_DLG);
                }
            }, 5000);
        } else {
            Toast.makeText(AddVisitor_Bluetooth.this, "Please turn on Bluetooth Device and connect it..", Toast.LENGTH_SHORT).show();
        }
    }

    private void CheckScanning() {
        Log.d("debug", "Check scanning started");
        scanningthread = null;
        Runnable runnable = new ScanningTimer();
        scanningthread = new Thread(runnable);
        scanningthread.start();
    }

    private void BluetoothTimerThread() {
        Log.d("debug", "Bluetooth Timer started");
        bluetooththread = null;
        Runnable runnable = new BluetoothTimer();
        bluetooththread = new Thread(runnable);
        bluetooththread.start();
    }

    class ScanningTimer implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doscanning();
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    public void doscanning() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (scanningstarted) {
                        scanningstarted = false;
                        if (!devicefound) {
                            Log.d("debug", "Please Switch On Bluetooth PrinterBluetooth Device to Pair");
                            Toast.makeText(AddVisitor_Bluetooth.this, "Please Switch On Bluetooth Device to Pair",
                                    Toast.LENGTH_SHORT).show();
                            Startscanning();
                        } else if (devicefound) {
                            devicefound = false;
                            Log.d("debug", "Device Found and Scanning Thread is interrupting");
                            scanningthread.interrupt();
                        } else if (devicenamenotfound){
                            devicenamenotfound = false;
                            Log.d("debug", "Device Name not Found So sleeping for 10 seconds");
                            Thread.sleep(10000);
                            Startscanning();
                        } else {
                            Log.d("debug", "Please Switch On Device to Scan");
                            Toast.makeText(AddVisitor_Bluetooth.this, "Please Switch On Device to Scan", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (btconnected) {
                        scanningthread.interrupt();
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    class BluetoothTimer implements Runnable {
        int i = 0;

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    checkConnection();
                    i = i + 1;
                    Log.d("debug", "bluetooth timer count "+i);
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    public void checkConnection() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (devicenotconnected) {
                        devicenotconnected = false;
                        if (connectingdevice) {
                            connectingdevice = false;
                            Log.d("debug", "Device not connected");
                            Log.d("debug", "Please Turn On the Bluetooth Printer");
                            getPairedDevices();
                        }
                    }
                    if (btconnected) {
                        Log.d("debug", "Bluetooth Device finally connected..");
                        bluetooththread.interrupt();
                        if (submitpressed) {
                            submitpressed = false;
                            btconnected = false;
                            CheckInDetails();
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    private void MobileNoSuggestThread() {
        Log.d("debug", "MobileNo Suggest Timer Started");
        mobilesuggestthread = null;
        Runnable runnable = new SuggestTimer();
        mobilesuggestthread = new Thread(runnable);
    }

    class SuggestTimer implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    suggesting();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    public void suggesting() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (details.isMobileAutoSuggestSuccess()) {
                        details.setMobileAutoSuggestSuccess(false);
                        Successview();
                        Extrafields();
                        AddVisitor_Bluetooth.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.
                                SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        addvisitorslayout.setVisibility(View.VISIBLE);
                        mobilesuggestthread.interrupt();
                    }
                    if (details.isMobileAutoSuggestFailure()) {
                        details.setMobileAutoSuggestFailure(false);
                        Extrafields();
                        if (otpcheck) {
                            otpcheck = false;
                            Random rand = new Random();
                            int num = rand.nextInt(9000) + 1000;
                            editor.putString("OTP", ""+num+" ");
                            editor.commit();
                            SMSOTP smsotp = task.new SMSOTP("91"+Mobile, settings.getString("OTP", ""));
                            smsotp.execute();
                            showdialog(OTP_DLG);
                            showToast("OTP Sent");
                        } else if (manualcheck) {
                            manualcheck = false;
                            AddVisitor_Bluetooth.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.
                                    SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            addvisitorslayout.setVisibility(View.VISIBLE);
                            mobile_et.setText(Mobile);
                            name_et.requestFocus();
                        }
                        mobilesuggestthread.interrupt();
                    }
                    if (details.isMobileNoExist()) {
                        details.setMobileNoExist(false);
                        showdialog(MOBILE_DLG);
                        mobilesuggestthread.interrupt();
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Capturing Camera Image will launch camera app request image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
        previewCapturedImage();
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                functionCalls.checkimage_and_delete("Hello Camera", Mobile, fileUri.getPath());
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Display image from a path to ImageView
     */
    private void previewCapturedImage() {
        try {
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            /*options.inSampleSize = 16;*/

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);
            photo_img.setImageBitmap(rotateImage(bitmap, fileUri.getPath()));
            UpdateVisitorImage = "Yes";
            Visitorsimage = true;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap rotateImage(Bitmap src, String Imagepath) {
        Bitmap bmp = null;
        // create new matrix
        Matrix matrix = new Matrix();
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(Imagepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        if (orientation == 1) {
            bmp = src;
        } else if (orientation == 3) {
            matrix.postRotate(180);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        } else if (orientation == 8) {
            matrix.postRotate(270);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        } else {
            matrix.postRotate(90);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        }
        return bmp;
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(android.os.Environment.getExternalStorageDirectory(),
                "Entrylog" + File.separator + IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        Random rand = new Random();
        int num = rand.nextInt(9000) + 1000;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + Mobile+""+num + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    public void getPairedDevices() {
        boolean devicepaired = false;
        Set<BluetoothDevice> pairedDevice = mBluetoothAdapter.getBondedDevices();
        if (pairedDevice.size() > 0) {
            try {
                for (BluetoothDevice device : pairedDevice) {
                    arrayListpaired.add(device.getName() + " " + device.getAddress());
                    arrayListPairedBluetoothDevices.add(device);
                    Log.d("debug", "Already Paired Devices: " + device.getName());
                    if (device.getName().equals("BP-201")) {
                        if (!connectingdevice) {
                            connectingdevice = true;
                            Log.d("debug", "Paired Devices: " + device.getName());
                            WorkService.workThread.connectBt(device.getAddress());
                        }
                        devicepaired = true;
                        break;
                    }
                }
                if (!devicepaired) {
                    Log.d("debug", "Device not Paired so starting scanning");
                    Startscanning();
                }
            } catch (Exception e) {
            }
        } else {
            Log.d("debug", "No Device Bonded");
            Startscanning();
        }
    }

    private void Startscanning() {
        Log.d("debug", "Start Scanning");
        mBluetoothAdapter.startDiscovery();
        scanningstarted = true;
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        if (!devicefound) {
                            try {
                                if (device.getName().equals("BP-201")) {
                                    devicefound = true;
                                    Log.d("debug", "Pairing Devices Found: " + device.getName());
                                    PairDevice(device);
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    Log.d("debug", "nodevice found to scan so starting again to scan");
                    mBluetoothAdapter.startDiscovery();
                }
            }
        };

        // Register the BroadcastReceiver
        scanningdevice = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        scanningregistered = true;
        registerReceiver(mReceiver, scanningdevice);
    }

    private void PairDevice(final BluetoothDevice device) {
        pairingstarted = true;
        Log.d("debug", "Started Device Pairing");
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPairing = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
                    try {
                        byte[] pin = (byte[]) BluetoothDevice.class.getMethod("convertPinToBytes", String.class).invoke(BluetoothDevice.class, "0000");
                        Method m = device.getClass().getMethod("setPin", byte[].class);
                        m.invoke(device, pin);
                        device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
                        Log.d("debug", "Device Paired");
                        if (!connectingdevice) {
                            connectingdevice = true;
                            WorkService.workThread.connectBt(device.getAddress());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
        registerReceiver(mPairing, filter);
    }

    private void makeConnection() throws IOException {
        try {
            Log.d("debug", "MakeConnection Initialzing");
            mmOutputStream = BTPrinting.GetSocket().getOutputStream();
            mmInputStream = BTPrinting.GetSocket().getInputStream();
            connetedsocket = true;
            sendData();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendData() {
        try {
            Log.d("debug", "Send Data Initialzing");
            //Read and Display from text file and print
            File myFile = new File(DataPath);
            Scanner reader = new Scanner(myFile);
            while (reader.hasNextLine()) {
                Log.d("debug", "OutputStream Started");
                mmOutputStream.write(0x1B);
                mmOutputStream.write(0x61);//line spacing
                mmOutputStream.write(0x00);//line spacing
                String s = reader.nextLine();
                String s1 = s + "\n";
                Log.d("debug", s1);
                if ((s1.equals("**sp" + "\n"))) {
                    mmOutputStream.write(0x0A);
                } else if (s1.equals("**bc" + "\n")) {
                    BarCode(2);
                    Thread.sleep(500);
                } else if (s1.equals("**qr" + "\n")) {
                    QRCode();
                    Thread.sleep(500);
                } else if (s1.equals("**pic" + "\n")) {
                    PrintImage(photo_img);
                    Thread.sleep(1000);
                } else {
                    mmOutputStream.write(s1.getBytes());
                }
            }
            mmOutputStream.write(0x18);
        } catch (IndexOutOfBoundsException e) {
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SaveData() {

        String path = functionCalls.filepath("Textfile");
        String filename = "Data.txt";
        try {
            File f = new File(path + File.separator + filename);
            FileOutputStream fOut = new FileOutputStream(f);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(OrganizationName + "\r\n");
            myOutWriter.append("VISITOR" + "\r\n");
            myOutWriter.append(" " + "\r\n");
            myOutWriter.append("**pic" + "\r\n");
            myOutWriter.append(" " + "\r\n");
            if (settings.getString("Scannertype", "").equals("Barcode")) {
                myOutWriter.append("**bc" + "\r\n");
            } else {
                myOutWriter.append("**qr" + "\r\n");
            }
            HashSet<String> Printdisplay = new HashSet<>();
            Printdisplay = printingService.printingset;
            printingdisplay = new ArrayList<>();
            printingdisplay.addAll(Printdisplay);
            Collections.sort(printingdisplay);
            if (printingdisplay.size() > 0) {
                functionCalls.LogStatus("Printing Display Size: "+printingdisplay.size());
                for (int i = 0; i < printingdisplay.size(); i++) {
                    String PrintOrder = printingdisplay.get(i).toString();
                    functionCalls.LogStatus("Print Order: "+PrintOrder);
                    String Display = PrintOrder.substring(2, PrintOrder.length());
                    functionCalls.LogStatus("Display: "+Display);
                    myOutWriter.append(Display+": ");
                    if (Display.equals("Name")) {
                        myOutWriter.append(Name + "\r\n");
                    }
                    if (Display.equals("Mobile")) {
                        myOutWriter.append(Mobile + "\r\n");
                    }
                    if (Display.equals("From")) {
                        myOutWriter.append(FromAddress + "\r\n");
                    }
                    if (Display.equals("To Meet")) {
                        myOutWriter.append(ToMeet + "\r\n");
                    }
                    if (Display.equals("Date")) {
                        if (!reprint) {
                            DateTime = CurrentDate() + " " + CurrentTime() + "\r\n";
                            myOutWriter.append(DateTime);
                        } else {
                            myOutWriter.append(DateTime);
                        }
                    }
                    if (Display.equals("Visitor Designation")) {
                        myOutWriter.append(Visitor_Designation + "\r\n");
                    }
                    if (Display.equals("Department")) {
                        myOutWriter.append(Department + "\r\n");
                    }
                    if (Display.equals("Purpose")) {
                        myOutWriter.append(Purpose + "\r\n");
                    }
                    if (Display.equals("House No")) {
                        myOutWriter.append(House_number + "\r\n");
                    }
                    if (Display.equals("Flat No")) {
                        myOutWriter.append(Flat_number + "\r\n");
                    }
                    if (Display.equals("Block")) {
                        myOutWriter.append(Block + "\r\n");
                    }
                    if (Display.equals("No of Visitor")) {
                        myOutWriter.append(No_Visitor + "\r\n");
                    }
                    if (Display.equals("Class")) {
                        myOutWriter.append(aClass + "\r\n");
                    }
                    if (Display.equals("Section")) {
                        myOutWriter.append(Section + "\r\n");
                    }
                    if (Display.equals("Student")) {
                        myOutWriter.append(Student_Name + "\r\n");
                    }
                    if (Display.equals("Id Card")) {
                        myOutWriter.append(ID_Card + "\r\n");
                    }
                    if (Display.equals("Entry")) {
                        myOutWriter.append(User + "\r\n");
                    }
                }
            }
            myOutWriter.append(" " + "\r\n");
            myOutWriter.append(" " + "\r\n");
            myOutWriter.close();
            fOut.close();
            textfileready = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String CurrentDate() {
        Calendar cal = Calendar.getInstance();
        int curyear = cal.get(Calendar.YEAR);
        int curmonth = cal.get(Calendar.MONTH);
        int curdate = cal.get(Calendar.DAY_OF_MONTH);
        String Currentdate = "" + curdate + "/" + "" + (curmonth + 1) + "/" + curyear;
        Date Starttime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Starttime = new SimpleDateFormat("dd/MM/yyyy").parse(Currentdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String Date = sdf.format(Starttime);
        return Date;
    }

    private String CurrentTime() {
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

    private void QRCode() {
        Log.d("debug", "Starting QRCode");
        int nWidthX = 3;//5
        int necl = 2;//4
        Bundle data = new Bundle();
        data.putString(Global.STRPARA1, BarCodeValue);
        data.putInt(Global.INTPARA1, nWidthX);
        Log.d("debug", "QR_CODE Width: " + nWidthX);
        data.putInt(Global.INTPARA2, 5);
        Log.d("debug", "Progress Size: " + 5);
        data.putInt(Global.INTPARA3, necl);
        Log.d("debug", "QR_CODE necl: " + necl);
        WorkService.workThread.handleCmd(Global.CMD_POS_SETQRCODE, data);
    }

    private void BarCode(int i) {
        String strBarcode = BarCodeValue;
        int nOrgx = 0 * 12;
        int nType = 0x41 + i;
        Log.d("debug", "Type: " + i);
        int nWidthX = 1 + 2;
        int nHeight = (2 + 1) * 24;
        int nHriFontType = 0;
        int nHriFontPosition = 2;
        Log.d("debug", "Printing BarCode");
        Bundle data = new Bundle();
        data.putString(Global.STRPARA1, strBarcode);
        Log.d("debug", "BarCode:" + strBarcode);
        data.putInt(Global.INTPARA1, nOrgx);
        Log.d("debug", "BarCode:" + nOrgx);
        data.putInt(Global.INTPARA2, nType);
        Log.d("debug", "BarCode:" + nType);
        data.putInt(Global.INTPARA3, nWidthX);
        Log.d("debug", "BarCode:" + nWidthX);
        data.putInt(Global.INTPARA4, nHeight);
        Log.d("debug", "BarCode:" + nHeight);
        data.putInt(Global.INTPARA5, nHriFontType);
        Log.d("debug", "BarCode:" + nHriFontType);
        data.putInt(Global.INTPARA6, nHriFontPosition);
        Log.d("debug", "BarCode:" + nHriFontPosition);
        WorkService.workThread.handleCmd(Global.CMD_POS_SETBARCODE, data);
    }

    private void PrintImage(ImageView imageview) {
        int nPaperWidth = 256;
        Bitmap mBitmap = ((BitmapDrawable) imageview.getDrawable()).getBitmap();
        if (mBitmap != null) {
            Bundle data = new Bundle();
            data.putParcelable(Global.PARCE1, mBitmap);
            data.putInt(Global.INTPARA1, nPaperWidth);
            data.putInt(Global.INTPARA2, 0);
            WorkService.workThread.handleCmd(Global.CMD_POS_PRINTPICTURE, data);
        }
    }

    private void showdialog(int id) {
        switch (id) {
            case START_DLG:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Mobile Number");
                LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.dialogview, null);
                builder.setView(ll);
                builder.setCancelable(false);
                final EditText etmobile = (EditText) ll.findViewById(R.id.dialogmobile_etTxt);

                etmobile.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String number = editable.toString();
                        int test = number.length();
                        if (test >= 1) {
                            String trimnumber = number.substring(0, 1);
                            int num = Integer.parseInt(trimnumber);
                            if (num == 7 || num == 8 || num == 9) {

                            } else {
                                etmobile.setText("");
                            }
                        }
                    }
                });
                if (settings.getString("OTPAccess", "").equals("Yes")) {
                    builder.setPositiveButton("OTP", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Visitor_Entry = "1";
                            otpcheck = true;
                            checkmobilesuggest(etmobile);
                        }
                    });
                }
                builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog alert1 = builder.create();
                alert1.show();
                break;

            case END_DLG:
                AlertDialog.Builder endbuilder = new AlertDialog.Builder(this);
                endbuilder.setTitle("Printing Details");
                endbuilder.setCancelable(false);
                endbuilder.setMessage("Did a Data got a printed correctly...??");
                endbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                endbuilder.setNegativeButton("REPRINT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reprint = true;
                        PrintData();
                    }
                });
                AlertDialog endalert = endbuilder.create();
                endalert.show();
                break;

            case MOBILE_DLG:
                AlertDialog.Builder existbuilder = new AlertDialog.Builder(this);
                existbuilder.setTitle("Visitor Details");
                existbuilder.setCancelable(false);
                existbuilder.setMessage("Entered Mobile is already Logged In.. To Check In again please checkout it manually..");
                existbuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog existalert = existbuilder.create();
                existalert.show();
                break;

            case OTP_DLG:
                final AlertDialog.Builder otpbuilder = new AlertDialog.Builder(this);
                otpbuilder.setTitle("Mobile Number");
                otpbuilder.setMessage(Mobile);
                LinearLayout otpll = (LinearLayout) getLayoutInflater().inflate(R.layout.dialogview, null);
                otpbuilder.setView(otpll);
                otpbuilder.setCancelable(false);
                TextInputLayout tilmobile = (TextInputLayout) otpll.findViewById(R.id.timer_Til);
                tilmobile.setVisibility(View.GONE);
                TextInputLayout tilotp = (TextInputLayout) otpll.findViewById(R.id.otp_Til);
                tilotp.setVisibility(View.VISIBLE);
                final EditText otpetTxt = (EditText) otpll.findViewById(R.id.dialogotp_etTxt);

                otpbuilder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!otpetTxt.getText().toString().equals("")) {
                            String OTP = otpetTxt.getText().toString();
                            String SavedOTP = settings.getString("OTP", "");
                            if (OTP.equals(SavedOTP)) {
                                addvisitorslayout.setVisibility(View.VISIBLE);
                                mobile_et.setText(Mobile);
                                name_et.requestFocus();
                            } else {
                                otpetTxt.setError("Entered OTP is not matching please enter correct one..");
                                otpetTxt.setText("");
                                showToast("Entered OTP is not matching please enter correct one..");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        showdialog(OTP_DLG);
                                    }
                                }, 500);
                            }
                        } else {
                            otpetTxt.setError("Please enter OTP");
                            showToast("Please enter OTP");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showdialog(OTP_DLG);
                                }
                            }, 500);
                        }
                    }
                });
                otpbuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                if (otpcount == 2) {
                    otpbuilder.setNeutralButton("MANUAL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Visitor_Entry = "2";
                            addvisitorslayout.setVisibility(View.VISIBLE);
                            mobile_et.setText(Mobile);
                            name_et.requestFocus();
                        }
                    });
                } else {
                    otpbuilder.setNeutralButton("RESEND", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            otpcount = otpcount + 1;
                            otpresent = true;
                            otpbuilder.setMessage("OTP has been resent"+"\n"+Mobile);
                            SMSOTP smsotp = task.new SMSOTP("91"+Mobile, settings.getString("OTP", ""));
                            smsotp.execute();
                            showdialog(OTP_DLG);
                            showToast("OTP Resent");
                        }
                    });
                }
                AlertDialog alert2 = otpbuilder.create();
                alert2.show();
                if (otpresent) {
                    ((AlertDialog) alert2).getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
                }
                break;
        }
    }

    private void checkmobilesuggest(EditText etmobile) {
        if (!etmobile.getText().toString().equals("")) {
            Mobile = etmobile.getText().toString();
            if (Mobile.length() == 10) {
                etmobile.setText("");
                MobileAutoSuggest mobile = task.new MobileAutoSuggest(details, Organizationid, Mobile, mProgressBar,
                        AddVisitor_Bluetooth.this);
                mobile.execute();
                mobilesuggestthread.start();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showToast("Please Enter Valid Mobile Number");
                        showdialog(START_DLG);
                    }
                }, 1000);
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showToast("Enter Mobile Number");
                    showdialog(START_DLG);
                }
            }, 1000);
        }
    }

    private void Successview() {
        name_et.setText(details.getVisitors_Name());
        email_et.setText(details.getVisitors_Email());
        mobile_et.setText(Mobile);
        address_et.setText(details.getVisitors_Address());
        tomeet_et.setText(details.getVisitors_tomeet());
        vehicle_et.setText(details.getVisitors_VehicleNo());
        String Image_Url = "http://www.tellservice.com/entrylog/visitor_images/";
        String Image = details.getVisitors_Photo();
        String Image_Path = Image_Url + Image;
        Picasso.with(AddVisitor_Bluetooth.this).load(Image_Path).error(R.drawable.blankperson).into(photo_img);
        Et_field1.setText(details.getVisitor_Designation());
        Et_field2.setText(details.getDepartment());
        Et_field3.setText(details.getPurpose());
        Et_field4.setText(details.getHouse_number());
        Et_field5.setText(details.getFlat_number());
        Et_field6.setText(details.getBlock());
        Et_field7.setText(details.getNo_Visitor());
        Et_field8.setText(details.getaClass());
        Et_field9.setText(details.getSection());
        Et_field10.setText(details.getStudent_Name());
        Et_field11.setText(details.getID_Card());
        UpdateVisitorImage = "No";
        Visitorsimage = true;
    }

    private void Extrafields() {
        LogStatus("Fetch field Started");
        HashSet<String> hashSet = new HashSet<>();
        hashSet = fieldsService.fieldset;
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.addAll(hashSet);
        if (arrayList.size() > 0) {
            LogStatus("Size is more than 1");
            for (int i = 0; i < arrayList.size(); i++) {
                String value = arrayList.get(i).toString();
                LogStatus("Value["+i+"]: "+value);
                if (value.equals("Email")) {
                    emailLayout.setVisibility(View.VISIBLE);
                    emailLayout.setHint("Email Address");
                }
                if (value.equals("Visitor Designation")) {
                    Til_field1.setVisibility(View.VISIBLE);
                    Til_field1.setHint(value);
                }
                if (value.equals("Department")) {
                    Til_field2.setVisibility(View.VISIBLE);
                    Til_field2.setHint(value);
                }
                if (value.equals("Purpose")) {
                    Til_field3.setVisibility(View.VISIBLE);
                    Til_field3.setHint(value);
                }
                if (value.equals("House No")) {
                    Til_field4.setVisibility(View.VISIBLE);
                    Til_field4.setHint(value);
                }
                if (value.equals("Flat No")) {
                    Til_field5.setVisibility(View.VISIBLE);
                    Til_field5.setHint(value);
                }
                if (value.equals("Block")) {
                    Til_field6.setVisibility(View.VISIBLE);
                    Til_field6.setHint(value);
                }
                if (value.equals("No of Visitor")) {
                    Til_field7.setVisibility(View.VISIBLE);
                    Til_field7.setHint(value);
                }
                if (value.equals("Class")) {
                    Til_field8.setVisibility(View.VISIBLE);
                    Til_field8.setHint(value);
                }
                if (value.equals("Section")) {
                    Til_field9.setVisibility(View.VISIBLE);
                    Til_field9.setHint(value);
                }
                if (value.equals("Student Name")) {
                    Til_field10.setVisibility(View.VISIBLE);
                    Til_field10.setHint(value);
                }
                if (value.equals("ID Card No")) {
                    Til_field11.setVisibility(View.VISIBLE);
                    Til_field11.setHint(value);
                }
            }
        } else {
            LogStatus("No Fields Available");
            showToast("No Fields Available");
        }
        LogStatus("Staff field Started");
        HashSet<String> StaffSet = new HashSet<>();
        StaffSet = staffService.staffset;
        stafflist = new ArrayList<>();
        stafflist.addAll(StaffSet);
        if (stafflist.size() > 0) {
            LogStatus("Staff list Available");
            Staffadapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, stafflist);
            tomeet_et.setAdapter(Staffadapter);
            Collections.sort(stafflist);
            Staffadapter.notifyDataSetChanged();
            tomeet_et.setThreshold(1);
        } else {
            LogStatus("Staff list not Available");
        }
    }

    private void getExtraFields() {
        if (emailLayout.getVisibility() == View.VISIBLE) {
            Email = email_et.getText().toString();
        }
        if (Til_field1.getVisibility() == View.VISIBLE) {
            Visitor_Designation = Et_field1.getText().toString();
        }
        if (Til_field2.getVisibility() == View.VISIBLE) {
            Department = Et_field2.getText().toString();
        }
        if (Til_field3.getVisibility() == View.VISIBLE) {
            Purpose = Et_field3.getText().toString();
        }
        if (Til_field4.getVisibility() == View.VISIBLE) {
            House_number = Et_field4.getText().toString();
        }
        if (Til_field5.getVisibility() == View.VISIBLE) {
            Flat_number = Et_field5.getText().toString();
        }
        if (Til_field6.getVisibility() == View.VISIBLE) {
            Block = Et_field6.getText().toString();
        }
        if (Til_field7.getVisibility() == View.VISIBLE) {
            No_Visitor = Et_field7.getText().toString();
        }
        if (Til_field8.getVisibility() == View.VISIBLE) {
            aClass = Et_field8.getText().toString();
        }
        if (Til_field9.getVisibility() == View.VISIBLE) {
            Section = Et_field9.getText().toString();
        }
        if (Til_field10.getVisibility() == View.VISIBLE) {
            Student_Name = Et_field10.getText().toString();
        }
        if (Til_field11.getVisibility() == View.VISIBLE) {
            ID_Card = Et_field11.getText().toString();
        }
    }

    private void LogStatus(String str) {
        Log.d("debug", str);
    }

    private void showToast(String message) {
        Toast.makeText(AddVisitor_Bluetooth.this, message, Toast.LENGTH_SHORT).show();
    }

    static class MHandler extends Handler {

        WeakReference<AddVisitor_Bluetooth> mActivity;

        MHandler(AddVisitor_Bluetooth activity) {
            mActivity = new WeakReference<AddVisitor_Bluetooth>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            AddVisitor_Bluetooth theActivity = mActivity.get();
            switch (msg.what) {

                case Global.CMD_POS_SETQRCODERESULT: {
                    int result = msg.arg1;
                    Toast.makeText(theActivity, (result == 1) ? Global.toast_success : Global.toast_fail,
                            Toast.LENGTH_SHORT).show();
                    Log.d("debug", "QRCode Result: " + result);
                    /*if (result == 1) {
                        if (result == 1) {
                            BitmapFactory.Options options = new BitmapFactory.Options();

                            // downsizing image as it throws OutOfMemory Exception for larger
                            // images
                            options.inSampleSize = 8;

                            Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                                    options);
                            new Upload_Image(rotateImage(bitmap, fileUri.getPath()), dialog).execute();
                        }
                    }*/
                    break;
                }

                case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT: {
                    int result = msg.arg1;
                    /*Toast.makeText(theActivity, (result == 1) ? Global.toast_success : Global.toast_fail,
                            Toast.LENGTH_SHORT).show();*/
                    if (result == 1) {
                        btconnected = true;
                        deviceconnected = true;
                        Toast.makeText(theActivity, "Bluetooth Printer connected",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        devicenotconnected = true;
                        Toast.makeText(theActivity, "Please Switch On the Bluetooth Printer",
                                Toast.LENGTH_SHORT).show();
                    }
                    Log.d("debug", "Connect Result: " + result);
                    break;
                }

                case Global.CMD_POS_SETBARCODERESULT: {
                    int result = msg.arg1;
                    Toast.makeText(theActivity, (result == 1) ? Global.toast_success : Global.toast_fail,
                            Toast.LENGTH_SHORT).show();
                    Log.d("debug", "BarCode Result: " + result);
                    break;
                }

                case Global.CMD_POS_PRINTPICTURERESULT: {
                    int result = msg.arg1;
                    Toast.makeText(
                            theActivity,
                            (result == 1) ? Global.toast_success
                                    : Global.toast_fail, Toast.LENGTH_SHORT).show();
                    Log.v("debug", "Result: " + result);
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (bluetooththread.isAlive()) {
            bluetooththread.interrupt();
        }
        if (scanningthread.isAlive()) {
            scanningthread.interrupt();
        }
        if (mobilesuggestthread.isAlive()) {
            mobilesuggestthread.interrupt();
        }
        mBluetoothAdapter.disable();
        if (scanningregistered) {
            this.unregisterReceiver(mReceiver);
        }
        if (pairingstarted) {
            this.unregisterReceiver(mPairing);
        }
        functionCalls.deleteTextfile("Data.txt");
        super.onDestroy();
    }
}
