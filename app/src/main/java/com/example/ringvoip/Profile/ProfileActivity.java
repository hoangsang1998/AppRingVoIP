package com.example.ringvoip.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ringvoip.Home.HomeActivity;
import com.example.ringvoip.LinphoneService;
import com.example.ringvoip.Login.LoginActivity;
import com.example.ringvoip.R;

import org.linphone.core.Core;

public class ProfileActivity extends AppCompatActivity {

    TextView txtUserName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        txtUserName = findViewById(R.id.txtv_name);
        txtUserName.setText(LinphoneService.getCore().getIdentity());
    }

    public void btnBackEvent(View view) {
        Intent intentHome = new Intent(this, HomeActivity.class);
        startActivity(intentHome);
    }

    public void btnLogoutEvent(View view) {
        Core core = LinphoneService.getCore();

        core.setDefaultProxyConfig(null);
        core.stop();

        core.start();
        core.clearAllAuthInfo();
        core.clearProxyConfig();

        Intent intentLogin = new Intent(this, LoginActivity.class);
        startActivity(intentLogin);
    }
}
