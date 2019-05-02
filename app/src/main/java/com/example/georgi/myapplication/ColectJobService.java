package com.example.georgi.myapplication;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

public class ColectJobService extends JobService {
    private static final String TAG = "ColectJobService";
    private boolean jobCancelled = false;
    private GoogleApiClient mGoogleApiClient1;
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        Snapshot sa = Snapshot.getInstance(getApplicationContext());
        sa.callSnapShotGroupApis();
        sa.save();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        return false;
    }
}
