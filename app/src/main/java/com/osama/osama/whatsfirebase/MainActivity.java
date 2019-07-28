package com.osama.osama.whatsfirebase;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.osama.osama.whatsfirebase.adapters.TabsAccessAdapter;

public class MainActivity extends AppCompatActivity
{
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       mAuth = FirebaseAuth.getInstance();
       currentUser = mAuth.getCurrentUser();
       userRef = FirebaseDatabase.getInstance().getReference();
    }

    // whenever the app starts it should first check the state of current user
    @Override
    protected void onStart()
    {
        super.onStart();

        if(currentUser == null)
        {
            sendUserToLoginActivity();
        }
        else
        {
            verfiyUserExistence();
        }
    }

    // here we check the first time the user enter the app
    private void verfiyUserExistence()
    {
        final String currentUserId = mAuth.getCurrentUser().getUid();

        userRef.child("Users").child("Leader").child(currentUserId)
                .addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild("name"))
                {
                    sendUserToMainLeaderActivity();
                }
                else
                {
                    verfiyUserExistence1();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

    }

    private void verfiyUserExistence1()
    {
        final String currentUserId = mAuth.getCurrentUser().getUid();

        userRef.child("Users").child("Member").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("name"))
                {
                    sendUserToMainMemberActivity();
                }
                else
                {
                    // user is authenticated but not in  the database, send him to setup
                    sendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void sendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


    private void sendUserToSettingsActivity()
    {
        Intent settingsIntent = new Intent(MainActivity.this,SettingsActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();
    }

    private void sendUserToMainLeaderActivity()
    {
        Intent mainIntent = new Intent(MainActivity.this,MainLeaderActivity.class);
        // prevent user to be back to the MainActivity unless signin or signup
        //    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }


    private void sendUserToMainMemberActivity()
    {
        Intent mainIntent = new Intent(MainActivity.this,MainMemberActivity.class);
        // prevent user to be back to the MainActivity unless signin or signup
        //    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

}
