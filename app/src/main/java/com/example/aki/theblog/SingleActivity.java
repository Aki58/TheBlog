package com.example.aki.theblog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SingleActivity extends AppCompatActivity {
    private String mpost_key=null;
    private ImageView img;
    private TextView title;
    private TextView desc;
    private DatabaseReference mdatabse;
    private Button rmv;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);
        img=(ImageView)findViewById(R.id.image);
        title=(TextView)findViewById(R.id.title);
        desc=(TextView)findViewById(R.id.desc);
        rmv=(Button)findViewById(R.id.rmvbtn);
        mAuth=FirebaseAuth.getInstance();
        mdatabse= FirebaseDatabase.getInstance().getReference().child("Blog");
        mpost_key=getIntent().getExtras().getString("blog_id");
        mdatabse.child(mpost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String titlet=(String)dataSnapshot.child("title").getValue();
                String descd=(String)dataSnapshot.child("desc").getValue();
                String imagei=(String)dataSnapshot.child("image").getValue();
                String uidu=(String)dataSnapshot.child("uid").getValue();
                title.setText(titlet);
                desc.setText(descd);
                Picasso.with(SingleActivity.this).load(imagei).into(img);
                if (mAuth.getCurrentUser().getUid().equals(uidu))
                {
                    rmv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        rmv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdatabse.child(mpost_key).removeValue();
                Intent main=new Intent(SingleActivity.this,MainActivity.class);
                startActivity(main);
            }
        });

    }
}
