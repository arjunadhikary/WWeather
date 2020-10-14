package com.arjun.weather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import java.util.Objects;

public class DialogBox extends DialogFragment {
    private EditText searchView;

    interface sendLocation{
        void sendUserLocation(String location);
    }

    sendLocation sendLocation;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = Objects.requireNonNull(getActivity()).getLayoutInflater().inflate(R.layout.activity_dialoge_box,null);
        searchView = view.findViewById(R.id.search_location);
        Button ok = view.findViewById(R.id.ok);
        Button cancel = view.findViewById(R.id.cancel);
        ok.setOnClickListener(view12 -> {
            String location = searchView.getText().toString();
            if(!location.isEmpty()){
                //Send Data to main Activity using Interface
                sendLocation.sendUserLocation(location);
                Objects.requireNonNull(getDialog()).dismiss();
            }
            else{
                searchView.setError("Enter a Location");
            }
        });
        cancel.setOnClickListener(view1 -> {
            Objects.requireNonNull(getDialog()).dismiss();
        });
        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
             sendLocation = (sendLocation)getActivity();

        } catch (ClassCastException e) {
            Log.e("Attach Failed", "onAttach: " + e.getMessage());
        }
    }
}