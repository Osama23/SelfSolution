package com.osama.osama.whatsfirebase;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.osama.osama.whatsfirebase.model.TaskContent;

public class MainMemberActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference taskRef,userRef,taskMemRef;
    private Toolbar toolbar;
    private TextView mTitle;
    private RecyclerView tasksList;
    String currentUserId;
    String memUid,taskUid;
    String taskFromMember;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_member);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference();
        taskRef = FirebaseDatabase.getInstance().getReference().child("Tasks");
        taskMemRef = FirebaseDatabase.getInstance().getReference().child("MembersTasks");

        createToolbar();
        tasksList = findViewById(R.id.tasks_recycler_view_member);
        tasksList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainMemberActivity.this);
        // display the latest at the top
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        tasksList.setLayoutManager(linearLayoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        tasksList.addItemDecoration(itemDecorator);

        // find my own tasks // the uni-code
        Query mTasksQuery = taskMemRef.orderByChild("member_uid")
                .startAt(currentUserId).endAt(currentUserId + "\uf8ff");
        mTasksQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    taskFromMember = dataSnapshot.child("task_uid").getValue().toString();
                    Toast.makeText(getApplicationContext(),taskFromMember,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        display();

    }

    private void displayMemberTasks()
    {

    }

    private void display()
    {
        // we will use the Firebase Recycler adapter
        FirebaseRecyclerAdapter<TaskContent,MainMemberActivity.TasksViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<TaskContent, MainMemberActivity.TasksViewHolder>
                        (
                                TaskContent.class,
                                R.layout.layout_task_list_item,
                                MainMemberActivity.TasksViewHolder.class,
                                //   mTasksQuery
                                taskRef  // if we want all the tasks
                        )
                {
                    @Override
                    protected void populateViewHolder(MainMemberActivity.TasksViewHolder viewHolder,
                                                      final TaskContent model, int position)
                    {
                        // exactly where the user is
                        final String postKey = getRef(position).getKey();
                        // get the uid to know the state of the post's user
                        taskUid = getRef(position).getKey();

                        viewHolder.setTask_name(model.getTask_name());
                        viewHolder.setDate(model.getDate());
                        // send user to edit and delete activity
                        viewHolder.mView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                ////////////////
                                final AlertDialog.Builder builder = new AlertDialog.Builder(MainMemberActivity.this);
                                builder.setTitle("Task");

                                builder.setPositiveButton("Find Member",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                Intent clickPostIntent = new Intent(MainMemberActivity.this,
                                                        FindMembersActivity.class);
                                                clickPostIntent.putExtra("postKey",postKey);
                                                startActivity(clickPostIntent);
                                            }
                                        });
                                builder.setNegativeButton("Update", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        Intent clickPostIntent = new Intent(MainMemberActivity.this,
                                                ClickTaskActivity.class);
                                        clickPostIntent.putExtra("postKey",postKey);

                                        startActivity(clickPostIntent);
                                    }
                                });
                                builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        taskRef.child(postKey).removeValue();
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
        tasksList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class TasksViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView taskName, taskStartedTime;
        int countLikes;
        String current_user_id, currentTaskUid;
        DatabaseReference likeRef, userRef, postRef;

        public TasksViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


            current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        }

        public void setTask_name(String task_name) {
            taskName = itemView.findViewById(R.id.task_title);
            taskName.setText(task_name);
        }

        public void setDate(String date) {
            taskStartedTime = itemView.findViewById(R.id.task_timestamp);
            taskStartedTime.setText("" + date);
        }

        public void setUid(String uid) {
            currentTaskUid = uid;
        }
    }


    private void createToolbar()
    {
        toolbar = findViewById(R.id.main_app_bar_member);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mTitle = findViewById(R.id.toolbar_title_member);
        mTitle.setText("Tasks");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.main_find_friends_option)
        {
            sendUserToFindMembersActivity();
        }

        if(item.getItemId() == R.id.main_all_tasks)
        {
           sendUserToAllTasksActivity();
        }
        if(item.getItemId() == R.id.main_settings_option)
        {
            sendUserToSettingsActivity();
        }
        if(item.getItemId() == R.id.main_logout_option)
        {
            mAuth.signOut();
            sendUserToLoginActivity();
        }
        return true;
    }


    private void sendUserToAllTasksActivity()
    {
        Intent friendsIntent = new Intent(MainMemberActivity.this,AllTasks.class);
        startActivity(friendsIntent);
    }

    private void sendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(MainMemberActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


    private void sendUserToFindMembersActivity()
    {
        Intent friendsIntent = new Intent(MainMemberActivity.this,FindMembersActivity.class);
        startActivity(friendsIntent);
    }

    private void sendUserToSettingsActivity()
    {
        Intent settingsIntent = new Intent(MainMemberActivity.this,SettingsActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();
    }
}
