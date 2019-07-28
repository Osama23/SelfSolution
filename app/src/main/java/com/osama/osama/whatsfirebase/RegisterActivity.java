package com.osama.osama.whatsfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity
{
    private Button createAccountButton;
    private EditText userEmail,userPassword;
    private TextView alreadyHaveAccount;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        loadingBar = new ProgressDialog(this);
        initializeFields();
        // set click listeners to textviews and buttons
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendUserToLoginActivity();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                createNewAccount();
            }
        });

    }

    private void createNewAccount()
    {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        // check fields
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(getApplicationContext(),"Please, Enter an email ...",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(getApplicationContext(),"Please, Enter a password ...",Toast.LENGTH_LONG).show();
        }
        else
        {
            loadingBar.setTitle("Creating Your Account...");
            loadingBar.setMessage("Please, Wait while we are sitting your account...");
            //   loadingBar.setCancelable(false);
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            // check task
                            if(task.isSuccessful())
                            {
                                // store data
                                String currentUserId = mAuth.getCurrentUser().getUid();
                                rootRef.child("Users").child(currentUserId).setValue("");

                                sendUserToMainActivity();
                                Toast.makeText(getApplicationContext(),"Account Created Successfully...",
                                        Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String msg = task.getException().toString();
                                Toast.makeText(getApplicationContext(),"Error : " + msg,Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();

                            }

                        }
                    });
        }
    }

    private void initializeFields()
    {
        createAccountButton = findViewById(R.id.register_button);
        userEmail = findViewById(R.id.register_email);
        userPassword = findViewById(R.id.register_password);
        alreadyHaveAccount = findViewById(R.id.register_already_have_account);
    }

    private void sendUserToMainActivity()
    {
        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        // the user can not go back if he press the back button
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void sendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }
}
