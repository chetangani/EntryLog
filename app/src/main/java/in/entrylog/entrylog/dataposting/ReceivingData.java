package in.entrylog.entrylog.dataposting;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import in.entrylog.entrylog.adapters.VisitorsAdapters;
import in.entrylog.entrylog.values.DetailsValue;

/**
 * Created by Admin on 06-Jun-16.
 */
public class ReceivingData {

    public static final String Image_Url = "http://www.tellservice.com/entrylog/visitor_images/";
    public static final String Apk_Url = "http://www.tellservice.com/entrylog/apk/";
    /*public static final String Image_Url = "http://www.askdial.com/entrylog/visitor_images/";
    public static final String Apk_Url = "http://www.askdial.com/entrylog/apk/";*/

    public void LoginDetails(String result, DetailsValue details) {
        Log.d("debug", result);
        try {
            JSONObject jo = new JSONObject(result);
            if (jo != null) {
                String Response = jo.getString("message");
                if (Response.equals("Success")) {
                    String ID = jo.getString("organization_id");
                    details.setOrganizationID(ID);
                    String GuardID = jo.getString("security_guards_id");
                    details.setGuardID(GuardID);
                    String OrganizationName = jo.getString("organization_name");
                    details.setOrganizationName(OrganizationName);
                    String OverNightTime = jo.getString("overnight_stay_time");
                    details.setOverNightStay_Time(OverNightTime);
                    String BarCode = jo.getString("visitors_bar_code");
                    details.setVisitors_BarCode(BarCode);
                    details.setLoginSuccess(true);
                } else if (Response.equals("Failure")) {
                    details.setLoginFailure(true);
                } else if (Response.equals("Account Blocked")) {
                    details.setAccountblocked(true);
                } else {
                    details.setLoginExist(true);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void LogoutDetails(String result, DetailsValue details) {
        try {
            JSONObject jo = new JSONObject(result);
            if (jo != null) {
                String Response = jo.getString("message");
                if (Response.equals("Success")) {
                    details.setLoginSuccess(true);
                } else {
                    details.setLoginFailure(true);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void DisplayFields(String result, DetailsValue details, HashSet<String> hashSet) {
        ArrayList<String> list = new ArrayList<>();
        try {
            JSONArray ja = new JSONArray(result);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                if (jo != null) {
                    String field = jo.getString("get_organization_fields_name");
                    if (!field.equals("No Data")) {
                        list.add(field);
                        if (i == (ja.length()-1)) {
                            details.setFieldsexists(true);
                            hashSet.addAll(list);
                        }
                    } else {
                        details.setNoFieldsExists(true);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void GetStaffStatus(String result, DetailsValue details, HashSet<String> hashSet) {
        ArrayList<String> list = new ArrayList<>();
        try {
            JSONArray ja = new JSONArray(result);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                if (jo != null) {
                    String message = jo.getString("message");
                    if (message.equals("Success")) {
                        String Staff = jo.getString("staff_name");
                        list.add(Staff);
                        if (i == (ja.length()-1)) {
                            details.setStaffExists(true);
                            hashSet.addAll(list);
                        }
                    } else {
                        details.setNoStaffExist(true);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void VisitorsCheckinStatus(String result, DetailsValue details) {
        Log.d("debug", "Visitor Check in Status: "+result);
        try {
            JSONObject jo = new JSONObject(result);
            if (jo != null) {
                String Status = jo.getString("message");
                if (Status.equals("Success")) {
                    details.setVisitorsId(jo.getString("visitors_id"));
                    details.setVisitorCheckedIn(true);
                } else {
                    details.setVisitorCheckInError(true);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void VisitorsCheckOutStatus(String result, DetailsValue details) {
        try {
            JSONObject jo = new JSONObject(result);
            if (jo != null) {
                String Status = jo.getString("message");
                if (Status.equals("Success")) {
                    details.setVisitorsCheckOutSuccess(true);
                } else if (Status.equals("Failure")){
                    details.setVisitorsCheckOutFailure(true);
                } else {
                    details.setVisitorsCheckOutDone(true);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void CheckVisitorsStatus(String result, DetailsValue details, ArrayList<DetailsValue> arrayList,
                                    VisitorsAdapters adapters) {
        String Visitor_id, Visitors_Name, Visitors_Email, Visitors_Mobile, Visitors_Address, Visitors_tomeet, Visitors_VehicleNo = "",
                Visitors_Photo, Visitors_CheckInTime, Visitors_CheckInBy, Visitors_BarCode, Check_in_User, Check_out_User,
                Visitor_Designation, Department, Purpose, House_number, Flat_number, Block, No_Visitor, aClass,
                Section, Student_Name, ID_Card;
        Log.d("debug", "Check Visitors Status "+result);
        try {
            JSONArray ja = new JSONArray(result);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                if (jo != null) {
                    String Status = jo.getString("message");
                    if (Status.equals("Success")) {
                        details.setVisitorsFound(true);
                        details = new DetailsValue();
                        Visitor_id = jo.getString("visitors_id");
                        details.setVisitor_ID(Visitor_id);
                        Visitors_Name = jo.getString("visitors_name");
                        details.setVisitors_Name(Visitors_Name);
                        Visitors_Email = jo.getString("visitors_email");
                        details.setVisitors_Email(Visitors_Email);
                        Visitors_Mobile = jo.getString("visitors_mobile");
                        details.setVisitors_Mobile(Visitors_Mobile);
                        Visitors_Address = jo.getString("visitors_address");
                        details.setVisitors_Address(Visitors_Address);
                        Visitors_tomeet = jo.getString("to_meet");
                        details.setVisitors_tomeet(Visitors_tomeet);
                        Visitors_VehicleNo = jo.getString("visitors_vehicle_number");
                        details.setVisitors_VehicleNo(Visitors_VehicleNo);
                        String Image = jo.getString("visitors_photo");
                        Visitors_Photo = Image_Url + Image;
                        details.setVisitors_Photo(Visitors_Photo);
                        Visitors_CheckInTime = jo.getString("checked_in_time");
                        details.setVisitors_CheckInTime(Visitors_CheckInTime);
                        Visitors_CheckInBy = jo.getString("check_in_by");
                        details.setVisitors_CheckInBy(Visitors_CheckInBy);
                        Visitors_BarCode = jo.getString("visitors_bar_code");
                        details.setVisitors_BarCode(Visitors_BarCode);
                        Check_in_User = jo.getString("check_in_by_name");
                        details.setCheck_in_User(Check_in_User);
                        Check_out_User = jo.getString("check_out_by_name");
                        details.setCheck_out_User(Check_out_User);
                        Visitor_Designation = jo.getString("visitor_designation");
                        details.setVisitor_Designation(Visitor_Designation);
                        Department = jo.getString("department");
                        details.setDepartment(Department);
                        Purpose = jo.getString("purpose");
                        details.setPurpose(Purpose);
                        House_number = jo.getString("house_number");
                        details.setHouse_number(House_number);
                        Flat_number = jo.getString("flat_number");
                        details.setFlat_number(Flat_number);
                        Block = jo.getString("block");
                        details.setBlock(Block);
                        No_Visitor = jo.getString("no_visitor");
                        details.setNo_Visitor(No_Visitor);
                        aClass = jo.getString("class");
                        details.setaClass(aClass);
                        Section = jo.getString("section");
                        details.setSection(Section);
                        Student_Name = jo.getString("student_name");
                        details.setStudent_Name(Student_Name);
                        ID_Card = jo.getString("id_card_number");
                        details.setID_Card(ID_Card);
                        arrayList.add(details);
                        adapters.notifyDataSetChanged();
                    } else {
                        details.setNoVisitorsFound(true);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void AllVisitorsStatus(String result, DetailsValue details, ArrayList<DetailsValue> arrayList,
                                    VisitorsAdapters adapters) {
        String Visitor_id, Visitors_Name, Visitors_Email, Visitors_Mobile, Visitors_Address, Visitors_tomeet, Visitors_VehicleNo = "",
                Visitors_Photo, Visitors_CheckInTime, Visitors_CheckInBy, Visitors_CheckOutTime, Visitors_BarCode, Check_in_User,
                Check_out_User, Visitor_Designation, Department, Purpose, House_number, Flat_number, Block, No_Visitor, aClass,
                Section, Student_Name, ID_Card;
        Log.d("debug", "All Visitors Status "+result);
        try {
            JSONArray ja = new JSONArray(result);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                if (jo != null) {
                    String Status = jo.getString("message");
                    if (Status.equals("Success")) {
                        details.setVisitorsFound(true);
                        details = new DetailsValue();
                        Visitor_id = jo.getString("visitors_id");
                        details.setVisitor_ID(Visitor_id);
                        Visitors_Name = jo.getString("visitors_name");
                        details.setVisitors_Name(Visitors_Name);
                        Visitors_Email = jo.getString("visitors_email");
                        details.setVisitors_Email(Visitors_Email);
                        Visitors_Mobile = jo.getString("visitors_mobile");
                        details.setVisitors_Mobile(Visitors_Mobile);
                        Visitors_Address = jo.getString("visitors_address");
                        details.setVisitors_Address(Visitors_Address);
                        Visitors_tomeet = jo.getString("to_meet");
                        details.setVisitors_tomeet(Visitors_tomeet);
                        Visitors_VehicleNo = jo.getString("visitors_vehicle_number");
                        details.setVisitors_VehicleNo(Visitors_VehicleNo);
                        String Image = jo.getString("visitors_photo");
                        Visitors_Photo = Image_Url + Image;
                        details.setVisitors_Photo(Visitors_Photo);
                        Visitors_CheckInTime = jo.getString("checked_in_time");
                        details.setVisitors_CheckInTime(Visitors_CheckInTime);
                        Visitors_CheckInBy = jo.getString("check_in_by");
                        details.setVisitors_CheckInBy(Visitors_CheckInBy);
                        Visitors_CheckOutTime = jo.getString("checked_out_time");
                        details.setVisitors_CheckOutTime(Visitors_CheckOutTime);
                        Visitors_BarCode = jo.getString("visitors_bar_code");
                        details.setVisitors_BarCode(Visitors_BarCode);
                        Check_in_User = jo.getString("check_in_by_name");
                        details.setCheck_in_User(Check_in_User);
                        Check_out_User = jo.getString("check_out_by_name");
                        details.setCheck_out_User(Check_out_User);
                        Visitor_Designation = jo.getString("visitor_designation");
                        details.setVisitor_Designation(Visitor_Designation);
                        Department = jo.getString("department");
                        details.setDepartment(Department);
                        Purpose = jo.getString("purpose");
                        details.setPurpose(Purpose);
                        House_number = jo.getString("house_number");
                        details.setHouse_number(House_number);
                        Flat_number = jo.getString("flat_number");
                        details.setFlat_number(Flat_number);
                        Block = jo.getString("block");
                        details.setBlock(Block);
                        No_Visitor = jo.getString("no_visitor");
                        details.setNo_Visitor(No_Visitor);
                        aClass = jo.getString("class");
                        details.setaClass(aClass);
                        Section = jo.getString("section");
                        details.setSection(Section);
                        Student_Name = jo.getString("student_name");
                        details.setStudent_Name(Student_Name);
                        ID_Card = jo.getString("id_card_number");
                        details.setID_Card(ID_Card);
                        arrayList.add(details);
                        adapters.notifyDataSetChanged();
                    } else {
                        details.setNoVisitorsFound(true);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void VisitorManualCheckout(String result, DetailsValue details) {
        try {
            JSONObject jo = new JSONObject(result);
            if (jo != null) {
                String Status = jo.getString("message");
                if (Status.equals("Success")) {
                    details.setVisitorsCheckOutSuccess(true);
                } else {
                    details.setVisitorsCheckOutFailure(true);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void MobileAutoSuggestStatus(String result, DetailsValue details) {
        Log.d("debug", result);
        try {
            JSONObject jo = new JSONObject(result);
            if (jo != null) {
                String Status = jo.getString("message");
                if (Status.equals("Success")) {
                    details.setMobileAutoSuggestSuccess(true);
                    details.setVisitors_Name(jo.getString("visitors_name"));
                    details.setVisitors_Email(jo.getString("visitors_email"));
                    details.setVisitors_Address(jo.getString("visitors_address"));
                    details.setVisitors_tomeet(jo.getString("to_meet"));
                    details.setVisitors_Photo(jo.getString("visitors_photo"));
                    details.setVisitors_VehicleNo(jo.getString("visitors_vehicle_number"));
                    details.setVisitor_Designation(jo.getString("visitor_designation"));
                    details.setDepartment(jo.getString("department"));
                    details.setPurpose(jo.getString("purpose"));
                    details.setHouse_number(jo.getString("house_number"));
                    details.setFlat_number(jo.getString("flat_number"));
                    details.setBlock(jo.getString("block"));
                    details.setNo_Visitor(jo.getString("no_visitor"));
                    details.setaClass(jo.getString("class"));
                    details.setSection(jo.getString("section"));
                    details.setStudent_Name(jo.getString("student_name"));
                    details.setID_Card(jo.getString("id_card_number"));
                } else if (Status.equals("Failure")){
                    details.setMobileAutoSuggestFailure(true);
                } else {
                    details.setMobileNoExist(true);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void VisitorsStatus(String result, DetailsValue details, ArrayList<DetailsValue> arrayList,
                                    VisitorsAdapters adapters) {
        Log.d("debug", "Search result: "+result);
        String Visitor_id, Visitors_Name, Visitors_Email, Visitors_Mobile, Visitors_Address, Visitors_tomeet, Visitors_VehicleNo = "",
                Visitors_Photo, Visitors_CheckInTime, Visitors_CheckInBy, Visitors_BarCode, Check_in_User, Check_out_User,
                Visitor_Designation, Department, Purpose, House_number, Flat_number, Block, No_Visitor, aClass, Section,
                Student_Name, ID_Card, Visitors_CheckOutTime;
        try {
            JSONArray ja = new JSONArray(result);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                if (jo != null) {
                    String Status = jo.getString("message");
                    if (Status.equals("Success")) {
                        details.setVisitorsFound(true);
                        details = new DetailsValue();
                        Visitor_id = jo.getString("visitors_id");
                        details.setVisitor_ID(Visitor_id);
                        Visitors_Name = jo.getString("visitors_name");
                        details.setVisitors_Name(Visitors_Name);
                        Visitors_Email = jo.getString("visitors_email");
                        details.setVisitors_Email(Visitors_Email);
                        Visitors_Mobile = jo.getString("visitors_mobile");
                        details.setVisitors_Mobile(Visitors_Mobile);
                        Visitors_Address = jo.getString("visitors_address");
                        details.setVisitors_Address(Visitors_Address);
                        Visitors_tomeet = jo.getString("to_meet");
                        details.setVisitors_tomeet(Visitors_tomeet);
                        Visitors_VehicleNo = jo.getString("visitors_vehicle_number");
                        details.setVisitors_VehicleNo(Visitors_VehicleNo);
                        String Image = jo.getString("visitors_photo");
                        Visitors_Photo = Image_Url + Image;
                        details.setVisitors_Photo(Visitors_Photo);
                        Visitors_CheckInTime = jo.getString("checked_in_time");
                        details.setVisitors_CheckInTime(Visitors_CheckInTime);
                        Visitors_CheckInBy = jo.getString("check_in_by");
                        details.setVisitors_CheckInBy(Visitors_CheckInBy);
                        Visitors_CheckOutTime = jo.getString("checked_out_time");
                        details.setVisitors_CheckOutTime(Visitors_CheckOutTime);
                        Visitors_BarCode = jo.getString("visitors_bar_code");
                        details.setVisitors_BarCode(Visitors_BarCode);
                        Check_in_User = jo.getString("check_in_by_name");
                        details.setCheck_in_User(Check_in_User);
                        Check_out_User = jo.getString("check_out_by_name");
                        details.setCheck_out_User(Check_out_User);
                        Visitor_Designation = jo.getString("visitor_designation");
                        details.setVisitor_Designation(Visitor_Designation);
                        Department = jo.getString("department");
                        details.setDepartment(Department);
                        Purpose = jo.getString("purpose");
                        details.setPurpose(Purpose);
                        House_number = jo.getString("house_number");
                        details.setHouse_number(House_number);
                        Flat_number = jo.getString("flat_number");
                        details.setFlat_number(Flat_number);
                        Block = jo.getString("block");
                        details.setBlock(Block);
                        No_Visitor = jo.getString("no_visitor");
                        details.setNo_Visitor(No_Visitor);
                        aClass = jo.getString("class");
                        details.setaClass(aClass);
                        Section = jo.getString("section");
                        details.setSection(Section);
                        Student_Name = jo.getString("student_name");
                        details.setStudent_Name(Student_Name);
                        ID_Card = jo.getString("id_card_number");
                        details.setID_Card(ID_Card);
                        arrayList.add(details);
                        adapters.notifyDataSetChanged();
                    } else {
                        details.setNoVisitorsFound(true);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void PrintingFields_Status(String result, DetailsValue details, HashSet<String> OrderSet, HashSet<String> DisplaySet) {
        ArrayList<String> orderlist = new ArrayList<>();
        ArrayList<String> displaylist = new ArrayList<>();
        try {
            JSONArray ja = new JSONArray(result);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                if (jo != null) {
                    String Status = jo.getString("message");
                    if (Status.equals("Success")) {
                        String Visitors_Name = jo.getString("visitors_name");
                        if (!Visitors_Name.equals("")) {
                            orderlist.add(jo.getString("visitors_name_order"));
                            displaylist.add(jo.getString("visitors_name_order")+"_Name");
                        }
                        String Visitors_Mobile = jo.getString("visitors_mobile");
                        if (!Visitors_Mobile.equals("")) {
                            orderlist.add(jo.getString("visitors_mobile_order"));
                            displaylist.add(jo.getString("visitors_mobile_order")+"_Mobile");
                        }
                        String Visitors_Address = jo.getString("visitors_address");
                        if (!Visitors_Address.equals("")) {
                            orderlist.add(jo.getString("visitors_address_order"));
                            displaylist.add(jo.getString("visitors_address_order")+"_From");
                        }
                        String To_Meet = jo.getString("to_meet");
                        if (!To_Meet.equals("")) {
                            orderlist.add(jo.getString("to_meet_order"));
                            displaylist.add(jo.getString("to_meet_order")+"_To Meet");
                        }
                        String Check_in_time = jo.getString("checked_in_time");
                        if (!Check_in_time.equals("")) {
                            orderlist.add(jo.getString("checked_in_time_order"));
                            displaylist.add(jo.getString("checked_in_time_order")+"_Date");
                        }
                        String Visitors_Email = jo.getString("visitors_email");
                        if (!Visitors_Email.equals("")) {
                            orderlist.add(jo.getString("visitors_email_order"));
                            displaylist.add(jo.getString("visitors_email_order")+"_Email");
                        }
                        String visitors_vehicle_number = jo.getString("visitors_vehicle_number");
                        if (!visitors_vehicle_number.equals("")) {
                            orderlist.add(jo.getString("visitors_vehicle_number_order"));
                            displaylist.add(jo.getString("visitors_vehicle_number_order")+"_Vehicle Number");
                        }
                        /*String visitors_photo = jo.getString("visitors_photo");
                        if (!visitors_photo.equals("")) {
                            orderlist.add(jo.getString("visitors_photo_order"));
                            displaylist.add("Photo");
                        }*/
                        String visitors_designation = jo.getString("visitors_designation");
                        if (!visitors_designation.equals("")) {
                            orderlist.add(jo.getString("visitors_designation_order"));
                            displaylist.add(jo.getString("visitors_designation_order")+"_Designation");
                        }
                        String department = jo.getString("department");
                        if (!department.equals("")) {
                            orderlist.add(jo.getString("department_order"));
                            displaylist.add(jo.getString("department_order")+"_Department");
                        }
                        String purpose = jo.getString("purpose");
                        if (!purpose.equals("")) {
                            orderlist.add(jo.getString("purpose_order"));
                            displaylist.add(jo.getString("purpose_order")+"_Purpose");
                        }
                        String house_no = jo.getString("house_no");
                        if (!house_no.equals("")) {
                            orderlist.add(jo.getString("house_no_order"));
                            displaylist.add(jo.getString("house_no_order")+"_House No");
                        }
                        String flat_no = jo.getString("flat_no");
                        if (!flat_no.equals("")) {
                            orderlist.add(jo.getString("flat_no_order"));
                            displaylist.add(jo.getString("flat_no_order")+"_Flat no");
                        }
                        String block = jo.getString("block");
                        if (!block.equals("")) {
                            orderlist.add(jo.getString("block_order"));
                            displaylist.add(jo.getString("block_order")+"_Block");
                        }
                        String no_visitor = jo.getString("no_visitor");
                        if (!no_visitor.equals("")) {
                            orderlist.add(jo.getString("no_visitor_order"));
                            displaylist.add(jo.getString("no_visitor_order")+"_Visitors");
                        }
                        String aclass = jo.getString("class");
                        if (!aclass.equals("")) {
                            orderlist.add(jo.getString("class_order"));
                            displaylist.add(jo.getString("class_order")+"_Class");
                        }
                        String section = jo.getString("section");
                        if (!section.equals("")) {
                            orderlist.add(jo.getString("section_order"));
                            displaylist.add(jo.getString("section_order")+"_Section");
                        }
                        String student_name = jo.getString("student_name");
                        if (!student_name.equals("")) {
                            orderlist.add(jo.getString("student_name_order"));
                            displaylist.add(jo.getString("student_name_order")+"_Student");
                        }
                        String id_card = jo.getString("id_card");
                        if (!id_card.equals("")) {
                            orderlist.add(jo.getString("id_card_order"));
                            displaylist.add(jo.getString("id_card_order")+"_Id Card");
                        }
                        String Entry = jo.getString("entry_name");
                        if (!Entry.equals("")) {
                            orderlist.add(jo.getString("entry_order"));
                            displaylist.add(jo.getString("entry_order")+"_Entry");
                        }
                        OrderSet.addAll(orderlist);
                        DisplaySet.addAll(displaylist);
                        details.setPrinterOrderSuccess(true);
                    } else {
                        details.setPrinterOrderNoData(true);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void PermissionStatus(String result, DetailsValue details) {
        try {
            JSONArray ja = new JSONArray(result);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                if (jo != null) {
                    String Message = jo.getString("message");
                    if (Message.equals("Success")) {
                        details.setOTPAccess(jo.getString("otp_access"));
                        details.setImageAccess(jo.getString("image_access"));
                        details.setPrintertype(jo.getString("printer_type_name"));
                        details.setScannertype(jo.getString("scanner_name"));
                        details.setRfidStatus(jo.getString("rfid_status_name"));
                        details.setDeviceModel(jo.getString("device_model_name"));
                        Log.d("debug", "Device Model: "+jo.getString("device_model_name"));
                        details.setCameratype(jo.getString("camera_name"));
                        details.setPermissionSuccess(true);
                    } else {
                        details.setPermissionFailure(true);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void ApkStatus(String result, DetailsValue details) {
        try {
            JSONObject jo = new JSONObject(result);
            if (jo != null) {
                String updateapk = jo.getString("apk");
                details.setApkfile(updateapk);
                String apkpath = Apk_Url + updateapk;
                details.setApkdownloadUrl(apkpath);
                details.setApkfilexist(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
