package com.example.aki.theblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST = 1;
    private ImageButton probtn;
    private EditText user;
    private Button setbtn;
    private Uri iuri=null;
    private DatabaseReference du;
    private FirebaseAuth mAuth;
    private StorageReference sr;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        mAuth=FirebaseAuth.getInstance();
        sr= FirebaseStorage.getInstance().getReference().child("Profile");
        du= FirebaseDatabase.getInstance().getReference().child("Users");
        probtn=(ImageButton)findViewById(R.id.pbtn);
        user=(EditText)findViewById(R.id.ubtn);
        setbtn=(Button)findViewById(R.id.sbtn);
        pd=new ProgressDialog(this);
        probtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });
        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSetup();
            }
        });
    }

    private void startSetup() {
        final String utxt=user.getText().toString().trim();
        final String uid=mAuth.getCurrentUser().getUid();
        if (!TextUtils.isEmpty(utxt)&& iuri!=null){
            pd.setMessage("Setting Up");
            pd.show();
            StorageReference filepath=sr.child(iuri.getLastPathSegment());
            filepath.putFile(iuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String duri=taskSnapshot.getDownloadUrl().toString();
                    du.child(uid).child("name").setValue(utxt);
                    du.child(uid).child("image").setValue(duri);
                    Intent mainIntent=new Intent(SetupActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_REQUEST&&resultCode==RESULT_OK)
        {
            Uri imageUri=data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                iuri = result.getUri();
                probtn.setImageURI(iuri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
