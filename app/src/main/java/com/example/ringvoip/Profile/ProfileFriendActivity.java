package com.example.ringvoip.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.ringvoip.Call.CallOutgoingActivity;
import com.example.ringvoip.Chat.ChattingActivity;
import com.example.ringvoip.R;

public class ProfileFriendActivity extends AppCompatActivity {

    TextView txtSip, txtName;
    String isChattingActivity, userName, sipUri, userFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_friend);
        addEvents();
        addControls();
        addVariables();
        Intent intent = getIntent();
        txtName.setText(intent.getStringExtra("userFriend"));
        txtSip.setText(intent.getStringExtra("sipUri"));
    }

    private void addVariables() {
        Intent intent = getIntent();
        isChattingActivity = intent.getStringExtra("isChattingActivity");
        userName = intent.getStringExtra("userName");
        userFriend = intent.getStringExtra("userFriend");
        sipUri = intent.getStringExtra("sipUri");

    }

    private void addControls() {
        txtName = findViewById(R.id.txtUserName);
        txtSip = findViewById(R.id.txtSip);
    }

    private void addEvents() {

    }

    public void btnBackEvent(View view) {
        finish();
    }

    public void imgCallVideoEvent(View view) {
        Intent intentCallVideo = new Intent(this, CallOutgoingActivity.class);
        startActivity(intentCallVideo);
    }

    public void imgCallEvent(View view) {
        Intent intentCallVideo = new Intent(this, CallOutgoingActivity.class);
        startActivity(intentCallVideo);
    }

    public void imgChatEvent(View view) {
        if (isChattingActivity.equals("true")) {
            finish();
        } else {
            Intent intentChat = new Intent(ProfileFriendActivity.this, ChattingActivity.class);
            intentChat.putExtra("userFriend", userFriend);
            intentChat.putExtra("sipUri", sipUri);
//            intentChat.putExtra("user_contact", contact_username);
            startActivity(intentChat);
        }
    }
}
