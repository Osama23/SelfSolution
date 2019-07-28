package com.osama.osama.whatsfirebase;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.osama.osama.whatsfirebase.model.Member;
import com.osama.osama.whatsfirebase.model.TaskContent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class FindMembersActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference memberRef,taskRef,memberTask;
    private Toolbar toolbar;
    private TextView mTitle;
    private RecyclerView memberList;
    String currentUserId,postKey;
    private String memberUid,memberName;
    private String taskUid,taskName;
    private ProgressDialog loadingBar;
    String saveCurrentDate,saveCurrentTime,postRandomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_members);

        postKey = getIntent().getExtras().get("postKey").toString();
        // initalize our objects
        mAuth = FirebaseAuth.getInstance();
        memberTask = FirebaseDatabase.getInstance().getReference();
        memberRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Member");
        taskRef = FirebaseDatabase.getInstance().getReference().child("Tasks");
        loadingBar = new ProgressDialog(this);

       // create our toolbar
        createToolbar();
        // create our list
        memberList = findViewById(R.id.find_member_recycler_view);
        memberList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FindMembersActivity.this);
        // display the latest at the top
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        memberList.setLayoutManager(linearLayoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        memberList.addItemDecoration(itemDecorator);
        // assign values to our list
        displayAllMembers();
    }

    private void displayAllMembers()
    {
        // we will use the Firebase Recycler adapter
        FirebaseRecyclerAdapter<Member,MembersViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Member, MembersViewHolder>
                        (
                                Member.class,
                                R.layout.layout_task_list_item,
                                MembersViewHolder.class,
                                //  sortPostInDecendingOrder
                                memberRef
                        )
                {
                    @Override
                    protected void populateViewHolder(MembersViewHolder viewHolder, final Member model, int position)
                    {
                        // exactly where the user is
                        final String postKey = getRef(position).getKey();
                        // get the uid to know the state of the post's user
                        memberUid = getRef(position).getKey();
                        viewHolder.setName(model.getName());

                        // send user to edit and delete activity
                        viewHolder.mView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                ////////////////
                                final AlertDialog.Builder builder = new AlertDialog.Builder(FindMembersActivity.this);
                                builder.setTitle("Task");

                                builder.setPositiveButton("Assign", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // create database with the task and the member
                                        assignTaskToMember();

                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                       dialog.cancel();
                                    }
                                });

                                Dialog dialog = builder.create();
                                dialog.show();
                                dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_green_dark);

                            }
                        });

                    }
                };
        // set the adapter
        memberList.setAdapter(firebaseRecyclerAdapter);
    }

    private void assignTaskToMember()
    {
        currentUserId = mAuth.getCurrentUser().getUid();
        taskRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("task_name"))
                    {
                        taskUid = dataSnapshot.child("uid").getValue().toString();
                        taskName = dataSnapshot.child("task_name").getValue().toString();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        memberRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("name"))
                    {
                        memberUid = dataSnapshot.child("uid").getValue().toString();
                        memberName = dataSnapshot.child("name").getValue().toString();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        loadingBar.setTitle("Assigning Your Task...");
        loadingBar.setMessage("Please, Wait while we are assigning your task...");
        //   loadingBar.setCancelable(false);
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd:MMMM-yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());
        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(callForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        HashMap userMap = new HashMap<>();
        userMap.put("member_uid",memberUid);
        userMap.put("member_name", memberName);
        userMap.put("task_uid", postKey);
        userMap.put("task_name",taskName);

        memberTask.child("MembersTasks").child(memberUid + postRandomName).
                setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>()
        {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            sendUserToMainLeaderActivity();

                            loadingBar.dismiss();
                        }
                        else
                        {
                            String msg = task.getException().getMessage();
                            Toast.makeText(getApplicationContext()," " + msg,
                                    Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }

                    }
                });

    }

    public static class MembersViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        TextView memberName;
        String current_user_id, currentTaskUid;

        public MembersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        }

        public void setName(String name) {
            memberName = itemView.findViewById(R.id.task_title);
            memberName.setText(name);
        }

        public void setUid(String uid)
        {
            currentTaskUid = uid;
        }
    }


    // end of viewholder

    private void createToolbar()
    {
        toolbar = findViewById(R.id.find_member_app_bar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mTitle = findViewById(R.id.toolbar_title_find_member);
        mTitle.setText("Members");
    }

    private void sendUserToMainLeaderActivity()
    {
        Intent mainIntent = new Intent(FindMembersActivity.this,MainLeaderActivity.class);
        //   mainIntent.putExtra("postKey",postKey);
        startActivity(mainIntent);
    }

}
