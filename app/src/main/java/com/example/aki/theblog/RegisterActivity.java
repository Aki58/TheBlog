package com.example.aki.theblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private EditText pass;
    private Button regbtn;
    private FirebaseAuth mauth;
    private ProgressDialog mprg;
    private DatabaseReference mdatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name=(EditText)findViewById(R.id.nameField);
        email=(EditText)findViewById(R.id.emailField);
        pass=(EditText)findViewById(R.id.passField);
        regbtn=(Button)findViewById(R.id.btnreg);
        mauth=FirebaseAuth.getInstance();
        mprg=new ProgressDialog(this);
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        regbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }


        });


    }

    private void startRegister() {
    final String ntxt=name.getText().toString().trim();
    String etxt=email.getText().toString().trim();
    String ptxt=pass.getText().toString().trim();
        if (!TextUtils.isEmpty(ntxt)&& !TextUtils.isEmpty(etxt)&& !TextUtils.isEmpty(ptxt))
        {  mprg.setMessage("Happy to add u....");
            mprg.show();
           mauth.createUserWithEmailAndPassword(etxt,ptxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
               @Override
               public void onComplete(@NonNull Task<AuthResult> task) {
                   if (task.isSuccessful())
                   {
                       String uid=mauth.getCurrentUser().getUid();
                       DatabaseReference current_user=mdatabase.child(uid);
                       current_user.child("name").setValue(ntxt);
                       current_user.child("image").setValue("default");
                       mprg.dismiss();
                       Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
                       mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       startActivity(mainIntent);

                   }
               }
           });
        }
    }
}
