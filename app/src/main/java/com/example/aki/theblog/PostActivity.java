package com.example.aki.theblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {
    private ImageButton ibtn;
    private EditText ttxt;
    private EditText dtxt;
    private Button abtn;
    private Uri image=null;
    private StorageReference sr;
    private DatabaseReference dr;
    private ProgressDialog pd;
    private FirebaseAuth mAuth;
    private FirebaseUser mFuser;
    private DatabaseReference mdatabase;
    private static final int GALLERY_REQUEST=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ibtn=(ImageButton) findViewById(R.id.imageButton);
        ttxt=(EditText)findViewById(R.id.editText);
        dtxt=(EditText)findViewById(R.id.editText2);
        abtn=(Button)findViewById(R.id.button2);
        pd= new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mFuser=mAuth.getCurrentUser();
        mdatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(mFuser.getUid());
        sr= FirebaseStorage.getInstance().getReference();
        dr= FirebaseDatabase.getInstance().getReference().child("Blog");
        ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });
        abtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }
    private void startPosting(){
        pd.setMessage("Posting...");
        pd.show();
         final String txtt=ttxt.getText().toString().trim();
         final String txtd=dtxt.getText().toString().trim();
        if(!TextUtils.isEmpty(txtt)&&!TextUtils.isEmpty(txtd)&&image!=null){
             StorageReference filepath=sr.child("Blog_Images").child(image.getLastPathSegment());
            filepath.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   final Uri downloadUrl=taskSnapshot.getDownloadUrl();
                   final DatabaseReference post=dr.push();
                    mdatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            post.child("title").setValue(txtt);
                            post.child("desc").setValue(txtd);
                            post.child("image").setValue(downloadUrl.toString());
                            post.child("uid").setValue(mFuser.getUid());
                            post.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if ((task.isSuccessful())){
                                        startActivity(new Intent(PostActivity.this,MainActivity.class));
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    pd.dismiss();
                    startActivity(new Intent(PostActivity.this,MainActivity.class));
                }
            });
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==GALLERY_REQUEST&&resultCode==RESULT_OK)
        {
            image =data.getData();
            ibtn.setImageURI(image);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}
