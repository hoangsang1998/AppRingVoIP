package com.example.ringvoip.Call;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ringvoip.LinphoneService;
import com.example.ringvoip.R;

import org.linphone.core.Call;
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;

import static com.example.ringvoip.Call.CallOutgoingActivity.mCallTimer;

public class CallIncommingActivity extends AppCompatActivity {

    TextView txtv_caller;
    ImageButton btn_accept;
    ImageButton btn_decline;
    private CoreListenerStub mListenerCallIn;
    private Call mCall;
    public static boolean isCallIncommingActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_incomming);
        btn_decline = findViewById(R.id.btn_decline);
        btn_accept = findViewById(R.id.btn_accept);
        txtv_caller = findViewById(R.id.txtv_caller);
        txtv_caller.setText(LinphoneService.getCore().getCurrentCall().getRemoteAddress().getUsername());
        for (Call call : LinphoneService.getCore().getCalls()) {
            mCall = call;
            break;
        }
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallParams params = LinphoneService.getCore().createCallParams(mCall);
                params.enableVideo(true);
                mCall.acceptWithParams(params);
            }
        });
        btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Core core = LinphoneService.getCore();
                if (core.getCallsNb() > 0) {
                    Call call = core.getCurrentCall();
                    if (call == null) {
                        // Current call can be null if paused for example
                        call = core.getCalls()[0];
                    }
                    call.terminate();
                }
            }
        });
        mListenerCallIn = new CoreListenerStub() {
            @Override
            public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
                if (state == Call.State.Connected) {
                    isCallIncommingActivity = true;
                    startActivity(new Intent(CallIncommingActivity.this, CallOutgoingActivity.class));

                    finish();
                } else if (state == Call.State.End || state == Call.State.Released) {
                    // Once call is finished (end state), terminate the activity
                    // We also check for released state (called a few seconds later) just in case
                    // we missed the first one
                    finish();
//                        if (isFirstAdd) {
//                            pushToDatabase(call);
//                            isFirstAdd = false;
//                        }
//                        call.terminate();
                }
            }
        };
        LinphoneService.getCore().addListener(mListenerCallIn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LinphoneService.getCore().removeListener(mListenerCallIn);
    }
}
