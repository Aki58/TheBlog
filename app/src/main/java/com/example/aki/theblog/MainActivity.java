package com.example.aki.theblog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
private RecyclerView rv;
    private DatabaseReference mdatabase;
    private DatabaseReference mdatabaseu;
    private DatabaseReference mdatabsel;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean like=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null){
                    Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };
         mdatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        mdatabaseu=FirebaseDatabase.getInstance().getReference().child("Users");
        mdatabsel=FirebaseDatabase.getInstance().getReference().child("Likes");
        mdatabsel.keepSynced(true);
        mdatabaseu.keepSynced(true);
        mdatabase.keepSynced(true);
         rv=(RecyclerView)findViewById(R.id.blog_list);
         rv.setHasFixedSize(true);
         rv.setLayoutManager(new LinearLayoutManager(this));
        checkUserExist();

    }

    @Override
    protected void onStart() {
        mAuth.addAuthStateListener(mAuthListener);
        super.onStart();

        FirebaseRecyclerAdapter<Blog,BlogViewHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mdatabase
                ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {
                final String post_key=getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setLikebtn(post_key);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent single=new Intent(MainActivity.this,SingleActivity.class);
                        single.putExtra("blog_id",post_key);
                        startActivity(single);
                    }
                });
                viewHolder.likebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        like=true;
                        mdatabsel.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (like==true){
                                    if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())
                                            ) {
                                    mdatabsel.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        like=false;

                                    }
                                    else{
                                        mdatabsel.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("liked");
                                        like=false;

                                    }
                                    }}
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                                });

                    }
                    });
                }
            };
        rv.setAdapter(firebaseRecyclerAdapter);
        }
    private void checkUserExist() {
        if(mAuth.getCurrentUser()!=null)
        {
        final String uid=mAuth.getCurrentUser().getUid();
        mdatabaseu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(uid))
                {
                    Intent setupIntent=new Intent(MainActivity.this,SetupActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });}
    }
 public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;
     ImageButton likebtn;
     DatabaseReference mdatabasel;
     FirebaseAuth mAuth;
      public BlogViewHolder(View ItemView)
      {
        super(ItemView);
        mView=ItemView;
          likebtn=(ImageButton)mView.findViewById(R.id.lbtn);
          mdatabasel=FirebaseDatabase.getInstance().getReference().child("Likes");
          mdatabasel.keepSynced(true);
          mAuth=FirebaseAuth.getInstance();
      }
     public void setLikebtn(final String post_key)
     {
         mdatabasel.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid()))
                {
                    likebtn.setImageResource(R.mipmap.ic_done_black_24dp);
                }else{
                    likebtn.setImageResource(R.mipmap.ic_thumb_up_black_24dp);
                }
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });

     }
     public void setTitle(String title)
     {
         TextView post_title=(TextView)mView.findViewById(R.id.post_title);
         post_title.setText(title);
     }
     public void setDesc(String desc)
     {
         TextView post_desc=(TextView)mView.findViewById(R.id.post_desc);
         post_desc.setText(desc);
     }
     public void setUsername(String username)
     {
         TextView post_username=(TextView)mView.findViewById(R.id.uname);
         post_username.setText(username);
     }
     public void setImage(final Context ctx, final String image)
     {
         final ImageView post_image=(ImageView)mView.findViewById(R.id.post_image);
         Picasso.with(ctx).load(image).into(post_image);
         /*Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
             @Override
             public void onSuccess() {

             }

             @Override
             public void onError() {
                 Picasso.with(ctx).load(image).into(post_image);
             }
         });*/
     }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      if (item.getItemId() == R.id.add){
      startActivity(new Intent(MainActivity.this,PostActivity.class));
            }
        if (item.getItemId() == R.id.logout)
        {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
    mAuth.signOut();
    }
}
