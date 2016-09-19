package in.entrylog.entrylog.main.el101_102;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;

import in.entrylog.entrylog.R;
import in.entrylog.entrylog.database.DataBase;
import in.entrylog.entrylog.dataposting.ConnectingTask;
import in.entrylog.entrylog.dataposting.ConnectingTask.MobileAutoSuggest;
import in.entrylog.entrylog.dataposting.ConnectingTask.SMSOTP;
import in.entrylog.entrylog.dataposting.DataAPI;
import in.entrylog.entrylog.main.services.FieldsService;
import in.entrylog.entrylog.main.services.PrintingService;
import in.entrylog.entrylog.main.services.StaffService;
import in.entrylog.entrylog.main.services.Updatedata;
import in.entrylog.entrylog.values.DataPrinting;
import in.entrylog.entrylog.values.DetailsValue;
import in.entrylog.entrylog.values.EL101_102;
import in.entrylog.entrylog.values.FunctionCalls;
import in.entrylog.entrylog.values.SmartCardAdapter;

public class AddVisitors_EL101 extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final int START_DLG = 5;
    private static final int END_DLG = 6;
    private static final int MOBILE_DLG = 7;
    private static final int OTP_DLG = 8;
    private static final int NFC_DLG = 9;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

    private static Uri fileUri; // file url to store image/video
    static File mediaFile;

    EditText name_et, email_et, mobile_et, address_et, vehicle_et;
    AutoCompleteTextView tomeet_et;
    ImageView photo_img;
    LinearLayout addvisitorslayout;
    Button submit_btn;
    String Name, Email="", FromAddress, ToMeet, Vehicleno = "", Organizationid, OrganizationName, UpdateVisitorImage="",
            Visitors_ImagefileName = "", GuardID, User, HeaderPath, DataPath, OrganizationPath, EmptyPath, DateTime="",
            BarCodeValue="", format, Visitor_Designation="", Department="", Purpose="", House_number="", Flat_number="",
            Block="", No_Visitor="", aClass="", Section="", Student_Name="", ID_Card="", Visitor_Entry="";
    int codevalue, digits;
    static String Mobile = "";
    ConnectingTask task;
    DetailsValue details;
    ArrayList<DetailsValue> fieldvalues;
    Thread mobilesuggestthread;
    static ProgressDialog dialog = null;
    boolean Visitorsimage = false, barcodeprinting = false, reprint = false, writeNFC = false, otpcheck = false,
            manualcheck = false, otpresent = false, mobilesuggestsuccess = false, nfcavailable = false;
    View mProgressBar;
    DataBase dataBase;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    AlertDialog nfcdialog;
    FunctionCalls functionCalls;
    DataPrinting dataPrinting;
    FieldsService fieldsService;
    StaffService staffService;
    EL101_102 el101_102device;
    TextInputLayout Til_field1, Til_field2, Til_field3, Til_field4, Til_field5, Til_field6, Til_field7, Til_field8,
            Til_field9, Til_field10, Til_field11, emailLayout;
    EditText Et_field1, Et_field2, Et_field3, Et_field4, Et_field5, Et_field6, Et_field7, Et_field8, Et_field9,
            Et_field10, Et_field11, etmobile;
    ArrayAdapter<String> Staffadapter;
    static ArrayList<String> stafflist;
    int otpcount = 0;
    NfcAdapter nfcAdapter;
    NfcManager nfcManager;
    StringBuilder printdetails;

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
        dataPrinting = new DataPrinting();

        fieldsService = new FieldsService();
        staffService = new StaffService();
        el101_102device = new EL101_102();
        printdetails = new StringBuilder();

        dataBase = new DataBase(this);
        dataBase.open();

        digits = 4;
        format = String.format("%%0%dd", digits);

        functionCalls.OrientationView(AddVisitors_EL101.this);

        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = settings.edit();

        functionCalls.LogStatus("Enabling Printer");
        el101_102device.EnableBtn(true);

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

        if (settings.getString("RFID", "").equals("true")) {
            nfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
            nfcAdapter = nfcManager.getDefaultAdapter();
            if (nfcAdapter != null && nfcAdapter.isEnabled()) {
                nfcavailable = true;
                Toast.makeText(AddVisitors_EL101.this, "NFC Enabled" +"\n"+ "NFC Available: "+nfcavailable
                        +"\n"+ "WRITERFID: "+writeNFC, Toast.LENGTH_SHORT).show();
            }
        }

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

        MobileNoSuggestThread();

        Organizationid = settings.getString("OrganizationID", "");
        GuardID = settings.getString("GuardID", "");
        OrganizationName = settings.getString("OrganizationName", "");
        User = settings.getString("User", "");

        OrganizationPath = functionCalls.filepath("Textfile") + File.separator + "Organization.txt";
        HeaderPath = functionCalls.filepath("Textfile") + File.separator + "Header.txt";
        DataPath = functionCalls.filepath("Textfile") + File.separator + "Data.txt";
        EmptyPath = functionCalls.filepath("Textfile") + File.separator + "Empty.txt";

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
                        CheckInDetails();
                    } else {
                        Toast.makeText(AddVisitors_EL101.this, "Please take Visitors Photo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    CheckInDetails();
                }
            }
        });

        tomeet_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    vehicle_et.requestFocus();
                    handled = true;
                }
                return handled;
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
                                            "", "", BarCodeValue, Organizationid, GuardID,
                                            UpdateVisitorImage, Visitor_Designation, Department, Purpose, House_number,
                                            Flat_number, Block, No_Visitor, aClass, Section, Student_Name, ID_Card,
                                            settings.getString("Device", ""), Visitor_Entry);
                                }
                                functionCalls.LogStatus("Update Data Service: "+settings.getString("UpdateData", ""));
                                if (!settings.getString("UpdateData", "").equals("Running")) {
                                    Log.d("debug", "Service Started");
                                    Intent intent = new Intent(AddVisitors_EL101.this, Updatedata.class);
                                    startService(intent);
                                }
                                PrintingData();
                            } else {
                                Toast.makeText(AddVisitors_EL101.this, "Please take a Photo of Visitor", Toast.LENGTH_SHORT).show();
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

    private void PrintingData() {
        dialog = ProgressDialog.show(AddVisitors_EL101.this, "", "Updating file...", true);
        Log.d("debug", "Printing Header");
        el101_102device.SendCommad(new byte[]{0x1d, 0x21, 0x01});
        el101_102device.SendCommad(new byte[]{0x1b, 0x61, 0x01});
        printdetails.append(OrganizationName);
        el101_102device.printString(""+printdetails);
        printdetails.delete(0, printdetails.length());
        el101_102device.SendCommad(new byte[]{0x1d, 0x21, 0x00});
        el101_102device.SendCommad(new byte[]{0x1d, 0x21, 0x00});
        el101_102device.SendCommad(new byte[]{0x1d, 0x21, 0x00});
        printdetails.append("VISITOR");
        el101_102device.printString(""+printdetails);
        printdetails.delete(0, printdetails.length());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("debug", "Printing Image");
                el101_102device.imageprinting = true;
                el101_102device.PrintHumanImage(photo_img);
            }
        }, 1500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("debug", "Printing BarCode");
                if (el101_102device.imageprinting) {
                    el101_102device.imageprinting = false;
                }
                if (settings.getString("Scannertype", "").equals("Barcode")) {
                    el101_102device.printString("   "+"\n");
                    barcodeprinting = true;
                    el101_102device.printBarCode(BarCodeValue);
                } else {
                    el101_102device.printQRcode(195, BarCodeValue);
                }
            }
        }, 4000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("debug", "Printing Header");
                if (barcodeprinting) {
                    barcodeprinting = false;
                    el101_102device.printString("   "+"\n");
                }
                el101_102device.SendCommad(new byte[]{0x1b, 0x61, 0x00});
                el101_102device.SendCommad(new byte[]{0x1b, 0x61, 0x00});
                el101_102device.SendCommad(new byte[]{0x1b, 0x61, 0x00});
                el101_102device.SaveData(printdetails, Name, Mobile, FromAddress, ToMeet, DateTime, Visitor_Designation,
                        Department, Purpose, House_number, Flat_number, Block, No_Visitor, aClass, Section, Student_Name,
                        ID_Card, User, Email, Vehicleno, reprint);
                el101_102device.printString(""+printdetails);
                el101_102device.printString("   "+"\n");
            }
        }, 6000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                printdetails.delete(0, printdetails.length());
                showdialog(END_DLG);
            }
        }, 7500);
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
                        mobilesuggestsuccess = true;
                        if (otpcheck) {
                            otpcheck = false;
                            Random rand = new Random();
                            int num = rand.nextInt(9000) + 1000;
                            editor.putString("OTP", ""+num);
                            editor.commit();
                            SMSOTP smsotp = task.new SMSOTP("91"+Mobile, settings.getString("OTP", ""));
                            smsotp.execute();
                            functionCalls.showToast(AddVisitors_EL101.this, "OTP Sent");
                            showdialog(OTP_DLG);
                        } else if (manualcheck) {
                            manualcheck = false;
                            AddVisitors_EL101.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.
                                    SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            addvisitorslayout.setVisibility(View.VISIBLE);
                            mobile_et.setText(Mobile);
                            name_et.requestFocus();
                        }
                        mobilesuggestthread.interrupt();
                    }
                    if (details.isMobileAutoSuggestFailure()) {
                        details.setMobileAutoSuggestFailure(false);
                        Extrafields();
                        if (otpcheck) {
                            otpcheck = false;
                            Random rand = new Random();
                            int num = rand.nextInt(9000) + 1000;
                            editor.putString("OTP", ""+num);
                            editor.commit();
                            SMSOTP smsotp = task.new SMSOTP("91"+Mobile, settings.getString("OTP", ""));
                            smsotp.execute();
                            functionCalls.showToast(AddVisitors_EL101.this, "OTP Sent");
                            showdialog(OTP_DLG);
                        } else if (manualcheck) {
                            manualcheck = false;
                            AddVisitors_EL101.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.
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

    /**
     * Checking device has camera hardware or not
     */
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
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
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
            /*options.inSampleSize = 8;*/

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            photo_img.setImageBitmap(rotateImage(bitmap, fileUri.getPath()));
            functionCalls.LogStatus("Image Size: "+sizeOf(bitmap));
            UpdateVisitorImage = "Yes";
            Visitorsimage = true;
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError outOfMemoryError) {
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            photo_img.setImageBitmap(rotateImage(bitmap, fileUri.getPath()));
            functionCalls.LogStatus("Image Size: "+sizeOf(bitmap));
            UpdateVisitorImage = "Yes";
            Visitorsimage = true;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    protected int sizeOf(Bitmap data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return data.getRowBytes() * data.getHeight();
        } else {
            return data.getByteCount();
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

    @Override
    protected void onDestroy() {
        el101_102device.timehandler.removeCallbacks(el101_102device.timerunnable);
        if (el101_102device.mSerialPort != null)
            el101_102device.mSerialPort.close();
        el101_102device.mSerialPort = null;
        el101_102device.EnableBtn(false);
        functionCalls.deleteTextfile("Organization.txt");
        functionCalls.deleteTextfile("Header.txt");
        functionCalls.deleteTextfile("Empty.txt");
        functionCalls.deleteTextfile("Data.txt");
        super.onDestroy();
    }

    private void showdialog(int id) {
        switch (id) {
            case START_DLG:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Mobile Number");
                LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.dialogview, null);
                builder.setView(ll);
                builder.setCancelable(false);
                etmobile = (EditText) ll.findViewById(R.id.dialogmobile_etTxt);

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
                } else {
                    builder.setPositiveButton("MANUAL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Visitor_Entry = "2";
                            manualcheck = true;
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
                        if (nfcavailable) {
                            writeNFC = true;
                            showdialog(NFC_DLG);
                        } else {
                            finish();
                        }
                    }
                });
                endbuilder.setNegativeButton("REPRINT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reprint = true;
                        PrintingData();
                    }
                });
                AlertDialog endalert = endbuilder.create();
                endalert.show();
                break;

            case NFC_DLG:
                AlertDialog.Builder nfcbuilder = new AlertDialog.Builder(this);
                nfcbuilder.setTitle("Write Smart Card");
                nfcbuilder.setCancelable(false);
                nfcbuilder.setMessage("Please take a Smart Card Tag to write a data on it...");
                nfcbuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                nfcdialog = nfcbuilder.create();
                nfcdialog.show();
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
                if (!otpresent) {
                    otpbuilder.setMessage(Mobile);
                } else {
                    if (otpcount == 2) {
                        otpbuilder.setMessage("OTP has been resent 2 times"+"\n"+Mobile);
                    } else {
                        otpbuilder.setMessage("OTP has been resent"+"\n"+Mobile);
                    }
                }
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
                                if (mobilesuggestsuccess) {
                                    AddVisitors_EL101.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.
                                            SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                    addvisitorslayout.setVisibility(View.VISIBLE);
                                } else {
                                    addvisitorslayout.setVisibility(View.VISIBLE);
                                    mobile_et.setText(Mobile);
                                    name_et.requestFocus();
                                }
                            } else {
                                otpetTxt.setError("Entered OTP is not matching please enter correct one..");
                                otpetTxt.setText("");
                                functionCalls.showToast(AddVisitors_EL101.this,
                                        "Entered OTP is not matching please enter correct one..");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        showdialog(OTP_DLG);
                                    }
                                }, 500);
                            }
                        } else {
                            otpetTxt.setError("Please enter OTP");
                            functionCalls.showToast(AddVisitors_EL101.this, "Please enter OTP");
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
                            if (mobilesuggestsuccess) {
                                AddVisitors_EL101.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.
                                        SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            } else {
                                mobile_et.setText(Mobile);
                                name_et.requestFocus();
                            }
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
                            functionCalls.showToast(AddVisitors_EL101.this, "OTP Resent");
                        }
                    });
                }
                AlertDialog alert2 = otpbuilder.create();
                alert2.show();
                /*if (otpresent) {
                    ((AlertDialog) alert2).getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
                }*/
                break;
        }
    }

    private void checkmobilesuggest(EditText etmobile) {
        if (!etmobile.getText().toString().equals("")) {
            Mobile = etmobile.getText().toString();
            if (Mobile.length() == 10) {
                etmobile.setText("");
                MobileAutoSuggest mobile = task.new MobileAutoSuggest(details, Organizationid, Mobile, mProgressBar,
                        AddVisitors_EL101.this);
                mobile.execute();
                mobilesuggestthread.start();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        functionCalls.showToast(AddVisitors_EL101.this, "Please Enter Valid Mobile Number");
                        showdialog(START_DLG);
                    }
                }, 1000);
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    functionCalls.showToast(AddVisitors_EL101.this, "Enter Mobile Number");
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
        String Image_Url = DataAPI.Image_Url;
        String Image = details.getVisitors_Photo();
        String Image_Path = Image_Url + Image;
        Picasso.with(AddVisitors_EL101.this).load(Image_Path).into(photo_img);
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
        functionCalls.LogStatus("Fetch field Started");
        HashSet<String> hashSet = new HashSet<>();
        hashSet = fieldsService.fieldset;
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.addAll(hashSet);
        if (arrayList.size() > 0) {
            functionCalls.LogStatus("Size is more than 1");
            for (int i = 0; i < arrayList.size(); i++) {
                String value = arrayList.get(i).toString();
                functionCalls.LogStatus("Value["+i+"]: "+value);
                if (value.equals("Visitor Email")) {
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
                    Til_field7.setHint("No. of Visitor");
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
            functionCalls.LogStatus("No Fields Available");
            functionCalls.showToast(AddVisitors_EL101.this, "No Fields Available");
        }
        functionCalls.LogStatus("Staff field Started");
        HashSet<String> StaffSet = new HashSet<>();
        StaffSet = staffService.staffset;
        stafflist = new ArrayList<>();
        stafflist.addAll(StaffSet);
        if (stafflist.size() > 0) {
            functionCalls.LogStatus("Staff list Available");
            Staffadapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, stafflist);
            tomeet_et.setAdapter(Staffadapter);
            Collections.sort(stafflist);
            Staffadapter.notifyDataSetChanged();
            tomeet_et.setThreshold(1);
        } else {
            functionCalls.LogStatus("Staff list not Available");
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

    @Override
    protected void onResume() {
        fieldvalues = new ArrayList<DetailsValue>();
        super.onResume();
        functionCalls.LogStatus("OnResume NFCAvailable: "+nfcavailable);
        if (nfcavailable) {
            enableForegroundDispatchSystem();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Toast.makeText(AddVisitors_EL101.this, "Smart Card Intent", Toast.LENGTH_SHORT).show();
            if (writeNFC) {
                /*Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                NdefMessage ndefMessage = createNdefMessage(BarCodeValue);

                writeNdefMessage(tag, ndefMessage);*/
                SmartCardAdapter smartCardAdapter = new SmartCardAdapter();
                smartCardAdapter.writeSmartTag(AddVisitors_EL101.this, intent, BarCodeValue);
            }
        }
    }

    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, AddVisitors_EL101.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[] {};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }
}