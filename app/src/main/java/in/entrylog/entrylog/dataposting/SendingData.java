package in.entrylog.entrylog.dataposting;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import in.entrylog.entrylog.values.FunctionCalls;

/**
 * Created by Admin on 06-Jun-16.
 */
public class SendingData {

    String BASE_URL = DataAPI.BASE_URL;
    FunctionCalls functionCalls = new FunctionCalls();

    public String PostLogin(String Organization, String Username, String Password) {
        String responsestr = "";
        HashMap<String, String> datamap = new HashMap<>();
        datamap.put("organization_identity", Organization);
        datamap.put("security_guards_username", Username);
        datamap.put("security_guards_password", Password);
        try {
            responsestr = UrlPostConnection("Check_login_api", datamap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responsestr;
    }

    public String Logout(String GuardID) {
        String response = "";
        HashMap<String, String> datamap = new HashMap<>();
        datamap.put("security_guards_id", GuardID);
        datamap.put("is_logged", "0");
        try {
            response = UrlPostConnection("Logout", datamap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String GetFields(String organizationid) {
        String response = "";
        HashMap<String, String> datamap = new HashMap<>();
        datamap.put("organization_id", organizationid);
        try {
            response = UrlPostConnection("Fetch_fields", datamap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String GetStaffs(String organizationid) {
        String response = "";
        HashMap<String, String> datamap = new HashMap<>();
        datamap.put("organization_id", organizationid);
        try {
            response = UrlPostConnection("Staff", datamap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String VisitorsCheckIn(String Name, String Email, String Mobile, String Address, String Tomeet, String VehicleNo,
                                  String ImagefileName, String Organization_ID, String Security_ID, String Barcode,
                                  String visitor_Designation, String department, String purpose, String house_number,
                                  String flat_number, String block, String no_Visitor, String aClass, String section,
                                  String student_Name, String ID_Card, String Visitor_Entry, String Current_Time, String ID_Card_Type) {
        String response = "";
        HashMap<String, String> datamap = new HashMap<>();
        datamap.put("visitors_name", Name);
        datamap.put("visitors_email", Email);
        datamap.put("visitors_mobile", Mobile);
        datamap.put("visitors_address", Address);
        datamap.put("to_meet", Tomeet);
        datamap.put("visitors_vehicle_number", Barcode);
        datamap.put("visitors_photo", VehicleNo);
        datamap.put("organization_id", ImagefileName);
        datamap.put("security_guards_id", Security_ID);
        datamap.put("visitors_bar_code", Organization_ID);
        datamap.put("visitor_designation", visitor_Designation);
        datamap.put("department", department);
        datamap.put("purpose", purpose);
        datamap.put("house_number", house_number);
        datamap.put("flat_number", flat_number);
        datamap.put("block", block);
        datamap.put("no_visitor", no_Visitor);
        datamap.put("class", aClass);
        datamap.put("section", section);
        datamap.put("student_name", student_Name);
        datamap.put("id_card_number", ID_Card);
        datamap.put("verification_status_id", Visitor_Entry);
        datamap.put("checked_in_time", Current_Time);
        datamap.put("id_card_type", ID_Card_Type);
        try {
            response = UrlPostConnection("Check_in_visitors", datamap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String VisitorsCheckOut (String MobileNo, String Organization_id, String Security_id) {
        String response = "";
        HashMap<String, String> datamap = new HashMap<>();
        datamap.put("visitors_bar_code", MobileNo);
        datamap.put("organization_id", Organization_id);
        datamap.put("security_guards_id", Security_id);
        try {
            response = UrlPostConnection("Check_out_visitors", datamap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String CheckVisitors (String Organization_id) {
        String response = "";
        HashMap<String, String> datamap = new HashMap<>();
        datamap.put("organization_id", Organization_id);
        try {
            response = UrlPostConnection("Checked_in_visitors", datamap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String AllVisitors (String Organization_id) {
        String response = "";
        HashMap<String, String> datamap = new HashMap<>();
        datamap.put("organization_id", Organization_id);
        try {
            response = UrlPostConnection("Visitors", datamap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String VisitorManualCheckout(String VisitorID, String OrganizationID, String Checkoutby) {
        String response = "";
        HashMap<String, String> datamap = new HashMap<>();
        datamap.put("visitors_id", VisitorID);
        datamap.put("organization_id", OrganizationID);
        datamap.put("check_out_by", Checkoutby);
        try {
            response = UrlPostConnection("Manual_checkout", datamap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String MobileAutoSuggest (String Organization_id, String Mobile) {
        String response = "";
        HashMap<String, String> datamap = new HashMap<>();
        datamap.put("organization_id", Organization_id);
        datamap.put("visitors_mobile", Mobile);
        try {
            response = UrlPostConnection("Mobile_autosuggest", datamap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String SearchVisitors (String Organization_id, String checkin_date, String checkout_date, String visitor_name,
                                  String visitor_mobile, String visitor_email, String visitor_tomeet, String visitor_vehicleNo,
                                  String User_Status) {
        String response = "";
        HashMap<String, String> datamap = new HashMap<>();
        datamap.put("organization_id", Organization_id);
        datamap.put("check_in_time", checkin_date);
        datamap.put("check_out_time", checkout_date);
        datamap.put("visitors_mobile", visitor_mobile);
        datamap.put("visitors_vehicle_number", visitor_vehicleNo);
        datamap.put("to_meet", visitor_tomeet);
        datamap.put("visitors_name", visitor_name);
        datamap.put("visitors_email", visitor_email);
        datamap.put("check_status_id", User_Status);
        try {
            response = UrlPostConnection("Search_visitors", datamap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /*public String OvernightStay_Visitors (String Organization_id) {
        String response = "";
        HashMap<String, String> datamap = new HashMap<>();
        datamap.put("organization_id", Organization_id);
        try {
            response = UrlPostConnection("Overnight_stay_visitors", datamap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogStatus("OverNightstay: "+response);
        return response;
    }*/

    public String OTPGeneration(String Mobile, String OTP) {
        String response = "";
        HashMap<String, String> datamap = new HashMap<>();
        datamap.put("visitors_mobile", Mobile);
        datamap.put("otp", OTP);
        try {
            response = UrlPostConnection("Send_otp", datamap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String Printable_Fields(String Organization_id) {
        String response = "";
        HashMap<String, String> datamap = new HashMap<>();
        datamap.put("organization_id", Organization_id);
        try {
            response = UrlPostConnection("Printable_fields", datamap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String Permissions(String Organization_id) {
        String response = "";
        HashMap<String, String> datamap = new HashMap<>();
        datamap.put("organization_id", Organization_id);
        try {
            response = UrlPostConnection("Organization_permissions", datamap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String UpdatedApk() {
        String response = "";
        try {
            response = UrlGetConnection("Updated_apk");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String GetTime() {
        String response = "";
        try {
            response = UrlGetConnection("Get_time");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private String UrlPostConnection(String Post_Url, HashMap<String, String> datamap) throws IOException {
        String response = "";
        functionCalls.LogStatus("Connecting URL: "+BASE_URL + Post_Url);
        URL url = new URL(BASE_URL + Post_Url);
        functionCalls.LogStatus("URL Connection 1");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        functionCalls.LogStatus("URL Connection 2");
        conn.setReadTimeout(15000);
        functionCalls.LogStatus("URL Connection 3");
        conn.setConnectTimeout(15000);
        functionCalls.LogStatus("URL Connection 4");
        conn.setRequestMethod("POST");
        functionCalls.LogStatus("URL Connection 5");
        conn.setDoInput(true);
        functionCalls.LogStatus("URL Connection 6");
        conn.setDoOutput(true);
        functionCalls.LogStatus("URL Connection 7");

        OutputStream os = conn.getOutputStream();
        functionCalls.LogStatus("URL Connection 8");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        functionCalls.LogStatus("URL Connection 9");
        writer.write(getPostDataString(datamap));
        functionCalls.LogStatus("URL Connection 10");
        writer.flush();
        functionCalls.LogStatus("URL Connection 11");
        writer.close();
        functionCalls.LogStatus("URL Connection 12");
        os.close();
        functionCalls.LogStatus("URL Connection 13");
        int responseCode=conn.getResponseCode();
        functionCalls.LogStatus("URL Connection 14");
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            functionCalls.LogStatus("URL Connection 15");
            String line;
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            functionCalls.LogStatus("URL Connection 16");
            while ((line=br.readLine()) != null) {
                response+=line;
            }
            functionCalls.LogStatus("URL Connection 17");
        }
        else {
            response="";
            functionCalls.LogStatus("URL Connection 18");
        }
        functionCalls.LogStatus("URL Connection Response: "+response);
        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            functionCalls.LogStatus(result.toString());
        }

        return result.toString();
    }

    private String UrlGetConnection(String Get_Url) throws IOException {
        String response = "";
        functionCalls.LogStatus("Connecting URL: "+BASE_URL + Get_Url);
        URL url = new URL(BASE_URL + Get_Url);
        functionCalls.LogStatus("URL Get Connection 1");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        functionCalls.LogStatus("URL Get Connection 2");
        conn.setReadTimeout(15000);
        functionCalls.LogStatus("URL Get Connection 3");
        conn.setConnectTimeout(15000);
        functionCalls.LogStatus("URL Get Connection 4");
        int responseCode=conn.getResponseCode();
        functionCalls.LogStatus("URL Get Connection 5");
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            functionCalls.LogStatus("URL Get Connection 6");
            String line;
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            functionCalls.LogStatus("URL Get Connection 7");
            while ((line=br.readLine()) != null) {
                response+=line;
            }
            functionCalls.LogStatus("URL Get Connection 8");
        }
        else {
            response="";
            functionCalls.LogStatus("URL Get Connection 9");
        }
        functionCalls.LogStatus("URL Get Connection Response: "+response);
        return response;
    }
}
