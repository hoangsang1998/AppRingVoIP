package com.example.ringvoip.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ringvoip.Home.HomeActivity;
import com.example.ringvoip.LinphoneService;
import com.example.ringvoip.R;

import org.linphone.core.AccountCreator;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.ProxyConfig;
import org.linphone.core.RegistrationState;
import org.linphone.core.TransportType;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    EditText txtUserName, txtPassword, txtDomain;

    private AccountCreator mAccountCreator;
    private CoreListenerStub mCoreListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        addVariables();
        addControls();
        addEvent();




    }

    private void addVariables() {
        mAccountCreator = LinphoneService.getCore().createAccountCreator(null);
    }

    private void addEvent() {
        btnLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        configureAccount();
                    }
                });

        mCoreListener = new CoreListenerStub() {
            @Override
            public void onRegistrationStateChanged(Core core, ProxyConfig cfg, RegistrationState state, String message) {
                if (state == RegistrationState.Ok) {
                    finish();
                } else if (state == RegistrationState.Failed) {
                    Toast.makeText(LoginActivity.this, "Failure: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void addControls() {
        txtUserName = findViewById(R.id.etxt_username);
        txtPassword = findViewById(R.id.etxt_password);
        txtDomain = findViewById(R.id.etxt_domain);
        btnLogin = findViewById(R.id.btn_login);
    }

    public void onBackPressed() {
        showAlertDialog();
    }

    public void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("RingVoIP");
        builder.setMessage("Bạn có muốn thoát không?");
        builder.setCancelable(false);
        builder.setPositiveButton("Ứ chịu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(HomeActivity.this, "Không thoát được", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
                finishAffinity();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        LinphoneService.getCore().addListener(mCoreListener);
    }

    @Override
    protected void onPause() {
        LinphoneService.getCore().removeListener(mCoreListener);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void configureAccount() {
        // At least the 3 below values are required
        mAccountCreator.setUsername(txtUserName.getText().toString());
        mAccountCreator.setDomain(txtDomain.getText().toString());
        mAccountCreator.setPassword(txtPassword.getText().toString());

        // By default it will be UDP if not set, but TLS is strongly recommended
//        switch (mTransport.getCheckedRadioButtonId()) {
//            case R.id.transport_udp:
                mAccountCreator.setTransport(TransportType.Udp);
//                break;
//            case R.id.transport_tcp:
//                mAccountCreator.setTransport(TransportType.Tcp);
//                break;
//            case R.id.transport_tls:
//                mAccountCreator.setTransport(TransportType.Tls);
//                break;
//        }

        // This will automatically create the proxy config and auth info and add them to the Core
        ProxyConfig cfg = mAccountCreator.createProxyConfig();
        // Make sure the newly created one is the default
        LinphoneService.getCore().setDefaultProxyConfig(cfg);
    }
}
