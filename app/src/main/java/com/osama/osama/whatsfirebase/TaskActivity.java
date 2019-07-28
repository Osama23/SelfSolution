package com.osama.osama.whatsfirebase;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private Spinner statusSpinner;
    private EditText task_name,task_content,task_deadline,
            task_time,task_expected;
    String name,content,deadline,time,expected,status;
    String saveCurrentDate,saveCurrentTime,postRandomName;
    private Button add_task,osama;
    private static final String TAG = "TaskContent Activity";
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference taskRef;
    private  long countTask;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        taskRef = FirebaseDatabase.getInstance().getReference();

        loadingBar = new ProgressDialog(TaskActivity.this);

        initializeFields();

        mToolbar = findViewById(R.id.add_task_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add TaskContent");
        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        osama = findViewById(R.id.osama);
        osama.setOnClickListener(new View.OnClickListener() {
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
            Toast.makeText(TaskActivity.this,"Please,Add ", Toast.LENGTH_LONG).show();
        }
        else if(task_content == null)
        {
            Toast.makeText(TaskActivity.this,"Please,Say something about your image",
                    Toast.LENGTH_LONG).show();
        }
        else if(task_deadline == null)
        {
            Toast.makeText(TaskActivity.this,"Please,Add ", Toast.LENGTH_LONG).show();
        }
        else if(task_expected == null)
        {
            Toast.makeText(TaskActivity.this,"Please,Say something about your image",
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
        status = String.valueOf(statusSpinner.getSelectedItem());

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd:MMMM-yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());
        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(callForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        loadingBar.setTitle("Add New TaskContent");
        loadingBar.setMessage("Please wait, while we are updating your new task...");
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
        taskRef.child("Tasks").child(currentUserId + postRandomName).setValue(postMap)
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
        Intent mainIntent = new Intent(TaskActivity.this,MainLeaderActivity.class);
        // prevent user to be back to the MainActivity unless signin or signup
        //    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

    private void initializeFields()
    {
       task_name = findViewById(R.id.my_task_name);
       task_content = findViewById(R.id.my_task_content);
       task_deadline = findViewById(R.id.my_task_deadline);

        task_time = findViewById(R.id.my_task_time);
       task_expected = findViewById(R.id.my_task_expected);
       statusSpinner = (Spinner) findViewById(R.id.spinner1);
        List<String> list = new ArrayList<String>();
        list.add("completed");
        list.add("new");
        list.add("unassign");
        list.add("overdue");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(dataAdapter);
    }


}
