package com.example.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Database {

    private SQLiteDatabase db;
    private Context context;
    private final DatabaseHelper helper;

    public Database(Context c){
        context=c;
        helper = new DatabaseHelper(context);
    }

    //insert new row into the database
    public long insertData(String color1, String color2, String color3, String color4,String color5, String path){
        db = helper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(DatabaseConstants.COLOR1, color1);
        contentValues.put(DatabaseConstants.COLOR2, color2);
        contentValues.put(DatabaseConstants.COLOR3, color3);
        contentValues.put(DatabaseConstants.COLOR4, color4);
        contentValues.put(DatabaseConstants.COLOR5, color5);
        contentValues.put(DatabaseConstants.IMAGE_PATH, path);
        long id = db.insert(DatabaseConstants.TABLE_NAME, null, contentValues);
        return id;
    }

    //retrieve all of the data from the database
    public Cursor getData(){

        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = {DatabaseConstants.UID, DatabaseConstants.COLOR1, DatabaseConstants.COLOR2, DatabaseConstants.COLOR3, DatabaseConstants.COLOR4, DatabaseConstants.COLOR5, DatabaseConstants.IMAGE_PATH};
        Cursor cursor = db.query(DatabaseConstants.TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }

    //retrieve the thrid colour in the palette so it can be sent to saveeit webview
    public String getThirdColour(int id) {
        String uid = Integer.toString(id);
        Log.e("database uid", uid);
        //select plants from database of type 'herb'
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {DatabaseConstants.UID, DatabaseConstants.COLOR1, DatabaseConstants.COLOR2,DatabaseConstants.COLOR3,DatabaseConstants.COLOR4,DatabaseConstants.COLOR5,DatabaseConstants.IMAGE_PATH};

        //make sure its the correct id
        String selection = DatabaseConstants.UID + "='" +uid+ "'";  //Constants.TYPE = 'type'
        Cursor cursor = db.query(DatabaseConstants.TABLE_NAME, columns, selection, null, null, null, null);

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {

            //only take the third colour
            int index6 = cursor.getColumnIndex(DatabaseConstants.COLOR3);
            String hex = cursor.getString(index6);
            buffer.append(hex);
        }
        return buffer.toString();
    }

    //retrieve the image path from the database
    public String getSelectedImagePath(String imagePath) {
        //select plants from database of type 'herb'
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {DatabaseConstants.COLOR1, DatabaseConstants.COLOR2,DatabaseConstants.COLOR3,DatabaseConstants.COLOR4,DatabaseConstants.COLOR5,DatabaseConstants.IMAGE_PATH};

        //make sure its the correct image path
        String selection = DatabaseConstants.IMAGE_PATH + "='" +imagePath+ "'";  //Constants.TYPE = 'type'
        Cursor cursor = db.query(DatabaseConstants.TABLE_NAME, columns, selection, null, null, null, null);

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {

            //only take the image path
            int index6 = cursor.getColumnIndex(DatabaseConstants.IMAGE_PATH);
            String image = cursor.getString(index6);
            buffer.append(image);
        }
        return buffer.toString();
    }

    //return the number of rows in the database
    public int getProfilesCount() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {DatabaseConstants.UID, DatabaseConstants.COLOR1, DatabaseConstants.COLOR2, DatabaseConstants.COLOR3, DatabaseConstants.COLOR4, DatabaseConstants.COLOR5, DatabaseConstants.IMAGE_PATH};

        Cursor cursor = db.query(DatabaseConstants.TABLE_NAME, columns, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    //retrieve everything from the database except for the id
    public String getSelectedColours(String imagepath) {
        //select plants from database of type 'herb'
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {DatabaseConstants.COLOR1, DatabaseConstants.COLOR2,DatabaseConstants.COLOR3,DatabaseConstants.COLOR4,DatabaseConstants.COLOR5,DatabaseConstants.IMAGE_PATH};

        String selection = DatabaseConstants.IMAGE_PATH + "='" +imagepath+ "'";  //Constants.TYPE = 'type'
        Cursor cursor = db.query(DatabaseConstants.TABLE_NAME, columns, selection, null, null, null, null);

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {

            int index1 = cursor.getColumnIndex(DatabaseConstants.COLOR1);
            int index2 = cursor.getColumnIndex(DatabaseConstants.COLOR2);
            int index3 = cursor.getColumnIndex(DatabaseConstants.COLOR3);
            int index4 = cursor.getColumnIndex(DatabaseConstants.COLOR4);
            int index5 = cursor.getColumnIndex(DatabaseConstants.COLOR5);

            String color1 = cursor.getString(index1);
            String color2 = cursor.getString(index2);
            String color3 = cursor.getString(index3);
            String color4 = cursor.getString(index4);
            String color5 = cursor.getString(index5);
            buffer.append(color1 + " " + color2 + " " + color3 + " " + color4 + " " + color5 );
        }
        return buffer.toString();
    }


}
