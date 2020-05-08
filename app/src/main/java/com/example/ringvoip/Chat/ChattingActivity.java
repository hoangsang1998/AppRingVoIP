package com.example.ringvoip.Chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ringvoip.Profile.ProfileFriendActivity;
import com.example.ringvoip.R;

import java.util.ArrayList;

public class ChattingActivity extends AppCompatActivity {

    TextView txtFriendName;
    RecyclerView recyclerView;
    AdapterConversation adapterConversation;
    ArrayList<ChatMessageClass> arrayList;
    String username;
    EditText txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        addControls();
        addEvents();
        Intent intent = getIntent();
        username = intent.getStringExtra("userName");
        txtFriendName.setText(intent.getStringExtra("userFriend"));
        ChatMessageClass chatMessageClass = new ChatMessageClass("sang", "hiiii", "2020-03-30 | 10:59:27");
        ChatMessageClass chatMessageClass2 = new ChatMessageClass("sang1", "hiiii2", "2020-03-30 | 10:59:27");
        ChatMessageClass chatMessageClass3 = new ChatMessageClass("sang2", "hiiii3", "2020-03-30 | 10:59:27");
        ChatMessageClass chatMessageClass4 = new ChatMessageClass("sang", "hiii4", "2020-03-30 | 10:59:27");
        ChatMessageClass chatMessageClass5 = new ChatMessageClass("sang4", "hiiii5", "2020-03-30 | 10:59:27");
        arrayList.add(chatMessageClass);
        arrayList.add(chatMessageClass2);
        arrayList.add(chatMessageClass3);
        arrayList.add(chatMessageClass4);
        arrayList.add(chatMessageClass5);
        chatBoxView(0);
    }

    private void addControls() {
        txtFriendName = findViewById(R.id.txtv_name);
        recyclerView = findViewById(R.id.mRecyclerView);
        arrayList = new ArrayList<>();
        txtMessage = findViewById(R.id.etxt_message);
    }
    private void addEvents() {

    }

    public void btnSendMessageEvent(View view) {
        if (!txtMessage.getText().toString().trim().equals("")) {

        }
    }

    public void chatBoxView(int delayTime) {
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapterConversation = new AdapterConversation(arrayList, getApplicationContext(), username);
        recyclerView.setAdapter(adapterConversation);

        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (recyclerView.getAdapter().getItemCount() > 0) {
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                }
            }
        }, delayTime);

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (recyclerView.getAdapter().getItemCount() > 0) {
                                recyclerView.smoothScrollToPosition(
                                        recyclerView.getAdapter().getItemCount() - 1);
                            }
                        }
                    }, 200);
                }
            }
        });
    }

    public void btnCallVideoEvent(View view) {
//        Intent intentCallVideo = new Intent(this, CallOutgoingActivity.class);
//        startActivity(intentCallVideo);
    }

    public void btnCallEvent(View view) {
//        Intent intentCallVideo = new Intent(this, CallOutgoingActivity.class);
//        startActivity(intentCallVideo);
    }

    public void imgFriendEvent(View view) {
        Intent intent = getIntent();
        Intent intentFriendProfile = new Intent(this, ProfileFriendActivity.class);
        intentFriendProfile.putExtra("userName", intent.getStringExtra("userName"));
        intentFriendProfile.putExtra("userFriend", intent.getStringExtra("userFriend"));
        intentFriendProfile.putExtra("sipUri", intent.getStringExtra("userName") + "@hoangsang1998.com.vn");
        String isChattingActivity = "true";
        intentFriendProfile.putExtra("isChattingActivity", isChattingActivity);
        startActivity(intentFriendProfile);
    }
}
