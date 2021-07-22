package sg.edu.rp.c346.id19045784.p10_gettingmylocationsenhanced;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileWriter;

public class MyService extends Service {

    boolean started;
    private GoogleMap map;
    // declaring object of MediaPlayer
    private MediaPlayer player = new MediaPlayer();

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                map.clear();
                Location data = locationResult.getLastLocation();
                double lat = data.getLatitude();
                double lng1 = data.getLongitude();
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
                  //  Toast.makeText(MainActivity.this, "Failed to write!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }


    };


    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("MyService", "Service created");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Log.d("onStart", "testetestwse");
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFolder", "music.mp3");

            // specify the path of the audio file
            player.setDataSource(file.getPath());
            player.prepare();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // providing the boolean value as true to play the audio on loop
        player.setLooping(true);

        // starting the process
        player.start();

        // returns the status of the program
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("MyService", "Service exited");
        super.onDestroy();

        player.stop();
    }
}