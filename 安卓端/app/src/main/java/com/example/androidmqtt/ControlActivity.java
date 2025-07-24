package com.example.androidmqtt;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ControlActivity extends AppCompatActivity {

    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control);

        tvWelcome = findViewById(R.id.tv_welcome);

        // 获取传递过来的用户名
        String username = getIntent().getStringExtra("IP");

        // 在欢迎页面上显示“欢迎光临，用户名”
        tvWelcome.setText("欢迎光临，" + username);
    }


}
