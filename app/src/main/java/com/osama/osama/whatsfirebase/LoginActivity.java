package com.osama.osama.whatsfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity
{
    private TextView needNewAccount,forgetPassword;
    private EditText userEmail,userPassword;
    private Button loginButton,phoneLoginButton;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this);
        initializeFields();

        // set click listeners to textviews and buttons
        needNewAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendUserToRegisterActivity();
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                allowUserToLogin();
            }
        });

        phoneLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendUserToPhoneLoginActivity();
            }
        });

    }

    private void allowUserToLogin()
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
            loadingBar.setTitle("Sign In...");
            loadingBar.setMessage("Please Wait...");
            //   loadingBar.setCancelable(false);
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password).
                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // check task
                    if (task.isSuccessful())
                    {
                        ////////////////

          ///////////////////////
                        sendUserToMainActivity();
                        Toast.makeText(getApplicationContext(), "Logged In Successfully...",
                                Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    } else {
                        String msg = task.getException().toString();
                        Toast.makeText(getApplicationContext(), "Error : " + msg, Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();

                    }
                }
            });
        }
    }

    private void initializeFields()
    {
        loginButton = findViewById(R.id.login_button);
        phoneLoginButton = findViewById(R.id.phone_login_button);
        userEmail = findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_password);
        needNewAccount = findViewById(R.id.login_need_new_account);
        forgetPassword = findViewById(R.id.login_forget_password);
    }

    private void sendUserToRegisterActivity()
    {
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void sendUserToPhoneLoginActivity()
    {
        Intent phoneIntent = new Intent(LoginActivity.this,PhoneLoginActivity.class);
        startActivity(phoneIntent);
    }

    private void sendUserToMainActivity()
    {

        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
      //  mainIntent.putExtra("postKey",postKey);
        // the user can not go back if he press the back button
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}

