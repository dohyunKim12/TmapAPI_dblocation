package com.example.tmapapi_firstattempt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.os.Handler;
import android.text.StaticLayout;
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

    Button btnInit, btnInsert, btnSelect;
    EditText edtLat, edtLong, edtLatResult, edtLongResult;

    SQLiteDatabase sqlDB;

    Handler handler = null;
    int i = 0;

    TMapView tMapView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btnPresent = (ImageButton)findViewById(R.id.btnPresent);

        edtLat = (EditText) findViewById(R.id.edtLat);
        edtLong = (EditText) findViewById(R.id.edtLong);
        edtLatResult = (EditText) findViewById(R.id.edtLatResult);
        edtLongResult = (EditText) findViewById(R.id.edtLongResult);
        btnInit = (Button) findViewById(R.id.btnInit);
        btnInsert = (Button) findViewById(R.id.btnInsert);
        btnSelect = (Button) findViewById(R.id.btnSelect);

        myHelper = new myDBHelper(this);
        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);

        presentLat = 37.4954383;
        presentLong = 126.9594522;
        TMapPoint presentPoint = new TMapPoint(presentLat, presentLong);
        TMapMarkerItem markerItem1 = new TMapMarkerItem();

        tMapView = new TMapView(this);
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
                tMapView.addMarkerItem("markerItem", markerItem1);
            }
        });


        btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlDB = myHelper.getWritableDatabase();
                myHelper.onUpgrade(sqlDB, 1, 2);
                sqlDB.close();
            }
        });
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlDB = myHelper.getWritableDatabase();
                sqlDB.execSQL("INSERT INTO locationTBL VALUES ( " + edtLat.getText().toString() + " , " + edtLong.getText().toString() + ");");
                sqlDB.close();
                Toast.makeText(getApplicationContext(),"입력됨.",Toast.LENGTH_SHORT).show();
            }
        });
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlDB = myHelper.getReadableDatabase();
                Cursor cursor;
                cursor = sqlDB.rawQuery("SELECT * FROM locationTBL;", null);

                String strLats = "경도" + "\r\n" + "-----" + "\r\n";
                String strLongs = "위도" + "\r\n" + "-----" + "\r\n";

                while(cursor.moveToNext()){
                    strLats += cursor.getString(0) + "\r\n";
                    strLongs += cursor.getString(1) + "\r\n";
                }

                edtLatResult.setText(strLats);
                edtLongResult.setText(strLongs);

                cursor.close();
                sqlDB.close();
            }
        });


        // Handler
        handler = new Handler();
        ThreadClass thread = new ThreadClass();
        handler.post(thread);


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
    class ThreadClass extends Thread {
        public void run() {
            if (i == 1){
                sqlDB = myHelper.getReadableDatabase();
                Cursor cursor;
                cursor = sqlDB.rawQuery("SELECT * FROM locationTBL;", null);

                String strLats = "경도" + "\r\n" + "-----" + "\r\n";
                String strLongs = "위도" + "\r\n" + "-----" + "\r\n";

                ArrayList<Double> aryLats = new ArrayList<Double>();
                ArrayList<Double> aryLongs = new ArrayList<Double>();

                while(cursor.moveToNext()) {
                    aryLats.add(Double.valueOf(cursor.getString(0)));
                    aryLongs.add(Double.valueOf(cursor.getString(1)));
                    strLats += cursor.getString(0) + "\r\n";
                    strLongs += cursor.getString(1) + "\r\n";
                }

                edtLatResult.setText(strLats);
                edtLongResult.setText(strLongs);

                cursor.close();
                sqlDB.close();


//                TMapView tMapView = new TMapView(getApplication());
                Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.map_pin_red);
                Double Lat = 0.0;
                Double Long = 0.0;

                for (int i = 0; i < aryLats.size(); i++){
                    TMapMarkerItem markerItem = new TMapMarkerItem();
                    Lat = aryLats.get(i);
                    Long = aryLongs.get(i);
                    TMapPoint point = new TMapPoint(Lat, Long);
                    markerItem.setIcon(bitmap2);
                    markerItem.setPosition(0.5f,1.0f);
                    markerItem.setTMapPoint(point);
                    tMapView.addMarkerItem("markeritem"+i,markerItem);
                }

                i = 0;
            }
            else {
                i = 1;
            }
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }

            handler.post(this);
        }
    }
}