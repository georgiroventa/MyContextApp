package com.example.georgi.myapplication;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class ColectJobService extends JobService {
    private static final String TAG = "ColectobService";
    private boolean jobCancelled = false;
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        new SnapshotApiActivity().callSnapShotGroupApis();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        return false;
    }
}
