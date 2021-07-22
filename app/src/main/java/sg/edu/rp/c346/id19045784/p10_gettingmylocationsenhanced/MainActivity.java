package sg.edu.rp.c346.id19045784.p10_gettingmylocationsenhanced;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {
    TextView tvLang, tvLong;
    Button btnStart, btnStop, btnCheckUpdate;
    ToggleButton toggleMusic;
    private GoogleMap map;
    LocationRequest mLocationRequest;
    String folderLocation;
    Double Lat;
    Double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLang = findViewById(R.id.tvLang);
        tvLong = findViewById(R.id.tvLng);
        toggleMusic = findViewById(R.id.toggleMusic);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnCheckUpdate = findViewById(R.id.btnCheckRecord);

        mLocationRequest = new LocationRequest();

        folderLocation = getFilesDir().getAbsolutePath() + "/MyFolder";


        File folder = new File(folderLocation);
        if (folder.exists() == false){
            boolean result = folder.mkdir();
            if (result == true){
                Log.d("File Read/Write", "Folder created");
            }
        }

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)
                fm.findFragmentById(R.id.map);

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        checkPermission();
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    String msg = "Lat: " + location.getLatitude() + " Lng : " + location.getLongitude();
                    tvLang.setText("Latitude: " + location.getLatitude());
                    tvLong.setText("Longitude: " + location.getLongitude());
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    LatLng poi_Singapore = new LatLng(location.getLatitude(), location.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi_Singapore,
                            15));

                    Marker op = map.addMarker(new
                            MarkerOptions()
                            .position(poi_Singapore)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


                } else {
                    String msg = "No Last known location found";
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
        tvLang.setText("Latitude: " + Lat);
        tvLong.setText("Longitude: " + lng);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                UiSettings ui = map.getUiSettings();
                ui.setCompassEnabled(true);

                ui.setZoomControlsEnabled(true);

                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

                if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                } else {
                    Log.e("GMap - Permission", "GPS access has not been granted");
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }


            }
        });
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    map.clear();
                    Location data = locationResult.getLastLocation();
                    double lat = data.getLatitude();
                    double lng1 = data.getLongitude();
                    tvLang.setText("Latitude: " + lat);
                    tvLong.setText("Longitude: " + lng1);
                    // Toast.makeText(MainActivity.this, "Lat : " + lat + " Lng : " + lng , Toast.LENGTH_SHORT).show();
                    LatLng poi_Singapore = new LatLng(lat, lng1);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi_Singapore,
                            15));

                    Marker op = map.addMarker(new
                            MarkerOptions()
                            .position(poi_Singapore)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


                    try{
                        String folderLocation_I = getFilesDir().getAbsolutePath() + "/MyFolder";;
                        File targetFile_I = new File(folderLocation_I, "location.txt");
                        FileWriter write_I = new FileWriter(targetFile_I, true);
                        write_I.write(lat + ", " + lng1  + "\n");
                        write_I.flush();
                        write_I.close();
                    }
                    catch (Exception e){
                        Toast.makeText(MainActivity.this, "Failed to write!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }


        };
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setSmallestDisplacement(500);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
                startService(new Intent(MainActivity.this, MyService2.class));


            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, MyService2.class));
            }
        });

        btnCheckUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                startActivity(intent);
            }
        });

        toggleMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    startService(new Intent(MainActivity.this, MyService.class));
                }
                else {
                    stopService(new Intent(MainActivity.this, MyService.class));
                }
            }
        });

    }

    private void checkPermission() {
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {

        } else {
            Log.e("GMap - Permission", "GPS access has not been granted");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

        }
    }
}