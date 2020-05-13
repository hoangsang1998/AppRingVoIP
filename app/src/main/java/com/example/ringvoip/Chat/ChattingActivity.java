package com.example.ringvoip.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ringvoip.Call.CallOutgoingActivity;
import com.example.ringvoip.Home.ChatRoomClass;
import com.example.ringvoip.LinphoneService;
import com.example.ringvoip.Profile.ProfileFriendActivity;
import com.example.ringvoip.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.CallParams;
import org.linphone.core.ChatMessage;
import org.linphone.core.ChatMessageListenerStub;
import org.linphone.core.ChatRoom;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChattingActivity extends AppCompatActivity {

    TextView txtFriendName;
    RecyclerView recyclerView;
    AdapterConversation adapterConversation;
    ArrayList<ChatMessageClass> arrayList;
    String username, contact_user, chatRoom;
    EditText txtMessage;

    FirebaseDatabase database;
    DatabaseReference db_chatRoom, db_room, db_listMessage, db_chatlogs;
    ChatMessageClass newMessage, newMessage1;
    ChatRoomClass chatroomlog;
    Query listMessage;
    CoreListenerStub coreListenerStub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        addVariables();
        addControls();
        addEvents();
        loadConversations();

        txtFriendName.setText(contact_user);
        //---------------------------
        //nhan tin tu server
        coreListenerStub = new CoreListenerStub() {
            @Override
            public void onMessageReceived(Core lc, ChatRoom room, ChatMessage message) {
                if (room != null) {
                    if (message.hasTextContent()) {
                        String messageTime = new SimpleDateFormat("dd-MM-yyyy | HH:mm").format(new Date(message.getTime() * 1000L));
                        String messageContent = message.getTextContent();
                        String messageFrom = message.getFromAddress().getUsername();
                        ChatMessageClass chatMessageClass = new ChatMessageClass(messageFrom, messageContent, messageTime);
                        //display received message when at ChattingActivity with the sender
                        if (message.getCustomHeader("group") == null && !message.getFromAddress().getUsername().equals(username)) {
                            if (message.getFromAddress().getUsername().equals(contact_user)) {

                                if(message.getCustomHeader("fromApp") == null) {
                                    //luu neu k co getCustomHeader
                                    if(LinphoneService.flagService) {
                                        db_chatRoom.push().setValue(chatMessageClass);
                                        chatroomlog.setUsername1(contact_user);
                                        chatroomlog.setUsername2(username);
                                        chatroomlog.setContext(chatMessageClass.getContext());
                                        chatroomlog.setDatetime(getStringDateTimeChatRoom());
                                        db_room.setValue(chatroomlog);
                                    }

                                }

                                //them vao GUI
                                arrayList.add(chatMessageClass);
                                if(adapterConversation == null) {
                                    chatBoxView(0);
                                } else {
                                    adapterConversation.addItem(arrayList);
                                }

                                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
                if (state == Call.State.Released) {
                    newMessage1 = new ChatMessageClass(call.getCallLog().getFromAddress().getUsername()
                            , String.valueOf(call.getDuration()), getStringDateTime(), call.getCallLog().getStatus().toString());
                    arrayList.add(newMessage1);
                    if(adapterConversation == null) {
                        chatBoxView(0);
                    } else {
                        adapterConversation.addItem(arrayList);
                    }

                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);

                }
            }
        };
        LinphoneService.getCore().addListener(coreListenerStub);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LinphoneService.getCore().removeListener(coreListenerStub);
    }

    public void btnCallVideoEvent(View view)
    {
        Core core = LinphoneService.getCore();
        Address addressToCall = core.interpretUrl(txtFriendName.getText().toString());
        CallParams params = core.createCallParams(null);

//        Switch videoEnabled = findViewById(R.id.call_with_video);
        params.enableVideo(true);

        if (addressToCall != null) {
            core.inviteAddressWithParams(addressToCall, params);
        }
//        Intent intentCallVideo = new Intent(this, CallOutgoingActivity.class);
//        startActivity(intentCallVideo);
    }

    public void btnCallEvent(View view) {
        Core core = LinphoneService.getCore();
        Address addressToCall = core.interpretUrl(txtFriendName.getText().toString());
        CallParams params = core.createCallParams(null);

        params.enableVideo(false);

        if (addressToCall != null) {
            core.inviteAddressWithParams(addressToCall, params);
        }
//        Intent intentCallVideo = new Intent(this, CallOutgoingActivity.class);
//        startActivity(intentCallVideo);
    }

    public void imgFriendEvent(View view) {
        Intent intent = getIntent();
        Intent intentFriendProfile = new Intent(this, ProfileFriendActivity.class);
        intentFriendProfile.putExtra("userName", intent.getStringExtra("userName"));
        intentFriendProfile.putExtra("userFriend", intent.getStringExtra("userFriend"));
        intentFriendProfile.putExtra("sipUri", intent.getStringExtra("userFriend") + "@" + LinphoneService.getCore().getIdentity().split("@")[1]);
        String isChattingActivity = "true";
        intentFriendProfile.putExtra("isChattingActivity", isChattingActivity);
        startActivity(intentFriendProfile);
    }

    public void btnSendMessageEvent(View view) {
        if (!txtMessage.getText().toString().trim().equals("")) {
            txtMessage.setText(txtMessage.getText().toString().trim());
            //send message to server
            Core core = LinphoneService.getCore();
            String contactUserDomain = LinphoneService.getCore().getIdentity().split("@")[1];
            String contactUserUri = "sip:" + contact_user + "@" + contactUserDomain;
            Address address = core.interpretUrl(contactUserUri);
            ChatRoom chatRoom = core.createChatRoom(address);
            if (chatRoom != null) {
                ChatMessage chatMessage = chatRoom.createEmptyMessage();
                chatMessage.addCustomHeader("fromApp", "RingVoIP");
                chatMessage.addTextContent(txtMessage.getText().toString());
                if (chatMessage.getTextContent() != null) {
                    chatRoom.sendChatMessage(chatMessage);
                } else {
                    txtMessage.requestFocus();
                }
            } else {
                Log.e("ERROR: ", "Cannot create chat room");
            }

            //them vao firebase
            newMessage = new ChatMessageClass(username, txtMessage.getText().toString(), getStringDateTime());
            db_chatRoom.push().setValue(newMessage);
            chatroomlog.setUsername1(username);
            chatroomlog.setUsername2(contact_user);
            chatroomlog.setContext(newMessage.getContext());
            chatroomlog.setDatetime(getStringDateTimeChatRoom());
            db_room.setValue(chatroomlog);
            //them vao GUI
            arrayList.add(newMessage);
            if(adapterConversation == null) {
                chatBoxView(0);
            } else {
                adapterConversation.addItem(arrayList);
            }

            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
        }
        txtMessage.setText("");
    }

    private void addVariables() {
        Intent intent = getIntent();
//        username = intent.getStringExtra("userName");
        String temp = LinphoneService.getCore().getIdentity().split("@")[0];
        String HelloUser = temp.split(":")[1];
        username = HelloUser;
        contact_user = intent.getStringExtra("userFriend");
        database = FirebaseDatabase.getInstance();
        chatRoom = getChatRoomByTwoUsername(username, contact_user);
        db_chatRoom = database.getReferenceFromUrl("https://dbappchat-bbabc.firebaseio.com/chatlogs/" + chatRoom);
        chatroomlog = new ChatRoomClass();
        db_room = database.getReferenceFromUrl("https://dbappchat-bbabc.firebaseio.com/chatrooms/" + chatRoom);

        db_listMessage = database.getReferenceFromUrl("https://dbappchat-bbabc.firebaseio.com/chatlogs/" + chatRoom);
        listMessage = db_listMessage;
        db_chatlogs = database.getReferenceFromUrl("https://dbappchat-bbabc.firebaseio.com/chatlogs/");

    }



    private void loadConversations() {
        //get message
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

    // datetime string for message
    public static String getStringDateTime() {
        String result = "";
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat formatterNew = new SimpleDateFormat("dd-MM-yyyy | HH:mm", Locale.getDefault());
        result = formatterNew.format(currentTime);
        return result;
    }

    // datetime string for chatroom
    public static String getStringDateTimeChatRoom() {
        String result = "";
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat formatterNew = new SimpleDateFormat("yyyy-MM-dd | HH:mm:ss", Locale.getDefault());
        result = formatterNew.format(currentTime);
        return result;
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
}
