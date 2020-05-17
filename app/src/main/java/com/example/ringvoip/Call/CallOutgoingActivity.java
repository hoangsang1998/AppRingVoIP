package com.example.ringvoip.Call;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ringvoip.Chat.ChatMessageClass;
import com.example.ringvoip.Home.ChatRoomClass;
import com.example.ringvoip.LinphoneService;
import com.example.ringvoip.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;

import static com.example.ringvoip.Chat.ChattingActivity.getChatRoomByTwoUsername;
import static com.example.ringvoip.Chat.ChattingActivity.getStringDateTime;
import static com.example.ringvoip.Chat.ChattingActivity.getStringDateTimeChatRoom;
import static com.example.ringvoip.Chat.ChattingActivity.start;

public class CallOutgoingActivity extends AppCompatActivity {

    private CoreListenerStub mCoreListenerOut;
    TextView txtv_receiver;
    Chronometer chrono_timer;
    String chatRoom;
    DatabaseReference db_chatRoom, db_chatLog;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ChatMessageClass chatMessage;
    ChatRoomClass chatRoomClass;
    private AudioManager mAudioManager;
    ImageButton btn_speaker;
    ImageButton btn_mute;
    Core core = LinphoneService.getCore();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_outgoing);
        txtv_receiver = findViewById(R.id.txtv_receiver);
        try {
            txtv_receiver.setText(LinphoneService.getCore().getCurrentCall().getRemoteAddress().getUsername());
        }catch (Exception ex){

        }
        //--------------su kien mic------------------
        btn_mute = (ImageButton) findViewById(R.id.btn_mute);
        core.enableMic(true);
        btn_mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMic();
            }
        });
        //----------------ket su kien mic------------
        //-----------------su kien loa---------------
        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setSpeakerphoneOn(false);
        btn_speaker = findViewById(R.id.btn_speaker);
        btn_speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleAudio();
            }
        });
        //--------------------ket ham----------------
        chrono_timer = findViewById(R.id.chrono_timer);
        mCoreListenerOut = new CoreListenerStub() {
            @Override
            public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
                if (state == Call.State.End|| state == Call.State.Error) {
                    // Once call is finished (end state), terminate the activity
                    // We also check for released state (called a few seconds later) just in case
                    // we missed the first one

                mAudioManager.setSpeakerphoneOn(false);

//                    Toast.makeText(CallOutgoingActivity.this, "Máy bận", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else if (state == Call.State.Released) {
                    // Get elapsed time in milliseconds
                    long elapsedTimeMillis = System.currentTimeMillis()-start;
                    if(elapsedTimeMillis >= 2000) {
                        pushToDatabase(call);
                        finish();
                    }

                }
                else if (state == Call.State.Connected) {
//                    mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                    chrono_timer.setVisibility(View.VISIBLE);
                    chrono_timer.setBase(SystemClock.elapsedRealtime() - 1000 * call.getDuration());
                    chrono_timer.start();
                }
            }

        };
        LinphoneService.getCore().addListener(mCoreListenerOut);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LinphoneService.getCore().removeListener(mCoreListenerOut);
    }

    public void EvenBtnEndCall(View view) {
        Core core = LinphoneService.getCore();
        if (core.getCallsNb() > 0) {
            Call call = core.getCurrentCall();
            if (call == null) {
                // Current call can be null if paused for example
                call = core.getCalls()[0];
            }
            call.terminate();
        }
        finish();
    }

    private void toggleMic() {
        core.enableMic(!core.micEnabled());
        updateMicBtnState();
    }

    private void updateMicBtnState() {
        if (!core.micEnabled()) {
            btn_mute.setImageResource(R.drawable.btn_mute_enable);
        } else {
            btn_mute.setImageResource(R.drawable.btn_mute_disable);
        }
    }

    private void toggleAudio() {
        mAudioManager.setSpeakerphoneOn(!mAudioManager.isSpeakerphoneOn());
        updateSpeakerBtnState();
    }

    private void updateSpeakerBtnState() {
        if (mAudioManager.isSpeakerphoneOn()) {
            btn_speaker.setImageResource(R.drawable.ic_volume_off_black_24dp);
        } else {
            btn_speaker.setImageResource(R.drawable.btn_speaker_disable);
        }
    }

    private void pushToDatabase(Call call) {
        Call.Status callstatus = call.getCallLog().getStatus();
        String callFrom = call.getCallLog().getFromAddress().getUsername();
        String callTo = call.getCallLog().getToAddress().getUsername();

        chatRoom = getChatRoomByTwoUsername(callFrom, callTo);
        db_chatLog = database.getReferenceFromUrl("https://dbappchat-bbabc.firebaseio.com/chatlogs/" + chatRoom);
        db_chatRoom = database.getReferenceFromUrl("https://dbappchat-bbabc.firebaseio.com/chatrooms/" + chatRoom);

        String duration = getDuration(call);

        if(callstatus.toString().equals("Aborted") || callstatus.toString().equals("Success")) {//tự mình tắt máy
            chatMessage = new ChatMessageClass(callFrom, duration, getStringDateTime(), callstatus.toString());
        } else {
            chatMessage = new ChatMessageClass(callTo, duration, getStringDateTime(), callstatus.toString());
        }

        db_chatLog.push().setValue(chatMessage);

        chatRoomClass = new ChatRoomClass(callFrom, callTo, "", getStringDateTimeChatRoom());
        if (callstatus.equals(Call.Status.Declined)) {
            chatRoomClass.setContext("Call declined.");
        } else if (callstatus.equals(Call.Status.Success)) {
            chatRoomClass.setContext("Call ended | " + duration);
        } else if (callstatus.equals(Call.Status.Missed)) {
            chatRoomClass.setContext("Missed call.");
        } else {
            chatRoomClass.setContext("Call " + callstatus);
        }
        db_chatRoom.setValue(chatRoomClass);
    }

    public static String getDuration(Call call) {
        int intTimerInSec = call.getCallLog().getDuration();

        String timer;
        if (intTimerInSec < 60) {
            timer = intTimerInSec + "s";
        } else if (intTimerInSec < 3600) {
            int timerInMin = intTimerInSec / 60;
            int timerInSec = intTimerInSec % 60;
            timer = timerInMin + "m" + timerInSec + "s";
        } else {
            int timerInHour = intTimerInSec / 3600;
            int timerInMin = (intTimerInSec % 3600) / 60;
            int timerInSec = intTimerInSec - 3600 * timerInHour - 60 * timerInMin;
            timer = timerInHour + "h" + timerInMin + "m" + timerInSec + "s";
        }
        return timer;
    }
}
