package com.arjun.weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arjun.weather.Model.FiveDaysWeather;
import com.arjun.weather.Model.ItemHourly;
import com.arjun.weather.RecyclerView.MainAdapter;
import com.arjun.weather.utils.DialogBox;
import com.arjun.weather.utils.Divide;
import com.arjun.weather.utils.FormatDate;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements DialogBox.sendLocation,MainAdapter.setDataActivity{
    FusedLocationProviderClient providerClient;
    private int perm_id = 101;
    private boolean isTapped=false;
    private Menu menu;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private double lat, longs;
    private Divide divide = new Divide();
    private FiveDaysWeather fiveDaysWeather;
    private Weather weather;
    private String baseUrl = "https://api.openweathermap.org/data/2.5/";
    //Get all Views
    TextView cityName,date,weatherCondition,humidity,wind,bigTemperature,precipitation;
    ImageView icon;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setAllViews();
        preferences =getSharedPreferences("Location", Activity.MODE_PRIVATE);
        editor = preferences.edit();
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        String loc = preferences.getString("location",null);
        providerClient = LocationServices.getFusedLocationProviderClient(this);
        if(loc!=null) buildRetroift(baseUrl, loc);
        else buildRetroift(baseUrl,"Butwal");



    }

    private void setAllViews() {
        cityName = findViewById(R.id.city);
        weatherCondition = findViewById(R.id.weather);
        wind = findViewById(R.id.windData);
        humidity = findViewById(R.id.humData);
        precipitation = findViewById(R.id.pre);
        bigTemperature = findViewById(R.id.bigTemp);
        date = findViewById(R.id.weekday);
        icon = findViewById(R.id.imageIcon);
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv);
        MainAdapter adapter = new MainAdapter(fiveDaysWeather, this,this);
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
                outRect.right = 5;
            }
        });
        recyclerView.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_list, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_auto:
                if(!isTapped){ isTapped =true;menu.getItem(0).getIcon().setTint(Color.parseColor("#0000FF"));}
                else{ isTapped = false;menu.getItem(0).getIcon().setTint(Color.parseColor("#000000")); }
                getLastLocation();
                break;
            case R.id.search_location:
                showDialogBox();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDialogBox() {
        DialogBox box = new DialogBox();
        box.show(getSupportFragmentManager(), "Search_Location");

    }

    private void buildRetroift(String baseUrl,double lat,double lon) {
        Log.d("Base_Url", "onClick: " + baseUrl);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weather = retrofit.create(Weather.class);
        String appid = "api_key";
        getAllData(String.valueOf(lat),String.valueOf(lon), appid);

    }


    private void getAllData(String lat,String lon, String apiKey) {

        Call<FiveDaysWeather> weatherCall = weather.getDataOnCoords(lat,lon, apiKey, "metric");
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
                divide.convertData(fiveDaysWeather);
                Log.e("TAG", "onCreate: "+divide.getDayOne().size());
                        getSupportActionBar().setTitle(fiveDaysWeather.getCity().getName()+", "+fiveDaysWeather.getCity().getCountry());
                setRecyclerView();
            }

            @Override
            public void onFailure(Call<FiveDaysWeather> call, Throwable t) {
                Log.e("TAG", "onResponse: " + t.getMessage());
            }
        });

    }


    private void buildRetroift(String baseUrl, String userData) {
        Log.d("Base_Url", "onClick: " + baseUrl);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weather = retrofit.create(Weather.class);
        //API key will go down here
        String appid = "a48dc14ca469afef26f6969098961782";
        getAllData(userData, appid);

    }


    private void getAllData(String search, String apiKey) {

        Call<FiveDaysWeather> weatherCall = weather.getAllWeatherData(search, apiKey, "metric");
        weatherCall.enqueue(new Callback<FiveDaysWeather>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<FiveDaysWeather> call, Response<FiveDaysWeather> response) {
                if (!response.isSuccessful()) {
                    Log.e("TAG", "onResponse: " + response.code());
                    return;
                }
                assert response.body() != null;
                fiveDaysWeather = response.body();
                //TODO: Convert Decimal point value to Integer
                getSupportActionBar()
                        .setTitle(fiveDaysWeather.getCity().getName()+", "+fiveDaysWeather.getCity().getCountry());
                weatherCondition.setText(fiveDaysWeather.getList().get(0).getWeather().get(0).getMain());
                wind.setText((fiveDaysWeather.getList().get(0).getWind().getSpeed())+" km/hr");
                humidity.setText((fiveDaysWeather.getList().get(0).getMain().getHumidity())+"%");
                precipitation.setText(String.valueOf(fiveDaysWeather.getList().get(0).getMain().getPressure()));
                bigTemperature.setText(String.valueOf(fiveDaysWeather.getList().get(0).getMain().getTemp()));
                Glide
                        .with(MainActivity.this)
                        .load("https://openweathermap.org/img/wn/"
                                +fiveDaysWeather.getList().get(0).getWeather().get(0).getIcon()
                                +"@2x.png").into(icon);
                    FormatDate formatDate = new FormatDate();
                try {
                    date.setText(formatDate.convertDate(fiveDaysWeather.getList().get(0).getDtTxt()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                setRecyclerView();
            }

            @Override
            public void onFailure(Call<FiveDaysWeather> call, Throwable t) {
                Log.e("TAG", "onResponse: " + t.getMessage());
            }
        });

    }

    @Override
    public void sendUserLocation(String location,boolean toSave) {
        if(toSave){
            //Store user Preference here
            editor.putString("location",location);
            editor.commit();
        }
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
            }
        }
    }



    private void getLastLocation(){
        // check if permissions are given
        if (checkPermission()) {
            // check if location is enabled
            if (isLocationEnabled()) {
                // getting last location from FusedLocationClient object
                providerClient.getLastLocation().addOnCompleteListener(task -> {
                            Location location = task.getResult();
                            if (location == null) { requestNewLocationData();}
                            else {
                                lat= location.getLatitude();
                                longs=location.getLongitude();
                                buildRetroift(baseUrl,lat,longs);
                            }
                        });
            }

            else {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
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


    @Override
    public void setPosition(int position) {
        Toast.makeText(this, "Got Position"+position, Toast.LENGTH_SHORT).show();
        ArrayList<ItemHourly> itemHourlies = null;
        if(position==0){
            itemHourlies = new ArrayList<>(divide.getDayOne());
        }else if(position==1){
            itemHourlies = new ArrayList<>(divide.getDayTwo());
        }else if(position==2){
            itemHourlies = new ArrayList<>(divide.getDayThree());
        }else if(position==3){
            itemHourlies = new ArrayList<>(divide.getDayFOur());
        }else {
            itemHourlies = new ArrayList<>(divide.getDayFive());
        }


        Intent intent = new Intent(this,ItemsHourly.class);
        intent.putExtra("private_list", new Gson().toJson(itemHourlies));;
        startActivity(intent);
    }
}