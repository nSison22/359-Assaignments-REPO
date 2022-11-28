package com.example.finalproject;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {
//database to hold 5 colour hex codes and string image path

    private Context context;

    private static final String CREATE_TABLE =
            "CREATE TABLE "+
                    DatabaseConstants.TABLE_NAME + " (" +
                    DatabaseConstants.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DatabaseConstants.COLOR1 + " TEXT, " +
                    DatabaseConstants.COLOR2 + " TEXT, "+
                    DatabaseConstants.COLOR3 + " TEXT, "+
                    DatabaseConstants.COLOR4 + " TEXT, " +
                    DatabaseConstants.COLOR5 + " TEXT, " +
                    DatabaseConstants.IMAGE_PATH + " TEXT);" ;

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + DatabaseConstants.TABLE_NAME;

    public DatabaseHelper(Context context){
        super (context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);
        this.context = context;
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

//    create the databse
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);
            Toast.makeText(context, "onCreate() called", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            Toast.makeText(context, "exception onCreate() db", Toast.LENGTH_LONG).show();
        }
    }

//    making changes to database schema
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_TABLE);
            onCreate(db);
            Toast.makeText(context, "onUpgrade called", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            Toast.makeText(context, "exception onUpgrade() db", Toast.LENGTH_LONG).show();
        }
    }
}
