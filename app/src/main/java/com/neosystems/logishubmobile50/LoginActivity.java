package com.neosystems.logishubmobile50;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        String DeviceToken = intent.getExtras().getString("DeviceToken");

        Toast.makeText(getApplicationContext(), DeviceToken, Toast.LENGTH_LONG).show();

        TextView txtAddOption = (TextView) findViewById(R.id.textView2);
        txtAddOption.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication(), Login2Activity.class));
            }
        });


    }
}
