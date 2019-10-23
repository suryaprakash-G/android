package com.bus.bus_beacon;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HttpsURLConnection;


public class service extends Service implements LocationListener,SensorEventListener{
    HttpURLConnection client;
    public static final String BROADCAST_ACTION = "com.websmithing.broadcasttest.displayevent";
    private final Handler handler = new Handler();
    Intent intent;
    private LocationManager locationManager;
    private String provider;
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener gel;
    private long lastUpdate;
    private float g[]={0,0,0};
    int sm;
    IBinder bnd;
    boolean alrb;
    String ip="192.168.29.58:3006";
    public double lng,lat;
    private Sensor gyro;
    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(){

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_GAME);
        gel=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
               // if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                getAccelerometer(event);
                  //}

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        intent = new Intent(BROADCAST_ACTION);
//            ip= intent.getExtras().getString("s");

        Toast.makeText(this, "service started",
                Toast.LENGTH_LONG).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            System.out.println("location unknown");
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);

    }
    @Override
    public void onSensorChanged(SensorEvent event) {
       // if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            getAccelerometer(event);
      //  }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        g[0] = values[0];
        g[1] = values[1];
        g[2] = values[2];
      //  System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        }

    private void DisplayLoggingInfo() {
        Log.d("location updated" ,"entered DisplayLoggingInfo");

        intent.putExtra("la",lat);
        intent.putExtra("lo",lng);
        intent.putExtra("g1",String.valueOf(g[0]));
        intent.putExtra("g2",String.valueOf(g[1]));
        intent.putExtra("g3",String.valueOf(g[2]));
        sendBroadcast(intent);
    }
    public void onLocationChanged(Location location) {
        lat = (double) (location.getLatitude());
        lng = (double) (location.getLongitude());
        DisplayLoggingInfo();
        System.out.println("lat " + getLat() + "\nlong "+getLng()+"\ngyr "+g[0]+g[1]+g[2]);
        send();

    }
public void send(){


    try {
        URL url = new URL("http://"+ip+"/lc/lc");
        client = (HttpURLConnection) url.openConnection();
        System.out.println("sending to \n http://"+ip+"/lc/lc");
        String t = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        client.setRequestMethod("POST");
        client.setRequestProperty("Key","Value");
        client.setRequestProperty("Content-Type","application/Json");
        client.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(client.getOutputStream());
        Map<String, String> postData = new HashMap<>();
        postData.put("b", "123");
        postData.put("la", String.valueOf(lat));
        postData.put("lo", String.valueOf(lng));
        postData.put("g1",String.valueOf(g[0]));
        postData.put("g2",String.valueOf(g[1]));
        postData.put("g3",String.valueOf(g[2]));
        postData.put("t",t );
        writer.write(postData.toString());
        writer.flush();

        int responseCode=client.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {

            BufferedReader in=new BufferedReader( new InputStreamReader(client.getInputStream()));
            StringBuffer sb = new StringBuffer("");
            String line="";
            while((line = in.readLine()) != null) {
                sb.append(line);
                break;
            }
            in.close();
            System.out.println(sb.toString());
        }
    } catch (ProtocolException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }

}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }
    // service meths
    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "service unbound",
                Toast.LENGTH_LONG).show();
        sensorManager.unregisterListener(this, gyro);
        return alrb;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent) {

    }

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        Toast.makeText(this, "service killed",
                Toast.LENGTH_LONG).show();
    }


}


