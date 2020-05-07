package com.example.ringvoip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.ringvoip.Login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent_groupchat = new Intent(MainActivity.this, LoginActivity.class);
//        intent_groupchat.putExtra("username", username);
//        intent_groupchat.putExtra("sipuri", sipuri);
        startActivity(intent_groupchat);
    }
}
