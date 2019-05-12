package com.example.georgi.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ContextDataActivity extends AppCompatActivity {

    private TextView date_time_tv, activity_tv, time_sec_tv, headphone_tv;
    private TextView weather_tv, location_tv;
    private Button button;

    SimpleDateFormat format_day = new SimpleDateFormat("EEEE");
    String dayWeek = format_day.format(new Date());
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("DateAboutContextUser").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(dayWeek);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context_data);

        date_time_tv = (TextView)findViewById(R.id.date_time_tv);
        activity_tv = (TextView)findViewById(R.id.activity_tv);
        time_sec_tv = (TextView)findViewById(R.id.time_sec_tv);
        headphone_tv = (TextView)findViewById(R.id.headphone_tv);
        weather_tv = (TextView)findViewById(R.id.weather_tv);
        location_tv = (TextView)findViewById(R.id.location_tv);
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displayDataContext();
            }
        });
        displayDataContext();

    }

    public void displayDataContext(){
        Query query  = mDatabase.limitToLast(1);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    Log.i("User key", child.getKey());
                    //display current time
                    String time_dateDb = child.getKey();
                    date_time_tv.setText("Date and current time: " + time_dateDb);

                    //display user's activity
                    String activityDb = child.child("activity").getValue(String.class);
                    activity_tv.setText("User's activity: " + activityDb);

                    //display time in seconds
                    Long time_secDb = child.child("time").getValue(Long.class);
                    time_sec_tv.setText("Time in seconds: " + time_secDb);

                    //display status phone
                    String headphoneDb = child.child("headphone").getValue(String.class);
                    headphone_tv.setText("Headphone status: " + headphoneDb);

                    //dispaly location in latitude and longitude
                    Double latitudeDb = child.child("latitude").getValue(Double.class);
                    Double longitudeDb = child.child("longitude").getValue(Double.class);
                    String locationDb = "Latitude: " + latitudeDb + "\nLongitude: " + longitudeDb;
                    location_tv.setText("Current location\n" + locationDb);

                    //display weather
                    Double temperatureDb = child.child("temperature (°C)").getValue(Double.class);
                    Double humdityDb = child.child("humidity").getValue(Double.class);
                    String weatherDb = "Temperature: " + temperatureDb + " °C \nHumidity: " + humdityDb + " %";
                    weather_tv.setText("Current weather\n" + weatherDb);


                    Log.i("User activity", child.child("activity").getValue().toString());
                    Log.i("Time in seconds", child.child("time").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ContextDataActivity.this,ParentActivity.class);
        startActivity(intent);
        finish();
    }
}
