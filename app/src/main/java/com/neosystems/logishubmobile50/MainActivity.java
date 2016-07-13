package com.neosystems.logishubmobile50;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("aa", "onCreate: 11111111111111111111");
        System.out.println("We are going to use GIT as a version control system.");
    }
}
