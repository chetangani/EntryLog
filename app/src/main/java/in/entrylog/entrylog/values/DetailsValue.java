package in.entrylog.entrylog.values;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.ArrayList;

/**
 * Created by Admin on 06-Jun-16.
 */
public class DetailsValue {
    boolean LoginSuccess, LoginFailure, Fieldsexists, NoFieldsExists, VisitorCheckedIn, VisitorCheckInError, VisitorsCheckOutSuccess,
            VisitorsCheckOutFailure, VisitorImageUpload, MobileAutoSuggestSuccess, MobileAutoSuggestFailure, MobileNoExist, LoginExist,
            VisitorsCheckOutDone, NoVisitorsFound, VisitorsFound, StaffExists, NoStaffExist, PrinterOrderSuccess, PrinterOrderNoData,
            Accountblocked, PermissionSuccess, PermissionFailure, Apkfilexist;
    String GuardID, OrganizationID, OrganizationName, Fields, ImageFileName, VisitorsId, OverNightStay_Time;
    int DisplayFields;
    String Visitors_Name, Visitors_Email, Visitors_Mobile, Visitors_Address, Visitors_tomeet, Visitors_VehicleNo, Visitors_Photo,
            Visitors_CheckInTime, Visitors_CheckInBy, Visitor_ID, Visitors_CheckOutTime, Visitors_BarCode, Check_in_User,
            Check_out_User, Visitor_Designation, Department, Purpose, House_number, Flat_number, Block, No_Visitor, aClass, Section,
            Student_Name, ID_Card, OTPAccess, ImageAccess, Printertype, Scannertype, RfidStatus, DeviceModel, Cameratype, Apkfile,
            ApkdownloadUrl;
    BluetoothSocket Socket;
    BluetoothDevice device;
    static ArrayList<String> arrayFields;

    public DetailsValue() {
    }

    public DetailsValue(String visitors_Name, String visitors_Email, String visitors_Mobile, String visitors_Address,
                        String visitors_tomeet, String visitors_VehicleNo, String visitors_Photo, String visitors_CheckInTime,
                        String visitors_CheckInBy) {
        Visitors_Name = visitors_Name;
        Visitors_Email = visitors_Email;
        Visitors_Mobile = visitors_Mobile;
        Visitors_Address = visitors_Address;
        Visitors_tomeet = visitors_tomeet;
        Visitors_VehicleNo = visitors_VehicleNo;
        Visitors_Photo = visitors_Photo;
        Visitors_CheckInTime = visitors_CheckInTime;
        Visitors_CheckInBy = visitors_CheckInBy;
    }

    public DetailsValue(String visitors_Name, String visitors_Email, String visitors_Mobile, String visitors_Address,
                        String visitors_tomeet, String visitors_VehicleNo, String visitors_Photo) {
        Visitors_Name = visitors_Name;
        Visitors_Email = visitors_Email;
        Visitors_Mobile = visitors_Mobile;
        Visitors_Address = visitors_Address;
        Visitors_tomeet = visitors_tomeet;
        Visitors_VehicleNo = visitors_VehicleNo;
        Visitors_Photo = visitors_Photo;
    }

    public boolean isLoginSuccess() {
        return LoginSuccess;
    }

    public void setLoginSuccess(boolean loginSuccess) {
        LoginSuccess = loginSuccess;
    }

    public boolean isLoginFailure() {
        return LoginFailure;
    }

    public void setLoginFailure(boolean loginFailure) {
        LoginFailure = loginFailure;
    }

    public boolean isLoginExist() {
        return LoginExist;
    }

    public void setLoginExist(boolean loginExist) {
        LoginExist = loginExist;
    }

    public boolean isAccountblocked() {
        return Accountblocked;
    }

    public void setAccountblocked(boolean accountblocked) {
        Accountblocked = accountblocked;
    }

    public String getFields() {
        return this.Fields;
    }

    public void setFields(String fields) {
        this.Fields = fields;
    }

    public String getOrganizationID() {
        return this.OrganizationID;
    }

    public void setOrganizationID(String organizationID) {
        this.OrganizationID = organizationID;
    }

    public String getOrganizationName() {
        return OrganizationName;
    }

    public void setOrganizationName(String organizationName) {
        OrganizationName = organizationName;
    }

    public int getDisplayFields() {
        return this.DisplayFields;
    }

    public void setDisplayFields(int displayFields) {
        this.DisplayFields = displayFields;
    }

    public boolean isFieldsexists() {
        return Fieldsexists;
    }

    public void setFieldsexists(boolean fieldsexists) {
        Fieldsexists = fieldsexists;
    }

    public boolean isStaffExists() {
        return StaffExists;
    }

    public void setStaffExists(boolean staffExists) {
        StaffExists = staffExists;
    }

    public boolean isNoFieldsExists() {
        return NoFieldsExists;
    }

    public void setNoFieldsExists(boolean noFieldsExists) {
        NoFieldsExists = noFieldsExists;
    }

    public boolean isNoStaffExist() {
        return NoStaffExist;
    }

    public void setNoStaffExist(boolean noStaffExist) {
        NoStaffExist = noStaffExist;
    }

    public String getImageFileName() {
        return ImageFileName;
    }

    public void setImageFileName(String imageFileName) {
        ImageFileName = imageFileName;
    }

    public boolean isVisitorCheckedIn() {
        return VisitorCheckedIn;
    }

    public void setVisitorCheckedIn(boolean visitorCheckedIn) {
        VisitorCheckedIn = visitorCheckedIn;
    }

    public boolean isVisitorCheckInError() {
        return VisitorCheckInError;
    }

    public void setVisitorCheckInError(boolean visitorCheckInError) {
        VisitorCheckInError = visitorCheckInError;
    }

    public String getVisitorsId() {
        return VisitorsId;
    }

    public void setVisitorsId(String visitorsId) {
        VisitorsId = visitorsId;
    }

    public boolean isVisitorsCheckOutSuccess() {
        return VisitorsCheckOutSuccess;
    }

    public void setVisitorsCheckOutSuccess(boolean visitorsCheckOutSuccess) {
        VisitorsCheckOutSuccess = visitorsCheckOutSuccess;
    }

    public boolean isVisitorsCheckOutFailure() {
        return VisitorsCheckOutFailure;
    }

    public void setVisitorsCheckOutFailure(boolean visitorsCheckOutFailure) {
        VisitorsCheckOutFailure = visitorsCheckOutFailure;
    }

    public boolean isVisitorsCheckOutDone() {
        return VisitorsCheckOutDone;
    }

    public void setVisitorsCheckOutDone(boolean visitorsCheckOutDone) {
        VisitorsCheckOutDone = visitorsCheckOutDone;
    }

    public String getGuardID() {
        return GuardID;
    }

    public void setGuardID(String guardID) {
        GuardID = guardID;
    }

    public String getVisitor_ID() {
        return Visitor_ID;
    }

    public void setVisitor_ID(String visitor_ID) {
        Visitor_ID = visitor_ID;
    }

    public String getVisitors_Name() {
        return Visitors_Name;
    }

    public void setVisitors_Name(String visitors_Name) {
        Visitors_Name = visitors_Name;
    }

    public String getVisitors_Email() {
        return Visitors_Email;
    }

    public void setVisitors_Email(String visitors_Email) {
        Visitors_Email = visitors_Email;
    }

    public String getVisitors_Mobile() {
        return Visitors_Mobile;
    }

    public void setVisitors_Mobile(String visitors_Mobile) {
        Visitors_Mobile = visitors_Mobile;
    }

    public String getVisitors_Address() {
        return Visitors_Address;
    }

    public void setVisitors_Address(String visitors_Address) {
        Visitors_Address = visitors_Address;
    }

    public String getVisitors_tomeet() {
        return Visitors_tomeet;
    }

    public void setVisitors_tomeet(String visitors_tomeet) {
        Visitors_tomeet = visitors_tomeet;
    }

    public String getVisitors_VehicleNo() {
        return Visitors_VehicleNo;
    }

    public void setVisitors_VehicleNo(String visitors_VehicleNo) {
        Visitors_VehicleNo = visitors_VehicleNo;
    }

    public String getVisitors_BarCode() {
        return Visitors_BarCode;
    }

    public void setVisitors_BarCode(String visitors_BarCode) {
        Visitors_BarCode = visitors_BarCode;
    }

    public String getVisitors_Photo() {
        return Visitors_Photo;
    }

    public void setVisitors_Photo(String visitors_Photo) {
        Visitors_Photo = visitors_Photo;
    }

    public String getVisitors_CheckInTime() {
        return Visitors_CheckInTime;
    }

    public void setVisitors_CheckInTime(String visitors_CheckInTime) {
        Visitors_CheckInTime = visitors_CheckInTime;
    }

    public String getVisitors_CheckOutTime() {
        return Visitors_CheckOutTime;
    }

    public void setVisitors_CheckOutTime(String visitors_CheckOutTime) {
        Visitors_CheckOutTime = visitors_CheckOutTime;
    }

    public String getVisitors_CheckInBy() {
        return Visitors_CheckInBy;
    }

    public void setVisitors_CheckInBy(String visitors_CheckInBy) {
        Visitors_CheckInBy = visitors_CheckInBy;
    }

    public BluetoothSocket getSocket() {
        return Socket;
    }

    public void setSocket(BluetoothSocket socket) {
        Socket = socket;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public boolean isVisitorImageUpload() {
        return VisitorImageUpload;
    }

    public void setVisitorImageUpload(boolean visitorImageUpload) {
        VisitorImageUpload = visitorImageUpload;
    }

    public boolean isMobileAutoSuggestSuccess() {
        return MobileAutoSuggestSuccess;
    }

    public void setMobileAutoSuggestSuccess(boolean mobileAutoSuggestSuccess) {
        MobileAutoSuggestSuccess = mobileAutoSuggestSuccess;
    }

    public boolean isMobileAutoSuggestFailure() {
        return MobileAutoSuggestFailure;
    }

    public void setMobileAutoSuggestFailure(boolean mobileAutoSuggestFailure) {
        MobileAutoSuggestFailure = mobileAutoSuggestFailure;
    }

    public String getOverNightStay_Time() {
        return OverNightStay_Time;
    }

    public void setOverNightStay_Time(String overNightStay_Time) {
        OverNightStay_Time = overNightStay_Time;
    }

    public boolean isMobileNoExist() {
        return MobileNoExist;
    }

    public void setMobileNoExist(boolean mobileNoExist) {
        MobileNoExist = mobileNoExist;
    }

    public boolean isNoVisitorsFound() {
        return NoVisitorsFound;
    }

    public void setNoVisitorsFound(boolean noVisitorsFound) {
        NoVisitorsFound = noVisitorsFound;
    }

    public boolean isVisitorsFound() {
        return VisitorsFound;
    }

    public void setVisitorsFound(boolean visitorsFound) {
        VisitorsFound = visitorsFound;
    }

    public String getCheck_in_User() {
        return Check_in_User;
    }

    public void setCheck_in_User(String check_in_User) {
        Check_in_User = check_in_User;
    }

    public String getCheck_out_User() {
        return Check_out_User;
    }

    public void setCheck_out_User(String check_out_User) {
        Check_out_User = check_out_User;
    }

    public static ArrayList<String> getArrayFields() {
        return arrayFields;
    }

    public static void setArrayFields(ArrayList<String> arrayFields) {
        DetailsValue.arrayFields = arrayFields;
    }

    public boolean isPrinterOrderSuccess() {
        return PrinterOrderSuccess;
    }

    public void setPrinterOrderSuccess(boolean printerOrderSuccess) {
        PrinterOrderSuccess = printerOrderSuccess;
    }

    public boolean isPrinterOrderNoData() {
        return PrinterOrderNoData;
    }

    public void setPrinterOrderNoData(boolean printerOrderNoData) {
        PrinterOrderNoData = printerOrderNoData;
    }

    public String getVisitor_Designation() {
        return Visitor_Designation;
    }

    public void setVisitor_Designation(String visitor_Designation) {
        Visitor_Designation = visitor_Designation;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public String getPurpose() {
        return Purpose;
    }

    public void setPurpose(String purpose) {
        Purpose = purpose;
    }

    public String getHouse_number() {
        return House_number;
    }

    public void setHouse_number(String house_number) {
        House_number = house_number;
    }

    public String getFlat_number() {
        return Flat_number;
    }

    public void setFlat_number(String flat_number) {
        Flat_number = flat_number;
    }

    public String getBlock() {
        return Block;
    }

    public void setBlock(String block) {
        Block = block;
    }

    public String getNo_Visitor() {
        return No_Visitor;
    }

    public void setNo_Visitor(String no_Visitor) {
        No_Visitor = no_Visitor;
    }

    public String getaClass() {
        return aClass;
    }

    public void setaClass(String aClass) {
        this.aClass = aClass;
    }

    public String getSection() {
        return Section;
    }

    public void setSection(String section) {
        Section = section;
    }

    public String getStudent_Name() {
        return Student_Name;
    }

    public void setStudent_Name(String student_Name) {
        Student_Name = student_Name;
    }

    public String getID_Card() {
        return ID_Card;
    }

    public void setID_Card(String ID_Card) {
        this.ID_Card = ID_Card;
    }

    public boolean isPermissionSuccess() {
        return PermissionSuccess;
    }

    public void setPermissionSuccess(boolean permissionSuccess) {
        PermissionSuccess = permissionSuccess;
    }

    public boolean isPermissionFailure() {
        return PermissionFailure;
    }

    public void setPermissionFailure(boolean permissionFailure) {
        PermissionFailure = permissionFailure;
    }

    public String getOTPAccess() {
        return OTPAccess;
    }

    public void setOTPAccess(String OTPAccess) {
        this.OTPAccess = OTPAccess;
    }

    public String getImageAccess() {
        return ImageAccess;
    }

    public void setImageAccess(String imageAccess) {
        ImageAccess = imageAccess;
    }

    public String getPrintertype() {
        return Printertype;
    }

    public void setPrintertype(String printertype) {
        Printertype = printertype;
    }

    public String getScannertype() {
        return Scannertype;
    }

    public void setScannertype(String scannertype) {
        Scannertype = scannertype;
    }

    public String getRfidStatus() {
        return RfidStatus;
    }

    public void setRfidStatus(String rfidStatus) {
        RfidStatus = rfidStatus;
    }

    public String getDeviceModel() {
        return DeviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        DeviceModel = deviceModel;
    }

    public String getCameratype() {
        return Cameratype;
    }

    public void setCameratype(String cameratype) {
        Cameratype = cameratype;
    }

    public String getApkfile() {
        return Apkfile;
    }

    public void setApkfile(String apkfile) {
        Apkfile = apkfile;
    }

    public String getApkdownloadUrl() {
        return ApkdownloadUrl;
    }

    public void setApkdownloadUrl(String apkdownloadUrl) {
        ApkdownloadUrl = apkdownloadUrl;
    }

    public boolean isApkfilexist() {
        return Apkfilexist;
    }

    public void setApkfilexist(boolean apkfilexist) {
        Apkfilexist = apkfilexist;
    }
}
