package in.entrylog.entrylog.main.el201;

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
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
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

import com.POSD.controllers.PrinterController;
import com.POSD.util.MachineVersion;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

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
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

import in.entrylog.entrylog.R;
import in.entrylog.entrylog.database.DataBase;
import in.entrylog.entrylog.dataposting.ConnectingTask;
import in.entrylog.entrylog.dataposting.ConnectingTask.MobileAutoSuggest;
import in.entrylog.entrylog.dataposting.ConnectingTask.SMSOTP;
import in.entrylog.entrylog.main.el101_102.AddVisitors_EL101;
import in.entrylog.entrylog.main.services.FieldsService;
import in.entrylog.entrylog.main.services.PrintingService;
import in.entrylog.entrylog.main.services.StaffService;
import in.entrylog.entrylog.main.services.Updatedata;
import in.entrylog.entrylog.util.ImageProcessing;
import in.entrylog.entrylog.values.DataPrinting;
import in.entrylog.entrylog.values.DetailsValue;
import in.entrylog.entrylog.values.FunctionCalls;

public class AddVisitors_EL201 extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final int NFC_DLG = 4;
    private static final int START_DLG = 5;
    private static final int END_DLG = 6;
    private static final int MOBILE_DLG = 7;
    private static final int OTP_DLG = 8;
    private PrinterController printerController = null;
    int flag;
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
            Visitors_ImagefileName = "", GuardID, User, HeaderPath, DataPath, OrganizationPath, DateTime="", BarCodeValue="",
            format, Visitor_Designation="", Department="", Purpose="", House_number="", Flat_number="", Block="", No_Visitor="",
            aClass="", Section="", Student_Name="", ID_Card="", Visitor_Entry="", BuildManu="";
    int codevalue, digits;
    static String Mobile = "", Visitors_id;
    ConnectingTask task;
    DetailsValue details;
    ArrayList<String> fieldslist, fieldvalues;
    Thread mythread, bluetooththread, scanningthread, mobilesuggestthread;
    static ProgressDialog dialog = null;
    boolean Visitorsimage = false, textfileready = false, imageprinting = false, barcodeprinting = false, reprint = false,
            writeNFC = false, otpcheck = false, manualcheck = false, otpresent = false, nfcavailable = false;
    static boolean completed = false;
    View mProgressBar;
    DataBase dataBase;
    NfcAdapter nfcAdapter;
    NfcManager nfcManager;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    AlertDialog nfcdialog;
    FunctionCalls functionCalls;
    DataPrinting dataPrinting;
    FieldsService fieldsService;
    StaffService staffService;
    PrintingService printingService;
    TextInputLayout Til_field1, Til_field2, Til_field3, Til_field4, Til_field5, Til_field6, Til_field7, Til_field8,
            Til_field9, Til_field10, Til_field11, emailLayout;
    EditText Et_field1, Et_field2, Et_field3, Et_field4, Et_field5, Et_field6, Et_field7, Et_field8, Et_field9,
            Et_field10, Et_field11, etmobile;
    ArrayAdapter<String> Staffadapter;
    static ArrayList<String> stafflist, printingorder, printingdisplay;
    int otpcount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_entrylog_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_add_visitors);

        BuildManu = Build.MANUFACTURER;

        if (BuildManu.equals("LS888")) {
            printerController = PrinterController.getInstance(this);
            flag = printerController.PrinterController_Open();
            if (flag == 0) {
                Toast.makeText(AddVisitors_EL201.this, "connect_Success", Toast.LENGTH_SHORT).show();
            } else if (flag == -1){
                Toast.makeText(AddVisitors_EL201.this, "Will not Connect to this Device", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddVisitors_EL201.this, "connect_Failure", Toast.LENGTH_SHORT).show();
            }
        }

        details = new DetailsValue();
        task = new ConnectingTask();
        functionCalls = new FunctionCalls();
        dataPrinting = new DataPrinting();

        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = settings.edit();

        fieldsService = new FieldsService();
        staffService = new StaffService();
        printingService = new PrintingService();

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

        functionCalls.OrientationView(AddVisitors_EL201.this);

        if (settings.getString("RFID", "").equals("true")) {
            nfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
            nfcAdapter = nfcManager.getDefaultAdapter();
            if (nfcAdapter != null && nfcAdapter.isEnabled()) {
                nfcavailable = true;
                writeNFC = true;
                Toast.makeText(AddVisitors_EL201.this, "NFC Enabled" +"\n"+ "NFC Available: "+nfcavailable
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

        OrganizationPath = functionCalls.filepath("Textfile") + File.separator + "Organization.txt";
        HeaderPath = functionCalls.filepath("Textfile") + File.separator + "Header.txt";
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

        MobileNoSuggestThread();

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
                        Toast.makeText(AddVisitors_EL201.this, "Please take Visitors Photo", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        if (BuildManu.equals("LS888")) {
            printerController.PrinterController_Close();
        }
        functionCalls.deleteTextfile("Organization.txt");
        functionCalls.deleteTextfile("Header.txt");
        functionCalls.deleteTextfile("Data.txt");
        super.onDestroy();
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
                                if (!settings.getString("UpdateData", "").equals("Running")) {
                                    Log.d("debug", "Service Started");
                                    Intent intent = new Intent(AddVisitors_EL201.this, Updatedata.class);
                                    startService(intent);
                                }
                                if (BuildManu.equals("LS888")) {
                                    PrintingData();
                                } else {
                                    showdialog(END_DLG);
                                    showToast("This Device is not suitable to get print details..");
                                }
                            } else {
                                Toast.makeText(AddVisitors_EL201.this, "Please take a Photo of Visitor", Toast.LENGTH_SHORT).show();
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
        try {
            dialog = ProgressDialog.show(AddVisitors_EL201.this, "", "Updating file...", true);
            Log.d("debug", "Saving Text");
            dataPrinting.SaveOrganization(OrganizationName);
            dataPrinting.SaveHeader();
            SaveData();
            Log.d("debug", "Printing Header");
            printerController.PrinterController_PrinterLanguage(0);
            printerController.PrinterController_Font_Times();
            printerController.PrinterController_Set_Center();
            printString(OrganizationPath);
            printerController.PrinterController_Font_Normal_mode();
            printString(HeaderPath);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("debug", "Printing Image");
                    imageprinting = true;
                    Bitmap actualbitmap = ((BitmapDrawable) photo_img.getDrawable()).getBitmap();
                    int width=256;
                    int height=192;
                    Bitmap bitMap = Bitmap.createScaledBitmap(actualbitmap, width, height, true);
                    Bitmap bb = ImageProcessing.bitMaptoGrayscale(bitMap);
                    Bitmap bitmap = ImageProcessing.convertGreyImgByFloyd(bb);
                    printerController.PrinterController_Bitmap(bitmap);
                }
            }, 500);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("debug", "Printing BarCode");
                    if (imageprinting) {
                        imageprinting = false;
                    }
                    barcodeprinting = true;
                    new Thread() {
                        public void run() {
                            stringtocode(BarCodeValue);
                        };
                    }.start();
                }
            }, 2000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("debug", "Printing Header");
                    if (barcodeprinting) {
                        barcodeprinting = false;
                    }
                    printerController.PrinterController_PrinterLanguage(0);
                    printerController.PrinterController_Set_Left();
                    printerController.PrinterController_Font_Normal_mode();
                    printString(DataPath);
                    printerController.PrinterController_Take_The_Paper(1);
                }
            }, 3500);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    showdialog(END_DLG);
                }
            }, 5000);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(AddVisitors_EL201.this, "Error", Toast.LENGTH_SHORT).show();
        }
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
                        AddVisitors_EL201.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.
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
                            editor.putString("OTP", ""+num);
                            editor.commit();
                            SMSOTP smsotp = task.new SMSOTP("91"+Mobile, settings.getString("OTP", ""));
                            smsotp.execute();
                            showdialog(OTP_DLG);
                            showToast("OTP Sent");
                        } else if (manualcheck) {
                            manualcheck = false;
                            AddVisitors_EL201.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.
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
            /*options.inSampleSize = 8;*/

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);
            photo_img.setImageBitmap(rotateImage(bitmap, fileUri.getPath()));
            LogStatus("Image Size: "+sizeOf(bitmap));
            UpdateVisitorImage = "Yes";
            Visitorsimage = true;
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);
            photo_img.setImageBitmap(rotateImage(bitmap, fileUri.getPath()));
            LogStatus("Image Size: "+sizeOf(bitmap));
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
        Log.d("debug", ""+orientation);
        if (orientation == 1) {
            /*bmp = src;*/
            matrix.postRotate(270);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
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

    public void SaveData() {

        String path = functionCalls.filepath("Textfile");
        String filename = "Data.txt";
        try {
            File f = new File(path + File.separator + filename);
            FileOutputStream fOut = new FileOutputStream(f);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

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
                    if (Display.equals("Email")) {
                        myOutWriter.append(Email + "\r\n");
                        /*printString(Display+": "+Email+"\n");*/
                    }
                    if (Display.equals("Vehicle Number")) {
                        myOutWriter.append(Vehicleno + "\r\n");
                        /*printString(Display+": "+Vehicleno+"\n");*/
                    }
                }
            }
            myOutWriter.append(" " + "\r\n");
            myOutWriter.append(" "+"\r\n");
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
                            showdialog(NFC_DLG);
                        } else {
                            finish();
                        }
                    }
                });
                endbuilder.setNegativeButton("REPRINT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (BuildManu.equals("LS888")) {
                            reprint = true;
                            PrintingData();
                        } else {
                            showdialog(END_DLG);
                            showToast("This Device is not suitable to get print details..");
                        }
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
                        AddVisitors_EL201.this);
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
        Picasso.with(AddVisitors_EL201.this).load(Image_Path).error(R.drawable.blankperson).into(photo_img);
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
        Toast.makeText(AddVisitors_EL201.this, message, Toast.LENGTH_SHORT).show();
    }

    public void printString(String str) {
        String s1 = null;
        try {
            Log.d("debug", "Send Data Initialzing");
            //Read and Display from text file and print
            File myFile = new File(str);
            Scanner reader = new Scanner(myFile);
            while (reader.hasNextLine()) {
                Log.d("debug", "OutputStream Started");
                String s = reader.nextLine();
                s1 = s + "\n";
                Log.d("debug", s1);
                printerController.PrinterController_Print(s1.getBytes());
            }
        } catch (IndexOutOfBoundsException e) {

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stringtocode(String value) {
        try {
            printerController.PrinterController_PrinterLanguage(2);
            Bitmap btm = Create2DCode(value);
            if (0 == printerController.Write_Command(decodeBitmap(btm)))
                System.out.println("printerController.Write_Commandok");
            else {
                System.out.println("printerController.Write_Commandno");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap Create2DCode(String str) throws WriterException {

        BitMatrix matrix = null;
        try {
            if ("T001(Q)".equals(MachineVersion.getMachineVersion())){
                System.out.println("508");
                matrix = new MultiFormatWriter().encode(new String(str.getBytes("GBK"),"ISO-8859-1"),
                        BarcodeFormat.QR_CODE, 200, 200);
            }else {
                System.out.println("762");
                matrix = new MultiFormatWriter().encode(new String(str.getBytes("GB2312"),"ISO-8859-1"),
                        BarcodeFormat.QR_CODE, 200, 200);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private byte[] decodeBitmap(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<String>(); // binaryString list
        StringBuffer sb;

        @SuppressWarnings("unused")
        int bitLen = bmpWidth / 8;
        int zeroCount = bmpWidth % 8;
        String zeroStr = "";
        if (zeroCount > 0) {
            bitLen = bmpWidth / 8 + 1;
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr = zeroStr + "0";
            }
        }
        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);

                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;

                if (r > 160 && g > 160 && b > 160)
                    sb.append("0");
                else
                    sb.append("1");
            }
            if (zeroCount > 0) {
                sb.append(zeroStr);
            }
            list.add(sb.toString());
        }
        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8
                        : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<String>();
        commandList.add(commandHexString + widthHexString + heightHexString);
        commandList.addAll(bmpHexList);
        byte[] bytes = hexList2Byte(commandList);
        return bytes;
    }

    private byte[] hexList2Byte(List<String> list) {

        List<byte[]> commandList = new ArrayList<byte[]>();

        for (String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        byte[] bytes = sysCopy(commandList);
        return bytes;
    }

    private byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    private List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<String>();
        for (String binaryStr : list) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);
                String hexString = myBinaryStrToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;
    }

    private String hexStr = "0123456789ABCDEF";
    private String[] binaryArray = { "0000", "0001", "0010", "0011", "0100",
            "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100",
            "1101", "1110", "1111" };

    private String myBinaryStrToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }

    private byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogStatus("OnResume NFCAvailable: "+nfcavailable);
        if (nfcavailable) {
            enableForegroundDispatchSystem();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Toast.makeText(AddVisitors_EL201.this, "Smart Card Intent", Toast.LENGTH_SHORT).show();
            if (!writeNFC) {
                Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if (parcelables != null && parcelables.length > 0) {
                    readTextFromMessage((NdefMessage) parcelables[0]);
                } else {
                    Toast.makeText(AddVisitors_EL201.this, "No Ndef Message Found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                NdefMessage ndefMessage = createNdefMessage(BarCodeValue);

                writeNdefMessage(tag, ndefMessage);
            }
        }
    }

    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, AddVisitors_EL201.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[] {};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private NdefMessage createNdefMessage(String content) {
        NdefRecord ndefRecord = createTextRecord(content);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ ndefRecord });
        return ndefMessage;
    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {
        try {
            if (tag == null) {
                Toast.makeText(AddVisitors_EL201.this, "Tag Object cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                formatTag(tag, ndefMessage);
            } else {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(AddVisitors_EL201.this, "Tag is not writable", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }
                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                Toast.makeText(AddVisitors_EL201.this, "Tag written", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {

        }
    }

    private NdefRecord createTextRecord(String content) {
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) languageSize & 0x1F);
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());
        } catch (UnsupportedEncodingException e) {

        }
        return null;
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if (ndefFormatable == null) {
                Toast.makeText(AddVisitors_EL201.this, "This is not ndef formatable", Toast.LENGTH_SHORT).show();
                return;
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();
            Toast.makeText(AddVisitors_EL201.this, "Tag written", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }

    public String getTextfromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1, payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {

        }
        return tagContent;
    }

    private void readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];
            String tagcontent = getTextfromNdefRecord(ndefRecord);
            Toast.makeText(AddVisitors_EL201.this, tagcontent, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(AddVisitors_EL201.this, "No Ndef Records Found", Toast.LENGTH_SHORT).show();
        }
    }
}
