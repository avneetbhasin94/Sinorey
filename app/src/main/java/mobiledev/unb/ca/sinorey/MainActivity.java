package mobiledev.unb.ca.sinorey;

//ADD PACKAGE HERE

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import  java.util.Scanner;

/**
 * ShootAndCropActivity demonstrates capturing and cropping camera images
 * - user presses button to capture an image using the device camera
 * - when they return with the captured image Uri, the app launches the crop action intent
 * - on returning from the crop action, the app displays the cropped image
 *
 *
 *
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
    static String cityl;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button backgroundBtn = (Button)findViewById(R.id.background_btn);
        Button mergeBtn = (Button)findViewById(R.id.merge);
        mergeBtn.setOnClickListener(this);

        //handle button clicks
        backgroundBtn.setOnClickListener(this);

        //retrieve a reference to the UI button
        Button captureBtn = (Button)findViewById(R.id.capture_btn);
        //handle button clicks
        captureBtn.setOnClickListener(this);

        tvAddress = (TextView) findViewById(R.id.tvAddress);
        appLocationService = new AppLocationService(MainActivity.this);
        //Log.i(TAG,city);





    }

    /**
     * Click method to handle user pressing button to launch camera
     */
    public void onClick(View v) {
        if (v.getId() == R.id.capture_btn) {
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

        if(v.getId() == R.id.background_btn){
            try{
                Location location = appLocationService
                        .getLocation(LocationManager.GPS_PROVIDER);

                //you can hard-code the lat & long if you have issues with getting it
                //remove the below if-condition and use the following couple of lines
                double latitude = 45.4214;
                double longitude = -75.6919;

                if (location != null) {
                  // double latitude = location.getLatitude();
                   // double longitude = location.getLongitude();
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(latitude, longitude,
                            getApplicationContext(), new GeocoderHandler());


                    //city=LocationAddress.city;
                    //Log.i(TAG,cityl);
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

            /*Bitmap bitmap=null;
            File f= new File("home1//ugrads//asingh1//CS2063//Project//Sinorey//app//src//main//res//mipmap-hdpi//ic_launcher.png");
            String getDirectoryPath = f.getParent();
            String path = "/res/mipmap-hdpi/ic_launcher.png";
            System.out.print("the path is" +getDirectoryPath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
           // try {
                bitmap = BitmapFactory.decodeFile(path, options);
            //}
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ImageView image = (ImageView)findViewById(R.id.picture);
            image.setImageBitmap(bitmap);
        } */

            //File imgFile = new  File("/CS2063/Project/picture.jpg");



                //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

           /* if ("Fredericton".equals(cityloc)) {
                 mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pic2);
            }
            else if ("Ottawa".equals(cityloc)) {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ottawa);
            }
            else{
                mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pic2);
                Log.i(TAG,cityloc);
            }

                ImageView myImage = (ImageView) findViewById(R.id.backpicture);



                //myImage.setImageBitmap(mBitmap);



                // TODO - set scaled bitmap size (mScaledBitmapSize) in range [2..4] * BITMAP_SIZE



                // TODO - create the scaled bitmap using size set above
               // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b64);
               mScaledBitmap = Bitmap.createScaledBitmap(mBitmap, 1000, 1000, true);

            myImage.setImageBitmap(mScaledBitmap); */







           // }
           // else{
               // System.out.println("it's not there");
           // }


            /*try {
                ImageView i = (ImageView)findViewById(R.id.picture);
                Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL("http://www.frederictonkeepsakes.com/files/PostCards/PostCards-800/FKPC_802.01_op_640x426.jpg").getContent());
                i.setImageBitmap(bitmap);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/


            /*if (v.getId() == R.id.merge){
                Log.i(TAG,"is it even going in");
                Bitmap overlay= overlay();
                ImageView myImage1 = (ImageView) findViewById(R.id.backpicture);
                myImage1.setImageBitmap(overlay);
                Log.i(TAG,"Should do this");




            }*/

            }

        if (v.getId() == R.id.merge){
            Log.i(TAG,"is it even going in");
            Bitmap overlay= overlay();
            ImageView myImage1 = (ImageView) findViewById(R.id.backpicture);
            myImage1.setImageBitmap(overlay);
            Log.i(TAG,"Should do this");




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
                Log.i(TAG, "Pic Uri 1");
                picUri = data.getData();
                Log.i(TAG, "Pic Uri 2");
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
                Log.i(TAG, "PIC_CROP");
                System.out.println(PIC_CROP);
            }
        }
    }

    /**
     * Helper method to carry out crop operation
     */
    private void performCrop(){
        //take care of exceptions
        try {
            Log.i(TAG, "Enter Try");
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            Log.i(TAG, "Intent maybe not fucked up");
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
            Log.i(TAG, "Intent maybe fucked up 2");
            startActivityForResult(cropIntent, PIC_CROP);
            Log.i(TAG, "Intent maybe fucked up 3");
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
        Bitmap bmOverlay = Bitmap.createBitmap(mScaledBitmap.getWidth(), mScaledBitmap.getHeight(), mScaledBitmap.getConfig());

        Canvas canvas = new Canvas(bmOverlay);

        canvas.drawBitmap(mScaledBitmap, new Matrix(), null);
        Log.i(TAG, "should go here bro");
        thePic= Bitmap.createScaledBitmap(thePic, 1200, 1200, true);
        canvas.drawBitmap(thePic, 1600, 3000, null);

        return bmOverlay;
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
            System.out.print("the locationsdsswds is"+locationAddress);
            //tvAddress.setText(locationAddress);
           String line[]= locationAddress.split(" ");
            cityloc = line[0];
            cityl=line[0];
            System.out.print("theass location is "+cityl);

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
            else{
                mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pic2);
                Log.i(TAG,cityloc);
            }

            ImageView myImage = (ImageView) findViewById(R.id.backpicture);



            //myImage.setImageBitmap(mBitmap);


            // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b64);
            mScaledBitmap = Bitmap.createScaledBitmap(mBitmap, 4000, 6000, true);

            myImage.setImageBitmap(mScaledBitmap);



        }

    }
}