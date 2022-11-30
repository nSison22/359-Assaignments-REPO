package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;
    ArrayList<String> imagePaths = new ArrayList<>();
    LayoutInflater mLayoutInflater;

    TextView textViewColourOne, textViewColourTwo, textViewColourThree, textViewColourFour, textViewColourFive;

    Bitmap bitmap;
    Button saveitButton;
    Database database;

    ImageView imageViewQR;

    public ViewPagerAdapter(Context context, ArrayList<String> imagePaths){
        this.context = context;
        this.imagePaths = imagePaths;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        database = new Database(context);
    }

    @Override
    public int getCount() {
        return imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

//  extract the colour set as the background of the textview and convert it to hex representation
    public String getHex(TextView textView){
        ColorDrawable bgColour = (ColorDrawable) textView.getBackground();
        int colourValue = bgColour.getColor();

        int red = (colourValue >> 16) & 0xFF;
        int green = (colourValue >> 8) & 0xFF;
        int blue = (colourValue >> 0) & 0xFF;

        String hex = String.format("%02x%02x%02x", red, green, blue);

        return hex;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position){
        //inflating the item.xml
        View itemView = mLayoutInflater.inflate(R.layout.galleryitem, container, false);

        saveitButton = (Button)itemView.findViewById(R.id.saaveitBtn);
        imageViewQR = (ImageView) itemView.findViewById(R.id.imageViewQR);
        saveitButton.setOnClickListener(new View.OnClickListener() {

            //open saveeit webview in separate activity
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (context, SaveitActivity.class);
                String hex = database.getThirdColour(position+1);
                Log.e("Sending in hex", hex);
                intent.putExtra("HEX_CODE", hex);
                context.startActivity(intent);
            }
        });

        textViewColourOne = (TextView) itemView.findViewById(R.id.textViewColourOne);
        textViewColourTwo = (TextView)itemView.findViewById(R.id.textViewColourTwo);
        textViewColourThree = (TextView)itemView.findViewById(R.id.textViewColourThree);
        textViewColourFour = (TextView)itemView.findViewById(R.id.textViewColourFour);
        textViewColourFive = (TextView)itemView.findViewById(R.id.textViewColourFive);

        //referencing the image view from the item.xml file
        ImageView imageView = itemView.findViewById(R.id.iamgeViewmain);

        //setting the image in the imageView
        Bitmap myBitmap = BitmapFactory.decodeFile(imagePaths.get(position));
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        //make smaller compressed bitmap for colour calculation
        Bitmap resizedBitmap = ThumbnailUtils.extractThumbnail(myBitmap, myBitmap.getWidth()/3, myBitmap.getHeight()/3);
        //rotate bitmap from landscape to portrait
        Bitmap rotatedBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);

        imageView.setImageBitmap(rotatedBitmap);

        bitmap = resizedBitmap;

        Log.e("bitmap", "setting bitmap to new image");

        if (!paletteExist(position)){
            CalculatePalette task = new CalculatePalette(position);
            Log.e("Instantiateitem calc", position + "");
            task.execute();
        }

        generateQR(position);
        updateDarkMode();

        //adding the view
        Objects.requireNonNull(container).addView(itemView);
        return itemView;
    }

    public boolean paletteExist(int position){
        for (int i = 0; i < database.getProfilesCount();i++){
            Log.e("Database i", i + "");
            Log.e("database" , database.getSelectedImagePath(imagePaths.get(i)));
//            Log.e("photo   " , imagePaths.get(position));

            if (imagePaths.get(position).equals(database.getSelectedImagePath(imagePaths.get(i)))) {
                String colours = database.getSelectedColours(imagePaths.get(position));
                String[] colourSplit = colours.split(" ");
                Log.e("palette check", colourSplit[0] + "");

                textViewColourOne.setBackgroundColor(Color.parseColor("#" + colourSplit[0]));
                textViewColourTwo.setBackgroundColor(Color.parseColor("#" + colourSplit[1]));
                textViewColourThree.setBackgroundColor(Color.parseColor("#" + colourSplit[2]));
                textViewColourFour.setBackgroundColor(Color.parseColor("#" + colourSplit[3]));
                textViewColourFive.setBackgroundColor(Color.parseColor("#" + colourSplit[4]));
                return true;
            }
        }
        return false;
    }

    public void alterDatabase(int position){
        boolean exist = false;
        String hex1, hex2, hex3, hex4, hex5;
        hex1 = getHex(textViewColourOne);
        hex2 = getHex(textViewColourTwo);
        hex3 = getHex(textViewColourThree);
        hex4 = getHex(textViewColourFour);
        hex5 = getHex(textViewColourFive);

        Log.e("Database count", database.getProfilesCount() + "");

        //go through the database and see if the image alreayd exists
        for (int i = 0; i < database.getProfilesCount();i++){
            Log.e("Database i", i + "");
            Log.e("database" , database.getSelectedImagePath(imagePaths.get(i)));
//            Log.e("photo   " , imagePaths.get(position));

            if (imagePaths.get(position).equals(database.getSelectedImagePath(imagePaths.get(i)))) {
                exist = true;
            }
        }

        //if the image doesn't exist, add a new row to the database
        if (!exist){
            Long id = database.insertData(hex1, hex2, hex3, hex4, hex5, imagePaths.get(position));
//            databaseSize++;
            if (id < 0)
            {
                Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
            }
        }


    }

//    https://www.youtube.com/watch?v=qBUDFpJ5Nc0&t=0s
    private void generateQR(int position){
        String string = database.getSelectedColours(imagePaths.get(position));
        MultiFormatWriter writer = new MultiFormatWriter();
        Log.e("QR and database", string+ "");
        try{
            if (!string.equals("")){
                BitMatrix  matrix = writer.encode(string, BarcodeFormat.QR_CODE, 200, 200);
                BarcodeEncoder encoder = new BarcodeEncoder();
                Bitmap bitmapQR = encoder.createBitmap(matrix);

                imageViewQR.setImageBitmap(bitmapQR);
                Log.e("qr code", "qrcode generated");
            }

        }catch(WriterException e){
            e.printStackTrace();
        }
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((LinearLayout) object);
    }

    //calculate the colour palette in the background thread
    class CalculatePalette extends AsyncTask<Void, Void, Void> {
        int[][] buckets = new int[27][4];
        int[][] maxBuckets = new int[27][2];
        int One, Two, Three, Four, Five;
        int position;

        CalculatePalette(int position){
            this.position = position;
        }

        @Override
        protected Void doInBackground(Void... params) {
            calculatePalette();
            findBuckets();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
            Log.e("task", "resetting textview colours");

            textViewColourOne.setBackgroundColor(Color.rgb(buckets[One][0]/buckets[One][3], buckets[One][1]/buckets[One][3], buckets[One][2]/buckets[One][3]));
            textViewColourTwo.setBackgroundColor(Color.rgb(buckets[Two][0]/buckets[Two][3], buckets[Two][1]/buckets[Two][3], buckets[Two][2]/buckets[Two][3]));
            textViewColourThree.setBackgroundColor(Color.rgb(buckets[Three][0]/buckets[Three][3], buckets[Three][1]/buckets[Three][3], buckets[Three][2]/buckets[Three][3]));
            textViewColourFour.setBackgroundColor(Color.rgb(buckets[Four][0]/buckets[Four][3], buckets[Four][1]/buckets[Four][3], buckets[Four][2]/buckets[Four][3]));
            textViewColourFive.setBackgroundColor(Color.rgb(buckets[Five][0]/buckets[Five][3], buckets[Five][1]/buckets[Five][3], buckets[Five][2]/buckets[Five][3]));

            alterDatabase(position);
            generateQR(position);
//            reset = true;
        }

        private void findBuckets(){
//            Log.e("task", "finding max buckets = 5");

            //initialize array to hold buckets index and pixel count
            for (int i = 0; i < 27; i++){
                maxBuckets[i][0] = i;
                maxBuckets[i][1] = buckets[i][3];
            }

            boolean sorted = false;
            int tempIndexZero, tempIndexOne;

            //bubble sort the array so it goes in increasing order in terms of pixel count
            while (!sorted){
                sorted = true;
                for(int i = 0; i < maxBuckets.length-1; i++){
                    if(maxBuckets[i][1] > maxBuckets[i+1][1]){
                        tempIndexZero = maxBuckets[i][0];
                        tempIndexOne = maxBuckets[i][1];
                        maxBuckets[i][0] =maxBuckets[i+1][0];
                        maxBuckets[i][1] = maxBuckets[i+1][1];
                        maxBuckets[i+1][0] = tempIndexZero;
                        maxBuckets[i+1][1] = tempIndexOne;
                        sorted = false;
                    }
                }
            }

            //find the top 5 most populated buckets
            One = maxBuckets[maxBuckets.length-1][0];
            Two = maxBuckets[maxBuckets.length-2][0];
            Three = maxBuckets[maxBuckets.length-3][0];
            Four = maxBuckets[maxBuckets.length-4][0];
            Five = maxBuckets[maxBuckets.length-5][0];

        }

        private void calculatePalette(){
//        algorithim: https://spin.atomicobject.com/2016/12/07/pixels-and-palettes-extracting-color-palettes-from-images/
            //initialized 27x4 array to 0
            for (int i = 0; i < 27; i++){
                for (int j = 0; j < 4; j++){
                    buckets[i][j] = 0;
                }
            }

//            Log.e("task", "going through each pixel in the bitmap through async task");

            for (int i = 0; i < bitmap.getWidth(); i++){
                for (int j = 0; j < bitmap.getHeight(); j++){

                    int pixel = bitmap.getPixel(i, j);

                    //pull rgb values
                    double redValue = Color.red(pixel);
                    double greenValue = Color.green(pixel);
                    double blueValue = Color.blue(pixel);

                    double pixelRed, pixelBlue, pixelGreen;
                    int bucketNumber;

                    //find which bucket the pixel value belongs to
                    pixelRed = Math.floor(redValue/(256/3));
                    pixelBlue = Math.floor(blueValue/(256/3));
                    pixelGreen = Math.floor(greenValue/(256/3));

                    if (pixelRed > 2){
                        pixelRed = 2;
                    }
                    if (pixelBlue > 2){
                        pixelBlue = 2;
                    }
                    if (pixelGreen > 2){
                        pixelGreen = 2;
                    }

                    bucketNumber = (int)((pixelRed*3*3) + (pixelGreen*3) + pixelBlue);

                    //add the indibidual rgb values to that bucket
                    buckets[bucketNumber][3]++;
                    buckets[bucketNumber][0] += redValue;
                    buckets[bucketNumber][1] += greenValue;
                    buckets[bucketNumber][2] += blueValue;

                }
            }


        }
    }

//    change UI from dark to light or light to dark depending on value from shared prefs
    public void updateDarkMode(){
        SharedPreferences sharedPrefs = context.getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
        Boolean isDark = sharedPrefs.getBoolean("isDark", false);

        Log.e("sharedPref", isDark + "");

        if (isDark){
            saveitButton.setBackgroundColor(Color.rgb(13, 90, 148));
        }

        else{
            saveitButton.setBackgroundColor(Color.rgb(42, 157, 244));

        }
    }

}
