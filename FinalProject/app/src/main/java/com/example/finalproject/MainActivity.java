package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.content.AsyncTaskLoader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

//future ideas:
//update colour palette through database in gallery
//expand and collapse additional colour palettes with database
//qrcode implementation with hex codes

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String Tag = "AndroidCameraApi";
    //    private Button btnTake;
//    private Button btnGallery;
//    private Button btnPallete;
    private ImageView btnTake, btnGallery, btnPallete;
    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private Switch darkModeSwitch;
    private LinearLayout mainPageLayout;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest.Builder captureRequestBulider;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private File folder;
    private String folderName = "MyPhotoDir";
    private static final int Request_CAMERA_PERMISSION = 200;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    SensorManager sensorManager;
    Sensor sensor;
    boolean isLandscape = false;
    TextView textView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainPageLayout = (LinearLayout)findViewById(R.id.mainPage);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        textView = (TextView) findViewById(R.id.textView);

        textureView = findViewById(R.id.texture);
        if (textureView != null) {
            textureView.setSurfaceTextureListener(textureListener);
        }

        btnTake = findViewById(R.id.photoButton);
        btnGallery = findViewById(R.id.pictureGallaryButton);
        btnPallete = findViewById(R.id.colourPalletButton);

//        take a picture if the take picture button was pressed
        if (btnTake != null) {
            btnTake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    TakePicture task = new TakePicture();
//                    task.execute();
                    takePicture();
                }
            });
        }

//        open the photo gallery if the button was pressed
        if (btnGallery != null) {
            btnGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, CustomGalleryActivity.class);
                    intent.putExtra("EXTRA_PAGE", 1);
//                    intent.putExtra("IMAGE_PATH", file);
                    startActivity(intent);
                }
            });
        }

//        open the pallette gallery if the button was pressed
        if (btnPallete != null) {
            btnPallete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, RecyclerViewActivity.class);
//                    intent.putExtra("IMAGE_PATH", file);
                    startActivity(intent);
                }
            });
        }

        darkModeSwitch = (Switch) findViewById(R.id.switch1);

//       update the shared prefs if the switch was changed
        if (darkModeSwitch != null){
            darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    SharedPreferences sharedPreferences = getSharedPreferences("SharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    if (darkModeSwitch.isChecked()){
                        editor.putBoolean("isDark", true);
                    }
                    else{
                        editor.putBoolean("isDark", false);
                    }

                    editor.commit();
                    updateDarkMode();
                }
            });
        }
    }

    //    make the changes on this activity based on the shared prefs values
    public void updateDarkMode(){
        SharedPreferences sharedPrefs = getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
        Boolean isDark = sharedPrefs.getBoolean("isDark", false);

        Log.e("sharedPref", isDark + "");

        if (isDark){
            mainPageLayout.setBackgroundColor(Color.rgb(32, 33, 33));
            textView.setTextColor(Color.rgb(255, 255, 235));
            darkModeSwitch.setTextColor(Color.rgb(255, 255, 235));

            btnPallete.setBackgroundColor(Color.rgb(13, 90, 148));
            btnGallery.setBackgroundColor(Color.rgb(13, 90, 148));
            btnTake.setBackgroundColor(Color.rgb(13, 90, 148));

        }

        else{
            mainPageLayout.setBackgroundColor(Color.rgb(255, 255, 255));
            textView.setTextColor(Color.rgb(0, 0, 0));
            darkModeSwitch.setTextColor(Color.rgb(0, 0, 0));

            btnPallete.setBackgroundColor(Color.rgb(42, 157, 244));
            btnGallery.setBackgroundColor(Color.rgb(42, 157, 244));
            btnTake.setBackgroundColor(Color.rgb(42, 157, 244));

        }
    }


    //adapted code: https://www.youtube.com/watch?v=MhsG3jYEsek&t=901s
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

        }
    };

    //    open the camera so photos can be taken
    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];

            //add premission for camera and let user grant the permission
            if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Request_CAMERA_PERMISSION);
                return;
            }

            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //take a photo; called when the corresponding button was pressed
//    save the photo on the device in the app if allowed
    protected void takePicture() {
        if (cameraDevice == null) {
            return;
        }
//        check if files can be read and written
        if (!isExternalStorageAvailableForRW() || isExternalStorageReadOnly()) {
            btnTake.setEnabled(false);
        }

//      if accessing the phone storage is available
        if (isStoragePermissionGranted()) {
            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {

//              determine size of the photo
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
                Size[] jpegSize = null;
                if (characteristics != null) {
                    jpegSize = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                }

                int width = 640;
                int height = 480;
                if (jpegSize != null && jpegSize.length > 0) {
                    width = jpegSize[0].getWidth();
                    height = jpegSize[0].getHeight();
                }

                ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
                List<Surface> outputSurfaces = new ArrayList<>(2);
                outputSurfaces.add(reader.getSurface());
                outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
                final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureBuilder.addTarget(reader.getSurface());
                captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

                //orientation
                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
                file = null;
                folder = new File(folderName);
                String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
                String imageFileName = "IMG_" + timeStamp + ".jpg";
                file = new File(getExternalFilesDir(folderName), "/" + imageFileName);
                //Toast.makeText(MainActivity.this, imageFileName, Toast.LENGTH_SHORT).show();
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                    //                   get the image from the camera using callback
                    @Override
                    public void onImageAvailable(ImageReader imageReader) {
                        Image image = null;
                        try {
                            image = reader.acquireLatestImage();
                            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                            byte[] bytes = new byte[buffer.capacity()];
                            buffer.get(bytes);
                            save(bytes);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (image != null) {
                                image.close();
                            }
                        }
                    }

                    //                   save the image in data of raw bytes to file for storing
                    private void save(byte[] bytes) throws IOException {
                        OutputStream output = null;
                        try {
                            output = new FileOutputStream(file);
                            output.write(bytes);
                        } finally {
                            if (null != output) {
                                output.close();
                            }
                        }
                    }
                };

                reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
//               camera started capturing image for output
                final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                        super.onCaptureStarted(session, request, timestamp, frameNumber);
                        Toast.makeText(MainActivity.this, "Saved: " + file, Toast.LENGTH_LONG).show();
                        Log.e(Tag, "Saved: " + file);
                        createCameraPreview();
                    }
                };

                cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                    //                   camera has finishe dconfiguring and session starts processing capture requests
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        try {
                            session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    }
                }, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

//        int FROM_CAMERA = 0;
//        Intent intent = new Intent(MainActivity.this, CustomGalleryActivity.class);
//        intent.putExtra("EXTRA_PAGE", FROM_CAMERA);
//        startActivity(intent);
    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    //check to see if the storage is read only
    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private boolean isExternalStorageAvailableForRW() {
        //check if the external storage is available for read and write by calling
        //Environment.getExternalStorageState() method, if the returend stated is MEDIA_MOUNTED
        //then you can read and write files. so return true in that case otherwise false
        String extStorageState = Environment.getExternalStorageState();
        if (extStorageState.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    //    check to see if permission is granted to write to external storage
    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBulider = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBulider.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //the camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }

                    //when the session is read, we start displaying the preview
                    cameraCaptureSessions = cameraCaptureSession;

                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(MainActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    protected void updatePreview() {
        if (null == cameraDevice) {
            Log.e(Tag, "updatePreview error, return");
        }
        captureRequestBulider.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBulider.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


//
//    class TakePicture extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            takePicture();
//            return null;
//        }


    //    if the camera access in app has not been allowed
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Request_CAMERA_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                Toast.makeText(MainActivity.this, "Sorry you can't use this app without granding permission to the camera", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(textureView.isAvailable()){
            openCamera();
        }
        else{
            textureView.setSurfaceTextureListener(textureListener);
        }

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    //    check acclerometer values for portrait mode and landscape mode
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == sensor.getType()){
            float X_Axis = sensorEvent.values[0];
            float Y_Axis = sensorEvent.values[1];

            if((X_Axis <= 6 && X_Axis >= -6) && Y_Axis > 5){
                isLandscape = false;
//                Log.e(Tag, "not landscape");
                textView.setText("Portrait Mode");
            }
            else if(X_Axis >= 6 || X_Axis <= -6){
                isLandscape = true;
//                Log.e(Tag, "is landscape");
                textView.setText("Landscape Mode");
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}