package mobiledev.unb.ca.sinorey;

//ADD PACKAGE HERE

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import  java.util.Scanner;

/**

 */
public class MainActivity extends Activity implements OnClickListener {

    //keep track of camera capture intent
    final int CAMERA_CAPTURE = 1;
    //keep track of cropping intent
    final int PIC_CROP = 2;
    //captured picture uri
    private Uri picUri;
    private String cityloc = " ";

    private final static String TAG = "Main Activity";

    AppLocationService appLocationService;
    TextView tvAddress;
    static Bitmap mScaledBitmap;
    static Bitmap thePic;
    static  Bitmap  mBitmap;
    String mCurrentPhotoPath;
    private ProgressBar progressBar;
    static Bitmap overlay;
    static String cityl;
    private int downloadTime = 4;
    ContentValues values = new ContentValues();
    private Button backgroundBtn;
    private  Button mergeBtn;
    private ImageButton share_btn;
    private ImageButton save_btn;
    File pictureFile;
    private static boolean wasMerged = false;
    private static boolean wasGenerated = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backgroundBtn = (Button)findViewById(R.id.background_btn);
        mergeBtn  = (Button)findViewById(R.id.merge);
        share_btn = (ImageButton)findViewById(R.id.share_imgBtn);
        mergeBtn.setOnClickListener(this);

        save_btn =(ImageButton)findViewById(R.id.save_imgBtn);
        save_btn.setOnClickListener(this);




        backgroundBtn.setOnClickListener(this);
        Button captureBtn = (Button)findViewById(R.id.capture_btn);
        captureBtn.setOnClickListener(this);
        share_btn.setOnClickListener(this);


        tvAddress = (TextView) findViewById(R.id.tvAddress);
        appLocationService = new AppLocationService(MainActivity.this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);



    }


    public void onClick(View v) {
        if (v.getId() == R.id.capture_btn) {
            takePicture();

        }

        if(v.getId() == R.id.background_btn){




            getLocation();

        }

        if (v.getId() == R.id.merge){
            mergePicture();

        }


        if (v.getId() == R.id.share_imgBtn) {
            sharePicture();

        }

        if(v.getId()==R.id.save_imgBtn)
            {
                DownloaderTask downloaderTask = new DownloaderTask();
                if (overlay != null) {
                    downloaderTask.execute();

                }



            else {

                String errorMessage = "There is no picture to save";
                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                toast.show();

            }
            }



    }

    /**
     * Handle user returning from both capturing and cropping the image
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if(requestCode == CAMERA_CAPTURE){
                //get the Uri for the captured image

                picUri = data.getData();
                //carry out the crop operation
                performCrop();
            }
            //user is returning from cropping the image
            else if(requestCode == PIC_CROP){
                Log.i(TAG, "Entered elseif");
                //get the returned data
                Bundle extras = data.getExtras();
                //get the cropped bitmap
                 thePic = extras.getParcelable("data");
                //retrieve a reference to the ImageView
                ImageView picView = (ImageView)findViewById(R.id.picture);
                //display the returned cropped image

                picView.setImageBitmap(thePic);
               // wasPictureTaken =true;

                Log.i(TAG, "PIC_CROP");
                System.out.println(PIC_CROP);
            }
        }
    }


    public void getLocation()
    {
        try{
            Location location = appLocationService
                    .getLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LocationAddress locationAddress = new LocationAddress();
                locationAddress.getAddressFromLocation(latitude, longitude,
                        getApplicationContext(), new GeocoderHandler());

            } else {
                showSettingsAlert();
            }
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoopsie Daisies!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
             toast.show();
        }


    }
    public void takePicture()
    {
        try {
            //use standard intent to capture an image
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //we will handle the returned data in onActivityResult
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support capturing images!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void mergePicture()
    {
        Log.i(TAG,"is it even going in");
        if(thePic==null )
        {
            String errorMessage = "Please take a picture first ";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
        else if(mScaledBitmap==null){
            String errorMessage = "Please take generate background in order to merge ";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
        else {


            overlay= overlay();
            if(overlay!=null) {
                ImageView myImage1 = (ImageView) findViewById(R.id.backpicture);
                myImage1.setImageBitmap(overlay);
                Log.i(TAG, "Should do this");
            }
            else{
                String errorMessage = "The picture was already merged ";
                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    }

    public void sharePicture()
    {

            if(pictureFile==null)
            {
                String errorMessage = "There is no picture to share ";
                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                toast.show();
            }
        else {
                Uri uri = Uri.parse(pictureFile.getAbsolutePath());
                System.out.print("the file path is" + pictureFile.getAbsolutePath());


                ArrayList<Uri> imageUris = new ArrayList<Uri>();
                imageUris.add(uri); // Add your image URIs here


                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent, "Share images to.."));
            }

    }

    public void downloadPostcard()
    {
        System.out.println("if its going into share button");


        Bitmap bmp = overlay;



        pictureFile = getOutputMediaFile();

        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        if(bmp!=null) {
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                System.out.print("fileoutputstream" + fos);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                System.out.print("to see if picture is going thisailsjajs");
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }


            values.put("_data", pictureFile.getAbsolutePath());
            ContentResolver cr = getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
        else{
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    // Geo Location Methods
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MainActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
    private void performCrop(){
        //take care of exceptions
        try {
            Log.i(TAG, "Enter Try");
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult

            startActivityForResult(cropIntent, PIC_CROP);

        }
        //respond to users whose devices do not support the crop action
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public static Bitmap overlay() {
        Bitmap bmOverlay =null;


       if (!wasMerged)
        {
             bmOverlay = Bitmap.createBitmap(mScaledBitmap.getWidth(), mScaledBitmap.getHeight(), mScaledBitmap.getConfig());

            Canvas canvas = new Canvas(bmOverlay);

            canvas.drawBitmap(mScaledBitmap, new Matrix(), null);
            thePic = Bitmap.createScaledBitmap(thePic, 800, 800, true);
            canvas.drawBitmap(thePic, 1000, 2000, null);
            wasMerged=true;
            //wasPictureTaken = false;

        }
        else if (wasMerged==true)
        {
            bmOverlay=null;

        }
        return bmOverlay;


    }
    private  File getOutputMediaFile(){


        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() +"/Sinorey/");
        if(!mediaStorageDir.exists())//check if file already exists
        {
            mediaStorageDir.mkdirs();//if not, create it
        }


        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        values.put(MediaStore.Images.Media.TITLE, mImageName);
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        System.out.println("the another loactionj could be " + mediaStorageDir.getPath() + File.separator + mImageName);

        return mediaFile;

    }

    public class DownloaderTask extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.INVISIBLE);


            save_btn.setEnabled(false);
            progressBar.setMax(downloadTime);
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);

        }
        @Override
        protected String doInBackground(Void... params) {

            downloadPostcard();

            for(int i = 0; i < downloadTime; i++) {
                try {
                    Thread.sleep(1000);
                    publishProgress(i + 1);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return "Image has been saved";
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            save_btn.setEnabled(true);
            progressBar.setProgress(0);
            progressBar.setVisibility(View.INVISIBLE);




            Context context = getApplicationContext();
            String text = result;
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }


        @Override
        protected void onProgressUpdate(Integer... values) {

            progressBar.setProgress(values[0]);
        }
    }
    public class GeocoderHandler extends Handler {
        String locationAddress;
        @Override
        public void handleMessage(Message message) {

            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }

           String line[]= locationAddress.split(" ");


            Context context = getApplicationContext();
            CharSequence text = locationAddress;
            int duration = Toast.LENGTH_SHORT;

           Toast toast = Toast.makeText(context,text, duration);
            toast.show();
            if ("Fredericton".equals(line[0])) {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fredericton);
            }
            else if ("Ottawa".equals(line[0])) {
                mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ottawa);
            }
            else if ("Calgary".equals(line[0])) {
                mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.calgary);
            }
            else if ("Vancouver".equals(line[0])) {
                mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.vancouver);
            }
            else{

                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fredericton);
                Log.i(TAG,cityloc);
            }

            ImageView myImage = (ImageView) findViewById(R.id.backpicture);


            if(wasGenerated==false) {

               mScaledBitmap = Bitmap.createScaledBitmap(mBitmap, 3000, 4000, true);
                wasGenerated=true;
                toast.show();


                myImage.setImageBitmap(mScaledBitmap);
            }
            else
            {
                String errorMessage = "Postcard background has already been generated";
                Toast toast1 = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
                toast1.show();

            }



        }

    }

}