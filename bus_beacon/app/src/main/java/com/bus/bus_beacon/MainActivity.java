package com.bus.bus_beacon;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;

public class MainActivity extends Activity implements View.OnClickListener {


    private TextView latituteField;
    private TextView longitudeField;
    public double lng,lat;
    public void str(){ startService(new Intent(this, service.class));}
    public void stp(){ stopService(new Intent(this, service.class));}
    /** Called when the activity is first created. */
    Button str,stp;
    EditText ip;
    private Intent intent;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        intent = new Intent(this, service.class);
        str=(Button)findViewById(R.id.str);
        stp=(Button)findViewById(R.id.stp);
        ip=(EditText)findViewById(R.id.ip);
        str.setOnClickListener(this);
        stp.setOnClickListener(this);
        latituteField = (TextView) findViewById(R.id.TextView02);
        longitudeField = (TextView) findViewById(R.id.TextView04);
            latituteField.setText("Location not available");
            longitudeField.setText("Location not available");


    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updatel(intent);
        }
    };
   // public static void up(){new MainActivity().updatel();}
    public void updatel(Intent intent) {
        double la=0.0,lo=0.0;
       double lat = intent.getDoubleExtra("la",la);
       double lng = intent.getDoubleExtra("lo",lo);
        latituteField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.str:
              //  intent.putExtra("s",ip.getText().toString());
                startService(new Intent(this, service.class));
                registerReceiver(broadcastReceiver, new IntentFilter(service.BROADCAST_ACTION));
                break;
            case R.id.stp:

                unregisterReceiver(broadcastReceiver);
                stopService(new Intent(this, service.class));
                break;

        }
    }
}

