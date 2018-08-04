package app.shouoff.Croping;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import com.fenchtose.nocropper.BitmapResult;
import com.fenchtose.nocropper.CropInfo;
import com.fenchtose.nocropper.CropResult;
import com.fenchtose.nocropper.CropperCallback;
import com.fenchtose.nocropper.CropperView;
import com.fenchtose.nocropper.ScaledCropper;
import com.google.android.gms.vision.CameraSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import app.shouoff.R;
import app.shouoff.app.App;

import static app.shouoff.mediadata.EditCameraImage.flip;

public class CropImage extends AppCompatActivity
{
    CropperView mImageView;
    ImageView snap_button,rotate_button;
    private Bitmap mBitmap;
    private boolean isSnappedToCenter = false;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        TextView save_image = (TextView) findViewById(R.id.save_image);
        mImageView=(CropperView)findViewById(R.id.imageview);
        snap_button=(ImageView)findViewById(R.id.snap_button);
        rotate_button=(ImageView)findViewById(R.id.rotate_button);

        snap_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    snapImage();
            }
        });

        rotate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage();
            }
        });

        save_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                BitmapResult result = mImageView.getCroppedBitmap();
                Bitmap bitmap = result.getBitmap();

                saveToInternalStorage(bitmap);

                Intent intent = new Intent();
                intent.putExtra("PATH",imagePath);
                setResult(1001,intent);
                finish();
            }
        });

        try {
            loadNewImage(getIntent().getStringExtra("Image"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mImageView.setMakeSquare(false);
    }

    private void loadNewImage(String filePath) throws IOException
    {
        if (getIntent().hasExtra("gallery"))
        {
            mBitmap = BitmapFactory.decodeFile(filePath);
        }
        else
        {
            Bitmap decode = BitmapFactory.decodeFile(filePath);
            mBitmap = modifyOrientation(decode,filePath);
        }

        int maxP = Math.max(mBitmap.getWidth(), mBitmap.getHeight());
        float scale1280 = (float)maxP / 1280;

        if (mImageView.getWidth() != 0) {
            mImageView.setMaxZoom(mImageView.getWidth() * 2 / 1280f);
        } else {

            ViewTreeObserver vto = mImageView.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    mImageView.setMaxZoom(mImageView.getWidth() * 2 / 1280f);
                    return true;
                }
            });
        }

        mBitmap = Bitmap.createScaledBitmap(mBitmap, (int)(mBitmap.getWidth()/scale1280),
                (int)(mBitmap.getHeight()/scale1280), true);

        mImageView.setImageBitmap(mBitmap);
    }

    private String saveToInternalStorage(Bitmap bitmapImage)
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File dir= new File(App.getQUOTATIONS());
        File mypath = null;
        if (!dir.exists())
            dir.mkdirs();

        mypath=new File(dir,timeStamp+"postt.png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 60, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        imagePath = mypath.getPath();
        return dir.getAbsolutePath();
    }

    private void rotateImage()
    {
        mBitmap = BitmapUtils.rotateBitmap(mBitmap, 90);
        mImageView.setImageBitmap(mBitmap);
    }

    private void snapImage()
    {
        if (isSnappedToCenter)
        {
            mImageView.cropToCenter();
        } else {
            mImageView.fitToCenter();
        }

        isSnappedToCenter = !isSnappedToCenter;
    }

    public  Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException
    {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation)
        {
            case 0:
                if (getDeviceName().contains("Samsung"))
                {
                    if (getIntent().getStringExtra("type").equalsIgnoreCase("back"))
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
                    if (getIntent().getStringExtra("type").equalsIgnoreCase("back"))
                    {

                    }
                    else
                    {
                        return rotate(bitmap,180, true, false);
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
}
