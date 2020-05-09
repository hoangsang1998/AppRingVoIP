package com.example.ringvoip.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ringvoip.Chat.ChattingActivity;
import com.example.ringvoip.Contact.ContactActivity;
import com.example.ringvoip.Profile.ProfileActivity;
import com.example.ringvoip.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HomeActivity extends AppCompatActivity implements AdapterChatRoom.DataChatClickListener{
    ImageView imgUserName;
    TextView txtUserName;
    RecyclerView recChat;
    BottomNavigationView navFooter;
    ArrayList<ChatRoomClass> chatRoomList;
    String username, sipuri;

    FirebaseDatabase database;
    DatabaseReference db_chatRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addControls();
        addEvents();
        addVariables();
        loadHome();
        Intent intent = getIntent();
//        username = intent.getStringExtra("username");
        username="sang";
        sipuri = intent.getStringExtra("sipuri");
    }

    private void loadHome() {
        final Query listChatRoomRemain = db_chatRoom.getRef()
                .orderByKey()
                .startAt("&" + username + "&")
                .endAt("\uf8ff");
        listChatRoomRemain.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get data when after update meessage
                if (dataSnapshot.exists()) {// get data whem first open
                    if (!chatRoomList.isEmpty()) chatRoomList.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        if (item.child("username1").getValue() != null && item.child("username1").getValue() != null) {
                            if (item.child("username1").getValue().equals(username) || item.child("username2")
                                    .getValue()
                                    .equals(username)) {
                                chatRoomList.add(item.getValue(ChatRoomClass.class));
                            }
                        }
                    }
                    initView();
                    listChatRoomRemain.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error loading database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener
            navigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_chat:
                    break;
                case R.id.navigation_contact:
                    Intent intent_contact = new Intent(HomeActivity.this, ContactActivity.class);
//                    intent_groupchat.putExtra("username", username);
//                    intent_groupchat.putExtra("sipuri", sipuri);
                    startActivity(intent_contact);
                    break;
            }
            return false;
        }
    };

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
    public void onBackPressed() {
        showAlertDialog();
    }

    private void initView() {
        recChat = findViewById(R.id.recChat);
        recChat.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recChat.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recChat.addItemDecoration(dividerItemDecoration);

        //sort by datetime
        Collections.sort(chatRoomList, new Comparator<ChatRoomClass>() {
            @Override
            public int compare(ChatRoomClass lhs, ChatRoomClass rhs) {
                return rhs.getDatetime().compareTo(lhs.getDatetime());
            }
        });

        AdapterChatRoom adapterChatRoom = new AdapterChatRoom(chatRoomList, this, username);
        adapterChatRoom.setClickListener(this);
        recChat.setAdapter(adapterChatRoom);
    }

    private void addVariables() {
        chatRoomList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        db_chatRoom = database.getReferenceFromUrl("https://dbappchat-bbabc.firebaseio.com/chatrooms");
    }

    private void addEvents() {
        navFooter.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    private void addControls() {
        navFooter = findViewById(R.id.nav_view);
        imgUserName = findViewById(R.id.imgUserName);
        txtUserName = findViewById(R.id.txtUserName);
        recChat = findViewById(R.id.recChat);
    }

    public void imgUserNameEvent(View view) {
        Intent intentProfile = new Intent(this, ProfileActivity.class);
        startActivity(intentProfile);
    }

    @Override
    public void onDataChatClick(View view, int position) {
        Intent intentChat = new Intent(HomeActivity.this, ChattingActivity.class);
        intentChat.putExtra("userName", username);
        intentChat.putExtra("sipUri", sipuri);
        intentChat.putExtra("userFriend",
                getUserContact(chatRoomList.get(position).getUsername1(),
                        chatRoomList.get(position).getUsername2(),
                        username));
        startActivity(intentChat);
    }

    public static String getUserContact(String username1, String username2, String username) {
        if (username1.equals(username)) {
            return username2;
        } else return username1;
    }
}
