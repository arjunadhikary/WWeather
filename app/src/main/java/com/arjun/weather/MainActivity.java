package com.arjun.weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arjun.weather.Model.FiveDaysWeather;
import com.arjun.weather.Model.Main;
import com.arjun.weather.RecyclerView.MainAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements DialogBox.sendLocation {
    FusedLocationProviderClient providerClient;
    private int perm_id = 101;
    private double lat, longs;
    private FiveDaysWeather fiveDaysWeather = new FiveDaysWeather();
    private Weather weather;
    private String baseUrl = "https://api.openweathermap.org/data/2.5/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        buildRetroift(baseUrl, "Butwal");
        providerClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();



    }

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv);
        MainAdapter adapter = new MainAdapter(fiveDaysWeather, this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.right = 20;
            }
        });
        recyclerView.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_list, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        DialogBox box = new DialogBox();
        box.show(getSupportFragmentManager(), "Search_Location");


        return super.onOptionsItemSelected(item);
    }

    private void buildRetroift(String baseUrl, String userData) {
        Log.d("Base_Url", "onClick: " + baseUrl);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weather = retrofit.create(Weather.class);
        String appid = "";
        getAllData(userData, appid);

    }


    private void getAllData(String search, String apiKey) {

        Call<FiveDaysWeather> weatherCall = weather.getAllWeatherData(search, apiKey, "metric");
        weatherCall.enqueue(new Callback<FiveDaysWeather>() {
            @Override
            public void onResponse(Call<FiveDaysWeather> call, Response<FiveDaysWeather> response) {
                if (!response.isSuccessful()) {
                    Log.e("TAG", "onResponse: " + response.code());
                    return;
                }
                Log.e("TAG", "onResponse:Data received ");
                assert response.body() != null;
                fiveDaysWeather = response.body();
                Log.e("TAG", "onResponse:Total Data " + response.body().getList().size());
                Log.e("data", "onBindViewHolder: " + fiveDaysWeather.getCity().getName());
                setRecyclerView();
            }

            @Override
            public void onFailure(Call<FiveDaysWeather> call, Throwable t) {
                Log.e("TAG", "onResponse: " + t.getMessage());
            }
        });

    }

    @Override
    public void sendUserLocation(String location) {
        Toast.makeText(this, "Location:" + location, Toast.LENGTH_SHORT).show();
        buildRetroift(baseUrl, location);
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, perm_id);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == perm_id) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }else{
                Toast.makeText(this,
                        "This app requires Location Permission to work correctly",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermission()) { getLastLocation();}
    }

    private void getLastLocation(){
        // check if permissions are given
        if (checkPermission()) {
            // check if location is enabled
            if (isLocationEnabled()) {
                // getting last location from FusedLocationClient object
                providerClient.getLastLocation().addOnCompleteListener(task -> {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            }
                            else {
                                lat= location.getLatitude();
                                longs=location.getLongitude();
                                Toast.makeText(MainActivity.this, "Lat:"+lat+" long:"+longs, Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            else {
                Toast.makeText(this, "Please turn on your location...", Toast.LENGTH_LONG).show();
                Intent intent
                        = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }
        else {
            // if permissions aren't available,request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){
        // Initializing LocationRequest object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        providerClient = LocationServices.getFusedLocationProviderClient(this);

        providerClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult){
            Location mLastLocation = locationResult.getLastLocation();
            lat =mLastLocation.getLatitude();
            longs= mLastLocation.getLongitude();
        }
    };

}