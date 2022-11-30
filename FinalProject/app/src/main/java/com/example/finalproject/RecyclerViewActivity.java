package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class RecyclerViewActivity extends AppCompatActivity {
    RecyclerView myRecycler;
    Database db;
    RecyclerViewAdapter myAdapter;
    DatabaseHelper helper;
    RelativeLayout recyclerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        myRecycler = (RecyclerView) findViewById(R.id.recycler);

        recyclerLayout = (RelativeLayout) findViewById(R.id.recyclerLayout);

        db = new Database(this);
        helper = new DatabaseHelper(this);

        Cursor cursor = db.getData();

        int index1 = cursor.getColumnIndex(DatabaseConstants.COLOR1);
        int index2 = cursor.getColumnIndex(DatabaseConstants.COLOR2);
        int index3 = cursor.getColumnIndex(DatabaseConstants.COLOR3);
        int index4 = cursor.getColumnIndex(DatabaseConstants.COLOR4);
        int index5 = cursor.getColumnIndex(DatabaseConstants.COLOR5);


        ArrayList<String> mArrayList = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String colour1 = cursor.getString(index1);
            String colour2 = cursor.getString(index2);
            String colour3 = cursor.getString(index3);
            String colour4 = cursor.getString(index4);
            String colour5 = cursor.getString(index5);
            String s = colour1 +"," + colour2 + "," + colour3+ "," + colour4 + "," + colour5;
            mArrayList.add(s);
            cursor.moveToNext();
        }

        myAdapter = new RecyclerViewAdapter(mArrayList);
        myRecycler.setAdapter(myAdapter);

        updateDarkMode();

    }

    public void updateDarkMode(){
        SharedPreferences sharedPrefs = getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
        Boolean isDark = sharedPrefs.getBoolean("isDark", true);

        Log.e("sharedPref", isDark + "");

        if (isDark){
            recyclerLayout.setBackgroundColor(Color.rgb(32, 33, 33));

        }

        else{
            recyclerLayout.setBackgroundColor(Color.rgb(255, 255, 255));

        }
    }


}