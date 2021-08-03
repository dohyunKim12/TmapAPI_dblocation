package com.example.tmapapi_firstattempt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String API_KEY = "l7xx9f2b58a7392548a5985f294f0b3e125e";
    double Longtitude = 0;
    double presentLong = 0;
    double Latitude = 0;
    double presentLat = 0;

    //GpsTracker
    //public GpsTracker gpsTracker;

    TMapView tMapView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        TMapView tMapView = new TMapView(this);
        ImageButton btnPresent = (ImageButton)findViewById(R.id.btnPresent);
        TMapMarkerItem markerItem1 = new TMapMarkerItem();

        presentLat = 37.4954383;
        presentLong = 126.9594522;
        TMapPoint presentPoint = new TMapPoint(presentLat, presentLong);

        tMapView.setSKTMapApiKey(API_KEY);
        linearLayoutTmap.addView(tMapView);

        //Event settings
        tMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback(){
            @Override
            public boolean onPressEvent(ArrayList arrayList, ArrayList arrayList1, TMapPoint tMapPoint, PointF pointF){
                Toast.makeText(MainActivity.this, "onPress~!", Toast.LENGTH_SHORT).show();
                return false;
            }
            @Override
            public boolean onPressUpEvent(ArrayList arrayList, ArrayList arrayList1, TMapPoint tMapPoint, PointF pointF){
                Toast.makeText(MainActivity.this, "onPressUp~!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        //marker Icon
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_pin_red);
//
//        markerItem1.setIcon(bitmap);
//        markerItem1.setPosition(0.5f,1.0f);
//        markerItem1.setTMapPoint(tMapPoint1);
//        markerItem1.setName("SKTtower");
//        tMapView.addMarkerItem("markerItme1",markerItem1);

//        tMapView.setCenterPoint(126.985302, 37.570841);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_pin_red);
        //btn settings
        btnPresent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                tMapView.setCenterPoint(presentLong,presentLat);
                markerItem1.setIcon(bitmap);
                markerItem1.setPosition(0.5f,1.0f);
                markerItem1.setTMapPoint(presentPoint);
                tMapView.addMarkerItem("markerItme1",markerItem1);

            }
        });


    }

}