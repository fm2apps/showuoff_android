package app.shouoff.mediadata;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Surface;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;



import app.shouoff.Croping.CropImage;
import app.shouoff.R;
import app.shouoff.activity.CreatPost;
import app.shouoff.app.App;
import app.shouoff.common.Constants;
import app.shouoff.photoeditor.PhotoEditorActivity;
import app.shouoff.silicompressorr.SiliCompressor;
import app.shouoff.trimmer.TrimmerActivity;


public class CustomCamera extends AppCompatActivity implements Callback,OnClickListener
{
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private ImageView flipCamera,captureImage,gallery_image,record_video,gallery_video,flash;
    public static int cameraId;
    private int rotation;
    float mDist = 0;
    private Context context=this;
    private boolean flashmode = false;
    private String flash_mode="",type="";
    Camera.Parameters paramss;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera);

        cameraId = CameraInfo.CAMERA_FACING_BACK;
        type="back";
        initialized();
    }

    private void initialized()
    {
        flash=(ImageView)findViewById(R.id.flash);
        gallery_image = (ImageView) findViewById(R.id.gallery_image);
        record_video = (ImageView) findViewById(R.id.record_video);
        gallery_video = (ImageView) findViewById(R.id.gallery_video);

        flipCamera = (ImageView) findViewById(R.id.flipCamera);
        captureImage = (ImageView) findViewById(R.id.captureImage);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();

        surfaceHolder.addCallback(this);
        flipCamera.setOnClickListener(this);
        captureImage.setOnClickListener(this);
        gallery_image.setOnClickListener(this);
        record_video.setOnClickListener(this);
        gallery_video.setOnClickListener(this);
        flash.setOnClickListener(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Camera.getNumberOfCameras() > 1) {
            flipCamera.setVisibility(View.VISIBLE);
        }

        if (!getBaseContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH)) {
            flash.setVisibility(View.GONE);
        }
        else
        {
            flash_mode=Parameters.FLASH_MODE_TORCH;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        if (!openCamera(CameraInfo.CAMERA_FACING_BACK))
        {
            alertCameraDialog();
        }
    }

    private boolean openCamera(int id)
    {
        boolean result = false;
        cameraId = id;
        releaseCamera();
        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (camera != null) {
            try {
                setUpCamera(camera);
                camera.setErrorCallback(new ErrorCallback() {

                    @Override
                    public void onError(int error, Camera camera) {

                    }
                });
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
                releaseCamera();
            }
        }
        return result;
    }

    private void setUpCamera(Camera c) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;

            default:
                break;
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // frontFacing
            rotation = (info.orientation + degree) % 330;
            rotation = (360 - rotation) % 360;
        } else {
            // Back-facing
            rotation = (info.orientation - degree + 360) % 360;
        }
        c.setDisplayOrientation(rotation);
        Parameters params = c.getParameters();

        showFlashButton(params);

        List<String> focusModes = params.getSupportedFlashModes();
        if (focusModes != null) {
            if (focusModes
                    .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
        }
        params.setPictureFormat(ImageFormat.JPEG);
        params.setJpegQuality(100);
        params.setRotation(rotation);
        camera.setParameters(params);
        paramss=camera.getParameters();
    }

    private void showFlashButton(Parameters params)
    {
        boolean showFlash = (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH) && params.getFlashMode() != null)
                && params.getSupportedFlashModes() != null
                && params.getSupportedFocusModes().size() > 1;

        flash.setVisibility(showFlash ? View.VISIBLE
                : View.INVISIBLE);
    }

    private void releaseCamera() {
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.setErrorCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
            camera = null;
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.flipCamera:
                flipCamera();
                break;
            case R.id.captureImage:
                takeImage();
                break;
            case R.id.gallery_image:
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                startActivityForResult(i,22);
                break;
            case R.id.gallery_video:
                Intent ii = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                ii.setType("video/*");
                startActivityForResult(ii, 44);
                break;
            case R.id.record_video:
                Intent videoCaptureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (videoCaptureIntent.resolveActivity(getPackageManager()) != null)
                {
                    videoCaptureIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
                    startActivityForResult(videoCaptureIntent,44);
                }
                break;
            case R.id.flash:
                flashOnButton();
                break;
            default:
                break;
        }
    }

    private void flashOnButton()
    {
        if (flashmode)
        {
            flashmode=false;
            flash_mode=Parameters.FLASH_MODE_TORCH;
            flash.setImageResource(R.drawable.flash_on);
        }
        else
        {
            flashmode=true;
            flash_mode=Parameters.FLASH_MODE_OFF;
            flash.setImageResource(R.drawable.flash_off);
        }
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 301 && resultCode==1001)
        {
            Intent intent = new Intent(context, PhotoEditorActivity.class);
            intent.putExtra("selectedImagePath", data.getExtras().get("PATH").toString());
            startActivityForResult(intent,2000);

        }
        else if (requestCode == 2000 && resultCode==2001)
        {
            // TODO: 18/4/18 Submit image
            if (data.hasExtra("gif_image"))
            {
                Intent intent=new Intent(context, CreatPost.class);
                intent.putExtra("image","image");
                intent.putExtra("image_gif","image_gif");
                intent.putExtra("thumbnail",data.getExtras().get("thumbnail").toString());
                intent.putExtra("imagePath",data.getExtras().get("imagePath").toString());
                startActivity(intent);
            }
            else
            {
                Intent intent=new Intent(context, CreatPost.class);
                intent.putExtra("image","image");
                intent.putExtra("imagePath",data.getExtras().get("imagePath").toString());
                setResult(222,intent);
                startActivity(intent);
            }
        }
        else if (requestCode == 22 && resultCode == RESULT_OK && data != null)
        {
            // TODO: 18/4/18 Gallery Image
            try
            {
                Uri uri = data.getData();
                String path = "";
                path = Constants.getPath(context,uri);
                startActivityForResult(new Intent(context, CropImage.class)
                        .putExtra("gallery","gallery")
                        .putExtra("Image",path),301);
            }
            catch (URISyntaxException e)
            {
                e.printStackTrace();
            }
        }
        else if (requestCode==44 && resultCode==RESULT_OK)
        {
            // TODO: 18/4/18  Trim video
            Uri uri = data.getData();

            MediaPlayer mp = MediaPlayer.create(this, uri);
            int duration = mp.getDuration();

           String s= String.format("%d",((TimeUnit.MILLISECONDS.toMinutes(duration)*60))+
                            TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));

            if (Integer.valueOf(s)>60)
            {
                Intent intent = new Intent(context, TrimmerActivity.class);
                intent.putExtra("VIDEO", Constants.getRealPathFromURIPath(uri, CustomCamera.this));
                startActivityForResult(intent,55);
            }
            else
            {
                String videopath = Constants.getRealPathFromURIPath(uri, CustomCamera.this);
                Intent intent=new Intent(context, CreatPost.class);
                intent.putExtra("video","video");
                intent.putExtra("path",videopath);
                setResult(222,intent);
                startActivity(intent);
            }
        }
        else if (requestCode==55 && resultCode==101)
        {
            String videopath = data.getExtras().get("PATH").toString();
            Intent intent=new Intent(context, CreatPost.class);
            intent.putExtra("video","video");
            intent.putExtra("path",videopath);
            setResult(222,intent);
            startActivity(intent);
        }
    }

    private void takeImage()
    {
        Camera.Parameters params=camera.getParameters();
        params.setPictureFormat(ImageFormat.JPEG);
        params.setJpegQuality(100);
        params.setFlashMode(flash_mode);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
        {
            List<Camera.Size> allSizes = params.getSupportedPictureSizes();
            Camera.Size size = allSizes.get(0);
            for (int i = 0; i < allSizes.size(); i++)
            {
                if (allSizes.get(i).width > size.width)
                    size = allSizes.get(i);
            }
            params.setPictureSize(size.width, size.height);
        }
        else
        {
            for (Camera.Size size : params.getSupportedPictureSizes())
            {
                if (1600 <= size.width & size.width <= 1920)
                {
                    params.setPreviewSize(size.width, size.height);
                    params.setPictureSize(size.width, size.height);
                    break;
                }
            }
        }

        camera.setParameters(params);

        camera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        }, new PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

            }
        }, new PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                saveImage(data);
            }
        });
    }

    public void saveImage(byte[] content)
    {
        Bitmap bitmap = BitmapFactory.decodeByteArray(content, 0, content.length);

        Camera.Parameters params=camera.getParameters();
        params.setFlashMode(Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);

        File dir= new File(App.getQUOTATIONS());
        File mypath = null;

        if (!dir.exists())
            dir.mkdirs();

        mypath=new File(dir,"TempGhost.jpg");
        FileOutputStream fos = null;

        try{
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fos);
        }catch (Exception e){
            e.printStackTrace();
        }

        String path = (Environment.getExternalStorageDirectory()+
                File.separator+"ShowUoff"+
                File.separator+"CameraImage"+
                File.separator+"TempGhost.jpg");

        startActivityForResult(new Intent(context, CropImage.class)
                .putExtra("type",type)
                        .putExtra("Image",path)
                ,301);
    }

    private void flipCamera()
    {
        int id = (cameraId == CameraInfo.CAMERA_FACING_BACK ? CameraInfo.CAMERA_FACING_FRONT
                : CameraInfo.CAMERA_FACING_BACK);

        if (cameraId==CameraInfo.CAMERA_FACING_BACK)
            type="front";
        else
            type="back";

        if (!openCamera(id))
        {
            alertCameraDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        type="back";
    }

    private void alertCameraDialog()
    {
        AlertDialog.Builder dialog = createAlert(CustomCamera.this,
                "Camera info", "error to open camera");
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        dialog.show();
    }

    private Builder createAlert(Context context, String title, String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(
                new ContextThemeWrapper(context,
                        android.R.style.Theme_Holo_Light_Dialog));
        dialog.setIcon(R.mipmap.ic_launcher);
        if (title != null)
            dialog.setTitle(title);
        else
            dialog.setTitle("Information");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        return dialog;

    }

    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        camera.setParameters(params);
    }

    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    // currently set to auto-focus on single touch
                }
            });
        }
    }

    /** Determine the space between the first two fingers */
    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // Get the pointer ID
      //  Camera.Parameters params = camera.getParameters();
        int action = event.getAction();


        if (event.getPointerCount() > 1)
        {
            // handle multi-touch events
            if (action == MotionEvent.ACTION_POINTER_DOWN)
            {
                mDist = getFingerSpacing(event);
            } else if (action == MotionEvent.ACTION_MOVE && paramss.isZoomSupported()) {
                camera.cancelAutoFocus();
                handleZoom(event, paramss);
            }
        } else {
            // handle single touch events
            if (action == MotionEvent.ACTION_UP) {
                handleFocus(event, paramss);
            }
        }
        return true;
    }

    class VideoCompressAsyncTask extends AsyncTask<String, String, String>
    {
        Context mContext;

        public VideoCompressAsyncTask(Context context)
        {
            mContext = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... paths) {
            String filePath = null;
            try {

                filePath = SiliCompressor.with(mContext).compressVideo(Uri.parse(paths[0]), paths[1]);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return  filePath;

        }

        @Override
        protected void onPostExecute(String compressedFilePath)
        {
            super.onPostExecute(compressedFilePath);
            File imageFile = new File(compressedFilePath);
            float length = imageFile.length() / 1024f; // Size in KB
            String value;
            if(length >= 1024)
                value = length/1024f+" MB";
            else
                value = length+" KB";
            String text = String.format(Locale.US, "%s\nName: %s\nSize: %s","file", imageFile.getName(), value);

            Log.e("Silicompressor", "Path: "+compressedFilePath);
        }
    }

    /*private void compress(String input,String out)
    {
        GiraffeCompressor.create() //two implementations: mediacodec and ffmpeg,default is mediacodec
                .input(inputFile) //set video to be compressed
                .output(outputFile) //set compressed video output
                .bitRate(203600)//set bitrate 码率
                .resizeFactor("")//set video resize factor 分辨率缩放,默认保持原分辨率
                .watermark("/sdcard/videoCompressor/watermarker.png")//add watermark(take a long time) 水印图片(需要长时间处理)
                .ready()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GiraffeCompressor.Result>() {
                    @Override
                    public void onCompleted()
                    {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();


                    }

                    @Override
                    public void onNext(GiraffeCompressor.Result s) {

                    }
                });

    }*/

}
