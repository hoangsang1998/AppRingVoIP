package com.example.ringvoip.Call;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ringvoip.R;

public class CallIncommingActivity extends AppCompatActivity {

    TextView txtv_caller;
    ImageButton btn_accept;
    ImageButton btn_decline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_incomming);
        btn_decline = findViewById(R.id.btn_decline);
        btn_accept = findViewById(R.id.btn_accept);
        txtv_caller = findViewById(R.id.txtv_caller);
    }
}
