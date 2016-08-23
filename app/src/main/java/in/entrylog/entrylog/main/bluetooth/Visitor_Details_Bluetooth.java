package in.entrylog.entrylog.main.bluetooth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import in.entrylog.entrylog.R;
import in.entrylog.entrylog.dataposting.ConnectingTask;
import in.entrylog.entrylog.dataposting.ConnectingTask.VisitorManualCheckout;
import in.entrylog.entrylog.main.services.PrintingService;
import in.entrylog.entrylog.myprinter.BTPrinting;
import in.entrylog.entrylog.myprinter.Global;
import in.entrylog.entrylog.myprinter.WorkService;
import in.entrylog.entrylog.values.DetailsValue;
import in.entrylog.entrylog.values.FunctionCalls;

public class Visitor_Details_Bluetooth extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final int END_DLG = 6;
    private static Handler mHandler = null;
    LinearLayout maincontent, checkoutlayout, checkoutuserlayout;
    CoordinatorLayout coordinatorLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String Visitor_Name, Visitor_Mobile, Visitor_Fromaddress, Visitor_ToMeet, Visitor_CheckinTime, Visitor_CheckoutTime,
            Visitor_VehicleNo, Visitor_EntryGate, Visitor_Photo, ContextView, Visitor_id, Organization_ID, CheckingUser, DataPath,
            OrganizationName, BarCodeValue, CheckinUser="", CheckoutUser="", Visitor_Designation, Department, Purpose,
            House_number, Flat_number, Block, No_Visitor, aClass, Section, Student_Name, ID_Card;
    ImageView Visitor_image;
    TextView tv_name, tv_mobile, tv_address, tv_tomeet, tv_checkintime, tv_checkouttime, tv_vehicleno, tv_entry, tv_exit;
    Button Checkout_btn, Print_btn;
    ConnectingTask task;
    DetailsValue detailsValue;
    FunctionCalls functionCalls;
    ProgressDialog dialog = null;
    Thread checkingoutthread, bluetooththread, scanningthread;
    static BluetoothAdapter mBluetoothAdapter;
    static ArrayList<String> arrayListpaired;
    static ArrayList<BluetoothDevice> arrayListPairedBluetoothDevices;
    static boolean btconnected = false, deviceconnected = false, devicenotconnected = false;
    boolean connetedsocket = false, submitpressed = false, devicefound = false, scanningstarted = false,
            connectingdevice = false, devicenamenotfound = false, pairingstarted = false, scanningregistered = false;
    BroadcastReceiver mReceiver, mPairing;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    SharedPreferences settings;
    IntentFilter scanningdevice;
    PrintingService printingService;
    static ArrayList<String> printingorder, printingdisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int screenSize1 = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        int rotation1 = this.getWindowManager().getDefaultDisplay().getRotation();
        switch(screenSize1) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                setTheme(R.style.AppTheme);
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                switch (rotation1) {
                    case Surface.ROTATION_0:
                        setTheme(R.style.MyTheme);
                        break;
                    case Surface.ROTATION_90:
                        setTheme(R.style.AppTheme);
                        break;
                    case Surface.ROTATION_270:
                        setTheme(R.style.AppTheme);
                        break;
                }
                break;
        }
        setContentView(R.layout.activity_visitor_details);

        task = new ConnectingTask();
        detailsValue = new DetailsValue();
        functionCalls = new FunctionCalls();
        printingService = new PrintingService();

        functionCalls.OrientationView(Visitor_Details_Bluetooth.this);

        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        Intent intent = getIntent();
        Bundle bnd = intent.getExtras();
        ContextView = bnd.getString("View");
        Visitor_Photo = bnd.getString("Image");
        Organization_ID = bnd.getString("OrganizationID");
        Visitor_id = bnd.getString("VisitorID");
        Visitor_Name = bnd.getString("Name");
        Visitor_Mobile = bnd.getString("Mobile");
        Visitor_Fromaddress = bnd.getString("From");
        Visitor_ToMeet = bnd.getString("ToMeet");
        Visitor_CheckinTime = bnd.getString("CheckinTime");
        Visitor_CheckoutTime = bnd.getString("CheckoutTime");
        BarCodeValue = bnd.getString("BarCode");
        Visitor_VehicleNo = bnd.getString("VehicleNo");
        Visitor_EntryGate = bnd.getString("Entry");
        CheckingUser = bnd.getString("CheckingUser");
        CheckinUser = bnd.getString("CheckinUser");
        CheckoutUser = bnd.getString("CheckoutUser");
        if (CheckoutUser.equals("null")) {
            CheckoutUser = "";
        }
        Visitor_Designation = bnd.getString("visitor_designation");
        Department = bnd.getString("department");
        Purpose = bnd.getString("purpose");
        House_number = bnd.getString("house_number");
        Flat_number = bnd.getString("flat_number");
        Block = bnd.getString("block");
        No_Visitor = bnd.getString("no_visitor");
        aClass = bnd.getString("class");
        Section = bnd.getString("section");
        Student_Name = bnd.getString("student_name");
        ID_Card = bnd.getString("id_card_number");

        checkoutlayout = (LinearLayout) findViewById(R.id.detailscheckoutdatelayout);
        checkoutuserlayout = (LinearLayout) findViewById(R.id.exitgate_layout);

        tv_name = (TextView) findViewById(R.id.visitor_name);
        tv_mobile = (TextView) findViewById(R.id.visitor_mobile);
        tv_address = (TextView) findViewById(R.id.visitor_fromaddress);
        tv_tomeet = (TextView) findViewById(R.id.visitor_tomeet);
        tv_checkintime = (TextView) findViewById(R.id.visitorcheckin_date);
        tv_checkouttime = (TextView) findViewById(R.id.visitorcheckout_date);
        tv_vehicleno = (TextView) findViewById(R.id.visitor_vehicleno);
        tv_entry = (TextView) findViewById(R.id.entry_gate);
        tv_exit = (TextView) findViewById(R.id.exit_gate);

        Checkout_btn = (Button) findViewById(R.id.checkout_btn);
        Print_btn = (Button) findViewById(R.id.detailsprint_btn);

        Visitor_image = (ImageView) findViewById(R.id.visitor_image);

        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                switch (rotation) {
                    case Surface.ROTATION_0:
                        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_content);
                        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
                        collapsingToolbarLayout.setTitle(Visitor_Name);
                        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
                        break;
                    case Surface.ROTATION_90:
                        maincontent = (LinearLayout) findViewById(R.id.main_content);
                        break;
                    case Surface.ROTATION_270:
                        maincontent = (LinearLayout) findViewById(R.id.main_content);
                        break;
                }
                break;
        }

        if (ContextView.equals("Manually Checkout")) {
            Checkout_btn.setVisibility(View.VISIBLE);
            checkoutlayout.setVisibility(View.GONE);
        } else if (ContextView.equals("Visitors")) {
            Print_btn.setVisibility(View.VISIBLE);
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mBluetoothAdapter.enable();
            arrayListpaired = new ArrayList<String>();
            arrayListPairedBluetoothDevices = new ArrayList<BluetoothDevice>();

            mHandler = new MHandler(this);
            WorkService.addHandler(mHandler);

            if (null == WorkService.workThread) {
                Log.d("debug", "Starting Work Service");
                Intent workintent = new Intent(this, WorkService.class);
                startService(workintent);
            }

            DataPath = functionCalls.filepath("Textfile") + File.separator + "Data.txt";

            OrganizationName = settings.getString("OrganizationName", "");

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
        } else {
        }

        Picasso.with(Visitor_Details_Bluetooth.this).load(Visitor_Photo).error(R.drawable.blankperson).into(Visitor_image);
        tv_name.setText(Visitor_Name);
        tv_mobile.setText(Visitor_Mobile);
        tv_address.setText(Visitor_Fromaddress);
        tv_tomeet.setText(Visitor_ToMeet);
        tv_checkintime.setText(Visitor_CheckinTime);
        tv_checkouttime.setText(Visitor_CheckoutTime);
        tv_vehicleno.setText(Visitor_VehicleNo);
        tv_entry.setText(CheckinUser);
        tv_exit.setText(CheckoutUser);

        Checkout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VisitorManualCheckout visitorManualCheckout = task.new VisitorManualCheckout(detailsValue, Organization_ID,
                        Visitor_id, CheckingUser, Visitor_Details_Bluetooth.this);
                dialog = ProgressDialog.show(Visitor_Details_Bluetooth.this, "", "Checking Out Please wait...", true);
                checkingoutthread = null;
                Runnable runnable = new TestCheckOut();
                checkingoutthread = new Thread(runnable);
                checkingoutthread.start();
                visitorManualCheckout.execute();
            }
        });
        
        Print_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btconnected) {
                    PrintData();
                } else {
                    submitpressed = true;
                    Toast.makeText(Visitor_Details_Bluetooth.this, "Please turn ON bluetooth device", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void PrintData() {
        dialog = ProgressDialog.show(Visitor_Details_Bluetooth.this, "", "Printing Data...", true);
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
            Toast.makeText(Visitor_Details_Bluetooth.this, "Please turn on Bluetooth Device and connect it..", Toast.LENGTH_SHORT).show();
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
        int i = 0;

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doscanning();
                    i = i + 1;
                    Log.d("debug", "scanning timer count "+i);
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
                            Toast.makeText(Visitor_Details_Bluetooth.this, "Please Switch On Bluetooth Device to Pair",
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
                            Toast.makeText(Visitor_Details_Bluetooth.this, "Please Switch On Device to Scan", Toast.LENGTH_SHORT).show();
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
                    Thread.sleep(1000);
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
                            PrintData();
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    class TestCheckOut implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    dochecking();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    public void dochecking() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (detailsValue.isVisitorsCheckOutSuccess()) {
                        detailsValue.setVisitorsCheckOutSuccess(false);
                        dialog.dismiss();
                        Toast.makeText(Visitor_Details_Bluetooth.this, "Successfully Checked Out", Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else if (detailsValue.isVisitorsCheckOutFailure()) {
                        detailsValue.setVisitorsCheckOutFailure(false);
                        dialog.dismiss();
                        Toast.makeText(Visitor_Details_Bluetooth.this, "CheckOut Failed Please try once again", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    public void getPairedDevices() {
        boolean devicepaired = false;
        Set<BluetoothDevice> pairedDevice = mBluetoothAdapter.getBondedDevices();
        if(pairedDevice.size() > 0)
        {
            try {
                for(BluetoothDevice device : pairedDevice) {
                    arrayListpaired.add(device.getName()+ " " +device.getAddress());
                    arrayListPairedBluetoothDevices.add(device);
                    Log.d("debug", "Already Paired Devices: "+device.getName());
                    if (device.getName().equals("BP-201")) {
                        if (!connectingdevice) {
                            connectingdevice = true;
                            Log.d("debug", "Paired Devices: "+device.getName());
                            WorkService.workThread.connectBt(device.getAddress());
                        }
                        devicepaired = true;
                        break;
                    }
                }if (!devicepaired) {
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
                        try {
                            if (device.getName().equals("BP-201")) {
                                devicefound = true;
                                Log.d("debug", "Pairing Devices Found: " + device.getName());
                                PairDevice(device);
                            }
                        } catch (Exception e) {
                                /*if (device.getAddress().equals("88:68:2E:00:05:9F")) {
                                    devicefound = true;
                                    Log.d("debug", "Pairing Devices Found with Exception");
                                    PairDevice(device);
                                }*/
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
                    PrintImage(Visitor_image);
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
                    if (Display.equals("Name")) {
                        myOutWriter.append(Visitor_Name + "\r\n");
                    }
                    if (Display.equals("Mobile")) {
                        myOutWriter.append(Visitor_Mobile + "\r\n");
                    }
                    if (Display.equals("From")) {
                        myOutWriter.append(Visitor_Fromaddress + "\r\n");
                    }
                    if (Display.equals("To Meet")) {
                        myOutWriter.append(Visitor_ToMeet + "\r\n");
                    }
                    if (Display.equals("Date")) {
                        myOutWriter.append(Visitor_CheckinTime + "\r\n");
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
                    if (Display.equals("ID Card No")) {
                        myOutWriter.append(ID_Card + "\r\n");
                    }
                    if (Display.equals("Entry")) {
                        myOutWriter.append(CheckinUser + "\r\n");
                    }
                }
            }
            myOutWriter.append(" " + "\r\n");
            myOutWriter.append(" " + "\r\n");
            myOutWriter.close();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    static class MHandler extends Handler {

        WeakReference<Visitor_Details_Bluetooth> mActivity;

        MHandler(Visitor_Details_Bluetooth activity) {
            mActivity = new WeakReference<Visitor_Details_Bluetooth>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Visitor_Details_Bluetooth theActivity = mActivity.get();
            switch (msg.what) {

                case Global.CMD_POS_SETQRCODERESULT: {
                    int result = msg.arg1;
                    Toast.makeText(theActivity, (result == 1) ? Global.toast_success : Global.toast_fail,
                            Toast.LENGTH_SHORT).show();
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

    private void showdialog(int id) {
        switch (id) {
            case END_DLG:
                AlertDialog.Builder endbuilder = new AlertDialog.Builder(this);
                endbuilder.setTitle("Printing Details");
                endbuilder.setCancelable(false);
                endbuilder.setMessage("Did a Data got a printed correctly...??");
                endbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                endbuilder.setNegativeButton("REPRINT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PrintData();
                    }
                });
                AlertDialog endalert = endbuilder.create();
                endalert.show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (ContextView.equals("Visitors")) {
            if (bluetooththread.isAlive()) {
                bluetooththread.interrupt();
            }
            if (scanningthread.isAlive()) {
                scanningthread.interrupt();
            }
            mBluetoothAdapter.disable();
            if (scanningregistered) {
                this.unregisterReceiver(mReceiver);
            }
            if (pairingstarted) {
                this.unregisterReceiver(mPairing);
            }
            functionCalls.deleteTextfile("Data.txt");
        }
        super.onDestroy();
    }
}