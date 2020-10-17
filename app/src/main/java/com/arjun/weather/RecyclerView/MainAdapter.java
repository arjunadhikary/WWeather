package com.arjun.weather.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.arjun.weather.utils.FormatDate;
import com.arjun.weather.Model.FiveDaysWeather;
import com.arjun.weather.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    FiveDaysWeather fiveDaysWeather;
    Context context;
   public interface setDataActivity{
        void setPosition(int position);

    }
    setDataActivity setDataActivity;
    public MainAdapter(FiveDaysWeather itemHourlyList, Context context,setDataActivity dataActivity){
        this.fiveDaysWeather = itemHourlyList;
        this.setDataActivity = dataActivity;
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
                .load("https://openweathermap.org/img/wn/" + fiveDaysWeather.getList().get(position)
                        .getWeather().get(0)
                        .getIcon() + "@2x.png")
                .into(holder.icon1);


        //If position only 8,16,24,32,40
//        Random rnd = new Random();
        String[] colorsTxt = context.getApplicationContext().getResources().getStringArray(R.array.colors);
        List<Integer> colors = new ArrayList<Integer>();
        for (String s : colorsTxt) {
            int newColor = Color.parseColor(s);
            colors.add(newColor);
        }

        holder.tempmin.setText(String.valueOf(fiveDaysWeather.getList().get(position).getMain().getTempMin()));
        holder.tempmax.setText(String.valueOf(fiveDaysWeather.getList().get(position).getMain().getTempMax()));
        holder.nomtemp.setText(String.valueOf(fiveDaysWeather.getList().get(position).getMain().getTemp()));
        holder.cardView.setCardBackgroundColor(colors.get(position));
        FormatDate date = new FormatDate();
        String fdate = date.onlyDay(fiveDaysWeather.getList().get(position).getDtTxt());
        holder.day.setText(fdate);
    }


    @Override
    public int getItemCount() {
        return 5;
    }

     class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            LinearLayout layout;
            ImageView icon1;
            CardView cardView;
            TextView tempmax,tempmin,nomtemp,day;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.parentRelative);
            day = itemView.findViewById(R.id.day_name_text_view);
            nomtemp = itemView.findViewById(R.id.temp_text_view);
            cardView = itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);
            icon1 = itemView.findViewById(R.id.weather_image_view);
            tempmax = itemView.findViewById(R.id.max_temp_text_view);
            tempmin = itemView.findViewById(R.id.min_temp_text_view);

        }

         @Override
         public void onClick(View view) {
            setDataActivity.setPosition(getAdapterPosition());

         }
     }
}
