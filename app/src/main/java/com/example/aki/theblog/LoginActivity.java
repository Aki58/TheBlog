package com.example.aki.theblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText email;
    private EditText pass;
    private Button lbtn;
    private Button addbtn;
    private FirebaseAuth mauth;
    private DatabaseReference mdatabaseu;
    private ProgressDialog pd;
    private SignInButton gbtn;
    private static final int RC_SIGN_IN=1;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuthL=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
        email=(EditText)findViewById(R.id.lemail);
        pass=(EditText)findViewById(R.id.lpass);
        lbtn=(Button)findViewById(R.id.lbtn);
        addbtn=(Button)findViewById(R.id.abtn);
        gbtn=(SignInButton)findViewById(R.id.gbtn);
        mauth=FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);
        mdatabaseu = FirebaseDatabase.getInstance().getReference().child("Users");
        mdatabaseu.keepSynced(true);
        lbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg=new Intent(LoginActivity.this,RegisterActivity.class);
                reg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(reg);
            }
        });



        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).
                        requestEmail().
                        build();
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        gbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }



    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
         startActivityForResult(signInIntent, RC_SIGN_IN);
         }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
     super.onActivityResult(requestCode, resultCode, data);

         // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
         if (requestCode == RC_SIGN_IN) {
             pd.setMessage("Adding U!!");
             pd.show();
         GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
         if (result.isSuccess()) {
                 // Google Sign In was successful, authenticate with Firebase
                 GoogleSignInAccount account = result.getSignInAccount();
                 firebaseAuthWithGoogle(account);
                 } else {
                // Google Sign In failed, update UI appropriately
                 // ...
             pd.dismiss();
                 }
             }
         }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
         Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

         AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
         mauth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                if (!task.isSuccessful()) {
                 Log.w(TAG, "signInWithCredential", task.getException());
                  Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                  }
                 else{
                   pd.dismiss();}
                  checkUserExist();
             }
             });
           }



    private void checkLogin() {
        String etxt=email.getText().toString().trim();
        String ptxt=pass.getText().toString().trim();

        if(!TextUtils.isEmpty(etxt)&&!TextUtils.isEmpty(ptxt))
        {pd.setMessage("Checking...");
            pd.show();
            mauth.signInWithEmailAndPassword(etxt,ptxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()==true)
                    {   pd.dismiss();
                       checkUserExist();
                    }
                    else{pd.dismiss();
                        Toast.makeText(LoginActivity.this,"No Account",Toast.LENGTH_LONG).show();}


                }
            });
        }
    }

    private void checkUserExist() {
        if (mauth.getCurrentUser()!=null)
        {final String uid=mauth.getCurrentUser().getUid();
        mdatabaseu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(uid))
                {
                    Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
                else
                {
                    Intent setupIntent=new Intent(LoginActivity.this,SetupActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });}
    }
}
