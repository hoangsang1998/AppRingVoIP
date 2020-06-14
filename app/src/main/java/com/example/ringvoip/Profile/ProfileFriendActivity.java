package com.example.ringvoip.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.example.ringvoip.Call.CallOutgoingActivity;
import com.example.ringvoip.Chat.ChattingActivity;
import com.example.ringvoip.LinphoneService;
import com.example.ringvoip.R;

import org.linphone.core.Address;
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.tools.Log;

import java.util.ArrayList;

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
//        txtSip.setText(intent.getStringExtra("sipUri"));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Ask runtime permissions, such as record audio and camera
        // We don't need them here but once the user has granted them we won't have to ask again
        checkAndRequestCallPermissions();
    }

    private void checkAndRequestCallPermissions() {
        ArrayList<String> permissionsList = new ArrayList<>();

        // Some required permissions needs to be validated manually by the user
        // Here we ask for record audio and camera to be able to make video calls with sound
        // Once granted we don't have to ask them again, but if denied we can
        int recordAudio =
                getPackageManager()
                        .checkPermission(Manifest.permission.RECORD_AUDIO, getPackageName());
        Log.i(
                "[Permission] Record audio permission is "
                        + (recordAudio == PackageManager.PERMISSION_GRANTED
                        ? "granted"
                        : "denied"));
        int camera =
                getPackageManager().checkPermission(Manifest.permission.CAMERA, getPackageName());
        Log.i(
                "[Permission] Camera permission is "
                        + (camera == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));

        if (recordAudio != PackageManager.PERMISSION_GRANTED) {
            Log.i("[Permission] Asking for record audio");
            permissionsList.add(Manifest.permission.RECORD_AUDIO);
        }

        if (camera != PackageManager.PERMISSION_GRANTED) {
            Log.i("[Permission] Asking for camera");
            permissionsList.add(Manifest.permission.CAMERA);
        }

        if (permissionsList.size() > 0) {
            String[] permissions = new String[permissionsList.size()];
            permissions = permissionsList.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, 0);
        }
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
        Core core = LinphoneService.getCore();
        Address addressToCall = core.interpretUrl(txtName.getText().toString());
        CallParams params = core.createCallParams(null);

//        Switch videoEnabled = findViewById(R.id.call_with_video);
        params.enableVideo(true);

        if (addressToCall != null) {
            core.inviteAddressWithParams(addressToCall, params);
        }
//        Intent intentCallVideo = new Intent(this, CallOutgoingActivity.class);
//        startActivity(intentCallVideo);
    }

    public void imgCallEvent(View view) {
        Core core = LinphoneService.getCore();
        Address addressToCall = core.interpretUrl(txtName.getText().toString());
        CallParams params = core.createCallParams(null);

        params.enableVideo(false);

        if (addressToCall != null) {
            core.inviteAddressWithParams(addressToCall, params);
        }
//        Intent intentCallVideo = new Intent(this, CallOutgoingActivity.class);
//        startActivity(intentCallVideo);
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
