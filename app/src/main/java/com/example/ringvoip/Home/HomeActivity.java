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

import com.example.ringvoip.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addControls();
        addEvents();
        addVariables();
        Intent intent = getIntent();
//        username = intent.getStringExtra("username");
        username="sang";
        sipuri = intent.getStringExtra("sipuri");
        ChatRoomClass chatRoomClass = new ChatRoomClass("sang", "quynh","hello","2020-03-30 | 10:59:27" );
        ChatRoomClass chatRoomClass1 = new ChatRoomClass("sang", "thang","hello","2020-03-30 | 10:59:27" );
        ChatRoomClass chatRoomClass2 = new ChatRoomClass("sang", "vi","hello","2020-03-30 | 10:59:27" );
        ChatRoomClass chatRoomClass3 = new ChatRoomClass("sang", "duy","hello","2020-03-30 | 10:59:27" );
        ChatRoomClass chatRoomClass4 = new ChatRoomClass("sang", "nhan","hello","2020-03-30 | 10:59:27" );
        chatRoomList.add(chatRoomClass);
        chatRoomList.add(chatRoomClass1);
        chatRoomList.add(chatRoomClass2);
        chatRoomList.add(chatRoomClass3);
        chatRoomList.add(chatRoomClass4);
        initView();
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
//                    Intent intent_contact = new Intent(HomeActivity.this, ContactActivity.class);
////                    intent_groupchat.putExtra("username", username);
////                    intent_groupchat.putExtra("sipuri", sipuri);
//                    startActivity(intent_contact);
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
//        Intent intentProfile = new Intent(this, ProfileActivity.class);
//        startActivity(intentProfile);
    }

    @Override
    public void onDataChatClick(View view, int position) {
//        Intent intentChat = new Intent(HomeActivity.this, ChattingActivity.class);
//        intentChat.putExtra("userName", username);
//        intentChat.putExtra("sipUri", sipuri);
//        intentChat.putExtra("userFriend",
//                getUserContact(chatRoomList.get(position).getUsername1(),
//                        chatRoomList.get(position).getUsername2(),
//                        username));
//        startActivity(intentChat);
    }

    public static String getUserContact(String username1, String username2, String username) {
        if (username1.equals(username)) {
            return username2;
        } else return username1;
    }
}
