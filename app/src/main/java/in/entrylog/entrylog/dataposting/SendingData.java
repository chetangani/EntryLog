package in.entrylog.entrylog.dataposting;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Admin on 06-Jun-16.
 */
public class SendingData {

    String BASE_URL = DataAPI.BASE_URL;

    private void LogStatus(String message){
        Log.d("debug", message);
    }

    public String PostLogin(String Organization, String Username, String Password) {
        String responsestr = "";
        try {
            String postReceiverURL = BASE_URL + "Check_login_api";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(postReceiverURL);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("organization_identity", Organization));
            nameValuePairs.add(new BasicNameValuePair("security_guards_username", Username));
            nameValuePairs.add(new BasicNameValuePair("security_guards_password", Password));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                responsestr = EntityUtils.toString(httpEntity).trim();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responsestr;
    }

    public String Logout(String GuardID) {
        String responsestr = "";
        try {
            String postReceiverURL = BASE_URL + "Logout";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(postReceiverURL);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("security_guards_id", GuardID));
            nameValuePairs.add(new BasicNameValuePair("is_logged", "0"));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                responsestr = EntityUtils.toString(httpEntity).trim();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responsestr;
    }

    public String GetFields(String organizationid) {
        String responsestr = "";
        try {
            String postReceiveURL = BASE_URL + "Fetch_fields";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(postReceiveURL);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("organization_id", organizationid));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                responsestr = EntityUtils.toString(httpEntity).trim();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogStatus("Fields: "+responsestr);
        return responsestr;
    }

    public String GetStaffs(String organizationid) {
        String responsestr = "";
        try {
            String postReceiveURL = BASE_URL + "Staff";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(postReceiveURL);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("organization_id", organizationid));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                responsestr = EntityUtils.toString(httpEntity).trim();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogStatus("Staffs: "+responsestr);
        return responsestr;
    }

    public String VisitorsCheckIn(String Name, String Email, String Mobile, String Address, String Tomeet, String VehicleNo,
                                  String ImagefileName, String Organization_ID, String Security_ID, String Barcode,
                                  String visitor_Designation, String department, String purpose, String house_number,
                                  String flat_number, String block, String no_Visitor, String aClass, String section,
                                  String student_Name, String ID_Card, String Visitor_Entry, String Current_Time) {
        String response = "";
        try {
            LogStatus("1");
            String PostReceiveURL = BASE_URL + "Check_in_visitors";
            LogStatus("2");
            HttpClient httpClient = new DefaultHttpClient();
            LogStatus("3");
            HttpPost httpPost = new HttpPost(PostReceiveURL);
            LogStatus("4");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            LogStatus("5");
            nameValuePairs.add(new BasicNameValuePair("visitors_name", Name));
            LogStatus("6 Name "+Name);
            nameValuePairs.add(new BasicNameValuePair("visitors_email", Email));
            LogStatus("7 Email "+Email);
            nameValuePairs.add(new BasicNameValuePair("visitors_mobile", Mobile));
            LogStatus("8 Mobile "+Mobile);
            nameValuePairs.add(new BasicNameValuePair("visitors_address", Address));
            LogStatus("9 Address "+Address);
            nameValuePairs.add(new BasicNameValuePair("to_meet", Tomeet));
            LogStatus("10 Tomeet "+Tomeet);
            nameValuePairs.add(new BasicNameValuePair("visitors_vehicle_number", Barcode));
            LogStatus("11 VehicleNo "+Barcode);
            nameValuePairs.add(new BasicNameValuePair("visitors_photo", VehicleNo));
            LogStatus("12 ImagefileName "+VehicleNo);
            nameValuePairs.add(new BasicNameValuePair("organization_id", ImagefileName));
            LogStatus("13 Organization_ID "+ImagefileName);
            nameValuePairs.add(new BasicNameValuePair("security_guards_id", Security_ID));
            LogStatus("14 Security_ID "+Security_ID);
            nameValuePairs.add(new BasicNameValuePair("visitors_bar_code", Organization_ID));
            LogStatus("15 Barcode "+Organization_ID);
            nameValuePairs.add(new BasicNameValuePair("visitor_designation", visitor_Designation));
            LogStatus("16 visitor_Designation "+visitor_Designation);
            nameValuePairs.add(new BasicNameValuePair("department", department));
            LogStatus("17 department "+department);
            nameValuePairs.add(new BasicNameValuePair("purpose", purpose));
            LogStatus("18 purpose"+purpose);
            nameValuePairs.add(new BasicNameValuePair("house_number", house_number));
            LogStatus("19 house_number "+house_number);
            nameValuePairs.add(new BasicNameValuePair("flat_number", flat_number));
            LogStatus("20 flat_number "+flat_number);
            nameValuePairs.add(new BasicNameValuePair("block", block));
            LogStatus("21 block "+block);
            nameValuePairs.add(new BasicNameValuePair("no_visitor", no_Visitor));
            LogStatus("22 no_Visitor "+no_Visitor);
            nameValuePairs.add(new BasicNameValuePair("class", aClass));
            LogStatus("23 aClass "+aClass);
            nameValuePairs.add(new BasicNameValuePair("section", section));
            LogStatus("24 section "+section);
            nameValuePairs.add(new BasicNameValuePair("student_name", student_Name));
            LogStatus("25 student_Name "+student_Name);
            nameValuePairs.add(new BasicNameValuePair("id_card_number", ID_Card));
            LogStatus("26 ID_Card "+ID_Card);
            nameValuePairs.add(new BasicNameValuePair("verification_status_id", Visitor_Entry));
            LogStatus("27 Visitor_Entry "+Visitor_Entry);
            nameValuePairs.add(new BasicNameValuePair("checked_in_time", Current_Time));
            LogStatus("27 Current_Time "+Current_Time);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            LogStatus("28");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            LogStatus("29");
            HttpEntity httpEntity = httpResponse.getEntity();
            LogStatus("30");
            if (httpEntity != null) {
                response = EntityUtils.toString(httpEntity).trim();
                LogStatus("31");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogStatus(response);
        return response;
    }

    public String VisitorsCheckOut (String MobileNo, String Organization_id, String Security_id) {
        String response = "";
        try {
            String PostReceiveURL = BASE_URL + "Check_out_visitors";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(PostReceiveURL);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("visitors_bar_code", MobileNo));
            nameValuePairs.add(new BasicNameValuePair("organization_id", Organization_id));
            nameValuePairs.add(new BasicNameValuePair("security_guards_id", Security_id));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                response = EntityUtils.toString(httpEntity).trim();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String CheckVisitors (String Organization_id) {
        String response = "";
        try {
            String PostReceiveURL = BASE_URL + "Checked_in_visitors";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(PostReceiveURL);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("organization_id", Organization_id));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                response = EntityUtils.toString(httpEntity).trim();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String AllVisitors (String Organization_id) {
        String response = "";
        try {
            String PostReceiveURL = BASE_URL + "Visitors";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(PostReceiveURL);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("organization_id", Organization_id));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                response = EntityUtils.toString(httpEntity).trim();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String VisitorManualCheckout(String VisitorID, String OrganizationID, String Checkoutby) {
        String response = "";
        try {
            String PostReceiveURL = BASE_URL + "Manual_checkout";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(PostReceiveURL);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("visitors_id", VisitorID));
            nameValuePairs.add(new BasicNameValuePair("organization_id", OrganizationID));
            nameValuePairs.add(new BasicNameValuePair("check_out_by", Checkoutby));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                response = EntityUtils.toString(httpEntity).trim();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String MobileAutoSuggest (String Organization_id, String Mobile) {
        String response = "";
        try {
            String PostReceiveURL = BASE_URL + "Mobile_autosuggest";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(PostReceiveURL);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("organization_id", Organization_id));
            LogStatus("organization_id: "+Organization_id);
            nameValuePairs.add(new BasicNameValuePair("visitors_mobile", Mobile));
            LogStatus("visitors_mobile: "+Mobile);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                response = EntityUtils.toString(httpEntity).trim();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogStatus("MobileAuto: "+response);
        return response;
    }

    public String SearchVisitors (String Organization_id, String checkin_date, String checkout_date, String visitor_name,
                                  String visitor_mobile, String visitor_email, String visitor_tomeet, String visitor_vehicleNo,
                                  String User_Status) {
        String response = "";
        try {
            String PostReceiveURL = BASE_URL + "Search_visitors";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(PostReceiveURL);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("organization_id", Organization_id));
            nameValuePairs.add(new BasicNameValuePair("check_in_time", checkin_date));
            nameValuePairs.add(new BasicNameValuePair("check_out_time", checkout_date));
            nameValuePairs.add(new BasicNameValuePair("visitors_mobile", visitor_mobile));
            nameValuePairs.add(new BasicNameValuePair("visitors_vehicle_number", visitor_vehicleNo));
            nameValuePairs.add(new BasicNameValuePair("to_meet", visitor_tomeet));
            nameValuePairs.add(new BasicNameValuePair("visitors_name", visitor_name));
            nameValuePairs.add(new BasicNameValuePair("visitors_email", visitor_email));
            nameValuePairs.add(new BasicNameValuePair("check_status_id", User_Status));
            Log.d("debug", "status: "+User_Status);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                response = EntityUtils.toString(httpEntity).trim();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String OvernightStay_Visitors (String Organization_id) {
        String response = "";
        try {
            String PostReceiveURL = BASE_URL + "Overnight_stay_visitors";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(PostReceiveURL);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("organization_id", Organization_id));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                response = EntityUtils.toString(httpEntity).trim();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("debug", "OverNightstay: "+response);
        return response;
    }

    public String OTPGeneration(String Mobile, String OTP) {
        String response = "";
        try {
            String PostReceiveURL = "http://www.askdial.com/index.php/admin/" + "Send_otp";
            Log.d("debug", PostReceiveURL);
            HttpClient httpClient = new DefaultHttpClient();
            Log.d("debug", "1");
            HttpPost httpPost = new HttpPost(PostReceiveURL);
            Log.d("debug", "2");
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            Log.d("debug", "3");
            nameValuePairs.add(new BasicNameValuePair("visitors_mobile", Mobile));
            Log.d("debug", Mobile);
            nameValuePairs.add(new BasicNameValuePair("otp", OTP));
            Log.d("debug", OTP);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            Log.d("debug", "4");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.d("debug", "5");
            HttpEntity httpEntity = httpResponse.getEntity();
            Log.d("debug", "6");
            if (httpEntity != null) {
                response = EntityUtils.toString(httpEntity).trim();
                Log.d("debug", "7");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("debug", "8");
        Log.d("debug", response);
        return response;
    }

    public String Printable_Fields(String Organization_id) {
        String response = "";
        try {
            String PostReceiveURL = BASE_URL + "Printable_fields";
            Log.d("debug", PostReceiveURL);
            HttpClient httpClient = new DefaultHttpClient();
            Log.d("debug", "1");
            HttpPost httpPost = new HttpPost(PostReceiveURL);
            Log.d("debug", "2");
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            Log.d("debug", "3");
            nameValuePairs.add(new BasicNameValuePair("organization_id", Organization_id));
            Log.d("debug", Organization_id);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            Log.d("debug", "4");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.d("debug", "5");
            HttpEntity httpEntity = httpResponse.getEntity();
            Log.d("debug", "6");
            if (httpEntity != null) {
                response = EntityUtils.toString(httpEntity).trim();
                Log.d("debug", "7");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("debug", "8");
        Log.d("debug", "Response: "+response);
        return response;
    }

    public String Permissions(String Organization_id) {
        String response = "";
        try {
            String PostReceiveURL = BASE_URL + "Organization_permissions";
            Log.d("debug", PostReceiveURL);
            HttpClient httpClient = new DefaultHttpClient();
            Log.d("debug", "1");
            HttpPost httpPost = new HttpPost(PostReceiveURL);
            Log.d("debug", "2");
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            Log.d("debug", "3");
            nameValuePairs.add(new BasicNameValuePair("organization_id", Organization_id));
            Log.d("debug", Organization_id);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            Log.d("debug", "4");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.d("debug", "5");
            HttpEntity httpEntity = httpResponse.getEntity();
            Log.d("debug", "6");
            if (httpEntity != null) {
                response = EntityUtils.toString(httpEntity).trim();
                Log.d("debug", "7");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("debug", "8");
        Log.d("debug", "Response: "+response);
        return response;
    }

    public String UpdatedApk() {
        String response = "";
        try {
            String PostReceiveURL = BASE_URL + "Updated_apk";
            Log.d("debug", PostReceiveURL);
            HttpClient httpClient = new DefaultHttpClient();
            Log.d("debug", "1");
            HttpPost httpPost = new HttpPost(PostReceiveURL);
            Log.d("debug", "2");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.d("debug", "3");
            HttpEntity httpEntity = httpResponse.getEntity();
            Log.d("debug", "4");
            if (httpEntity != null) {
                response = EntityUtils.toString(httpEntity).trim();
                Log.d("debug", "5");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("debug", "6");
        Log.d("debug", "Response: "+response);
        return response;
    }

    public String GetTime() {
        String response = "";
        try {
            String PostReceiveURL = BASE_URL + "Get_time";
            Log.d("debug", PostReceiveURL);
            HttpClient httpClient = new DefaultHttpClient();
            Log.d("debug", "1");
            HttpPost httpPost = new HttpPost(PostReceiveURL);
            Log.d("debug", "2");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.d("debug", "3");
            HttpEntity httpEntity = httpResponse.getEntity();
            Log.d("debug", "4");
            if (httpEntity != null) {
                response = EntityUtils.toString(httpEntity).trim();
                Log.d("debug", "5");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("debug", "6");
        Log.d("debug", "Time Response: "+response);
        return response;
    }
}
