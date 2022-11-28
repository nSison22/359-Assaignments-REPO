package com.example.assaignment3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

        randomNum1.setOnTouchListener(this);
        randomNum2.setOnTouchListener(this);
        randomNum3.setOnTouchListener(this);
        randomNum4.setOnTouchListener(this);

        mult2.setOnDragListener(this);
        mult3.setOnDragListener(this);
        mult5.setOnDragListener(this);
        mult10.setOnDragListener(this);



    }

    public void getRandomNumber(){
        new getRandomNumbersJSONDataTask().execute("https://api.quantumnumbers.anu.edu.au?length=4&type=uint8");
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
//DRAG/TOUCH LISTENER METHODS------------------------------------
    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        switch (dragEvent.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                Toast.makeText(this,  "ACTION_DRAG_STARTED", Toast.LENGTH_SHORT).show();
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                Toast.makeText(this,  "ACTION_DRAG_ENTERED", Toast.LENGTH_SHORT).show();
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                Toast.makeText(this,  "ACTION_EXITED", Toast.LENGTH_SHORT).show();//no action necessary
                break;
            case DragEvent.ACTION_DROP:

                Toast.makeText(this,  "drop", Toast.LENGTH_SHORT).show();

                //handle the dragged view being dropped over a target view
                View view = (View) dragEvent.getLocalState();

                //stop displaying the view where it was before it was dragged
                view.setVisibility(View.INVISIBLE);

                //view dragged item is being dropped on
                TextView dropTarget = (TextView) v;

                //view being dragged and dropped
                TextView dropped = (TextView) view;

                //update the text in the target view to reflect the data being dropped
                dropTarget.setText(""+ dropTarget.getText()+" = " +dropped.getText());

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

    private class getRandomNumbersJSONDataTask extends AsyncTask<String,Void,String> {

        Exception exception = null;

        @Override
            protected String doInBackground(String... urls) {
                try{
                    return readJSONData(urls[0]);
                }catch(IOException e){
                    exception = e;
                }
                return null;
            }        }

    protected void onPostExecute(String result){
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject weatherObservationItems =
                    new JSONObject(jsonObject.getString("IDK THIS ONE YET"));

            Toast.makeText(getBaseContext(),
                    weatherObservationItems.getString("#OFRANDOMNUMBER") +
                            " - " + weatherObservationItems.getString("THERANDOMNUMBER"),
                    Toast.LENGTH_SHORT).show();
//                txtLocation.setText("LOCATION: " + weatherObservationItems.getString("stationName"));

        } catch (Exception e) {
            Log.d("ReadWeatherJSONDataTask", e.getLocalizedMessage());
        }
    }

}
