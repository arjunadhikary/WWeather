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

import com.arjun.weather.Model.ItemHourly;
import com.arjun.weather.R;
import com.arjun.weather.utils.FormatDate;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;


public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    public boolean isShimmer = true;
    ArrayList<ItemHourly> fiveDaysWeather;
    Context context;
    int shimmerNumber = 5;
    setDataActivity setDataActivity;

    public MainAdapter(ArrayList<ItemHourly> itemHourlyList, Context context, setDataActivity dataActivity) {
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
        String[] colorsTxt = context.getApplicationContext().getResources().getStringArray(R.array.colors);
        List<Integer> colors = new ArrayList<Integer>();
        for (String s : colorsTxt) {
            int newColor = Color.parseColor(s);
            colors.add(newColor);
        }
        if (isShimmer) {
            holder.cardView.setCardBackgroundColor(null);
            holder.shimmerFrameLayout.startShimmerAnimation();
        } else {
            Glide.with(context)
                    .load("https://openweathermap.org/img/wn/" + fiveDaysWeather.get(position)
                            .getWeather().get(0)
                            .getIcon() + "@2x.png")
                    .into(holder.icon1);
            holder.shimmerFrameLayout.stopShimmerAnimation();
            holder.nomtemp.setText(String.format("%s°C", (fiveDaysWeather.get(position).getMain().getTemp())));
            holder.cardView.setCardBackgroundColor(colors.get(position));
            FormatDate date = new FormatDate();
            String fdate = date.onlyDay(fiveDaysWeather.get(position).getDtTxt());
            holder.day.setText(fdate);
        }
    }

    @Override
    public int getItemCount() {
        return isShimmer ? shimmerNumber : fiveDaysWeather.size();
    }


    public interface setDataActivity {
        void setPosition(int position);

    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout layout;
        ImageView icon1;
        CardView cardView;
        ShimmerFrameLayout shimmerFrameLayout;
        TextView nomtemp, day;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.parentRelative);
            day = itemView.findViewById(R.id.day_name_text_view);
            nomtemp = itemView.findViewById(R.id.temp_text_view);
            cardView = itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmer);
            icon1 = itemView.findViewById(R.id.weather_image_view);


        }

        @Override
        public void onClick(View view) {
            if (!isShimmer) setDataActivity.setPosition(getAdapterPosition());

        }
    }
}
