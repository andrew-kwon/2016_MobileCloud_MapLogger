package kr.ac.kookmin.cs.maplog;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    static Context myContext;
    private TextView locationText;
    private Button myLocation;
    private Button addLocation;
    private GoogleMap mMap;
    LocationManager myLocationManager;
    GPSClass myGPS;
    private double currentLatitude;
    private double currentLongtitude;
    private int MY_PERMISSION_ACCESS_COURSE_LOCATION=11;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS=13;
    MapDB myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myContext=getApplicationContext();
        myGPS= new GPSClass();
        myDB = new MapDB();
        locationText = (TextView) findViewById(R.id.location);
        myLocation = (Button) findViewById(R.id.myLocation);
        addLocation = (Button) findViewById(R.id.addLocation);

        myLocationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,myGPS); //start chcecking

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                LatLng myplace = new LatLng(currentLatitude, currentLongtitude);
                mMap=myDB.showMyLog(mMap);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myplace,13));

            }
        });
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addMaplog(""+currentLatitude,""+currentLongtitude);
            }
        });


    }

    public static Context getContext(){return myContext;}
    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        LatLng sydney = new LatLng(37.5595, 127.0887);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
    }

    public void addMaplog(String latitude, String longtitude) {

        showAlert(latitude, longtitude);

    }

    public void showAlert(String latitude, String longtitude )
    {

        final String curLati = latitude;
        final String curLonti = longtitude;
        AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);
        alert.setTitle("Log");
        alert.setMessage("위치 제목을 입력하세요.");
        // Set an EditText view to get user input
        final EditText input = new EditText(MapsActivity.this);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String logName = input.getText().toString();
                if (logName.equals("")) {
                    Toast.makeText(getApplicationContext(), "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else
                {
                    myDB.sendLogtoDB(logName, curLati, curLonti);
                }
            }
        });
        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
        alert.show();
    }
    class GPSClass implements LocationListener {

        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
//        Log.i("Message: ","Location changed, " + location.getAccuracy() + " , " + location.getLatitude()+ "," + location.getLongitude());
            currentLatitude=location.getLatitude();
            currentLongtitude=location.getLongitude();
            locationText.setText(" "+currentLatitude+" : "+currentLongtitude);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
//    }
    }
}
