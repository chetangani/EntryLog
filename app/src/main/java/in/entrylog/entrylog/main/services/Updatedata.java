package in.entrylog.entrylog.main.services;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import in.entrylog.entrylog.database.DataBase;
import in.entrylog.entrylog.dataposting.ConnectingTask;
import in.entrylog.entrylog.dataposting.ConnectingTask.VisitorsCheckIn;
import in.entrylog.entrylog.values.DetailsValue;
import in.entrylog.entrylog.values.FunctionCalls;

/**
 * Created by Admin on 16-Jul-16.
 */
public class Updatedata extends Service {
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String Image_Url = "http://www.tellservice.com/entrylog/index.php/admin/api/Upload_images2";
    ConnectingTask task;
    DetailsValue details;
    DataBase dataBase;
    String Name, Email="", FromAddress, ToMeet, Vehicleno="", Visitors_ImagefileName, Organizationid, GuardID, ImagePath,
            UploadImage, UpdatedID="", BarCodeValue, Visitor_Designation, Department, Purpose, House_number, Flat_number,
            Block, No_Visitor, aClass, Section, Student_Name, ID_Card, Visitor_Entry;
    static String Mobile, Visitors_id, Device;
    ArrayList<DetailsValue> arrayList;
    static boolean completed = false;
    Thread updatethread;
    static boolean Servicerunning = false;
    FunctionCalls functionCalls;
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    public static boolean getServiceRunning() {
        return Updatedata.Servicerunning;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        details = new DetailsValue();
        task = new ConnectingTask();
        dataBase = new DataBase(this);
        dataBase.open();
        arrayList = new ArrayList<DetailsValue>();//http://www.tellservice.com/entrylog/index.php/admin/api/Upload_images2
        Servicerunning = true;
        functionCalls = new FunctionCalls();

        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = settings.edit();

        editor.putString("UpdateData", "Running");
        editor.commit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        updatethread = null;
        Runnable runnable = new CheckData();
        updatethread = new Thread(runnable);
        updatethread.start();

        GetUploadingdetails();

        return Service.START_STICKY;
    }

    private static class Upload_Image extends AsyncTask<String, String, String> {
        String responsestr = "", Mobile, Visitors_id;
        Bitmap image;

        public Upload_Image(Bitmap image, String mobile, String visitors_id) {
            this.image = image;
            this.Mobile = mobile;
            this.Visitors_id = visitors_id;
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d("debug", "Image upload 1");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Log.d("debug", "Image upload 2");
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            Log.d("debug", "Image upload 3");
            byte[] imageInByte = byteArrayOutputStream.toByteArray();
            long lengthbmp = imageInByte.length;
            Log.d("debug", "Image size to Upload: "+lengthbmp);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            Log.d("debug", "Image upload 4");
            ArrayList<NameValuePair> datatosend = new ArrayList<>();
            Log.d("debug", "Image upload 5");
            datatosend.add(new BasicNameValuePair("visitors_photo", encodedImage));
            Log.d("debug", "Image upload 6");
            datatosend.add(new BasicNameValuePair("visitors_mobile", Mobile));
            Log.d("debug", "Image upload Mobile" + Mobile);
            datatosend.add(new BasicNameValuePair("visitors_id", Visitors_id));
            Log.d("debug", "Image upload Visitors ID" + Visitors_id);
            HttpParams httpRequestParams = getHttpRequestParam();
            Log.d("debug", "Image upload 7");
            HttpClient httpClient = new DefaultHttpClient(httpRequestParams);
            Log.d("debug", "Image upload 8");
            HttpPost httpPost = new HttpPost(Image_Url);
            Log.d("debug", "Image upload 9");
            try {
                Log.d("debug", "Image upload 10");
                httpPost.setEntity(new UrlEncodedFormEntity(datatosend));
                Log.d("debug", "Image upload 11");
                HttpResponse httpResponse = httpClient.execute(httpPost);
                Log.d("debug", "Image upload 12");
                HttpEntity httpEntity = httpResponse.getEntity();
                Log.d("debug", "Image upload 13");
                if (httpEntity != null) {
                    Log.d("debug", "Image upload 14");
                    responsestr = EntityUtils.toString(httpEntity).trim();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("debug", responsestr);
            return responsestr;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("debug", "Image upload result: " + result);
            completed = true;
        }
    }

    private static HttpParams getHttpRequestParam() {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 1000 * 30);
        HttpConnectionParams.setSoTimeout(httpParams, 1000 * 30);
        return httpParams;
    }

    class CheckData implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    UploadData();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    protected int sizeOf(Bitmap data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return data.getRowBytes() * data.getHeight();
        } else {
            return data.getByteCount();
        }
    }

    private void UploadData() {
        if (details.isVisitorCheckedIn()) {
            details.setVisitorCheckedIn(false);
            Visitors_id = details.getVisitorsId();
            if (UploadImage.equals("Yes")) {
                BitmapFactory.Options options = new BitmapFactory.Options();

                // downsizing image as it throws OutOfMemory Exception for larger
                // images
                /*options.inSampleSize = 8;*/
                Bitmap bitmap = BitmapFactory.decodeFile(ImagePath, options);
                Log.d("debug", "Upload Image Size: "+sizeOf(bitmap));
                new Upload_Image(rotateImage(bitmap, ImagePath), Mobile, Visitors_id).execute();
            } else {
                completed = true;
            }
        }
        if (completed) {
            completed = false;
            Cursor deleteupdated = dataBase.deleteCheckinData(UpdatedID);
            deleteupdated.moveToNext();
            functionCalls.LogStatus("Data Delete: "+UpdatedID);
            if (UploadImage.equals("Yes")) {
                functionCalls.deleteImagefile(ImagePath);
            }
            Cursor datadetails = dataBase.readCheckinData();
            if (datadetails.getCount() > 0) {
                Updatedetails();
            } else {
                Cursor maindata = dataBase.readentrylogdata();
                if (maindata.getCount() > 0) {
                    GetUploadingdetails();
                } else {
                    updatethread.interrupt();
                    this.stopSelf();
                }
            }
        }
    }

    public static Bitmap rotateImage(Bitmap src, String Imagepath) {
        Bitmap bmp = null;
        // create new matrix
        Matrix matrix = new Matrix();
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(Imagepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        if (orientation == 1) {
            if (Device.equals("EL201")) {
                matrix.postRotate(270);
                bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
            } else {
                bmp = src;
            }
        } else if (orientation == 3) {
            matrix.postRotate(180);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        } else if (orientation == 8) {
            matrix.postRotate(270);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        } else {
            matrix.postRotate(90);
            bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        }
        return bmp;
    }

    private void Updatedetails() {
        Cursor data = dataBase.readCheckinData();
        if (data.getCount() > 0) {
            data.moveToNext();
            Name = data.getString(data.getColumnIndex("visitors_name"));
            LogStatus("Service Name: "+Name);
            Email = data.getString(data.getColumnIndex("visitors_email"));
            LogStatus("Service Email: "+Email);
            Mobile = data.getString(data.getColumnIndex("visitors_mobile"));
            LogStatus("Service Mobile: "+Mobile);
            FromAddress = data.getString(data.getColumnIndex("visitors_address"));
            LogStatus("Service Address: "+FromAddress);
            ToMeet = data.getString(data.getColumnIndex("to_meet"));
            LogStatus("Service ToMeet: "+ToMeet);
            Vehicleno = data.getString(data.getColumnIndex("visitors_vehicle_no"));
            LogStatus("Service VehicleNo: "+Vehicleno);
            Visitors_ImagefileName = data.getString(data.getColumnIndex("visitors_photo"));
            LogStatus("Service ImageName: "+Visitors_ImagefileName);
            BarCodeValue = data.getString(data.getColumnIndex("bar_code"));
            LogStatus("Service BarCodeValue: "+BarCodeValue);
            Organizationid = data.getString(data.getColumnIndex("organization_id"));
            LogStatus("Service OrganizationID: "+Organizationid);
            GuardID = data.getString(data.getColumnIndex("security_guards_id"));
            LogStatus("Service GuardId: "+GuardID);
            ImagePath = data.getString(data.getColumnIndex("image_path"));
            LogStatus("Service ImagePath: "+ImagePath);
            UploadImage = data.getString(data.getColumnIndex("upload_image"));
            LogStatus("Service Upload Image: "+UploadImage);
            UpdatedID = data.getString(data.getColumnIndex("_id"));
            LogStatus("Service UpdatedID: "+UpdatedID);
            Visitor_Designation = data.getString(data.getColumnIndex("visitor_designation"));
            LogStatus("Service Visitor_Designation: "+Visitor_Designation);
            Department = data.getString(data.getColumnIndex("department"));
            LogStatus("Service Department: "+Department);
            Purpose = data.getString(data.getColumnIndex("purpose"));
            LogStatus("Service Purpose: "+Purpose);
            House_number = data.getString(data.getColumnIndex("house_number"));
            LogStatus("Service House_number: "+House_number);
            Flat_number = data.getString(data.getColumnIndex("flat_number"));
            LogStatus("Service Flat_number: "+Flat_number);
            Block = data.getString(data.getColumnIndex("block"));
            LogStatus("Service Block: "+Block);
            No_Visitor = data.getString(data.getColumnIndex("no_visitor"));
            LogStatus("Service No_Visitor: "+No_Visitor);
            aClass = data.getString(data.getColumnIndex("class"));
            LogStatus("Service aClass: "+aClass);
            Section = data.getString(data.getColumnIndex("section"));
            LogStatus("Service Section: "+Section);
            Student_Name = data.getString(data.getColumnIndex("student_name"));
            LogStatus("Service Student_Name: "+Student_Name);
            ID_Card = data.getString(data.getColumnIndex("id_card_number"));
            LogStatus("Service ID_Card: "+ID_Card);
            Device = data.getString(data.getColumnIndex("device"));
            LogStatus("Service Device: "+Device);
            Visitor_Entry = data.getString(data.getColumnIndex("visitor_entry"));
            LogStatus("Service Visitor_Entry: "+Visitor_Entry);
            VisitorsCheckIn checkin = task.new VisitorsCheckIn(details, Name, Email, Mobile,
                    FromAddress, ToMeet, Vehicleno, Visitors_ImagefileName, Organizationid, GuardID, BarCodeValue,
                    Visitor_Designation, Department, Purpose, House_number, Flat_number, Block, No_Visitor, aClass,
                    Section, Student_Name, ID_Card, Visitor_Entry);
            checkin.execute();
        }
    }

    private void GetUploadingdetails() {
        Cursor data = dataBase.readentrylogdata();
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                String Name = data.getString(data.getColumnIndex("visitors_name"));
                LogStatus("Database Name "+Name);
                String Email = data.getString(data.getColumnIndex("visitors_email"));
                LogStatus("Database Email "+Email);
                String Mobile = data.getString(data.getColumnIndex("visitors_mobile"));
                LogStatus("Database Mobile "+Mobile);
                String FromAddress = data.getString(data.getColumnIndex("visitors_address"));
                LogStatus("Database FromAddress "+FromAddress);
                String ToMeet = data.getString(data.getColumnIndex("to_meet"));
                LogStatus("Database ToMeet "+ToMeet);
                String Vehicleno = data.getString(data.getColumnIndex("visitors_vehicle_no"));
                LogStatus("Database Vehicleno "+Vehicleno);
                String Visitors_ImagefileName = data.getString(data.getColumnIndex("visitors_photo"));
                LogStatus("Database Visitors_ImagefileName "+Visitors_ImagefileName);
                String BarCode = data.getString(data.getColumnIndex("bar_code"));
                LogStatus("Database BarCode "+BarCode);
                String Organizationid = data.getString(data.getColumnIndex("organization_id"));
                LogStatus("Database Organizationid "+Organizationid);
                String GuardID = data.getString(data.getColumnIndex("security_guards_id"));
                LogStatus("Database GuardID "+GuardID);
                String ImagePath = data.getString(data.getColumnIndex("image_path"));
                LogStatus("Database ImagePath "+ImagePath);
                String UploadImage = data.getString(data.getColumnIndex("upload_image"));
                LogStatus("Database UploadImage "+UploadImage);
                String Visitor_Designation = data.getString(data.getColumnIndex("visitor_designation"));
                LogStatus("Database Visitor_Designation "+Visitor_Designation);
                String Department = data.getString(data.getColumnIndex("department"));
                LogStatus("Database Department "+Department);
                String Purpose = data.getString(data.getColumnIndex("purpose"));
                LogStatus("Database Purpose "+Purpose);
                String House_number = data.getString(data.getColumnIndex("house_number"));
                LogStatus("Database House_number "+House_number);
                String Flat_number = data.getString(data.getColumnIndex("flat_number"));
                LogStatus("Database Flat_number "+Flat_number);
                String Block = data.getString(data.getColumnIndex("block"));
                LogStatus("Database Block "+Block);
                String No_Visitor = data.getString(data.getColumnIndex("no_visitor"));
                LogStatus("Database No_Visitor "+No_Visitor);
                String Aclass = data.getString(data.getColumnIndex("class"));
                LogStatus("Database Aclass "+Aclass);
                String Section = data.getString(data.getColumnIndex("section"));
                LogStatus("Database Section "+Section);
                String Student_Name = data.getString(data.getColumnIndex("student_name"));
                LogStatus("Database Student_Name "+Student_Name);
                String ID_Card = data.getString(data.getColumnIndex("id_card_number"));
                LogStatus("Database ID_Card "+ID_Card);
                String Device = data.getString(data.getColumnIndex("device"));
                LogStatus("Database Device "+Device);
                String Visitor_Entry = data.getString(data.getColumnIndex("visitor_entry"));
                LogStatus("Database Visitor_Entry: "+Visitor_Entry);
                dataBase.insertcheckin(Name, Email, Mobile, FromAddress, ToMeet, Vehicleno, Visitors_ImagefileName,
                        ImagePath, BarCode, Organizationid, GuardID, UploadImage, Visitor_Designation, Department, Purpose,
                        House_number, Flat_number, Block, No_Visitor, Aclass, Section, Student_Name, ID_Card, Device, Visitor_Entry);
            }
            Cursor delete = dataBase.deleteentrylogdata();
            delete.moveToNext();
        }

        Updatedetails();
    }

    private void LogStatus(String str) {
        Log.d("debug", str);
    }

    @Override
    public void onDestroy() {
        Servicerunning = false;
        editor.putString("UpdateData", "Destroyed");
        editor.commit();
    }

    public String compressImage(String imageUri) {

        String filePath = imageUri;
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = imageUri;
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}
