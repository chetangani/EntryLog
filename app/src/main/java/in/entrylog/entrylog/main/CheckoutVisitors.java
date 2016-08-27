package in.entrylog.entrylog.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

//import me.dm7.barcodescanner.zxing.ZXingScannerView;

import in.entrylog.entrylog.dataposting.ConnectingTask;
import in.entrylog.entrylog.values.DetailsValue;
import in.entrylog.entrylog.dataposting.ConnectingTask.VisitorsCheckOut;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class CheckoutVisitors extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    DetailsValue detailsValue;
    ConnectingTask task;
    String OrganizationID, SecurityID;
    Thread mythread;
    ProgressDialog checkoutdialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();

        detailsValue = new DetailsValue();
        task = new ConnectingTask();

        Intent intent = getIntent();
        Bundle bnd = intent.getExtras();
        OrganizationID = bnd.getString("ID");
        SecurityID = bnd.getString("GuardID");
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();   // Stop camera on pause
    }

    @Override
    public void handleResult(final Result result) {
        // show the scanner result into dialog box.
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setMessage(result.getText().toString());
        builder.setPositiveButton("CHECK OUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VisitorsCheckOut checkOut = task.new VisitorsCheckOut(detailsValue, result.getText().toString(),
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
        alert1.show();*/
        VisitorsCheckOut checkOut = task.new VisitorsCheckOut(detailsValue, result.getText().toString(),
                OrganizationID, SecurityID);
        checkOut.execute();
        checkoutdialog = ProgressDialog.show(CheckoutVisitors.this, "", "Checking Out...", true);
        mythread = null;
        Runnable runnable = new DisplayTimer();
        mythread = new Thread(runnable);
        mythread.start();
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

    @Override
    protected void onDestroy() {
        if (mythread.isAlive()) {
            mythread.interrupt();
        }
        super.onDestroy();
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
}
