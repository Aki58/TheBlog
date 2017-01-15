package com.example.aki.theblog;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Aki on 1/12/2017.
 */

public class TheBlog extends Application{
    public void onCreate(){
        super.onCreate();
          if (!(FirebaseApp.getApps(this).isEmpty()))
          {FirebaseDatabase.getInstance().setPersistenceEnabled(true);}
        /*Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built=builder.build();
        builder.indicatorsEnabled(false);
        builder.loggingEnabled(true);
        Picasso.setSingletonInstance(built);*/
    }




}
