package com.example.georgi.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class KmeansActivity extends AppCompatActivity {

    SimpleDateFormat format_day = new SimpleDateFormat("yyyy-MM-dd");
    String day = format_day.format(new Date());

    //reference to database
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("DataAboutContextUser").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private float array_latitude[];
    private float array_longitude[];
    private float array_temperature[];
    private float array_humidity[];
    private float array_activity[];
    private float array_time[];

    private float matrix_latitude[][];
    private float matrix_longitude[][];
    private float matrix_temperture[][];
    private float matrix_humidity[][];
    private float matrix_activity[][];
    private float matrix_time[][];
    public static float clusters[][] = new float[2][6];

    //the history of clusters
    private String history_clusters = "";
    TextView history;

    //verify if all data is available
    int flag = 0;

    float centroid_latitude[][];
    float centroid_longitude[][];
    float centroid_temperature[][];
    float centroid_humidity[][];
    float centroid_activity[][];
    float centroid_time[][];

    float latitude;
    float longitude;
    float temperature;
    float humidity;
    float activity;
    float time;

    //number of clusters
    int noOfClusters=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kmeans);
        colectData();
        history = (TextView)findViewById(R.id.tv_history);


        //bottom navigation view
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.nav_home:
                        Intent intent0 = new Intent(KmeansActivity.this, MainActivity.class);
                        startActivity(intent0);
                        break;
                    case R.id.nav_map:
                        Intent intent1 = new Intent(KmeansActivity.this, MapActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.nav_noise:
                        Intent intent2 = new Intent(KmeansActivity.this, NoiseLevelActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.nav_coordinates:
                        Intent intent3 = new Intent(KmeansActivity.this, RequestActivity.class);
                        startActivity(intent3);
                        break;

                    case R.id.nav_notification:
                        Intent intent4 = new Intent(KmeansActivity.this, KmeansActivity.class);
                        startActivity(intent4);
                        break;

                }
                return false;
            }
        });

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

    public float[] getArray_time() {
        return array_time;
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

    public void setArray_time(float[] array_time) {
        this.array_time = array_time;
    }

    public void colectData(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int no = 0;
                for(DataSnapshot number : dataSnapshot.getChildren()){
                    no += number.getChildrenCount();
                }
                Log.i("Numar de zile", String.valueOf(dataSnapshot.getChildrenCount()));
                int i = 0;

                //intialize the arrays
                array_latitude = new float[no];
                array_longitude = new float[no];
                array_temperature = new float[no];
                array_humidity = new float[no];
                array_activity = new float[no];
                array_time = new float[no];

                //initialize the matrices
                matrix_latitude = new float[noOfClusters][no];
                matrix_longitude = new float[noOfClusters][no];
                matrix_temperture = new float[noOfClusters][no];
                matrix_humidity = new float[noOfClusters][no];
                matrix_activity = new float[noOfClusters][no];
                matrix_time = new float[noOfClusters][no];

                for(DataSnapshot some : dataSnapshot.getChildren()) {
                    for (DataSnapshot result : some.getChildren()) {

                        if(result.child("latitude").getValue(Double.class) != null) {
                            latitude = result.child("latitude").getValue(Double.class).floatValue();
                        }
                        array_latitude[i] = latitude;

                        if(result.child("longitude").getValue(Double.class) != null){
                            longitude = result.child("longitude").getValue(Double.class).floatValue();
                        }
                        array_longitude[i] = longitude;

                        if(result.child("temperature (°C)").getValue(Double.class) != null ){
                            temperature = result.child("temperature (°C)").getValue(Double.class).floatValue();
                        }
                        array_temperature[i] = temperature;

                        if(result.child("humidity").getValue(Double.class) != null) {
                            humidity = result.child("humidity").getValue(Double.class).floatValue();
                        }
                        array_humidity[i] = humidity;

                        if(result.child("activity").getValue(Double.class) != null){
                            activity = result.child("activity").getValue(Double.class).floatValue();
                        }
                        array_activity[i] = activity;

                        if(result.child("time").getValue(Long.class) != null){
                            time = result.child("time").getValue(Long.class).floatValue();
                        }
                        array_time[i] = time;

                        i++;
                    }
                }

                System.out.println("sunt aiiiiciciii");
                for(int g = 0; g < array_time.length; g++){
                    System.out.print(", " + array_time[g]);
                }
                System.out.println();

                setArray_latitude(array_latitude);
                setArray_longitude(array_longitude);
                setArray_temperature(array_temperature);
                setArray_humidity(array_humidity);
                setArray_activity(array_activity);
                setArray_time(array_time);

                float min_lat = min_function(array_latitude);
                System.out.println(min_lat + "min_lat");
                float max_lat = max_function(array_latitude);
                System.out.println(max_lat + "max_lat");
                centroid_latitude = new float[][]{
                        {0,0},
                        {min_lat,max_lat}
                };

                float min_long = min_function(array_longitude);
                float max_long = max_function(array_longitude);
                centroid_longitude = new float[][]{
                        {0,0},
                        {min_long,max_long}
                };

                float min_temp = min_function(array_temperature);
                float max_temp = max_function(array_temperature);
                centroid_temperature = new float[][]{
                        {0,0},
                        {min_temp,max_temp}
                };

                float min_humidity = min_function(array_humidity);
                float max_humidity = max_function(array_humidity);
                centroid_humidity = new float[][]{
                        {0,0},
                        {min_humidity,max_humidity}
                };

                float min_activity = min_function(array_activity);
                float max_activity = max_function(array_activity);
                centroid_activity = new float[][]{
                        {0,0},
                        {min_activity,max_activity}
                };

                float min_time = min_function(array_time);
                float max_time = max_function(array_time);
                centroid_time = new float[][]{
                        {0,0},
                        {min_time,max_time}
                };

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
            float m1[][] = getCentroid(matrix_latitude, array_latitude, noOfClusters, centroid_latitude);
            for(int i = 0; i < matrix_latitude.length; i++){
                for(int j = 0; j < matrix_latitude[i].length; j++){
                    System.out.print(matrix_latitude[i][j] + ",");
                }
                System.out.println();
            }


            int lat1 = closest(m1[1][0], array_latitude);
            System.out.println( "LATITUDINE1" + array_latitude[lat1]);
            int lat2 = closest(m1[1][1], array_latitude);
            System.out.println( "LATITUDINE2" + array_latitude[lat2]);

            clusters[0][0] = m1[1][0];
            clusters[1][0] = m1[1][1];

            System.out.println("longitudine");
            System.out.println("======================================== ");
            float m2[][] = getCentroid(matrix_longitude, array_longitude, noOfClusters, centroid_longitude);
            for(int i = 0; i < matrix_longitude.length; i++){
                for(int j = 0; j < matrix_longitude[i].length; j++){
                    System.out.print(matrix_longitude[i][j] + ",");
                }
                System.out.println();
            }
            int longi = closest(m2[1][0], array_longitude);
            System.out.println("LONGITUDINE" + array_longitude[longi]);


            System.out.println("temperatura");
            System.out.println("======================================== ");
            float m3[][] = getCentroid(matrix_temperture, array_temperature, noOfClusters, centroid_temperature);
            for(int i = 0; i < matrix_temperture.length; i++){
                for(int j = 0; j < matrix_temperture[i].length; j++){
                    System.out.print(matrix_temperture[i][j] + ",");
                }
                System.out.println();
            }
            int temp = closest(m3[1][0], array_temperature);
            System.out.println("TEMPERATURA" + array_temperature[temp]);


            System.out.println("humidity");
            System.out.println("======================================== ");
            float m4[][] = getCentroid(matrix_humidity, array_humidity, noOfClusters, centroid_humidity);
            for(int i = 0; i < matrix_humidity.length; i++){
                for(int j = 0; j < matrix_humidity[i].length; j++){
                    System.out.print(matrix_humidity[i][j] + ",");
                }
                System.out.println();
            }
            int hum = closest(m4[1][0], array_humidity);
            System.out.println("HUMIDITY" + array_humidity[hum]);


            System.out.println("activity");
            System.out.println("======================================== ");
            float m5[][] = getCentroid(matrix_activity, array_activity, noOfClusters, centroid_activity);
            for(int i = 0; i < matrix_activity.length; i++){
                for(int j = 0; j < matrix_activity[i].length; j++){
                    System.out.print(matrix_activity[i][j] + ",");
                }
                System.out.println();
            }
            int act = closest(m5[1][0], array_activity);
            System.out.println("ACTIVITY" + array_activity[act]);


            System.out.println("time");
            System.out.println("======================================== ");
            float m6[][] = getCentroid(matrix_time, array_time, noOfClusters, centroid_time);
            for(int i = 0; i < matrix_time.length; i++){
                for(int j = 0; j < matrix_time[i].length; j++){
                    System.out.print(matrix_time[i][j] + ",");
                }
                System.out.println();
            }
            int time1 = closest(m6[1][0], array_time);
            System.out.println("TIME1_" + array_time[time1]);
            int time2 = closest(m6[1][1], array_time);
            System.out.println("TIME2_" + array_time[time2]);


            for(int i = 0; i < matrix_longitude.length - 1; i++){
                int flag = 0;
                for(int j = 0; j < matrix_longitude[i].length; j++){
                    if(array_longitude[lat1] == matrix_longitude[i][j]){
                        flag = 1;
                    }
                    if(flag == 1){
                        clusters[0][1] = m2[1][0];
                        clusters[1][1] = m2[1][1];
                    }
                    else
                    {
                        clusters[0][1] = m2[1][1];
                        clusters[1][1] = m2[1][0];
                    }
                }
            }
            for(int i = 0; i < matrix_temperture.length - 1; i++){
                int flag = 0;
                for(int j = 0; j < matrix_temperture[i].length; j++){
                    if(array_temperature[lat1] == matrix_temperture[i][j]){
                        flag = 1;
                    }
                    if(flag == 1){
                        clusters[0][2] = m3[1][0];
                        clusters[1][2] = m3[1][1];
                    }
                    else
                    {
                        clusters[0][2] = m3[1][1];
                        clusters[1][2] = m3[1][0];
                    }
                }
            }
            for(int i = 0; i < matrix_humidity.length - 1; i++){
                int flag = 0;
                for(int j = 0; j < matrix_humidity[i].length; j++){
                    if(array_humidity[lat1] == matrix_humidity[i][j]){
                        flag = 1;
                    }
                    if(flag == 1){
                        clusters[0][3] = m4[1][0];
                        clusters[1][3] = m4[1][1];
                    }
                    else
                    {
                        clusters[0][3] = m4[1][1];
                        clusters[1][3] = m4[1][0];
                    }
                }
            }
            for(int i = 0; i < matrix_activity.length - 1; i++){
                int flag = 0;
                for(int j = 0; j < matrix_activity[i].length; j++){
                    if(array_activity[lat1] == matrix_activity[i][j]){
                        flag = 1;
                    }
                    if(flag == 1){
                        clusters[0][4] = m5[1][0];
                        clusters[1][4] = m5[1][1];
                    }
                    else
                    {
                        clusters[0][4] = m5[1][1];
                        clusters[1][4] = m5[1][0];
                    }
                }
            }

            for(int i = 0; i < matrix_time.length - 1; i++){
                int flag = 0;
                for(int j = 0; j < matrix_time[i].length; j++){
                    if(array_activity[lat1] == matrix_time[i][j]){
                        flag = 1;
                    }
                    if(flag == 1){
                        clusters[0][5] = array_time[time1];
                        clusters[1][5] = array_time[time2];
                    }
                    else
                    {
                        clusters[0][5] = array_time[time2];
                        clusters[1][5] = array_time[time1];
                    }
                }
            }

            for(int i = 0; i < clusters.length; i++ ){
                history_clusters += "         Cluster" + (i+1) + "\n";
                System.out.print("Cluster" + (i+1) + ": ");
                for(int j = 0; j < clusters[i].length; j++){
                    System.out.print(clusters[i][j] + "  ");
                   history_clusters += clusters[i][j] + "  ";
                }
                System.out.println();
                history_clusters += "\n\n\n";
            }
            history.setText(history_clusters);
            flag = 0;
        }

    }

    public int closest(float x, float array[]){
        float distance = 100.0f;
        int j = 0;
        for(int i = 0; i < array.length; i++){
            if(array[i] > x){
                if((array[i] - x) < distance){
                    distance = array[i] - x;
                    j = i;
                }
            }
            else
            {
                if((x - array[i]) < distance)
                distance = x - array[i];
                j = i;
            }
        }
        return j;
    }

    public float min_function(float array[]){
        float min = 100.0f;
        for(int i = 0; i < array.length; i++){
            if(array[i] < min){
                min = array[i];
            }
        }
        return min;
    }

    public float max_function(float array[]){
        float max = 0.0f;
        for(int i = 0; i < array.length; i++){
            if(array[i] > max){
                max = array[i];
            }
        }
        return max;
    }


    //determine the centroids
    public static float[][] getCentroid(float matrix[][], float data[],int noofclusters,float centroid[][]){

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

            getCentroid(matrix,data,noofclusters,centroid);
        }

        if(isAchived){
            System.out.println(" Final Clusters are ");
            for(int i=0;i<noofclusters;i++){
                System.out.print("C"+(i+1)+":");
                for(int j=0;j<data.length;j++){
                    if(cluster[j]==i) {
                        matrix[i][j] = data[j];
                        System.out.print(data[j] + " ,");
                    }

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

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(KmeansActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }


}
