package com.example.ringvoip.Contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ringvoip.LinphoneService;
import com.example.ringvoip.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddFriendActivity extends AppCompatActivity {

    EditText etxt_add_username;
    DatabaseReference db_Contact;
    FirebaseDatabase database;
    String username, etxt_add_username_Stringtype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        etxt_add_username = findViewById(R.id.etxt_add_username);
        String temp = LinphoneService.getCore().getIdentity().split("@")[0];
        username= temp.split(":")[1];
        database = FirebaseDatabase.getInstance();
        db_Contact = database.getReferenceFromUrl("https://dbappchat-bbabc.firebaseio.com/contacts/" + username);
    }

    public void btnAddEvent(View view) {
        etxt_add_username_Stringtype = etxt_add_username.getText().toString();
        if (etxt_add_username_Stringtype.trim().equals(username)) {
            Toast.makeText(getApplicationContext(), "Cannot add friend to yourself", Toast.LENGTH_SHORT).show();
        } else if (!etxt_add_username_Stringtype.trim().equals("")) {

            //Check whether the contact has existed yet
            final Query existedContacts = db_Contact;

            existedContacts.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot each_contact : dataSnapshot.getChildren()) {
                        ContactClass check_contact = each_contact.getValue(ContactClass.class);
                        if (check_contact.getName().equals(etxt_add_username_Stringtype)) {
                            Toast.makeText(getApplicationContext(), "This user has already added", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    ContactClass check_contact = new ContactClass(etxt_add_username_Stringtype,
                            "sip:" + etxt_add_username_Stringtype + "@" + LinphoneService.getCore().getIdentity().split("@")[1]);
                    db_Contact.child(etxt_add_username_Stringtype).setValue(check_contact);

                    Toast.makeText(getApplicationContext(), "Successfully adding contact", Toast.LENGTH_SHORT).show();

                    existedContacts.removeEventListener(this);
                    etxt_add_username.setText("");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Please enter", Toast.LENGTH_SHORT).show();
        }

//        Intent intentContact = new Intent(this, ContactActivity.class);
//        startActivity(intentContact);
    }

    public void btnBackEvent(View view) {
        finish();
    }
}
