package com.example.georgi.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class KmeansActivity extends AppCompatActivity {

    SimpleDateFormat format_day = new SimpleDateFormat("EEEE");
    String dayWeek = format_day.format(new Date());
    //reference to database
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("DateAboutContextUser").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Wednesday");
    private float array_latitude[]/*= new float[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}*/;
    private float array_longitude[];
    private float array_temperature[];
    private float array_humidity[];
    private float array_activity[];
    private float array_headphone[];

    int flag = 0;
    float centroid_latitude[][] = new float[][]{
            {0,0},
            {45.75293350f,45.7497520f}
    };
    float centroid_longitude[][] = new float[][]{
            {0,0},
            {21.25074195f,21.24320220f}
    };
    float centroid_temperature[][] = new float[][]{
            {0,0},
            {10.0f,8.0f}
    };
    float centroid_humidity[][] = new float[][]{
            {0,0},
            {100.0f,99.0f}
    };
    float centroid_activity[][] = new float[][]{
            {0,0},
            {45.7496032f,45.74958038f}
    };
    float centroid_headphone[][] = new float[][]{
            {0,0},
            {45.7496032f,45.74958038f}
    };
    int noOfClusters=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kmeans);
        colectData();

    }

    public float[] getArray_latitude() {
        return array_latitude;
    }

    public float[] getArray_longitude() {
        return array_longitude;
    }

    public float[] getArray_temperature() {
        return array_temperature;
    }

    public float[] getArray_humidity() {
        return array_humidity;
    }

    public float[] getArray_activity() {
        return array_activity;
    }

    public float[] getArray_headphone() {
        return array_headphone;
    }

    public void setArray_latitude(float[] array_latitude) {
        this.array_latitude = array_latitude;
    }

    public void setArray_longitude(float[] array_longitude) {
        this.array_longitude = array_longitude;
    }

    public void setArray_temperature(float[] array_temperature) {
        this.array_temperature = array_temperature;
    }

    public void setArray_humidity(float[] array_humidity) {
        this.array_humidity = array_humidity;
    }

    public void setArray_activity(float[] array_activity) {
        this.array_activity = array_activity;
    }

    public void setArray_headphone(float[] array_headphone) {
        this.array_headphone = array_headphone;
    }

    public void colectData(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i("blablaa", String.valueOf(dataSnapshot.getChildrenCount()));
                //Log.i("blablaa11", String.valueOf(dataSnapshot.getChildren()));
                int i = 0;
                //intialize the matrix
                array_latitude = new float[(int)dataSnapshot.getChildrenCount()];
                array_longitude = new float[(int)dataSnapshot.getChildrenCount()];
                array_temperature = new float[(int)dataSnapshot.getChildrenCount()];
                array_humidity = new float[(int)dataSnapshot.getChildrenCount()];
                array_activity = new float[(int)dataSnapshot.getChildrenCount()];
                array_headphone = new float[(int)dataSnapshot.getChildrenCount()];
                for(DataSnapshot result : dataSnapshot.getChildren()){
                    //Log.i("copii", result.getKey().toString());
                    //Log.i("cevaaa1", String.valueOf(result.child("latitude").getValue(Double.class)));
                    float latitude = result.child("latitude").getValue(Double.class).floatValue();
                    array_latitude[i] = latitude;
                    float longitude = result.child("longitude").getValue(Double.class).floatValue();
                    array_longitude[i] = longitude;
                    float temperature = result.child("temperature (°C)").getValue(Double.class).floatValue();
                    array_temperature[i] = temperature;
                    float humidity = result.child("humidity").getValue(Double.class).floatValue();
                    array_humidity[i] = humidity;
//                    float activity = result.child("activity").getValue(Double.class).floatValue();
//                    array_longitude[i] = activity;
//                    float headphone = result.child("headphone").getValue(Double.class).floatValue();
//                    array_longitude[i] = headphone;
                    //Log.i("din vector", String.valueOf(matrix[i]));
                    //setMatrix(elem,i);

                    i++;
                }
                setArray_latitude(array_latitude);
                setArray_longitude(array_longitude);
                setArray_temperature(array_temperature);
                setArray_humidity(array_humidity);
                setArray_activity(array_activity);
                setArray_headphone(array_headphone);

                flag = 1;
                verify();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    public void verify(){
        if (flag == 1){
            System.out.println("latitudine");
            System.out.println("======================================== ");
            getCentroid(array_latitude, noOfClusters, centroid_latitude);
            System.out.println("longitudine");
            System.out.println("======================================== ");
            getCentroid(array_longitude, noOfClusters, centroid_longitude);
            System.out.println("temperatura");
            System.out.println("======================================== ");
            getCentroid(array_temperature, noOfClusters, centroid_temperature);
            System.out.println("humidity");
            System.out.println("======================================== ");
            getCentroid(array_humidity, noOfClusters, centroid_humidity);
            //getCentroid(array_activity, noOfClusters, centroid_activity);
            //getCentroid(array_headphone, noOfClusters, centroid_headphone);

        }
    }


    //determine the centroids
    public static float[][] getCentroid(float data[],int noofclusters,float centroid[][]){

        float distance[][]=new float[noofclusters][data.length];
        float cluster[]=new float[data.length];
        float clusternodecount[]=new float[noofclusters];

        centroid[0]=centroid[1];
        centroid[1]=new float[]{0,0};

        for(int i=0;i<noofclusters;i++){
            for(int j=0;j<data.length;j++){
                distance[i][j]=Math.abs(data[j]-centroid[0][i]);
            }
        }

        for(int j=0;j<data.length;j++){
            int smallerDistance=0;
            if(distance[0][j]<distance[1][j])
                smallerDistance=0;
            if(distance[1][j]<distance[0][j])
                smallerDistance=1;

            centroid[1][smallerDistance]=centroid[1][smallerDistance]+data[j];
            clusternodecount[smallerDistance]=clusternodecount[smallerDistance]+1;
            cluster[j]=smallerDistance;


        }

        for(int j=0;j<noofclusters;j++){
            centroid[1][j]=centroid[1][j]/clusternodecount[j];
        }

        boolean isAchived=true;
        for(int j=0;j<noofclusters;j++){
            if(isAchived && centroid[0][j] == centroid[1][j]){
                isAchived=true;
                continue;
            }
            isAchived=false;
        }

        if(!isAchived){

            getCentroid(data,noofclusters,centroid);
        }

        if(isAchived){
            System.out.println(" Final Clusters are ");
            for(int i=0;i<noofclusters;i++){
                System.out.print("C"+(i+1)+":");
                for(int j=0;j<data.length;j++){
                    if(cluster[j]==i)
                        System.out.print(data[j]+" ,");

                }
                System.out.println();
            }
            System.out.println(" Final centroids are ");
            for(int j=0;j<noofclusters;j++){
                System.out.print(centroid[1][j]+",");
            }
            System.out.println();
        }

        return centroid;

    }
}
