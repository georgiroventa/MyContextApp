package com.example.georgi.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
public class AwarenessLaunchActivity extends AppCompatActivity {

       @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_awareness_launch);

            //snap shot api demo
            findViewById(R.id.snap_shot_api_demo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    startActivity(new Intent(AwarenessLaunchActivity.this, SnapshotApiActivity.class));

                }
            });



           /* //fence api demo
           findViewById(R.id.headphone_fence_api_demo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    startActivity(new Intent(AwarenessLaunchActivity.this, HeadphoneFenceApiActivity.class));
                }
            });

            //activity recognition fence api demo
            findViewById(R.id.activity_fence_api_demo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    startActivity(new Intent(AwarenessLaunchActivity.this, ActivityFenceApiDemo.class));
                }
            });

            //combine fence api demo
            findViewById(R.id.combine_fence_api_demo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    startActivity(new Intent(AwarenessLaunchActivity.this, CombineFenceApiActivity.class));
                }
            });*/
        }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(AwarenessLaunchActivity.this,ParentActivity.class);
        startActivity(intent);
        finish();
    }

    }
