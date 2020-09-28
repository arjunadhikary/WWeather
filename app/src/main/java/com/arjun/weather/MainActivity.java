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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private EditText userSearch;
    private List<ItemHourly> weatherHourly = new ArrayList<>();
    private Weather weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userSearch = findViewById(R.id.search);
        Button send = findViewById(R.id.send);
        send.setOnClickListener(sendListener);


    }

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.allWeather);
        Adapter adapter = new Adapter(weatherHourly, this);
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
            String baseUrl = "https://api.openweathermap.org/data/2.5/";
            Log.d("Base_Url", "onClick: " + baseUrl);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            weather = retrofit.create(Weather.class);
            String appid = "Your_API_Key";
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
                    weatherHourly = response.body().getList();
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