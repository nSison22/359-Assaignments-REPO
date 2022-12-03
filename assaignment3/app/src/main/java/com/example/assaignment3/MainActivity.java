package com.example.assaignment3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnDragListener {

//    apiKEY:PKFlyIOALB5tBGN6Sf9h9ELrCteQVIk506LoysJg
    TextView randomNum1,randomNum2,randomNum3,randomNum4,mult2,mult3,mult5,mult10;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //views to drag
        randomNum1=(TextView) findViewById(R.id.draggable1);
        randomNum2=(TextView) findViewById(R.id.draggable2);
        randomNum3=(TextView) findViewById(R.id.draggable3);
        randomNum4=(TextView) findViewById(R.id.draggable4);
        //views to drop
        mult2=(TextView)findViewById(R.id.droppable1);
        mult3=(TextView)findViewById(R.id.droppable2);
        mult5=(TextView)findViewById(R.id.droppable3);
        mult10=(TextView)findViewById(R.id.droppable4);

        //views to drag are are set with OnTouchListeners
        randomNum1.setOnTouchListener(this);
        randomNum2.setOnTouchListener(this);
        randomNum3.setOnTouchListener(this);
        randomNum4.setOnTouchListener(this);

        //views to to be droppable to onDragListeners
        mult2.setOnDragListener(this);
        mult3.setOnDragListener(this);
        mult5.setOnDragListener(this);
        mult10.setOnDragListener(this);
//        checkConnection();


    }


//DRAG/TOUCH LISTENER METHODS------------------------------------
    @Override
    public boolean onDrag(View v, DragEvent dragEvent) {
        switch (dragEvent.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
//                Toast.makeText(this,  "ACTION_DRAG_STARTED", Toast.LENGTH_SHORT).show();
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
//                Toast.makeText(this,  "ACTION_DRAG_ENTERED", Toast.LENGTH_SHORT).show();
                break;
            case DragEvent.ACTION_DRAG_EXITED:
//                Toast.makeText(this,  "ACTION_EXITED", Toast.LENGTH_SHORT).show();//no action necessary
                break;
            case DragEvent.ACTION_DROP:


                //handle the dragged view being dropped over a target view
                View view = (View) dragEvent.getLocalState();


                //view dragged item is being dropped on
                TextView dropTarget = (TextView) v;

                //view being dragged and dropped
                TextView dropped = (TextView) view;

                //if the dragged item has text in it, it will update the dropTarget text if they meet the conditions
                if (( dropped).getText()!= "" ){
                    if(dropTarget==mult2 && Integer.parseInt(dropped.getText().toString())%2==0) {
                        dropTarget.setText("" + dropTarget.getText() + "\n" + dropped.getText());
                        ((TextView) view).setText("");
                    }
                    if(dropTarget==mult3 && Integer.parseInt(dropped.getText().toString())%3==0) {
                        dropTarget.setText("" + dropTarget.getText() + "\n" + dropped.getText());
                        ((TextView) view).setText("");
                    }
                    if(dropTarget==mult5 && Integer.parseInt(dropped.getText().toString())%5==0) {
                        dropTarget.setText("" + dropTarget.getText() + "\n" + dropped.getText());
                        ((TextView) view).setText("");
                    }
                    if(dropTarget==mult10 && Integer.parseInt(dropped.getText().toString())%10==0) {
                        dropTarget.setText("" + dropTarget.getText() + "\n" + dropped.getText());
                        ((TextView) view).setText("");
                    }

                }

                //make it bold to highlight the fact that an item has been dropped
                dropTarget.setTypeface(Typeface.DEFAULT_BOLD);

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                //no action necessary
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            //the user has touched the View to drag it
            //prepare the drag
            ClipData data = ClipData.newPlainText("","");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            //start dragging the item touched
            view.startDrag(data, shadowBuilder, view, 0);
            Toast.makeText(this, "onTouch - startDrag", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            return false;
        }
    }

//JSONASYNC TASK METHODS------------------------------------


    //checks if the app is connected to the internet
    public void checkConnection(){
        ConnectivityManager connectMgr =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            //fetch data

            String networkType = networkInfo.getTypeName().toString();
            Toast.makeText(this, "connected to " + networkType, Toast.LENGTH_LONG).show();
        }
        else {
            //display error
            Toast.makeText(this, "no network connection", Toast.LENGTH_LONG).show();
        }
    }

    //onClick function that initiates the AsyncTask using the web service
    public void getRandomNumber(View view){

        new getRandomNumbersJSONDataTask().execute("https://qrng.anu.edu.au/API/jsonI.php?length=4&type=uint8");
    }

    //onClick function that sets the text to the original state
    public void resetValues(View view){
        randomNum1.setText("");
        randomNum2.setText("");
        randomNum3.setText("");
        randomNum4.setText("");

        mult2.setText("Multiples of 2");
        mult3.setText("Multiples of 3");
        mult5.setText("Multiples of 5");
        mult10.setText("Multiples of 10");
    }

    private String readJSONData(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 2500;

        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("tag", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);

            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
                conn.disconnect();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private class getRandomNumbersJSONDataTask extends AsyncTask<String, Void, String> {

        Exception exception = null;

            //if the data is available, it will return the Json Object
        protected String doInBackground(String... urls) {
            try {
                return readJSONData(urls[0]);
            } catch (IOException e) {
                exception = e;
            }
            return null;
        }


        protected void onPostExecute(String result) {
            try {
                //instaniate Json Object and then extract the json array by accessing the  key data
                JSONObject jsonObject = new JSONObject(result);
                JSONArray rndmNumberItems = new JSONArray(jsonObject.getString("data"));
//                Log.d("values", rndmNumberItems.toString());
                Toast.makeText(getBaseContext(), rndmNumberItems.get(0).toString()+", "+rndmNumberItems.get(1).toString()+", "+rndmNumberItems.get(2).toString()+", "+rndmNumberItems.get(3).toString(), Toast.LENGTH_SHORT).show();
            //set the textviews to the 4 random numbers in the array.
                randomNum1.setText(rndmNumberItems.get(0).toString());
                randomNum2.setText(rndmNumberItems.get(1).toString());
                randomNum3.setText(rndmNumberItems.get(2).toString());
                randomNum4.setText(rndmNumberItems.get(3).toString());

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "something happened", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
