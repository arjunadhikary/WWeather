package com.arjun.weather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arjun.weather.Model.FiveDaysWeather;
import com.arjun.weather.Model.ItemHourly;
import com.arjun.weather.RecyclerView.Adapter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ItemsHourly extends AppCompatActivity {
    RecyclerView recyclerView;
    Adapter adapter;
    ArrayList<ItemHourly> listPrivate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hourly);
        listPrivate = new ArrayList<>();
        Type type = new TypeToken<List<ItemHourly>>() {
        }.getType();
        listPrivate = new Gson().fromJson(getIntent().getStringExtra("private_list"), type);
        Log.d("TAGYYYY", "setPosition: "+listPrivate.get(0).getMain().getTempMax());
        setRecyclerView();
    }

    private void setRecyclerView() {
        recyclerView = findViewById(R.id.hourly_main);
        adapter = new Adapter(listPrivate,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
