package com.osama.osama.whatsfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ClickTaskActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private EditText task_name,task_content,task_deadline,
            task_time,task_expected,task_status;
    String name,content,deadline,time,expected,status,uid,date;
    String saveCurrentDate,saveCurrentTime,postRandomName;
    private Button update_task;
    private static final String TAG = "ClickTaskActivity";
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference taskRef;
    private  long countTask;
    String currentUserId;
    String postKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_task);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        postKey = getIntent().getExtras().get("postKey").toString();

        taskRef = FirebaseDatabase.getInstance().getReference().child("Tasks").child(postKey);

        loadingBar = new ProgressDialog(ClickTaskActivity.this);

        initializeFields();

        mToolbar = findViewById(R.id.update_task_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Update TaskContent");
        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    uid = dataSnapshot.child("uid").getValue().toString();

                    name = dataSnapshot.child("task_name").getValue().toString();
                    content = dataSnapshot.child("content").getValue().toString();
                    deadline = dataSnapshot.child("deadline").getValue().toString();
                    status = dataSnapshot.child("status").getValue().toString();
                    expected = dataSnapshot.child("expected").getValue().toString();
                    time = dataSnapshot.child("time").getValue().toString();
                    date = dataSnapshot.child("date").getValue().toString();

                    task_name.setText(name);
                    task_content.setText(content);
                    task_deadline.setText(deadline);
                    task_expected.setText(expected);
                    task_status.setText(status);
                    task_time.setText(time + " " + date);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        update_task = (Button) findViewById(R.id.update_task);
        update_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateTaskInfo();
            }
        });
    }


    private void validateTaskInfo(){

        name = task_name.getText().toString();

        if(task_name == null)
        {
            Toast.makeText(ClickTaskActivity.this,"Please,Add ", Toast.LENGTH_LONG).show();
        }
        else if(task_content == null)
        {
            Toast.makeText(ClickTaskActivity.this,"Please,Say something about your image",
                    Toast.LENGTH_LONG).show();
        }
        else if(task_deadline == null)
        {
            Toast.makeText(ClickTaskActivity.this,"Please,Add ", Toast.LENGTH_LONG).show();
        }
        else if(task_expected == null)
        {
            Toast.makeText(ClickTaskActivity.this,"Please,Say something about your image",
                    Toast.LENGTH_LONG).show();
        }
        else if(task_status == null)
        {
            Toast.makeText(ClickTaskActivity.this,"Please,Say something about your image",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            storingTask();
        }
    }


    private void storingTask()
    {
        name = task_name.getText().toString();
        content = task_content.getText().toString();
        deadline = task_deadline.getText().toString();
        time = task_time.getText().toString();
        expected = task_expected.getText().toString();
        status = task_status.getText().toString();

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd:MMMM-yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());
        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(callForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        loadingBar.setTitle("Update TaskContent");
        loadingBar.setMessage("Please wait, while we are updating your task...");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(true);

        HashMap postMap = new HashMap();
        postMap.put("uid", currentUserId);
        postMap.put("task_name", name);
        postMap.put("content", content);
        postMap.put("deadline",deadline);
        postMap.put("date", saveCurrentDate);
        postMap.put("time", saveCurrentTime);

        //    postMap.put("counter", countPost);
        postMap.put("expected", expected);
        postMap.put("status", status);
        // before we pass data, we have to give it its random name
        taskRef.updateChildren(postMap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            sendUserToMainLeaderActivity();
                            Toast.makeText(getApplicationContext(), "Your post created successfully...",
                                    Toast.LENGTH_LONG).show();

                        }
                        else
                        {
                            String msg = task.getException().getMessage();
                            Toast.makeText(getApplicationContext(), "Error occured " + msg,
                                    Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }



    private void sendUserToMainLeaderActivity()
    {
        Intent mainIntent = new Intent(ClickTaskActivity.this,MainLeaderActivity.class);
        // prevent user to be back to the MainActivity unless signin or signup
        //    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

    private void initializeFields()
    {
        task_name = findViewById(R.id.update_task_name);
        task_content = findViewById(R.id.update_task_content);
        task_deadline = findViewById(R.id.update_task_deadline);
        task_time = findViewById(R.id.update_task_time);
        task_status = findViewById(R.id.update_task_status);
        task_expected = findViewById(R.id.update_task_expected);
    }
}
