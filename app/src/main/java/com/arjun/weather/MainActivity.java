package com.arjun.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arjun.weather.Model.FiveDaysWeather;
import com.arjun.weather.Model.ItemHourly;
import com.arjun.weather.RecyclerView.Adapter;
import com.arjun.weather.RecyclerView.MainAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private EditText userSearch;
    private FiveDaysWeather fiveDaysWeather;
    private Weather weather;
    private String baseUrl = "https://api.openweathermap.org/data/2.5/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        userSearch = findViewById(R.id.search);
//        Button send = findViewById(R.id.send);
//        send.setOnClickListener(sendListener);
        getSupportActionBar().setElevation(0);
        setRecyclerView();


    }

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv);
        MainAdapter adapter = new MainAdapter(fiveDaysWeather, this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

    }

    Button.OnClickListener sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String userData = userSearch.getText().toString();
            if (userData.isEmpty()) {
                Toast.makeText(MainActivity.this, "Enter valid Location to Search", Toast.LENGTH_SHORT).show();
                return;
            }
            buildRetroift(baseUrl,userData);

        }



    private void buildRetroift(String baseUrl,String userData) {
                Log.d("Base_Url", "onClick: " + baseUrl);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                weather = retrofit.create(Weather.class);
                String appid = "API_KEY";
            getAllData(userData, appid);

            }
        };


    private void getAllData(String search,String apiKey) {
            Call<FiveDaysWeather> weatherCall = weather.getAllWeatherData(search, apiKey,"metric");
            weatherCall.enqueue(new Callback<FiveDaysWeather>() {
                @Override
                public void onResponse(Call<FiveDaysWeather> call, Response<FiveDaysWeather> response) {
                    if (!response.isSuccessful()) {
                        Log.e("TAG", "onResponse: " + response.code());
                        return;
                    }
                    Log.e("TAG", "onResponse:Data received " );
                    assert response.body() != null;
                    fiveDaysWeather = response.body();
                    Log.e("TAG", "onResponse:Total Data "+response.body().getList().size());
                    setRecyclerView();
                }

                @Override
                public void onFailure(Call<FiveDaysWeather> call, Throwable t) {
                    Log.e("TAG", "onResponse: " + t.getMessage());
                }
            });

        }
}