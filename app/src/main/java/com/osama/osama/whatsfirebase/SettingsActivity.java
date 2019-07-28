package com.osama.osama.whatsfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity
{
    private Button updateProfile;
    private EditText username,userStatus;
    private RadioGroup radioJobTitle;
    private RadioButton radioJobButton;
    private CircleImageView userImage;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private StorageReference userProfileImageRef;

    private ProgressDialog loadingBar;
    String currentUserId;
    final static int GALLERY_PIC=1;
    private static final String TAG = "Settings Activity";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        // get the current user -id to store data easly
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference();

     //   postKey = getIntent().getExtras().get("postKey").toString();

        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        loadingBar = new ProgressDialog(this);

        initializeFields();

        // here we do not allow the user to write the name just only the first time
        username.setVisibility(View.INVISIBLE);

        // Read from the database to display image and username
        userRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // check first
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("image"))
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.with(SettingsActivity.this).load(image).
                                placeholder(R.drawable.profile_image).into(userImage);
                    }
                    else
                    {

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });


        userImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // send user to his mobile gallery
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PIC);

            }
        });


        updateProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateSettings();
            }
        });

        retrieveUserInfo();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PIC && resultCode == RESULT_OK && data != null)
        {
            Uri imageUri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we are updating your profile image...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);
                Uri resultUri = result.getUri();

                StorageReference filePath = userProfileImageRef.child(currentUserId + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(),"Profile image stored successfully to Firebase Storage..",
                                    Toast.LENGTH_LONG).show();
                            // put the image in database
                            // first, get the link from the firebase storage
                            final String downloadURL = task.getResult().getDownloadUrl().toString();
                            userRef.child("image").setValue(downloadURL)
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Intent selfIntent = new Intent(SettingsActivity.this,
                                                        SettingsActivity.class);
                                                startActivity(selfIntent);
                                                Toast.makeText(getApplicationContext(),
                                                        "Profile image stored successfully to Firebase database..",
                                                        Toast.LENGTH_LONG).show();
                                                loadingBar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(getApplicationContext(),
                                                        "Error Occured three" +message,Toast.LENGTH_LONG).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            String message = task.getException().getMessage();
                            Toast.makeText(getApplicationContext(),
                                    "Error Occured three" +message,Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }
        }
    }


    private void retrieveUserInfo()
    {
        // Read from the database to display image and username
        userRef.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists() &&
                        dataSnapshot.hasChild("name")
                        && dataSnapshot.hasChild("image"))
                {
                    String retName = dataSnapshot.child("name").getValue().toString();
                    String retStatus = dataSnapshot.child("status").getValue().toString();
                    String profileImage = dataSnapshot.child("image").getValue().toString();

                    username.setText(retName);
                    userStatus.setText(retStatus);
                    // now, use picasso to display image
                    //  Picasso.with(SettingsActivity.this).load(image)
                    // .placeholder(R.drawable.profile_image).into(userImage);
                }
                else if(dataSnapshot.exists() && dataSnapshot.hasChild("name") )
                {
                    String retName = dataSnapshot.child("name").getValue().toString();
                    String retStatus = dataSnapshot.child("status").getValue().toString();
                    username.setText(retName);
                    userStatus.setText(retStatus);
                }
                else
                {
                    // just on the start of creating new account let the name visible
                    username.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingsActivity.this,
                            "Please, Set and update your profile information.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void updateSettings()
    {
        String setUsername = username.getText().toString();

        // get selected radio button from radioGroup
        int selectedId = radioJobTitle.getCheckedRadioButtonId();
        // find the radiobutton by returned id
        radioJobButton = (RadioButton) findViewById(selectedId);

        String setStatus = radioJobButton.getText().toString();
        // check fields
        if(TextUtils.isEmpty(setUsername))
        {
            Toast.makeText(getApplicationContext(),"Please, Enter Your name ...",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            loadingBar.setTitle("Creating Your Account...");
            loadingBar.setMessage("Please, Wait while we are sitting your account...");
            //   loadingBar.setCancelable(false);
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            HashMap<String, String> userMap = new HashMap<>();
            userMap.put("uid",currentUserId);
            userMap.put("name", setUsername);
            userMap.put("job_title", setStatus);

            if(setStatus.equals("Leader"))
            {
                userRef.child("Users").child("Leader").child(currentUserId).setValue(userMap)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful())
                                {
                                    sendUserToMainLeaderActivity();

                                    loadingBar.dismiss();
                                }
                                else
                                {
                                    String msg = task.getException().getMessage();

                                    loadingBar.dismiss();
                                }
                            }
                        });
            }
            else if(setStatus.equals("Member"))
            {
                userRef.child("Users").child("Member").child(currentUserId).setValue(userMap)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful())
                                {
                                    sendUserToMainMemberActivity();
                                    loadingBar.dismiss();
                                }
                                else
                                {
                                    String msg = task.getException().getMessage();

                                    loadingBar.dismiss();
                                }
                            }
                        });
            }
            else
            {
               Toast.makeText(getApplicationContext(),"Choose Leader or Member",Toast.LENGTH_LONG).show();
            }

        }
    }

    private void initializeFields()
    {
        updateProfile = findViewById(R.id.settings_update_button);
        username = findViewById(R.id.settings_username);
        radioJobTitle = (RadioGroup) findViewById(R.id.radio_job_title);
        userImage = findViewById(R.id.settings_profile_image);

    }


    private void sendUserToMainLeaderActivity()
    {
        Intent mainIntent = new Intent(SettingsActivity.this,MainLeaderActivity.class);
     //   mainIntent.putExtra("postKey",postKey);
        startActivity(mainIntent);
    }

    private void sendUserToMainMemberActivity()
    {
        Intent mainIntent = new Intent(SettingsActivity.this,MainMemberActivity.class);
        //   mainIntent.putExtra("postKey",postKey);
        startActivity(mainIntent);
    }

}
