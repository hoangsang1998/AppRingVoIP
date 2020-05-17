package com.example.ringvoip.Call;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ringvoip.Chat.ChatMessageClass;
import com.example.ringvoip.Home.ChatRoomClass;
import com.example.ringvoip.LinphoneService;
import com.example.ringvoip.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.linphone.core.Call;
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.Reason;
import org.linphone.core.tools.Log;

import static com.example.ringvoip.Chat.ChattingActivity.getChatRoomByTwoUsername;
import static com.example.ringvoip.Chat.ChattingActivity.getStringDateTime;
import static com.example.ringvoip.Chat.ChattingActivity.getStringDateTimeChatRoom;
import static com.example.ringvoip.Chat.ChattingActivity.start;

public class CallOutgoingActivity extends AppCompatActivity {

    private static final int CAMERA_TO_TOGGLE_VIDEO = 0;
    private static final int MIC_TO_DISABLE_MUTE = 1;
    private static final int CAMERA_TO_ACCEPT_UPDATE = 2;

    private ImageButton mButtonTerminate, mButtonMute, mButtonSpeaker, mButtonSwitchCamera;
    private TextView mTextCallee;
    public static Chronometer mCallTimer;
    private ImageButton mButtonVideo;
    private TextureView mTextureLocalPreview, mTextureRemoteVideo;
    private ImageView mImageCallee;

    private int mPreviewX, mPreviewY;
    private AudioManager mAudioManager;
    private CoreListenerStub mCoreListener;
    private boolean mIsMuted = false;
    private boolean mIsUsingSpeaker = false;
    private boolean mIsVisible = true;
    private boolean mIsVideoCall = false;

    String chatRoom;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db_chatRoom, db_chatLog;
    ChatMessageClass chatMessage;
    ChatRoomClass chatRoomClass;

    Core core = LinphoneService.getCore();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_outgoing);

        mCoreListener = new CoreListenerStub(){
            @Override
            public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
                if(state == Call.State.Error){
                    if (call.getErrorInfo().getReason() == Reason.Declined) {
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.error_call_declined),
                                Toast.LENGTH_SHORT)
                                .show();
                    } else if (call.getErrorInfo().getReason() == Reason.NotFound) {
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.error_user_not_found),
                                Toast.LENGTH_SHORT)
                                .show();
                    } else if (call.getErrorInfo().getReason() == Reason.NotAcceptable) {
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.error_incompatible_media),
                                Toast.LENGTH_SHORT)
                                .show();
                    } else if (call.getErrorInfo().getReason() == Reason.Busy) {
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.error_user_busy),
                                Toast.LENGTH_SHORT)
                                .show();
                    } else if (message != null) {
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.error_unknown) + " - " + message,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else if (state == Call.State.Released) {//state == Call.State.End ||
                    // Once call is finished (end state), terminate the activity
                    // We also check for released state (called a few seconds later) just in case
                    // we missed the first one
                    // Get elapsed time in milliseconds
//                    long elapsedTimeMillis = System.currentTimeMillis()-start;
//                    if(elapsedTimeMillis >= 2000) {
//                        pushToDatabase(call);
//                        finish();
//                    }
                    finish();
                } else if (state == Call.State.StreamsRunning){
                    mCallTimer.setVisibility(View.VISIBLE);
                    mCallTimer.setBase(SystemClock.elapsedRealtime() - 1000 * call.getDuration());
                    mCallTimer.start();
                    updateInterfaceDependingOnVideo();
                } else if (state == Call.State.UpdatedByRemote){
                    boolean videoEnabled = call.getRemoteParams().videoEnabled();
                    acceptCallUpdate(videoEnabled);
                }
                //updateSpeakerButtonState(call.getCurrentParams().videoEnabled());
                if(core.getCallsNb() == 0) finish();
            }
        };

        mButtonTerminate = findViewById(R.id.button_call_terminate);
        mButtonTerminate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(core.getCallsNb() > 0) {
                    Call call = core.getCurrentCall();
                    if(call == null){
                        call = core.getCalls()[0];
                    }
                    call.terminate();
                }
            }
        });

        mButtonMute = findViewById(R.id.button_mute);
        mButtonMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAndRequestPermission(Manifest.permission.RECORD_AUDIO, MIC_TO_DISABLE_MUTE)) {
                    toggleMic();
                }
            }
        });
        mButtonSpeaker = findViewById(R.id.button_speaker);
        mButtonSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsUsingSpeaker = !mIsUsingSpeaker;
                toggleSpeaker(mIsUsingSpeaker);
            }
        });
        mButtonVideo = findViewById(R.id.button_video);
        mButtonVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAndRequestPermission(Manifest.permission.CAMERA, CAMERA_TO_TOGGLE_VIDEO)) {
                    mIsVideoCall = !mIsVideoCall;
                    toggleVideo(mIsVideoCall);
                }
            }
        });
        mTextureLocalPreview = findViewById(R.id.texture_local_preview);
        mTextureLocalPreview.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        moveLocalPreview(event);
                        return true;
                    }
                });
        mTextureRemoteVideo = findViewById(R.id.texture_remote_video);
        mTextureRemoteVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsVisible = !mIsVisible;
                makeButtonsVisibleOrGone(mIsVisible);
            }
        });


        mButtonSwitchCamera = findViewById(R.id.button_switch_camera);
        mButtonSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCamera();
            }
        });

        core.setNativeVideoWindowId(mTextureRemoteVideo);
        core.setNativePreviewWindowId(mTextureLocalPreview);

        mTextCallee = findViewById(R.id.text_callee);
        mTextCallee.setText(LinphoneService.getCore().getCurrentCall().getRemoteAddress().getUsername());
        mCallTimer = findViewById(R.id.call_timer);

        mImageCallee = findViewById(R.id.image_callee);
    }

    @Override
    protected void onStart() {
        super.onStart();

        core = LinphoneService.getCore();
        if(core != null){
            core.setNativeVideoWindowId(mTextureRemoteVideo);
            core.setNativePreviewWindowId(mTextureLocalPreview);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinphoneService.getCore().addListener(mCoreListener);
        updateInterfaceDependingOnVideo();
        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        if(core.getCallsNb() == 0) finish();
    }

    @Override
    protected void onPause() {
        LinphoneService.getCore().removeListener(mCoreListener);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mButtonSpeaker = mButtonTerminate = mButtonVideo = mButtonMute = null;
        mImageCallee = null;
        mTextureLocalPreview = null;
        mTextureRemoteVideo = null;
        super.onDestroy();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults[0] != PackageManager.PERMISSION_GRANTED) return;

        switch(requestCode){
            case CAMERA_TO_TOGGLE_VIDEO:
                core.reloadVideoDevices();
                toggleVideo(!core.getCurrentCall().getCurrentParams().videoEnabled());
                break;
            case MIC_TO_DISABLE_MUTE:
                toggleMic();
                break;
            case CAMERA_TO_ACCEPT_UPDATE:
                core.reloadVideoDevices();
                acceptCallUpdate(true);
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private boolean checkPermission(String permission) {
        int granted = getPackageManager().checkPermission(permission, getPackageName());
        Log.i(
                "[Permission] "
                        + permission
                        + " permission is "
                        + (granted == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));
        return granted == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkAndRequestPermission(String permission, int result) {
        if (!checkPermission(permission)) {
            Log.i("[Permission] Asking for " + permission);
            ActivityCompat.requestPermissions(this, new String[] {permission}, result);
            return false;
        }
        return true;
    }

    //SPEAKER
    private void toggleSpeaker(boolean isUsingSpeaker){
        mAudioManager.setSpeakerphoneOn(isUsingSpeaker);
        updateSpeakerButtonState(isUsingSpeaker);
    }

    private void updateSpeakerButtonState(boolean isUsing){
        if(isUsing){
//            mButtonSpeaker.setBackgroundResource(R.drawable.btn_call_disabled);
            mButtonSpeaker.setImageResource(R.drawable.ic_volume_up_white_24dp);
        } else {
//            mButtonSpeaker.setBackgroundResource(R.drawable.btn_call_enabled);
            mButtonSpeaker.setImageResource(R.drawable.ic_volume_off_black_24dp);
        }
    }

    //MICROPHONE MUTE
    private void toggleMic(){
        core.enableMic(!core.micEnabled());
        updateMicButtonState(!core.micEnabled());
    }

    private void updateMicButtonState(boolean isMuted){
        if(isMuted){
            mButtonMute.setBackgroundResource(R.drawable.btn_call_disabled);
            mButtonMute.setImageResource(R.drawable.ic_mic_off_black_24dp);
        } else {
            mButtonMute.setBackgroundResource(R.drawable.btn_call_enabled);
            mButtonMute.setImageResource(R.drawable.ic_mic_white_24dp);
        }
    }

    //VIDEO RELATED
    private void toggleVideo(boolean isVideoCall){
        Call call = core.getCurrentCall();
        if (call == null) return;
        if(isVideoCall) enableVideo();
        else disableVideo();
    }

    private void updateVideoButtonState(boolean isVideoCall){
        if(isVideoCall){
            mButtonVideo.setBackgroundResource(R.drawable.btn_call_disabled);
            mButtonVideo.setImageResource(R.drawable.ic_videocam_off_black_24dp);
        } else{
            mButtonVideo.setBackgroundResource(R.drawable.btn_call_enabled);
            mButtonVideo.setImageResource(R.drawable.ic_videocam_white_24dp);
        }
    }

    private void enableVideo(){
        Call call = core.getCurrentCall();
        call.enableCamera(true);
        CallParams params = core.createCallParams(call);
        params.enableVideo(true);
        try {
            call.update(params);
        } catch (Exception ex){
            Log.i("Unable to enable video call " + ex.toString());
        }
    }

    private void disableVideo(){
        Call call = core.getCurrentCall();
        if(call.getState() == Call.State.End || call.getState() == Call.State.Released) return;
        CallParams params = core.createCallParams(call);
        params.enableVideo(false);
        try {
            call.update(params);
        } catch (Exception ex){
            Log.i("Unable to disable video call " + ex.toString());
        }
    }

    private void switchCamera(){
        try{
            String currentDevice = core.getVideoDevice();
            String[] devices = core.getVideoDevicesList();
            int index = 0;
            for(String d : devices){
                if(d.equals(currentDevice)){
                    break;
                }
                index++;
            }

            String newDevice;
            if(index == 1) newDevice = devices[0];
            else if (devices.length > 1) newDevice = devices[1];
            else newDevice = devices[index];
            core.setVideoDevice(newDevice);

            Call call = core.getCurrentCall();
            if(call == null){
                Log.w("Trying to switch camera while not in call");
                return;
            }
            call.update(null);
        }catch (ArithmeticException ae){
            Log.e("Cannot switch camera");
        }
    }

    private void updateInterfaceDependingOnVideo(){
        Call call = core.getCurrentCall();
        if(call == null){
            showVideoControl(false);
            return;
        }

        boolean videoEnabled = call.getCurrentParams().videoEnabled();
        showVideoControl(videoEnabled);
        updateVideoButtonState(videoEnabled);
        if(!videoEnabled){
            mImageCallee.setVisibility(View.VISIBLE);
            mTextCallee.setVisibility(View.VISIBLE);
            mCallTimer.setVisibility(View.VISIBLE);
            mButtonMute.setVisibility(View.VISIBLE);
            mButtonVideo.setVisibility(View.VISIBLE);
            mButtonTerminate.setVisibility(View.VISIBLE);
            mButtonSpeaker.setVisibility(View.VISIBLE);
        }
    }


    private void showVideoControl(boolean videoEnabled){
        mImageCallee.setVisibility(videoEnabled ? View.GONE : View.VISIBLE);
        mButtonSwitchCamera.setVisibility(videoEnabled ? View.VISIBLE : View.GONE);
        mTextureLocalPreview.setVisibility(videoEnabled ? View.VISIBLE : View.GONE);
        mTextureRemoteVideo.setVisibility(videoEnabled ? View.VISIBLE : View.GONE);
    }

    private void makeButtonsVisibleOrGone(boolean visible) {
        mTextCallee.setVisibility(visible ? View.VISIBLE : View.GONE);
        mCallTimer.setVisibility(visible ? View.VISIBLE : View.GONE);
        mButtonMute.setVisibility(visible ? View.VISIBLE : View.GONE);
        mButtonVideo.setVisibility(visible ? View.VISIBLE : View.GONE);
        mButtonTerminate.setVisibility(visible ? View.VISIBLE : View.GONE);
        mButtonSpeaker.setVisibility(visible ? View.VISIBLE : View.GONE);
        mButtonSwitchCamera.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void acceptCallUpdate(boolean accept) {
        Call call = core.getCurrentCall();
        if (call == null) {
            return;
        }

        CallParams params = core.createCallParams(call);
        if (accept) {
            params.enableVideo(true);
            core.enableVideoCapture(true);
            core.enableVideoDisplay(true);
        }

        call.acceptUpdate(params);
    }

    //Move local video
    private void moveLocalPreview(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreviewX = (int) motionEvent.getX();
                mPreviewY = (int) motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();

                RelativeLayout.LayoutParams lp =
                        (RelativeLayout.LayoutParams) mTextureLocalPreview.getLayoutParams();

                lp.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                lp.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                int left = lp.leftMargin + (x - mPreviewX);
                int top = lp.topMargin + (y - mPreviewY);

                int width = lp.width;
                int height = lp.height;

                Point screenSize = new Point();
                getWindow().getWindowManager().getDefaultDisplay().getSize(screenSize);

                int statusBarHeight = 0;
                int resource =
                        getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resource > 0) {
                    statusBarHeight = getResources().getDimensionPixelSize(resource);
                }

                if (left < 0
                        || top < 0
                        || left + width >= screenSize.x
                        || top + height + statusBarHeight >= screenSize.y) return;

                lp.leftMargin = left;
                lp.topMargin = top;
                mTextureLocalPreview.setLayoutParams(lp);
                break;
        }
    }
}
