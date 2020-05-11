package com.example.ringvoip.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ringvoip.Chat.ChattingActivity;
import com.example.ringvoip.Contact.ContactActivity;
import com.example.ringvoip.LinphoneService;
import com.example.ringvoip.Login.LoginActivity;
import com.example.ringvoip.Profile.ProfileActivity;
import com.example.ringvoip.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.linphone.core.ChatMessage;
import org.linphone.core.ChatRoom;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.ProxyConfig;
import org.linphone.core.RegistrationState;
import org.linphone.core.tools.Log;

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
    CoreListenerStub coreListenerStub;

    private CoreListenerStub mCoreListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addControls();
        addEvents();
        addVariables();

        Intent intent = getIntent();

//        username = intent.getStringExtra("username");

        String temp = LinphoneService.getCore().getIdentity().split("@")[0];
        String HelloUser = "Xin chào: " + temp.split(":")[1];
        username= temp.split(":")[1];
        txtUserName.setText(HelloUser);
        sipuri = intent.getStringExtra("sipuri");
        //------------------
        recChat = findViewById(R.id.recChat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recChat.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recChat.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHome();
        // The best way to use Core listeners in Activities is to add them in onResume
        // and to remove them in onPause
        LinphoneService.getCore().addListener(mCoreListener);

        // Manually update the LED registration state, in case it has been registered before
        // we add a chance to register the above listener
        ProxyConfig proxyConfig = LinphoneService.getCore().getDefaultProxyConfig();
        if (proxyConfig != null) {
            updateLed(proxyConfig.getState());
        } else {
//             No account configured, we display the configuration activity
            startActivity(new Intent(this, LoginActivity.class));
        }
        //---------------------------------------
        //nhan tin tu server luu, cap nhat lai home
        coreListenerStub = new CoreListenerStub() {
            @Override
            public void onMessageReceived(Core lc, ChatRoom room, ChatMessage message) {
                if (!LinphoneService.flagService){
                    loadHome();
                }
            }
        };
        LinphoneService.getCore().addListener(coreListenerStub);


    }

    @Override
    protected void onPause() {
        // Like I said above, remove unused Core listeners in onPause
        LinphoneService.getCore().removeListener(mCoreListener);
        //----------------------------------
        LinphoneService.getCore().removeListener(coreListenerStub);
        super.onPause();
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

    @Override
    protected void onStart() {
        super.onStart();

        // Ask runtime permissions, such as record audio and camera
        // We don't need them here but once the user has granted them we won't have to ask again
//        checkAndRequestCallPermissions();
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        // Callback for when permissions are asked to the user
        for (int i = 0; i < permissions.length; i++) {
            Log.i(
                    "[Permission] "
                            + permissions[i]
                            + " is "
                            + (grantResults[i] == PackageManager.PERMISSION_GRANTED
                            ? "granted"
                            : "denied"));
        }
    }

    private void checkAndRequestCallPermissions() {
        ArrayList<String> permissionsList = new ArrayList<>();

        // Some required permissions needs to be validated manually by the user
        // Here we ask for record audio and camera to be able to make video calls with sound
        // Once granted we don't have to ask them again, but if denied we can
        int recordAudio =
                getPackageManager()
                        .checkPermission(Manifest.permission.RECORD_AUDIO, getPackageName());
        Log.i(
                "[Permission] Record audio permission is "
                        + (recordAudio == PackageManager.PERMISSION_GRANTED
                        ? "granted"
                        : "denied"));
        int camera =
                getPackageManager().checkPermission(Manifest.permission.CAMERA, getPackageName());
        Log.i(
                "[Permission] Camera permission is "
                        + (camera == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));

        if (recordAudio != PackageManager.PERMISSION_GRANTED) {
            Log.i("[Permission] Asking for record audio");
            permissionsList.add(Manifest.permission.RECORD_AUDIO);
        }

        if (camera != PackageManager.PERMISSION_GRANTED) {
            Log.i("[Permission] Asking for camera");
            permissionsList.add(Manifest.permission.CAMERA);
        }

        if (permissionsList.size() > 0) {
            String[] permissions = new String[permissionsList.size()];
            permissions = permissionsList.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, 0);
        }
    }

    private void updateLed(RegistrationState state) {
        switch (state) {
            case Ok: // This state means you are connected, to can make and receive calls & messages
//                mLed.setImageResource(R.drawable.led_connected);
                break;
            case None: // This state is the default state
            case Cleared: // This state is when you disconnected
//                mLed.setImageResource(R.drawable.led_disconnected);
                break;
            case Failed: // This one means an error happened, for example a bad password
//                mLed.setImageResource(R.drawable.led_error);
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case Progress: // Connection is in progress, next state will be either Ok or Failed
//                mLed.setImageResource(R.drawable.led_inprogress);
                break;
        }
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

        recChat.setHasFixedSize(true);




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
