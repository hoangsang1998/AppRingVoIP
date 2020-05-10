package com.example.ringvoip.Contact;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.ringvoip.Home.HomeActivity;
import com.example.ringvoip.LinphoneService;
import com.example.ringvoip.Login.UserClass;
import com.example.ringvoip.Profile.ProfileFriendActivity;
import com.example.ringvoip.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.linphone.core.Core;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity implements AdapterContactList.ContactClickListener{

    BottomNavigationView navFooter;
    RecyclerView recContact;
    ImageButton btnAddUser;
    ArrayList<ContactClass> contactList = new ArrayList<>();
    private Dialog dialog;
    String isChattingActivity = "false";
    FirebaseDatabase database;
    String username;
    Core core;
    String domain;
    ChildEventListener childEventListener, childEventListenerStatus;
    Query ListenerContact, ListenerContactStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        addVariables();
        addControls();
        addEvents();
        loadContacts();
        //cat nhat contacts
        final DatabaseReference db_ListenerContact = database.getReferenceFromUrl("https://dbappchat-bbabc.firebaseio.com/contacts/" + username);
        ListenerContact = db_ListenerContact;
        ListenerContact.addChildEventListener(childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                loadContacts();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //lang nghe de cap nhat trang thai online, ofline
        final DatabaseReference db_ListenerContactStatus = database.getReferenceFromUrl("https://dbappchat-bbabc.firebaseio.com/users");
        ListenerContactStatus = db_ListenerContactStatus;
        ListenerContactStatus.addChildEventListener(childEventListenerStatus = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                loadContacts();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        ListenerContact.removeEventListener(childEventListener);
        ListenerContactStatus.removeEventListener(childEventListenerStatus);
    }

    private void addVariables() {
        database = FirebaseDatabase.getInstance();
        String temp = LinphoneService.getCore().getIdentity().split("@")[0];
        username= temp.split(":")[1];
        core = LinphoneService.getCore();
        domain = core.getIdentity().split("@")[1];
    }

    private void loadContacts() {
        final Query db_root = database.getReference();
        db_root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get data when after update message
                if (dataSnapshot.exists()) {// get data when first open
                    if (dataSnapshot.child("contacts").exists() && dataSnapshot.child("users").exists()) {
                        if (!contactList.isEmpty()) contactList.clear();
                        for (DataSnapshot contact_dtss : dataSnapshot.child("contacts").child(username).getChildren()) {
                            ContactClass contact = contact_dtss.getValue(ContactClass.class);
                            assert contact != null;
                            System.out.println("datasnapshot users: " + contact.getName());
                            UserClass check_user = dataSnapshot.child("users").child(contact.getName()).getValue(UserClass.class);
                            String status;
                            // avoid error when getting value from a user isn't in Firebase database
                            try {
                                if (check_user.getStatus() == null) {
                                    status = "Offline";
                                } else {
                                    status = check_user.getStatus();
                                }
                            } catch (Exception e) {
                                status = "Offline";
                            }
                            contact.setStatus(status);
                            contactList.add(contact);
                        }
                        initView();
                        db_root.removeEventListener(this);
                    } else {
                        return;
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Lỗi load database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addEvents() {
        navFooter.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
//        ContactClass contact = new ContactClass("sang", "sip");
//        ContactClass contact2 = new ContactClass("sang2", "sip2");
//        ContactClass contact3 = new ContactClass("sang3", "sip3");
//        ContactClass contact4 = new ContactClass("sang4", "sip4");
//        ContactClass contact5 = new ContactClass("sang5", "sip5");
//        String status = "Offline";
//        contact.setStatus(status);
//        contact2.setStatus("Online");
//        contact3.setStatus(status);
//        contact4.setStatus(status);
//        contact5.setStatus(status);
//        contactList.add(contact);
//        contactList.add(contact2);
//        contactList.add(contact3);
//        contactList.add(contact4);
//        contactList.add(contact5);
//        initView();
    }

    private void addControls() {
        navFooter = findViewById(R.id.nav_view);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener
            navigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_chat:
                    Intent intent_Home = new Intent(ContactActivity.this, HomeActivity.class);
//                    intent_groupchat.putExtra("username", username);
//                    intent_groupchat.putExtra("sipuri", sipuri);
                    startActivity(intent_Home);
                    break;
                case R.id.navigation_contact:

                    break;
            }
            return false;
        }
    };

    public void onBackPressed() {
        showAlertDialog();
//        finishAffinity();
    }
    public void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("RingVoIP");
        builder.setMessage("Bạn có muốn thoát không?");
        builder.setCancelable(false);
        builder.setPositiveButton("Ứ chịu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(ContactActivity.this, "Không thoát được", Toast.LENGTH_SHORT).show();
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

    public void btnAddFriendEvent(View view) {
        Intent intentAddFriend = new Intent(this, AddFriendActivity.class);
        startActivity(intentAddFriend);
    }

    public void initView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        AdapterContactList adapterContactList = new AdapterContactList(contactList, getApplicationContext());

        adapterContactList.setClickListener(this);
        recyclerView.setAdapter(adapterContactList);
    }

    @Override
    public void onContactClick(View view, int position) {
        Intent intentProfileFriend = new Intent(ContactActivity.this, ProfileFriendActivity.class);
        intentProfileFriend.putExtra("userFriend", contactList.get(position).getName());
        intentProfileFriend.putExtra("sipUri", contactList.get(position).getName() + "@" + domain);
        intentProfileFriend.putExtra("contact_username", contactList.get(position).getName());
        intentProfileFriend.putExtra("isChattingActivity", isChattingActivity);
        startActivity(intentProfileFriend);
    }
}
