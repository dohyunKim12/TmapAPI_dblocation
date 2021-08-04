package com.example.tmapapi_firstattempt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
import android.widget.Button;
import android.widget.EditText;
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
    myDBHelper myHelper;
    Button btnInsert;
    EditText edtLat, edtLong;
    SQLiteDatabase sqlDB;

    TMapView tMapView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        TMapView tMapView = new TMapView(this);
        ImageButton btnPresent = (ImageButton)findViewById(R.id.btnPresent);
        TMapMarkerItem markerItem1 = new TMapMarkerItem();

        edtLat = (EditText) findViewById(R.id.edtLat);
        edtLong = (EditText) findViewById(R.id.edtLong);
        btnInsert = (Button) findViewById(R.id.btnInsert);
        myHelper = new myDBHelper(this);


        presentLat = 37.4954383;
        presentLong = 126.9594522;
        TMapPoint presentPoint = new TMapPoint(presentLat, presentLong);

        tMapView.setSKTMapApiKey(API_KEY);
        linearLayoutTmap.addView(tMapView);

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

        btnInsert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sqlDB = myHelper.getWritableDatabase();
                sqlDB.execSQL("INSERT INTO locationTBL VALUES ( " + edtLat.getText().toString() + " , " + edtLong.getText().toString() + ");");
                sqlDB.close();
                Toast.makeText(getApplicationContext(),"입력됨.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context){
            super(context, "locationDB", null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL("CREATE TABLE locationTBL ( locLatitude DOUBLE PRIMARY KEY, locLongtitude DOUBLE);");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS locationTBL");
            onCreate(db);
        }
    }
}