package in.entrylog.entrylog.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.entrylog.entrylog.R;
import in.entrylog.entrylog.main.CustomVolleyRequest;
import in.entrylog.entrylog.main.bluetooth.Visitor_Details_Bluetooth;
import in.entrylog.entrylog.main.el101_102.Visitor_Details_EL101;
import in.entrylog.entrylog.main.el201.Visitor_Details_EL201;
import in.entrylog.entrylog.values.DetailsValue;
import in.entrylog.entrylog.values.FunctionCalls;

/**
 * Created by Admin on 14-Jun-16.
 */
public class VisitorsAdapters extends RecyclerView.Adapter<VisitorsAdapters.VisitorsViewHolder> {

    private static final int REQUEST_FOR_ACTIVITY_CODE = 1;
    ArrayList<DetailsValue> arrayList = new ArrayList<DetailsValue>();
    Context context;
    String ContextView, Organization_ID, CheckingUser, Device, PrinterType;
    FunctionCalls functionCalls = new FunctionCalls();

    public VisitorsAdapters(ArrayList<DetailsValue> arrayList, Context context, String contextview, String organization_ID,
                            String checkingUser, String device, String printertype) {
        this.arrayList = arrayList;
        this.context = context;
        this.ContextView = contextview;
        this.Organization_ID = organization_ID;
        this.CheckingUser = checkingUser;
        this.Device = device;
        this.PrinterType = printertype;
    }

    @Override
    public VisitorsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.visitors_stay_cardview, parent, false);
        VisitorsViewHolder viewHolder = new VisitorsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VisitorsViewHolder holder, int position) {
        DetailsValue details = arrayList.get(position);
        /*Picasso.with(context).load(details.getVisitors_Photo()).into(holder.Visitor_Image);*/
        holder.imageLoader.get(details.getVisitors_Photo(), ImageLoader.getImageListener(holder.Visitor_Image,
                R.drawable.blankperson, R.drawable.blankperson));
        holder.Visitor_Image.setImageUrl(details.getVisitors_Photo(), holder.imageLoader);
        holder.tv_visitor_name.setText(details.getVisitors_Name());
        holder.tv_visitor_tomeet.setText(details.getVisitors_tomeet());
        holder.tv_visitor_checkintime.setText(functionCalls.Convertdate(details.getVisitors_CheckInTime()));
        try {
            if (!details.getVisitors_CheckOutTime().equals("")) {
                holder.tv_visitor_checkouttime.setText(functionCalls.Convertdate(details.getVisitors_CheckOutTime()));
            } else {
                holder.tv_visitor_checkouttime.setText("Visitor still not checked out");
            }
        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class VisitorsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        NetworkImageView Visitor_Image;
        ImageLoader imageLoader;
        TextView tv_visitor_name, tv_visitor_tomeet, tv_visitor_checkintime, tv_visitor_checkouttime;

        public VisitorsViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            Visitor_Image = (NetworkImageView) itemView.findViewById(R.id.visitors_card_image);
            imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
            tv_visitor_name = (TextView) itemView.findViewById(R.id.visitor_card_name);
            tv_visitor_tomeet = (TextView) itemView.findViewById(R.id.visitor_card_tomeet);
            tv_visitor_checkintime = (TextView) itemView.findViewById(R.id.visitor_card_checkintime);
            tv_visitor_checkouttime = (TextView) itemView.findViewById(R.id.visitor_card_checkouttime);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            DetailsValue details = arrayList.get(pos);

            String datetime = details.getVisitors_CheckInTime();
            String date = datetime.substring(0, datetime.indexOf(" "));
            String time = datetime.substring(datetime.indexOf(" ")+1);
            String checkouttime = "";
            try {
                if (!details.getVisitors_CheckOutTime().equals("")) {
                    checkouttime = functionCalls.Convertdate(details.getVisitors_CheckOutTime());
                }
            } catch (Exception e) {
            }

            Intent intent = null;
            if (ContextView.equals("Visitors")) {
                if (PrinterType.equals("Bluetooth")) {
                    intent = new Intent(context, Visitor_Details_Bluetooth.class);
                } else if (!PrinterType.equals("Bluetooth")) {
                    if (Device.equals("EL101")) {
                        intent = new Intent(context, Visitor_Details_EL101.class);
                    } else {
                        intent = new Intent(context, Visitor_Details_EL201.class);
                    }
                }
            } else {
                intent = new Intent(context, Visitor_Details_EL101.class);
            }
            intent.putExtra("View", ContextView);
            intent.putExtra("OrganizationID", Organization_ID);
            intent.putExtra("CheckingUser", CheckingUser);
            intent.putExtra("VisitorID", details.getVisitor_ID());
            intent.putExtra("Image", details.getVisitors_Photo());
            intent.putExtra("Name", details.getVisitors_Name());
            intent.putExtra("Mobile", details.getVisitors_Mobile());
            intent.putExtra("Email", details.getVisitors_Email());
            intent.putExtra("VehicleNo", details.getVisitors_VehicleNo());
            intent.putExtra("From", details.getVisitors_Address());
            intent.putExtra("ToMeet", details.getVisitors_tomeet());
            intent.putExtra("BarCode", details.getVisitors_BarCode());
            intent.putExtra("CheckinTime", functionCalls.Convertdate(details.getVisitors_CheckInTime()));
            intent.putExtra("CheckoutTime", checkouttime);
            intent.putExtra("VehicleNo", details.getVisitors_VehicleNo());
            intent.putExtra("Entry", details.getVisitors_CheckInBy());
            intent.putExtra("CheckinUser", details.getCheck_in_User());
            intent.putExtra("CheckoutUser", details.getCheck_out_User());
            intent.putExtra("visitor_designation", details.getVisitor_Designation());
            intent.putExtra("department", details.getDepartment());
            intent.putExtra("purpose", details.getPurpose());
            intent.putExtra("house_number", details.getHouse_number());
            intent.putExtra("flat_number", details.getFlat_number());
            intent.putExtra("block", details.getBlock());
            intent.putExtra("no_visitor", details.getNo_Visitor());
            intent.putExtra("class", details.getaClass());
            intent.putExtra("section", details.getSection());
            intent.putExtra("student_name", details.getStudent_Name());
            intent.putExtra("id_card_number", details.getID_Card());
            /*context.startActivity(intent);*/
            ((Activity) context).startActivityForResult(intent, REQUEST_FOR_ACTIVITY_CODE);
        }
    }
}
