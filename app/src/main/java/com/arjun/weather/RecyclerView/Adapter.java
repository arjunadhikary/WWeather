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
import com.arjun.weather.Model.ItemHourly;
import com.arjun.weather.R;
import com.arjun.weather.utils.FormatDate;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;


public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        ArrayList<ItemHourly> hourlyArrayList;
        Context context;

    public Adapter(ArrayList<ItemHourly> itemHourlyList, Context context){
        this.hourlyArrayList = itemHourlyList;
        this.context = context;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlehourly,
                parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Log.d("TAG", "onBindViewHolder: "+hourlyArrayList.get(position).getWeather().size());
        Glide.with(context)
                .load("https://openweathermap.org/img/wn/10d@2x.png")
                .into(holder.icon1);

            holder.tempmin.setText(String.valueOf(hourlyArrayList.get(position).getMain().getTempMin()));
            holder.tempmax.setText(String.valueOf(hourlyArrayList.get(position).getMain().getTempMax()));
            holder.day.setText(new FormatDate().onlyDay(hourlyArrayList.get(position).getDtTxt()));
    }

    @Override
    public int getItemCount() {
        return hourlyArrayList.size();
    }

     static class ViewHolder extends RecyclerView.ViewHolder{
            ImageView icon1;
            TextView tempmax,tempmin,date,day;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon1 = itemView.findViewById(R.id.weather_image_view);
            tempmax = itemView.findViewById(R.id.max_temp_text_view);
            date = itemView.findViewById(R.id.date_text_view);
            day = itemView.findViewById(R.id.day_name_text_view);
            tempmin = itemView.findViewById(R.id.min_temp_text_view);
        }
    }
}
