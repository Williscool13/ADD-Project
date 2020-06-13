package com.example.addproject;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.locks.ReentrantLock;

public class Splash extends Fragment {
    private static final String TAG = "Splash";

    private Button login_button;
    private FirebaseUser user;
    RelativeLayout relLayout1, relLayoutTitle;
    Handler handler = new Handler();
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            relLayoutTitle.setVisibility(View.VISIBLE);
            relLayout1.setVisibility(View.VISIBLE);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splash_layout, container, false);

        relLayout1 = (RelativeLayout) view.findViewById(R.id.relLayoutLogin);
        relLayoutTitle = (RelativeLayout) view.findViewById(R.id.app_title);

        login_button = (Button) view.findViewById(R.id.login_button);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).signIn();
                user = ((MainActivity)getActivity()).checkLoginStatus();
                Log.d(TAG, "User Attempting Log-in");

            }
        });

        handler.postDelayed(runnable, 2000);

        return view;
    }
}