package in.entrylog.entrylog.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

/**
 * Created by Admin on 12-Jul-16.
 */
public class DataBase {
    String DATABASE_NAME = "entrylog.db", DATABASE_PATH;
    int DATABASE_VERSION = 1;
    MyHelper mh;
    SQLiteDatabase sdb;
    File databasefile = null;

    public DataBase(Context context) {
        try {
            databasefile = filestorepath(DATABASE_NAME);
            if (databasefile.exists()) {
                Log.d("Database", "Exists in device");
            }
            else {
                Log.d("Database", "Does not exists in device");
            }
            DATABASE_PATH = filepath() + File.separator + DATABASE_NAME;
            mh = new MyHelper(context, DATABASE_PATH, null, DATABASE_VERSION);
        } catch (Exception e) {
        }
    }

    public void open() {
        sdb = mh.getWritableDatabase();
    }

    public void close() {
        sdb.close();
    }

    //delete database
    public void db_delete() {
        File file = new File(DATABASE_PATH);
        if(file.exists()) {
            file.delete();
        }
    }

    private class MyHelper extends SQLiteOpenHelper {

        public MyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("Create table CHECKINDATA(_id integer primary key, visitors_name text, visitors_email text, " +
                    "visitors_mobile text, visitors_address text, to_meet text, visitors_vehicle_no text, visitors_photo text, " +
                    "image_path text, bar_code text, organization_id text, security_guards_id text, upload_image text, " +
                    "visitor_designation text, department text, purpose text, house_number text, flat_number text, block text, " +
                    "no_visitor text, class text, section text, student_name text, id_card_number text, device text, " +
                    "visitor_entry text);");
            db.execSQL("Create table ENTRYLOGDATA(_id integer primary key, visitors_name text, visitors_email text, " +
                    "visitors_mobile text, visitors_address text, to_meet text, visitors_vehicle_no text, visitors_photo text, " +
                    "image_path text, bar_code text, organization_id text, security_guards_id text, upload_image text, " +
                    "visitor_designation text, department text, purpose text, house_number text, flat_number text, block text, " +
                    "no_visitor text, class text, section text, student_name text, id_card_number text, device text, " +
                    "visitor_entry text);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public String filepath() {
        File dir = new File(android.os.Environment.getExternalStorageDirectory(), "Entrylog"
                + File.separator + "Database");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String pathname = "" + dir;
        return pathname;
    }

    public File filestorepath(String file) {
        File dir = new File(android.os.Environment.getExternalStorageDirectory(), "Entrylog"
                + File.separator + "Database");
        Log.d("File Dir", "" + dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dir1 = new File(dir, File.separator + file);
        Log.d("File Dir1", "" + dir1);
        if (dir1.exists()) {
            Log.d("File Dir1", "Created");
        } else {
            Log.d("File Dir1", "Not Created");
        }
        return dir1;
    }

    public void insertcheckin(String Name, String Email, String Mobile, String Address, String ToMeet, String VehicleNo,
                              String Imagefile, String ImagePath, String Barcode, String OrganizationID, String SecurityID,
                              String Upload_Image, String visitor_Designation, String department, String purpose,
                              String house_number, String flat_number, String block, String no_Visitor, String aclass,
                              String section, String student_Name, String ID_Card, String Device, String Visitor_Entry) {
        ContentValues cv = new ContentValues();
        cv.put("visitors_name", Name);
        cv.put("visitors_email", Email);
        cv.put("visitors_mobile", Mobile);
        cv.put("visitors_address", Address);
        cv.put("to_meet", ToMeet);
        cv.put("visitors_vehicle_no", VehicleNo);
        cv.put("visitors_photo", Imagefile);
        cv.put("image_path", ImagePath);
        cv.put("bar_code", Barcode);
        cv.put("organization_id", OrganizationID);
        cv.put("security_guards_id", SecurityID);
        cv.put("upload_image", Upload_Image);
        cv.put("visitor_designation", visitor_Designation);
        cv.put("department", department);
        cv.put("purpose", purpose);
        cv.put("house_number", house_number);
        cv.put("flat_number", flat_number);
        cv.put("block", block);
        cv.put("no_visitor", no_Visitor);
        cv.put("class", aclass);
        cv.put("section", section);
        cv.put("student_name", student_Name);
        cv.put("id_card_number", ID_Card);
        cv.put("device", Device);
        cv.put("visitor_entry", Visitor_Entry);
        sdb.insert("CHECKINDATA", null, cv);
    }

    public void insertentrylogdata(String Name, String Email, String Mobile, String Address, String ToMeet, String VehicleNo,
                                   String Imagefile, String ImagePath, String Barcode, String OrganizationID, String SecurityID,
                                   String Upload_Image, String visitor_Designation, String department, String purpose,
                                   String house_number, String flat_number, String block, String no_Visitor, String aclass,
                                   String section, String student_Name, String ID_Card, String Device, String Visitor_Entry) {
        ContentValues cv = new ContentValues();
        cv.put("visitors_name", Name);
        cv.put("visitors_email", Email);
        cv.put("visitors_mobile", Mobile);
        cv.put("visitors_address", Address);
        cv.put("to_meet", ToMeet);
        cv.put("visitors_vehicle_no", VehicleNo);
        cv.put("visitors_photo", Imagefile);
        cv.put("image_path", ImagePath);
        cv.put("bar_code", Barcode);
        cv.put("organization_id", OrganizationID);
        cv.put("security_guards_id", SecurityID);
        cv.put("upload_image", Upload_Image);
        cv.put("visitor_designation", visitor_Designation);
        cv.put("department", department);
        cv.put("purpose", purpose);
        cv.put("house_number", house_number);
        cv.put("flat_number", flat_number);
        cv.put("block", block);
        cv.put("no_visitor", no_Visitor);
        cv.put("class", aclass);
        cv.put("section", section);
        cv.put("student_name", student_Name);
        cv.put("id_card_number", ID_Card);
        cv.put("device", Device);
        cv.put("visitor_entry", Visitor_Entry);
        sdb.insert("ENTRYLOGDATA", null, cv);
    }

    public Cursor readCheckinData() {
        Cursor c = null;
        c = sdb.rawQuery("SELECT * FROM CHECKINDATA", null);
        return c;
    }

    public Cursor readentrylogdata() {
        Cursor c = null;
        c = sdb.rawQuery("SELECT * FROM ENTRYLOGDATA", null);
        return c;
    }

    public Cursor deleteCheckinData(String id) {
        Cursor c = null;
        c = sdb.rawQuery("delete from CHECKINDATA WHERE _id = "+"'"+id+"'", null);
        return c;
    }

    public Cursor deleteentrylogdata() {
        Cursor c = null;
        c = sdb.rawQuery("delete from ENTRYLOGDATA", null);
        return c;
    }
}