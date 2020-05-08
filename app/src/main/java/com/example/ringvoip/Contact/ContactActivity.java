package com.example.ringvoip.Contact;

import androidx.annotation.NonNull;
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

import com.example.ringvoip.Home.HomeActivity;
import com.example.ringvoip.Profile.ProfileFriendActivity;
import com.example.ringvoip.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity implements AdapterContactList.ContactClickListener{

    BottomNavigationView navFooter;
    RecyclerView recContact;
    ImageButton btnAddUser;
    ArrayList<ContactClass> contactList = new ArrayList<>();
    private Dialog dialog;
    String isChattingActivity = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        addControls();
        addEvents();
    }

    private void addEvents() {
        navFooter.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        ContactClass contact = new ContactClass("sang", "sip");
        ContactClass contact2 = new ContactClass("sang2", "sip2");
        ContactClass contact3 = new ContactClass("sang3", "sip3");
        ContactClass contact4 = new ContactClass("sang4", "sip4");
        ContactClass contact5 = new ContactClass("sang5", "sip5");
        String status = "Offline";
        contact.setStatus(status);
        contact2.setStatus("Online");
        contact3.setStatus(status);
        contact4.setStatus(status);
        contact5.setStatus(status);
        contactList.add(contact);
        contactList.add(contact2);
        contactList.add(contact3);
        contactList.add(contact4);
        contactList.add(contact5);
        initView();
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
//        Intent intentAddFriend = new Intent(this, AddFriendActivity.class);
//        startActivity(intentAddFriend);
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
//        intentProfileFriend.putExtra("isChattingActivity", isChattingActivity);
        intentProfileFriend.putExtra("userFriend", contactList.get(position).getName());
        intentProfileFriend.putExtra("sipUri", contactList.get(position).getName() + "@hoangsang1998.com.vn");
        intentProfileFriend.putExtra("contact_username", contactList.get(position).getName());
        intentProfileFriend.putExtra("isChattingActivity", isChattingActivity);
        startActivity(intentProfileFriend);
    }
}
