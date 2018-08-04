package app.shouoff.mediadata;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.shouoff.R;
import app.shouoff.adapter.FilterImagesAdapter;
import app.shouoff.app.App;
import app.shouoff.common.ConnectionDetector;
import app.shouoff.common.Constants;
import app.shouoff.faceFilter.CameraSourcePreview;
import app.shouoff.faceFilter.FaceGraphicNew;
import app.shouoff.faceFilter.GraphicOverlay;
import app.shouoff.model.FilterImageModel;

public class EditCameraImage extends AppCompatActivity
{
    private RecyclerView filter_list;
    private Toolbar toolbar1;
    private TextView title1;
    private ArrayList<FilterImageModel> imageModels=new ArrayList<>();
    private FilterImagesAdapter imagesAdapter;
    private Context context=this;
    private ImageView click_image;

    /*Filter*/
    private static final String TAG = "FaceTracker";
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private RelativeLayout topLayout;
    private static final int RC_HANDLE_GMS = 9001;
    private ImageView image,reverse;
    Configuration configuration;
    private static String Temp="";
    private boolean temp=true;
    public static int camera_view=CameraSource.CAMERA_FACING_FRONT;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        configuration=getResources().getConfiguration();

        if (configuration.orientation==Configuration.ORIENTATION_LANDSCAPE)
        {
            setContentView(R.layout.activity_edit_camera_image_land);
        }
        else
        {
            setContentView(R.layout.activity_edit_camera_image);
        }
        Temp="portrait";
        initialized();
        if (getDeviceName().contains("Samsung"))
        {
            filter_listForSamsung(LinearLayoutManager.HORIZONTAL);
        }
        else
        {
            filter_list(LinearLayoutManager.HORIZONTAL);
        }
        Settings.System.putInt( context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);

        setToolbarContent();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            setContentView(R.layout.activity_edit_camera_image_land);
            initialized();

            if (getDeviceName().contains("Samsung"))
            {
                filter_listLandscapeSamsung(LinearLayoutManager.VERTICAL);
            }
            else
            {
                filter_listLandscape(LinearLayoutManager.VERTICAL);

            }

            Temp="landscape";
            startCameraSource();
        }
        else
        {
            setContentView(R.layout.activity_edit_camera_image);
            initialized();
            setToolbarContent();
            if (getDeviceName().contains("Samsung"))
            {
                filter_listForSamsung(LinearLayoutManager.HORIZONTAL);
            }
            else
            {
                filter_list(LinearLayoutManager.HORIZONTAL);
            }
            Temp="portrait";
            startCameraSource();
        }
    }

    /*Set Toolbar*/
    private void setToolbarContent()
    {
        toolbar1 = (Toolbar) findViewById(R.id.toolbar1);
        title1 = (TextView) findViewById(R.id.title1);
        title1.setText("Filter");
        title1.setVisibility(View.VISIBLE);
        toolbar1.setNavigationIcon(R.drawable.left_arrow);
        setSupportActionBar(toolbar1);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialized()
    {
        reverse=(ImageView)findViewById(R.id.reverse);
        click_image=(ImageView)findViewById(R.id.click_image);
        filter_list=(RecyclerView)findViewById(R.id.filter_list);
        image=(ImageView)findViewById(R.id.image);
        topLayout=(RelativeLayout)findViewById(R.id.topLayout);
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);

        click_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new LongOperation().execute();
            }
        });


        reverse.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mCameraSource != null) {
                    mCameraSource.release();
                    mCameraSource = null;
                }

                if (temp)
                {
                    camera_view=CameraSource.CAMERA_FACING_BACK;
                    createCameraSource();
                    startCameraSource();
                    temp=false;
                }
                else
                {
                    camera_view=CameraSource.CAMERA_FACING_FRONT;
                    createCameraSource();
                    startCameraSource();
                    temp=true;
                }
            }
        });

        createCameraSource();
    }

    @SuppressLint("StaticFieldLeak")
    private class LongOperation extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            mCameraSource.takePicture(new CameraSource.ShutterCallback()
            {
                @Override
                public void onShutter()
                {

                }
            }, new CameraSource.PictureCallback()
            {

                @SuppressLint("StaticFieldLeak")
                @Override
                public void onPictureTaken(byte[] bytes)
                {
                    mPreview.stop();
                    try
                    {
                        saveImage(bytes);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        return;
                    }
                }
            });
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result)
        {

        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private void createCameraSource()
    {
        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

        detector.setProcessor(new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory()).build());

        mCameraSource = new CameraSource.Builder(context,detector)
                .setFacing(camera_view)
                .setRequestedPreviewSize(680, 450)
                .setRequestedFps(12.0f)
                .setAutoFocusEnabled(true)
                .build();
        /*.setRequestedPreviewSize(2560, 1920)*/
    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face>
    {
        @Override
        public Tracker<Face> create(Face face)
        {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    private void createCameraSourceEdit(final String img, final Float height, final Float weight)
    {
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

        MultiProcessor.Factory<Face> factory = new MultiProcessor.Factory<Face>() {
            @Override
            public Tracker<Face> create(Face face)
            {
                return new GraphicFaceTrackerNew(mGraphicOverlay,img,height,weight);
            }
        };

        detector.setProcessor(new MultiProcessor.Builder<>(factory).build());

        mCameraSource = new CameraSource.Builder(context,detector)
                .setFacing(camera_view)
                .setRequestedPreviewSize(680, 450)
                .setRequestedFps(12.0f)
                .setAutoFocusEnabled(true)
                .build();
    }

    private class GraphicFaceTracker extends Tracker<Face>
    {
        private GraphicOverlay mOverlay;
        private FaceGraphicNew mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay)
        {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphicNew(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face)
        {
            mOverlay.add(mFaceGraphic);
            //mFaceGraphic.updateFace(face);
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }

    private class GraphicFaceTrackerNew extends Tracker<Face>
    {
        private GraphicOverlay mOverlay;
        private FaceGraphicNew mFaceGraphic;

        GraphicFaceTrackerNew(GraphicOverlay overlay,String img,Float height,Float weight)
        {
            mOverlay = overlay;
            mOverlay.clear();
            mFaceGraphic = new FaceGraphicNew(overlay,EditCameraImage.this,img,height,weight);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face)
        {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone()
        {
            mOverlay.remove(mFaceGraphic);
        }
    }


    private void startCameraSource()
    {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e)
            {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private void filter_list(int orientation)
    {
        imageModels.clear();
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"Cycle-helmet.png",1.1f,2.3f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"goggles.png",2.65f,2.1f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"Golf-cap.png",1.4f,2.22f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"orange-googles.png",2.9f,2.1f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"swimming-goggles.png",2.5f,2.1f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"face-3.png",1.4f,2.3f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"crown.png",1.6f,2.2f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"face-2.png",1.9f,2.2f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"face-1.png",1.9f,2.2f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"helmt-1_new.png",2.0f,2.0f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"helmet-2_land.png",2.2f,2.0f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"Crown-3.png",1.6f,2.2f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"Crown-2.png",1.6f,2.2f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"Crown-4.png",1.6f,2.2f));

        filter_list.setHasFixedSize(true);
        filter_list.setLayoutManager(new LinearLayoutManager(context,
                orientation,false));
        imagesAdapter = new FilterImagesAdapter(context,imageModels);
        filter_list.setAdapter(imagesAdapter);

        imagesAdapter.click(new FilterImagesAdapter.Check()
        {
            @Override
            public void show(View view, int pos)
            {
                if (ConnectionDetector.isInternetAvailable(context))
                {
                    createCameraSourceEdit(imageModels.get(pos).getId(),
                            imageModels.get(pos).getHeight(),imageModels.get(pos).getWeight());
                    startCameraSource();
                }
                else
                {
                    Toast.makeText(context, "Internet Connection Low", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void filter_listForSamsung(int orientation)
    {
        imageModels.clear();
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"Cycle-helmet.png",1.1f,2.1f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"goggles.png",2.85f,1.9f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"Golf-cap.png",1.4f,2.0f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"orange-googles.png",2.9f,1.9f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"swimming-goggles.png",2.7f,1.9f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"face-3.png",1.4f,2.0f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"crown.png",1.75f,1.95f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"face-2.png",1.99f,1.95f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"face-1.png",1.99f,1.95f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"helmt-1_new.png",2.45f,1.85f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"helmet-2_land.png",2.35f,2.0f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"Crown-3.png",1.8f,1.9f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"Crown-2.png",1.8f,1.9f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"Crown-4.png",1.8f,1.9f));

        filter_list.setHasFixedSize(true);
        filter_list.setLayoutManager(new LinearLayoutManager(context,
                orientation,false));
        imagesAdapter = new FilterImagesAdapter(context,imageModels);
        filter_list.setAdapter(imagesAdapter);

        imagesAdapter.click(new FilterImagesAdapter.Check()
        {
            @Override
            public void show(View view, int pos)
            {
                if (ConnectionDetector.isInternetAvailable(context))
                {
                    createCameraSourceEdit(imageModels.get(pos).getId(),
                            imageModels.get(pos).getHeight(),imageModels.get(pos).getWeight());
                    startCameraSource();
                }
                else
                {
                    Toast.makeText(context, "Internet Connection Low", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void filter_listLandscape(int orientation)
    {
        imageModels.clear();
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"Cycle-helmet.png",1.0f,2.3f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"goggles_land.png",3.4f,1.85f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"golf-cap_land.png",1.50f,2.0f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"orange-googles.png",2.45f,1.7f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"swim-goggles_land.png",3.2f,2.0f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"bunny_land.png",1.2f,2.1f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"crown.png",1.4f,2.1f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"face-2.png",1.6f,2.15f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"face-1.png",1.6f,2.15f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"helmt-1_new.png",2.0f,2.0f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"helmet-2_land.png",1.8f,2.0f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"crown-3_land.png",1.4f,2.15f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"crown_2_land.png",1.4f,2.15f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"crown_land.png",1.4f,2.15f));

        filter_list.setHasFixedSize(true);
        filter_list.setLayoutManager(new LinearLayoutManager(context,
                orientation,false));
        imagesAdapter = new FilterImagesAdapter(context,imageModels);
        filter_list.setAdapter(imagesAdapter);

        imagesAdapter.click(new FilterImagesAdapter.Check()
        {
            @Override
            public void show(View view, int pos)
            {
                if (ConnectionDetector.isInternetAvailable(context))
                {
                    createCameraSourceEdit(imageModels.get(pos).getId(),
                            imageModels.get(pos).getHeight(),imageModels.get(pos).getWeight());
                    startCameraSource();
                }
                else
                {
                    Toast.makeText(context, "Internet Connection Low", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void filter_listLandscapeSamsung(int orientation)
    {
        imageModels.clear();
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"Cycle-helmet.png",0.6f,2.3f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"goggles_land.png",3.2f,1.85f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"golf-cap_land.png",1.40f,2.0f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"orange-googles.png",2.2f,1.8f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"swim-goggles_land.png",3.0f,1.9f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"bunny_land.png",1.1f,2.1f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"crown.png",1.25f,2.1f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"face-2.png",1.45f,2.15f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"face-1.png",1.45f,2.15f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"helmet-1.png",2.0f,2.0f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"helmet-2.png",1.8f,2.0f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"crown-3_land.png",1.20f,2.15f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"crown_2_land.png",1.20f,2.15f));
        imageModels.add(new FilterImageModel(Constants.FILTER_IMAGE_URL+"crown_land.png",1.20f,2.15f));

        filter_list.setHasFixedSize(true);
        filter_list.setLayoutManager(new LinearLayoutManager(context,
                orientation,false));
        imagesAdapter = new FilterImagesAdapter(context,imageModels);
        filter_list.setAdapter(imagesAdapter);

        imagesAdapter.click(new FilterImagesAdapter.Check()
        {
            @Override
            public void show(View view, int pos)
            {
                if (ConnectionDetector.isInternetAvailable(context))
                {
                    createCameraSourceEdit(imageModels.get(pos).getId(),
                            imageModels.get(pos).getHeight(),imageModels.get(pos).getWeight());
                    startCameraSource();
                }
                else
                {
                    Toast.makeText(context, "Internet Connection Low", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saveImage(byte[] content) throws IOException
    {
        Bitmap bitmap = BitmapFactory.decodeByteArray(content, 0, content.length);

        File dir= new File(App.getQUOTATIONS());
        File mypath = null;

        if (!dir.exists())
            dir.mkdirs();

        mypath=new File(dir,"TempGhost.jpg");
        FileOutputStream fos = null;

        try{
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, fos);
        }catch (Exception e){
            e.printStackTrace();
        }

        image.setVisibility(View.VISIBLE);
        mPreview.setVisibility(View.GONE);
        String path = (Environment.getExternalStorageDirectory()+
                File.separator+"ShowUoff"+
                File.separator+"CameraImage"+
                File.separator+"TempGhost.jpg");

        Bitmap decode = BitmapFactory.decodeFile(path);
        Bitmap bit=modifyOrientation(decode,path);
        image.setImageBitmap(bit);

        takeScreen();
    }

    public static Bitmap loadBitmapFromView(Context context, View v)
    {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        v.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.EXACTLY));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap returnedBitmap = Bitmap.createBitmap(v.getMeasuredWidth(),
                v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(returnedBitmap);
        v.draw(c);

        return returnedBitmap;
    }

    public void takeScreen()
    {
        Bitmap bitmap = loadBitmapFromView(this, topLayout); //get Bitmap from the view

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File dir= new File(App.getQUOTATIONS());
        File mypath = null;
        if (!dir.exists())
            dir.mkdirs();

        mypath=new File(dir,timeStamp+"postt.png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 35, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent=new Intent();
        intent.putExtra("path",""+mypath.getPath());
        setResult(102,intent);
        finish();
    }

    public static String getDeviceName()
    {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException
    {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation)
        {
            case 0:
                if (getDeviceName().contains("Samsung"))
                {
                    if (!Temp.equalsIgnoreCase("portrait"))
                    {
                        if (camera_view==CameraSource.CAMERA_FACING_BACK)
                        {
                            return rotate(bitmap, 180,false,false);
                        }
                        else
                        {
                            return rotate(bitmap, 0,false,true);
                        }
                    }
                    else
                    {
                        if (camera_view==CameraSource.CAMERA_FACING_BACK)
                        {
                            return rotate(bitmap, 90,false,false);
                        }
                        else
                        {
                            return rotate(bitmap, 90,true,false);
                        }
                    }
                }
                else
                {
                    if (camera_view==CameraSource.CAMERA_FACING_BACK)
                    {

                    }
                    else
                    {
                        return flip(bitmap, true, false);
                    }
                }
            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees,boolean from,boolean to)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        matrix.preScale(from ? -1 : 1, to ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical)
    {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mCameraSource != null)
        {
            mCameraSource.release();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Constants.freeMemory();
        startCameraSource();
    }
}
