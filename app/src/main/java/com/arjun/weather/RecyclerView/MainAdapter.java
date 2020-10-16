package com.arjun.weather.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arjun.weather.Model.FiveDaysWeather;
import com.arjun.weather.Model.ItemHourly;
import com.arjun.weather.R;
import com.bumptech.glide.Glide;

import java.security.Permission;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    FiveDaysWeather fiveDaysWeather;
    public static final String data ="Data";
    Context context;
    interface setDataActivity{
        void setPosition(int position);

    }

    public MainAdapter(FiveDaysWeather itemHourlyList, Context context){
        this.fiveDaysWeather = itemHourlyList;
        this.context = context;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather,
                parent,
                false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context)
                .load("https://openweathermap.org/img/wn/10d@2x.png")
                .into(holder.icon1);

//        Log.e(data, "onBindViewHolder: "+fiveDaysWeather.getCity().getName().toString() );
        //If position only 8,16,24,32,40
        holder.tempmin.setText(String.valueOf(fiveDaysWeather.getList().get(position).getMain().getTempMin()));
        holder.tempmax.setText(String.valueOf(fiveDaysWeather.getList().get(position).getMain().getTempMax()));
    }

    @Override
    public int getItemCount() {
        return 5;
    }

     static class ViewHolder extends RecyclerView.ViewHolder{
            ImageView icon1;
            TextView tempmax,tempmin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon1 = itemView.findViewById(R.id.weather_image_view);
            tempmax = itemView.findViewById(R.id.max_temp_text_view);
            tempmin = itemView.findViewById(R.id.min_temp_text_view);
        }
    }
}
