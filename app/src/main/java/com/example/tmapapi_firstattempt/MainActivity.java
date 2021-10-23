package com.example.tmapapi_firstattempt;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<ArrayList<String>> records = new ArrayList<ArrayList<String>>();

    String API_KEY = "l7xx9f2b58a7392548a5985f294f0b3e125e";
    double presentLong = 0;
    double presentLat = 0;

    ImageButton btnPresent;
    EditText edtLatResult, edtLongResult;
    TextView txtAccCnt;

    Handler handler = null;
    int len_records = 0;
    int tmp_len_records = 0;

    TMapView tMapView = null;

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

        //Soongsil.Univ
        presentLat = 37.49637;
        presentLong = 126.95742;

        TMapPoint presentPoint = new TMapPoint(presentLat, presentLong);
        TMapMarkerItem markerItem_pres = new TMapMarkerItem();

        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey(API_KEY);
        linearLayoutTmap.addView(tMapView);

        geocoder = new Geocoder(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
        }


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.current_pos);
        //btn settings
        btnPresent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                tMapView.setCenterPoint(presentLong,presentLat);
                markerItem_pres.setIcon(bitmap);
                markerItem_pres.setPosition(0.5f,1.0f);
                markerItem_pres.setTMapPoint(presentPoint);
                tMapView.addMarkerItem("markerItem", markerItem_pres);

                txtAccCnt.setText("서울특별시 동작구 상도로 369");
            }
        });



        // Handler
        handler = new Handler();
        ThreadClass thread = new ThreadClass();
        handler.post(thread);

    }

    class ThreadClass extends Thread {
        public void run() {
            records.clear();
            URLConnector url = new URLConnector();
            url.start();

            try {url.join();}
            catch (Exception e){e.printStackTrace();}

            String result = url.getTemp();

            if(result.equals("no results\n")){
                len_records = 0;
                System.out.println("set len_rec to zero");
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
                tMapView.removeMarkerItem("markeritem"+i);
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
                markerItem.setPosition(0.5f,1.0f);
                markerItem.setTMapPoint(point);
                tMapView.addMarkerItem("markeritem"+i,markerItem);
            }

            tmp_len_records = len_records;

            if (len_records >= 1) {
                lats = lats.substring(0, lats.length()-1);      //마지막 개행문자 제거.
                longs = longs.substring(0, longs.length()-1);   //마지막 개행문자 제거.
            }

           // edtLatResult.setText(lats);
           // edtLongResult.setText(longs);

            handler.post(this);
        }
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