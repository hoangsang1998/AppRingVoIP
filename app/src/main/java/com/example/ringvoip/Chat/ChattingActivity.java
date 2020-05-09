package com.example.ringvoip.Chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ringvoip.Call.CallOutgoingActivity;
import com.example.ringvoip.Profile.ProfileFriendActivity;
import com.example.ringvoip.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChattingActivity extends AppCompatActivity {

    TextView txtFriendName;
    RecyclerView recyclerView;
    AdapterConversation adapterConversation;
    ArrayList<ChatMessageClass> arrayList;
    String username, contact_user, chatRoom;
    EditText txtMessage;

    FirebaseDatabase database;
    DatabaseReference db_chatRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        addVariables();
        addControls();
        addEvents();
        loadConversations();


        txtFriendName.setText(contact_user);

    }

    private void addVariables() {
        Intent intent = getIntent();
        username = intent.getStringExtra("userName");
        contact_user = intent.getStringExtra("userFriend");
        database = FirebaseDatabase.getInstance();
        chatRoom = getChatRoomByTwoUsername(username, contact_user);
        db_chatRoom = database.getReferenceFromUrl("https://dbappchat-bbabc.firebaseio.com/chatlogs/" + chatRoom);
    }

    public static String getChatRoomByTwoUsername(String username1, String username2) {
        String[] myArray = {username1, username2};
        StringBuilder result = new StringBuilder();
        int size = myArray.length;
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < myArray.length; j++) {
                if (myArray[i].compareTo(myArray[j]) > 0) {
                    String temp = myArray[i];
                    myArray[i] = myArray[j];
                    myArray[j] = temp;
                }
            }
        }
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                result.append(myArray[i]);
            } else {
                result.append("&").append(myArray[i]);
            }
        }
        return result.toString();
    }

    private void loadConversations() {
        final Query listMessage = db_chatRoom;
        listMessage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get data when after update message
                if (dataSnapshot.exists()) {// get data when first open
                    if (!arrayList.isEmpty()) arrayList.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        arrayList.add(item.getValue(ChatMessageClass.class));
                    }
//                    isFirstLoad = false;
                    chatBoxView(0);
                    listMessage.removeEventListener(this);
                } else {
                    return;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error load database", Toast.LENGTH_SHORT).show();
            }
        });
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
        Intent intentCallVideo = new Intent(this, CallOutgoingActivity.class);
        startActivity(intentCallVideo);
    }

    public void btnCallEvent(View view) {
        Intent intentCallVideo = new Intent(this, CallOutgoingActivity.class);
        startActivity(intentCallVideo);
    }

    public void imgFriendEvent(View view) {
        Intent intent = getIntent();
        Intent intentFriendProfile = new Intent(this, ProfileFriendActivity.class);
        intentFriendProfile.putExtra("userName", intent.getStringExtra("userName"));
        intentFriendProfile.putExtra("userFriend", intent.getStringExtra("userFriend"));
        intentFriendProfile.putExtra("sipUri", intent.getStringExtra("userFriend") + "@hoangsang1998.com.vn");
        String isChattingActivity = "true";
        intentFriendProfile.putExtra("isChattingActivity", isChattingActivity);
        startActivity(intentFriendProfile);
    }
}
