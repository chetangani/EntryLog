package in.entrylog.entrylog.main;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import java.io.UnsupportedEncodingException;

import in.entrylog.entrylog.dataposting.ConnectingTask;
import in.entrylog.entrylog.dataposting.ConnectingTask.VisitorsCheckOut;
import in.entrylog.entrylog.values.DetailsValue;
import in.entrylog.entrylog.values.FunctionCalls;
import in.entrylog.entrylog.values.SmartCardAdapter;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CheckoutVisitors extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    public static final String PREFS_NAME = "MyPrefsFile";
    private ZXingScannerView mScannerView;
    DetailsValue detailsValue;
    ConnectingTask task;
    String OrganizationID, SecurityID;
    Thread mythread;
    ProgressDialog checkoutdialog = null;
    NfcAdapter nfcAdapter;
    NfcManager nfcManager;
    SharedPreferences settings;
    boolean nfcavailable = false;
    FunctionCalls functionCalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();

        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (settings.getString("RFID", "").equals("true")) {
            nfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
            nfcAdapter = nfcManager.getDefaultAdapter();
            if (nfcAdapter != null && nfcAdapter.isEnabled()) {
                nfcavailable = true;
            } else {
                Toast.makeText(CheckoutVisitors.this, "NFC Enabled but not available in this device",
                        Toast.LENGTH_SHORT).show();
            }
        }

        detailsValue = new DetailsValue();
        task = new ConnectingTask();
        functionCalls = new FunctionCalls();

        OrganizationID = settings.getString("OrganizationID", "");
        SecurityID = settings.getString("GuardID", "");
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();   // Stop camera on pause
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcavailable) {
            enableForegroundDispatchSystem();
        }
    }

    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, CheckoutVisitors.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[] {};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    @Override
    public void handleResult(final Result result) {
        // show the scanner result into dialog box.

        checkingout(result.getText().toString());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Toast.makeText(CheckoutVisitors.this, "Smart Card Intent", Toast.LENGTH_SHORT).show();
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (parcelables != null && parcelables.length > 0) {
                readTextFromMessage((NdefMessage) parcelables[0]);
            } else {
                Toast.makeText(CheckoutVisitors.this, "No Ndef Message Found", Toast.LENGTH_SHORT).show();
            }
            /*SmartCardAdapter smartCardAdapter = new SmartCardAdapter();
            smartCardAdapter.readSmartTag(CheckoutVisitors.this, intent);*/
        }
    }

    private void readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];
            String tagcontent = getTextfromNdefRecord(ndefRecord);
            /*showdialog(tagcontent);*/
            checkingout(tagcontent);
        } else {
            Toast.makeText(CheckoutVisitors.this, "No Ndef Records Found", Toast.LENGTH_SHORT).show();
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

    class DisplayTimer implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    public void doWork() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    String Message = "";
                    String out = "";
                    if (detailsValue.isVisitorsCheckOutSuccess()) {
                        mythread.interrupt();
                        detailsValue.setVisitorsCheckOutSuccess(false);
                        checkoutdialog.dismiss();
                        Message = "Successfully Checked Out";
                        out = "Success";
                        functionCalls.ringtone(CheckoutVisitors.this);
                        createdialog(Message, out);
                    }
                    if (detailsValue.isVisitorsCheckOutFailure()) {
                        mythread.interrupt();
                        detailsValue.setVisitorsCheckOutFailure(false);
                        checkoutdialog.dismiss();
                        Message = "Checked Out Failed";
                        out = "Failure";
                        createdialog(Message, out);
                    }
                    if (detailsValue.isVisitorsCheckOutDone()) {
                        detailsValue.setVisitorsCheckOutDone(false);
                        checkoutdialog.dismiss();
                        Message = "Checked Out Already Done";
                        out = "Done";
                        createdialog(Message, out);
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    private void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    private void createdialog(String Message, String Checkout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutVisitors.this);
        builder.setTitle("CheckOut Result");
        builder.setMessage(Message);
        if (Checkout.equals("Success")) {
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        } else if (Checkout.equals("Failure")) {
            builder.setNeutralButton("ReScan", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reload();
                }
            });
        } else {
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    private void showdialog(final String result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setMessage(result);
        builder.setPositiveButton("CHECK OUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VisitorsCheckOut checkOut = task.new VisitorsCheckOut(detailsValue, result,
                        OrganizationID, SecurityID);
                checkOut.execute();
                checkoutdialog = ProgressDialog.show(CheckoutVisitors.this, "", "Checking Out...", true);
                mythread = null;
                Runnable runnable = new DisplayTimer();
                mythread = new Thread(runnable);
                mythread.start();
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    public void checkingout(String result) {
        VisitorsCheckOut checkOut = task.new VisitorsCheckOut(detailsValue, result,
                OrganizationID, SecurityID);
        checkOut.execute();
        checkoutdialog = ProgressDialog.show(CheckoutVisitors.this, "", "Checking Out...", true);
        mythread = null;
        Runnable runnable = new DisplayTimer();
        mythread = new Thread(runnable);
        mythread.start();
    }
}
