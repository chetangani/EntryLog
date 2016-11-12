package in.entrylog.entrylog.main;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Surface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.entrylog.entrylog.R;
import in.entrylog.entrylog.adapters.VisitorsAdapters;
import in.entrylog.entrylog.dataposting.ConnectingTask;
import in.entrylog.entrylog.dataposting.ConnectingTask.SearchVisitors;
import in.entrylog.entrylog.values.DetailsValue;
import in.entrylog.entrylog.values.FunctionCalls;

public class Search_Details extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final int VISITORS_DLG = 1;

    //region Declaration
    RecyclerView SearchVisitorsView;
    ArrayList<DetailsValue> SearchVisitorsList;
    VisitorsAdapters SearchVisitorsadapter;
    RecyclerView.LayoutManager layoutManager;
    ConnectingTask task;
    DetailsValue detailsValue;
    String Organization_ID, ContextView, CheckingUser, CheckinDate="", CheckoutDate="", SearchName="", SearchEmail="",
            SearchMobile="", SearchTomeet="", SearchVehicle="", Device="", PrinterType;
    LinearLayout SearchVehicle_layout, SearchDetails_layout, SearchDetails2_layout, SearchCheckdate_layout, Searchtomeet_layout,
        SearchMobile_layout, SearchName_layout, SearchCheckin_layout, SearchCheckout_layout;
    TextView tv_CheckinDate, tv_CheckoutDate, tv_VisitorName, tv_VisitorMobile, tv_VisitorMeet, tv_VisitorVehicle;
    SharedPreferences settings;
    Thread visitorsthread;
    FunctionCalls functionCalls;
    static ProgressDialog dialog = null;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_details);

        detailsValue = new DetailsValue();
        task = new ConnectingTask();
        functionCalls = new FunctionCalls();

        functionCalls.OrientationView(Search_Details.this);

        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        //region Get Intent Results
        Intent intent = getIntent();
        Bundle bnd = intent.getExtras();
        Organization_ID = bnd.getString("ID");
        ContextView = bnd.getString("VIEW");
        CheckingUser = bnd.getString("User");
        CheckinDate = bnd.getString("CHECKINDATE");
        CheckoutDate = bnd.getString("CHECKOUTDATE");
        SearchName = bnd.getString("SEARCHNAME");
        SearchEmail = bnd.getString("SEARCHEMAIL");
        SearchMobile = bnd.getString("SEARCHMOBILE");
        SearchTomeet = bnd.getString("SEARCHTOMEET");
        SearchVehicle = bnd.getString("SEARCHVEHICLE");
        Device = settings.getString("Device", "");
        PrinterType = settings.getString("Printertype", "");
        //endregion

        //region Search Linear Layouts Initialization
        SearchVehicle_layout = (LinearLayout) findViewById(R.id.searchvehicle_layout);
        SearchDetails_layout = (LinearLayout) findViewById(R.id.searchdetails_layout);
        SearchDetails2_layout = (LinearLayout) findViewById(R.id.searchdetails2_layout);
        SearchCheckdate_layout = (LinearLayout) findViewById(R.id.searchcheckdate_layout);
        Searchtomeet_layout = (LinearLayout) findViewById(R.id.searchtomeet_layout);
        SearchMobile_layout = (LinearLayout) findViewById(R.id.searchmobile_layout);
        SearchName_layout = (LinearLayout) findViewById(R.id.searchname_layout);
        SearchCheckin_layout = (LinearLayout) findViewById(R.id.searchcheckin_layout);
        SearchCheckout_layout = (LinearLayout) findViewById(R.id.searchcheckout_layout);
        //endregion

        //region Search TextView Initialization
        tv_CheckinDate = (TextView) findViewById(R.id.tv_checkindate);
        tv_CheckoutDate = (TextView) findViewById(R.id.tv_checkoutdate);
        tv_VisitorName = (TextView) findViewById(R.id.tv_visitorname);
        tv_VisitorMobile = (TextView) findViewById(R.id.tv_visitormobile);
        tv_VisitorMeet = (TextView) findViewById(R.id.tv_visitormeet);
        tv_VisitorVehicle = (TextView) findViewById(R.id.tv_visitorvehicle);
        //endregion

        SearchVisibility();

        //region RecyclerView with Adapter
        StaggeredRotationChanged();
        SearchVisitorsView = (RecyclerView) findViewById(R.id.searchvisitorsview);
        SearchVisitorsList = new ArrayList<DetailsValue>();
        SearchVisitorsadapter = new VisitorsAdapters(SearchVisitorsList, Search_Details.this, ContextView,
                Organization_ID, CheckingUser, Device, PrinterType);

        SearchVisitorsView.setHasFixedSize(true);
        SearchVisitorsView.setLayoutManager(layoutManager);
        SearchVisitorsView.setAdapter(SearchVisitorsadapter);
        //endregion

        if (ContextView.equals("Visitors")) {
            SearchVisitors searchvisitors = task.new SearchVisitors(SearchVisitorsList, SearchVisitorsadapter, detailsValue,
                    Organization_ID, convertdate(CheckinDate), convertdate(CheckoutDate), SearchName, SearchMobile,
                    SearchEmail, SearchTomeet, Search_Details.this, SearchVehicle, "");
            searchvisitors.execute();
            dialog = ProgressDialog.show(Search_Details.this, "", "Searching for a visitors..", true);
            visitorsthread = null;
            Runnable runnable = new VisitorsTimer();
            visitorsthread = new Thread(runnable);
            visitorsthread.start();
        } else {
            SearchVisitors searchvisitors = task.new SearchVisitors(SearchVisitorsList, SearchVisitorsadapter, detailsValue,
                    Organization_ID, convertdate(CheckinDate), convertdate(CheckoutDate), SearchName, SearchMobile,
                    SearchEmail, SearchTomeet, Search_Details.this, SearchVehicle, "1");
            searchvisitors.execute();
            dialog = ProgressDialog.show(Search_Details.this, "", "Searching for a visitors..", true);
            visitorsthread = null;
            Runnable runnable = new VisitorsTimer();
            visitorsthread = new Thread(runnable);
            visitorsthread.start();
        }
    }

    class VisitorsTimer implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    visiting();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    public void visiting() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (detailsValue.isVisitorsFound()) {
                        detailsValue.setVisitorsFound(false);
                        dialog.dismiss();
                        visitorsthread.interrupt();
                    }
                    if (detailsValue.isNoVisitorsFound()) {
                        detailsValue.setNoVisitorsFound(false);
                        dialog.dismiss();
                        visitorsthread.interrupt();
                        showdialog(VISITORS_DLG);
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    private void SearchVisibility() {
        //region Search Visibility Details
        if (!CheckinDate.equals("")) {
            if (SearchCheckdate_layout.getVisibility() != View.VISIBLE) {
                SearchCheckdate_layout.setVisibility(View.VISIBLE);
            }
            if (SearchCheckin_layout.getVisibility() != View.VISIBLE) {
                SearchCheckin_layout.setVisibility(View.VISIBLE);
                tv_CheckinDate.setText(CheckinDate);
            }
        }
        if (!CheckoutDate.equals("")) {
            if (SearchCheckdate_layout.getVisibility() != View.VISIBLE) {
                SearchCheckdate_layout.setVisibility(View.VISIBLE);
            }
            if (SearchCheckout_layout.getVisibility() != View.VISIBLE) {
                SearchCheckout_layout.setVisibility(View.VISIBLE);
                tv_CheckoutDate.setText(CheckoutDate);
            }
        }
        if (!SearchName.equals("")) {
            if (SearchDetails_layout.getVisibility() != View.VISIBLE) {
                SearchDetails_layout.setVisibility(View.VISIBLE);
            }
            if (SearchName_layout.getVisibility() != View.VISIBLE) {
                SearchName_layout.setVisibility(View.VISIBLE);
                tv_VisitorName.setText(SearchName);
            }
        }
        if (!SearchMobile.equals("")) {
            if (SearchDetails_layout.getVisibility() != View.VISIBLE) {
                SearchDetails_layout.setVisibility(View.VISIBLE);
            }
            if (SearchMobile_layout.getVisibility() != View.VISIBLE) {
                SearchMobile_layout.setVisibility(View.VISIBLE);
                tv_VisitorMobile.setText(SearchMobile);
            }
        }
        if (!SearchTomeet.equals("")) {
            if (SearchDetails2_layout.getVisibility() != View.VISIBLE) {
                SearchDetails2_layout.setVisibility(View.VISIBLE);
            }
            if (Searchtomeet_layout.getVisibility() != View.VISIBLE) {
                Searchtomeet_layout.setVisibility(View.VISIBLE);
                tv_VisitorMeet.setText(SearchTomeet);
            }
        }
        if (!SearchVehicle.equals("")) {
            if (SearchDetails2_layout.getVisibility() != View.VISIBLE) {
                SearchDetails2_layout.setVisibility(View.VISIBLE);
            }
            if (SearchVehicle_layout.getVisibility() != View.VISIBLE) {
                SearchVehicle_layout.setVisibility(View.VISIBLE);
                tv_VisitorVehicle.setText(SearchVehicle);
            }
        }
        //endregion
    }

    private void StaggeredRotationChanged() {
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                switch (rotation) {
                    case Surface.ROTATION_0:
                        layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                        break;
                    case Surface.ROTATION_90:
                        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        break;
                    case Surface.ROTATION_270:
                        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        break;
                }
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                break;
            default:
                layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
    }

    private String convertdate(String Date) {
        String date = "";
        if (!Date.equals("")) {
            String date1 = Date.substring(0, 2);
            String date2 = Date.substring(3, 5);
            String date3 = Date.substring(6, 10);
            date = date3 + "-" + date2 + "-" + date1;
        } else {
            date = Date;
        }
        return date;
    }

    protected void showdialog(int id) {
        switch (id) {
            case VISITORS_DLG:
                AlertDialog.Builder novisitors = new AlertDialog.Builder(this);
                novisitors.setTitle("Visitor Details");
                novisitors.setCancelable(false);
                novisitors.setMessage("No Visitors Found to display..");
                novisitors.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                });
                AlertDialog alertdialog = novisitors.create();
                alertdialog.show();
                break;
        }
    }
}
