package com.arjun.weather.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

 public class string_date {
     public String convertDate(String date){
         @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("EEEE");
         Date fDate;
         String name=null;
         try {
              fDate = format.parse(date);
               name = format.format(fDate);

         } catch ( ParseException e) {
             e.printStackTrace();
         }
         return name;
     }

}
