package com.example.led;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity {


    String channelId = "100";
    String channelName = "NotificationChannel";
    String description = "Chanel des";
    int gas, humidity, temperature;
    ProgressBar progressbar;

    Switch led1;
    Switch led2;
    Switch led3;
    Switch led4;
    TextView value;
    TextView hum;
    TextView t;

    TextView text;
    DatabaseReference dref;
    DatabaseReference dref1;
    DatabaseReference dref2;
    String status;
    String temp;
    String humi;

    private SharedPreferences sharedPreferences;
    public static final String ex = "led1";
    public static final String cx = "led2";
    public static final String dx = "led3";
    public static final String fx = "led4";

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        value = (TextView) findViewById(R.id.textView3);

        led1 = (Switch) findViewById(R.id.switch1);
        led2 = (Switch) findViewById(R.id.switch2);
        led3 = (Switch) findViewById(R.id.switch3);
        led4 = (Switch) findViewById(R.id.switch4);


        text = (TextView) findViewById(R.id.text);
        t = (TextView) findViewById(R.id.temp);
        hum = (TextView) findViewById(R.id.humi);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);


        sharedPreferences = getSharedPreferences("", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        led1.setChecked(sharedPreferences.getBoolean(ex, false));
        led2.setChecked(sharedPreferences.getBoolean(cx, false));
        led3.setChecked(sharedPreferences.getBoolean(dx, false));
        led4.setChecked(sharedPreferences.getBoolean(fx, false));
        progressbar.setVisibility(View.VISIBLE);



        //declaring time function.........
        time();
        //getting no internet pop up messege...............
        SetSwitchClickableOrNot(false);
        new CheckNetworkConnection(this, new CheckNetworkConnection.OnConnectionCallback() {
            @Override
            public void onConnectionSuccess() {
                SetSwitchClickableOrNot(false);
            }

            @Override
            public void onConnectionFail(String msg) {
                ShowAlert();
            }
        }).execute();

        //getting gas output from firebase.........
        dref = FirebaseDatabase.getInstance().getReference();
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                status = dataSnapshot.child("/Test/Int").getValue().toString();//sensor value saved on status variable
                value.setText(status);
                progressbar.setVisibility(View.GONE);
                SetSwitchClickableOrNot(true);
                gas = Integer.parseInt(status);
                Log.e("MainActivity", "Gas value : " + gas);
                if (gas > 1000) {
                    Log.e("MainActivity", "aLERT");
                    displayNotification();
                } else {
                    Log.e("MainActivity", "ok");
                }
            }
               @Override
                public void onCancelled(DatabaseError databaseError)
                 {

                 }
        });

        Log.e("MainActivity", "Testing Purpose : " + gas);

        //getting temperature output from firebase.........
        dref1 = FirebaseDatabase.getInstance().getReference();
        dref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                temp = dataSnapshot.child("/Test/temperature").getValue().toString();//sensor value saved on status variable
                t.setText(temp + "°C");
                temperature = Integer.parseInt(temp);
                Log.e("MainActivity", "Temperature value : " + temperature + "°C");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //getting humidity output from firebase.........
        dref2 = FirebaseDatabase.getInstance().getReference();
        dref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                humi = dataSnapshot.child("/Test/humidity").getValue().toString();//sensor value saved on status variable
                hum.setText(humi + "%");
                humidity = Integer.parseInt(humi);
                Log.e("MainActivity", "Humidity value : " + humidity + "%");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


///////////////////////////////////Button 1 click.......
        led1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {
                    // Write a message to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("/Test/led1");
                    myRef.setValue(0);
                    editor.putBoolean(ex, true);
                    Toast.makeText(MainActivity.this, "LED1 is ON now", Toast.LENGTH_SHORT).show();

                } else {
                    // Write a message to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("/Test/led1");
                    editor.putBoolean(ex, false);
                    myRef.setValue(1);
                    Toast.makeText(MainActivity.this, "LED1 is OFF now", Toast.LENGTH_SHORT).show();

                }
                editor.commit();
            }
        });
        ///////////////////////////////////Button 2 click.......
        led2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {
                    // Write a message to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("/Test/led2");
                    editor.putBoolean(cx, true);
                    myRef.setValue(2);
                    Toast.makeText(MainActivity.this, "LED2 is ON now", Toast.LENGTH_SHORT).show();


                } else {
                    // Write a message to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("/Test/led2");
                    editor.putBoolean(cx, false);
                    myRef.setValue(3);
                    Toast.makeText(MainActivity.this, "LED2 is OFF now", Toast.LENGTH_SHORT).show();

                }
                editor.commit();
            }
        });
        ///////////////////////////////////Button 3 click.......
        led3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {
                    // Write a message to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("/Test/led3");
                    editor.putBoolean(dx, true);
                    myRef.setValue(4);
                    Toast.makeText(MainActivity.this, "LED3 is ON now", Toast.LENGTH_SHORT).show();


                } else {
                    // Write a message to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("/Test/led3");
                    editor.putBoolean(dx, false);
                    myRef.setValue(5);
                    Toast.makeText(MainActivity.this, "LED3 is OFF now", Toast.LENGTH_SHORT).show();

                }
                editor.commit();
            }
        });
        ///////////////////////////////////Button 4 click.......
        led4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {
                    // Write a message to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("/Test/led4");
                    editor.putBoolean(fx, true);
                    myRef.setValue(6);
                    Toast.makeText(MainActivity.this, "LED4 is ON now", Toast.LENGTH_SHORT).show();


                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("/Test/led4");
                    editor.putBoolean(fx, false);
                    myRef.setValue(7);
                    Toast.makeText(MainActivity.this, "LED4 is OFF now", Toast.LENGTH_SHORT).show();


                }
                editor.commit();
            }
        });


    }

    //////////////for calculating time.....
    public void time() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");

                                String currenttime = simpleDateFormat.format(calendar.getTime());
                                text.setText(currenttime);
                            }
                        });
                    }
                } catch (Exception e) {
                    text.setText(R.string.app_name);
                }

            }
        };
        thread.start();
    }

    //for exit messege................
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure to Exit this app?");
        builder.setCancelable(true);
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //for getting notification
    private void displayNotification() {

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(10, builder.build());

    }

    //No Internet Pop Up.............................
   /* private boolean isConnected() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }*/

    void ShowAlert() {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("No Internet!")
                .setMessage("Please Check Your Internet Connection.")
                .setPositiveButton("Colse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    void SetSwitchClickableOrNot(boolean isClickable) {
        led1.setClickable(isClickable);
        led2.setClickable(isClickable);
        led3.setClickable(isClickable);
        led4.setClickable(isClickable);
    }

}












