package com.example.georgi.myapplication;


import android.support.annotation.NonNull;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Kmeans {
    private static final boolean DEBUG = false;
    private static final String TAG = "KMeans";
    private final Random mRandomState;
    private final int mMaxIterations;
    private float mSqConvergenceEpsilon;
    public Kmeans() {
        this(new Random());
    }
    public Kmeans(Random random) {
        this(random, 30 /* maxIterations */, 0.005f /* convergenceEpsilon */);
    }
    public Kmeans(Random random, int maxIterations, float convergenceEpsilon) {
        mRandomState = random;
        mMaxIterations = maxIterations;
        mSqConvergenceEpsilon = convergenceEpsilon * convergenceEpsilon;
    }


    public List<Mean> predict(final int k, final float[][] inputData) {
        checkDataSetSanity(inputData);
        int dimension = inputData[0].length;
        final ArrayList<Mean> means = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            Mean m = new Mean(dimension);
            for (int j = 0; j < dimension; j++) {
                m.mCentroid[j] = mRandomState.nextFloat();
            }
            means.add(m);
        }
        // Iterate until we converge or run out of iterations
        boolean converged = false;
        for (int i = 0; i < mMaxIterations; i++) {
            converged = step(means, inputData);
            if (converged) {
                if (DEBUG) Log.d(TAG, "Converged at iteration: " + i);
                break;
            }
        }
        if (!converged && DEBUG) Log.d(TAG, "Did not converge");
        Log.i("Kmeans", "am ajuns aici");


        return means;
    }
    /**
     * Score calculates the inertia between means.
     * This can be considered as an E step of an EM algorithm.
     *
     * @param means Means to use when calculating score.
     * @return The score
     */
    public static double score(@NonNull List<Mean> means) {
        double score = 0;
        final int meansSize = means.size();
        for (int i = 0; i < meansSize; i++) {
            Mean mean = means.get(i);
            for (int j = 0; j < meansSize; j++) {
                Mean compareTo = means.get(j);
                if (mean == compareTo) {
                    continue;
                }
                double distance = Math.sqrt(sqDistance(mean.mCentroid, compareTo.mCentroid));
                score += distance;
            }
        }
        return score;
    }

    public void checkDataSetSanity(float[][] inputData) {
        if (inputData == null) {
            throw new IllegalArgumentException("Data set is null.");
        } else if (inputData.length == 0) {
            throw new IllegalArgumentException("Data set is empty.");
        } else if (inputData[0] == null) {
            throw new IllegalArgumentException("Bad data set format.");
        }
        final int dimension = inputData[0].length;
        final int length = inputData.length;
        for (int i = 1; i < length; i++) {
            if (inputData[i] == null || inputData[i].length != dimension) {
                throw new IllegalArgumentException("Bad data set format.");
            }
        }
    }
    /**
     * K-Means iteration.
     *
     * @param means Current means
     * @param inputData Input data
     * @return True if data set converged
     */
    private boolean step(final ArrayList<Mean> means, final float[][] inputData) {
        // Clean up the previous state because we need to compute
        // which point belongs to each mean again.
        for (int i = means.size() - 1; i >= 0; i--) {
            final Mean mean = means.get(i);
            mean.mClosestItems.clear();
        }
        for (int i = inputData.length - 1; i >= 0; i--) {
            final float[] current = inputData[i];
            final Mean nearest = nearestMean(current, means);
            nearest.mClosestItems.add(current);
        }
        boolean converged = true;
        // Move each mean towards the nearest data set points
        for (int i = means.size() - 1; i >= 0; i--) {
            final Mean mean = means.get(i);
            if (mean.mClosestItems.size() == 0) {
                continue;
            }
            // Compute the new mean centroid:
            //   1. Sum all all points
            //   2. Average them
            final float[] oldCentroid = mean.mCentroid;
            mean.mCentroid = new float[oldCentroid.length];
            for (int j = 0; j < mean.mClosestItems.size(); j++) {
                // Update each centroid component
                for (int p = 0; p < mean.mCentroid.length; p++) {
                    mean.mCentroid[p] += mean.mClosestItems.get(j)[p];
                }
            }
            for (int j = 0; j < mean.mCentroid.length; j++) {
                mean.mCentroid[j] /= mean.mClosestItems.size();
            }
            // We converged if the centroid didn't move for any of the means.
            if (sqDistance(oldCentroid, mean.mCentroid) > mSqConvergenceEpsilon) {
                converged = false;
            }
        }
        return converged;
    }

    public static Mean nearestMean(float[] point, List<Mean> means) {
        Mean nearest = null;
        float nearestDistance = Float.MAX_VALUE;
        final int meanCount = means.size();
        for (int i = 0; i < meanCount; i++) {
            Mean next = means.get(i);
            // We don't need the sqrt when comparing distances in euclidean space
            // because they exist on both sides of the equation and cancel each other out.
            float nextDistance = sqDistance(point, next.mCentroid);
            if (nextDistance < nearestDistance) {
                nearest = next;
                nearestDistance = nextDistance;
            }
        }
        return nearest;
    }

    public static float sqDistance(float[] a, float[] b) {
        float dist = 0;
        final int length = a.length;
        for (int i = 0; i < length; i++) {
            dist += (a[i] - b[i]) * (a[i] - b[i]);
        }
        return dist;
    }
    /**
     * Definition of a mean, contains a centroid and points on its cluster.
     */
    public static class Mean {
        float[] mCentroid;
        final ArrayList<float[]> mClosestItems = new ArrayList<>();
        public Mean(int dimension) {
            mCentroid = new float[dimension];
        }
        public Mean(float ...centroid) {
            mCentroid = centroid;
        }
        public float[] getCentroid() {
            return mCentroid;
        }
        public List<float[]> getItems() {
            return mClosestItems;
        }
        @Override
        public String toString() {
            return "Mean(centroid: " + Arrays.toString(mCentroid) + ", size: "
                    + mClosestItems.size() + ")";
        }
    }
}
