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
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.osama.osama.whatsfirebase.model.TaskContent;

public class AllTasks extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference taskRef,userRef,taskMemRef;
    private Toolbar toolbar;
    private TextView mTitle;
    private RecyclerView tasksList;
    String currentUserId,taskUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference();
        taskRef = FirebaseDatabase.getInstance().getReference().child("Tasks");

        createToolbar();
        tasksList = findViewById(R.id.tasks_recycler_view_all);
        tasksList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AllTasks.this);
        // display the latest at the top
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        tasksList.setLayoutManager(linearLayoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        tasksList.addItemDecoration(itemDecorator);

        display();
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
                            public void onClick(View v) {

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
        String current_user_id, currentTaskUid;

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
        toolbar = findViewById(R.id.main_app_bar_all);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mTitle = findViewById(R.id.toolbar_title_all);
        mTitle.setText("Tasks");
    }


}
