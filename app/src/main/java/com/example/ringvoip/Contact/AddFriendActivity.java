package com.example.ringvoip.Contact;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ringvoip.R;

public class AddFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
    }

    public void btnAddEvent(View view) {
        Intent intentContact = new Intent(this, ContactActivity.class);
        startActivity(intentContact);
    }

    public void btnBackEvent(View view) {
        Intent intentContact = new Intent(this, ContactActivity.class);
        startActivity(intentContact);
    }
}
