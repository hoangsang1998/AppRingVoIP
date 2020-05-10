package com.example.ringvoip.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ringvoip.Home.HomeActivity;
import com.example.ringvoip.LinphoneService;
import com.example.ringvoip.Login.LoginActivity;
import com.example.ringvoip.Login.UserClass;
import com.example.ringvoip.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.linphone.core.Core;

public class ProfileActivity extends AppCompatActivity {

    TextView txtUserName;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        txtUserName = findViewById(R.id.txtv_name);
        database = FirebaseDatabase.getInstance();
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

        //Set offline
        try {
            String temp = txtUserName.getText().toString().split("@")[0];
            String userLogOut = temp.split(":")[1];
            final DatabaseReference db_theUserLogin = database.getReferenceFromUrl("https://dbappchat-bbabc.firebaseio.com/users/" + userLogOut);
            db_theUserLogin.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
//                        UserClass userLogin = new UserClass(txtUserName.getText().toString(), LinphoneService.getCore().getIdentity(), "Online");
//                        db_theUserLogin.setValue(userLogin);
                    } else {
                        db_theUserLogin.child("status").setValue("Offline");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        Intent intentLogin = new Intent(this, LoginActivity.class);
        startActivity(intentLogin);
    }
}
