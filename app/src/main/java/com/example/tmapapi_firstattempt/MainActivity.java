package com.example.tmapapi_firstattempt;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {
    ArrayList<ArrayList<String>> records = new ArrayList<ArrayList<String>>();

    String API_KEY = "l7xx9f2b58a7392548a5985f294f0b3e125e";
    double presentLong = 0;
    double presentLat = 0;

    ImageButton btnPresent;
    TextView txtAccCnt;

    Handler handler = null;
    int len_records = 0;
    int tmp_len_records = 0;
    static int record_cnt_prev = 999;
    static int record_cnt_cur = 0;

    TMapView tMapView = null;
    TMapGpsManager tMapGPS = null;

    Geocoder geocoder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPresent = (ImageButton) findViewById(R.id.btnPresent);
        txtAccCnt = (TextView) findViewById(R.id.accCnt);
        //edtLatResult = (EditText) findViewById(R.id.edtLatResult);
        //edtLongResult = (EditText) findViewById(R.id.edtLongResult);

        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);

        //Seoul NIA
//        presentLat = 37.568700;
//        presentLong = 126.979244;

        TMapPoint presentPoint = new TMapPoint(presentLat, presentLong);
        TMapMarkerItem markerItem_pres = new TMapMarkerItem();

        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey(API_KEY);
        //Initial Setting
        tMapView.setZoomLevel(17);
        tMapView.setIconVisibility(true);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        linearLayoutTmap.addView(tMapView);
        //Request for GPS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
        }

        tMapGPS = new TMapGpsManager(this);
        tMapGPS.setMinTime(1000);
        tMapGPS.setMinDistance(10);
        tMapGPS.setProvider(tMapGPS.NETWORK_PROVIDER);

        tMapGPS.OpenGps();


        geocoder = new Geocoder(this);

        // 현재위치 표시
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.current_pos2);
        //btn settings
        btnPresent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                tMapView.setCenterPoint(presentLong,presentLat);
                markerItem_pres.setIcon(bitmap);
                markerItem_pres.setPosition(0.5f,1.0f);
                markerItem_pres.setTMapPoint(presentPoint);
                tMapView.addMarkerItem("markerItem", markerItem_pres);
                String addr = "";
                List<Address> list = null;
                try{
                    list = geocoder.getFromLocation(Double.valueOf(presentLat), Double.valueOf(presentLong), 10);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (list != null){
                    if (list.size() == 0){
                        addr = "해당되는 주소 정보는 없습니다.";
                    }
                    else {addr = list.get(0).getAddressLine(0);}
                }
                txtAccCnt.setText(addr);
            }
        });
        // Handler
        handler = new Handler();
        queryThread thread = new queryThread();
        handler.post(thread);



    }

    @Override
    public void onLocationChange(Location location){
        presentLong = location.getLongitude();
        presentLat = location.getLatitude();
        tMapView.setLocationPoint(presentLong, presentLat);
        tMapView.setCenterPoint(presentLong, presentLat);
    }
    private void setBalloonView(TMapMarkerItem marker, String title, String address)
    {
        marker.setCanShowCallout(true);

        if( marker.getCanShowCallout() )
        {
            Thread ts = new Thread(){
                public void run(){
                    String img_url = "https://share.ryunchang.xyz/accident.jpg";
                    Bitmap bitmap = getImageFromURL(img_url);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 460, 350,false);
//                    bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50,false);
                    marker.setCalloutTitle(title);
                    marker.setCalloutSubTitle(address);
                    marker.setCalloutLeftImage(bitmap);
                }
            };
            ts.start();
        }
    }

    // Get Image From URL
    public static Bitmap getImageFromURL(String imageURL){
        Bitmap imgBitmap = null;
        HttpURLConnection conn = null;
        BufferedInputStream bis = null;

        try
        {
            URL url = new URL(imageURL);
            conn = (HttpURLConnection)url.openConnection();
            conn.connect();

            int nSize = conn.getContentLength();
            bis = new BufferedInputStream(conn.getInputStream(), nSize);
            imgBitmap = BitmapFactory.decodeStream(bis);
        }
        catch (Exception e){
            e.printStackTrace();
        } finally{
            if(bis != null) {
                try {bis.close();} catch (IOException e) {}
            }
            if(conn != null ) {
                conn.disconnect();
            }
        }

        return imgBitmap;

    }

    private Bitmap createMarkerIcon(int image)
    {
        Log.e("MapViewActivity", "(F)   createMarkerIcon()");

        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                image);
//        bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50,false);

        return bitmap;
    }
    class queryThread extends Thread {
        public void run() {
            URLConnector url = new URLConnector();
            url.start();

            try {url.join();}
            catch (Exception e){e.printStackTrace();}

            String result = url.getTemp();

            if(result.equals("no results\n")){
                record_cnt_cur = 0;
            }
            else cntParseJSON(result);

            if (record_cnt_prev != record_cnt_cur){
                update_records();
            }


            handler.post(this);
        }
    }
    void update_records()
    {
        System.out.println("rec_prev: "+record_cnt_prev+" rec_cur: "+record_cnt_cur);
        record_cnt_prev = record_cnt_cur;

        records.clear();
        URLConnector url = new URLConnector();
        url.start();

        try {url.join();}
        catch (Exception e){e.printStackTrace();}

        String result = url.getTemp();

        if(result.equals("no results\n")){
            len_records = 0;
        }
        else ParseJSON(result);

        //Toast msg
        if(tmp_len_records<len_records) {

            String occ_toast = records.get(len_records-1).get(0);
            String lat_geo = records.get(len_records-1).get(1);
            String long_geo = records.get(len_records-1).get(2);
            String text_toast = "";

            List<Address> list = null;
            try{
                list = geocoder.getFromLocation(Double.valueOf(lat_geo), Double.valueOf(long_geo), 10);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (list != null){
                if (list.size() == 0){
                    text_toast = "해당되는 주소 정보는 없습니다.";
                }
                else {text_toast = list.get(0).getAddressLine(0);}
            }

            /*Toast.makeText(getApplicationContext(),"교통사고 발생 알림!! \n시간 : "+occ_toast+"\n장소 : "+text_toast,Toast.LENGTH_LONG).show();*/

            View layout = getLayoutInflater().inflate(R.layout.toast_layout, null);
            TextView textView = layout.findViewById(R.id.Toast);
            textView.setText("교통사고 발생 알림!! \n시간 : "+occ_toast+"\n장소 : "+text_toast);
            Toast toastView = Toast.makeText(getApplicationContext(),"교통사고 발생 알림!! \n시간 : "+occ_toast+"\n장소 : "+text_toast,Toast.LENGTH_LONG);

            toastView.setView(layout);
            toastView.show();
        }
        String lats = "";
        String longs = "";

        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.accident_pos);
        Double Lat;
        Double Long;

        for (int i=0;i<tmp_len_records;i++){
            tMapView.removeMarkerItem2("markeritem"+i);
        }
        System.out.println("len_recodrds: "+len_records);

        for (int i=0;i< len_records;i++){
            TMapMarkerItem markerItem = new TMapMarkerItem();
            lats += (records.get(i).get(1).toString() + "\n");
            longs += (records.get(i).get(2).toString() + "\n");

            Lat = Double.valueOf(records.get(i).get(1));
            Long = Double.valueOf(records.get(i).get(2));

            TMapPoint point = new TMapPoint(Lat, Long);
            markerItem.setIcon(bitmap2);
            markerItem.setID("markeritem"+i);
            markerItem.setPosition(0.5f,1.0f);
            markerItem.setTMapPoint(point);
            tMapView.addMarkerItem("markeritem"+i,markerItem);
            String title_tmp = records.get(i).get(0).toString();
            title_tmp = title_tmp.substring(10,title_tmp.length()-3);
            String title_hour = title_tmp.substring(0,title_tmp.length()-3);
            String title_min = title_tmp.substring(4);
            String title = title_hour + "시 " + title_min + "분 ";


            setBalloonView(markerItem, title,"" );

        }

        tmp_len_records = len_records;

        if (len_records >= 1) {
            lats = lats.substring(0, lats.length()-1);      //마지막 개행문자 제거.
            longs = longs.substring(0, longs.length()-1);   //마지막 개행문자 제거.
        }

    }


    public void cntParseJSON(String target){
        try {
            JSONObject json = new JSONObject(target);

            JSONArray arr = json.getJSONArray("accident");
            record_cnt_cur = arr.length();
            System.out.println("recs: "+record_cnt_cur);
            return;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return;
    }
    public void ParseJSON(String target){
        try {
            JSONObject json = new JSONObject(target);

            JSONArray arr = json.getJSONArray("accident");

            System.out.println(arr.length());

            len_records = 0;
            for(int i = 0; i < arr.length(); i++){
                //System.out.println("init recs:"+ records);
                //record.clear();
                ArrayList<String> record = new ArrayList<String>();
                JSONObject json2 = arr.getJSONObject(i);
                record.add(json2.getString("occur_time"));
                //System.out.println("record " + i + ": "+record);
                record.add(json2.getString("latitude"));
                //System.out.println("record " + i + ": "+record);
                record.add(json2.getString("longitude"));
                //System.out.println("record " + i + ": "+record);

                records.add(record);
                //System.out.println("records : : : " + records.toString());

                len_records++;
                //System.out.println("len_rocrods: "+len_records);
                //System.out.println("rocrods: "+records);
            }

            return;
        }

        catch(Exception e){
            e.printStackTrace();
        }

        return;
    }

}