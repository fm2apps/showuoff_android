package app.shouoff.Croping;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.isseiaoki.simplecropview.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import app.shouoff.R;
import app.shouoff.app.App;

public class Croping extends AppCompatActivity implements View.OnClickListener
{
    CropImageView cropImageView;
    int source;
    String imagePath;
    public Toolbar toolbar;
    public TextView done,title,cancel;
    private ImageButton buttonRotateLeft,buttonRotateRight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_croping);

        toolbar=(Toolbar) findViewById(R.id.toolbar);
        done=(TextView)findViewById(R.id.done);
        cancel=(TextView)findViewById(R.id.cancel);
        buttonRotateLeft=(ImageButton)findViewById(R.id.buttonRotateLeft);
        buttonRotateRight=(ImageButton)findViewById(R.id.buttonRotateRight);
        done.setOnClickListener(this);
        cancel.setOnClickListener(this);
        buttonRotateRight.setOnClickListener(this);
        buttonRotateLeft.setOnClickListener(this);

        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        cropImageView.setMinFrameSizeInDp(150);
        cancel.setVisibility(View.VISIBLE);
        done.setVisibility(View.VISIBLE);

        try
        {
            if (getIntent().hasExtra("PROFILE"))
            {
                source = 102;
                File image = new File(getIntent().getStringExtra("PROFILE"));
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(getIntent().getStringExtra("PROFILE"));
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
                if (image.exists())
                {
                    Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("PROFILE"));
                    Bitmap bmRotated = rotateBitmap(bitmap, orientation);

                    int maxP = Math.max(bmRotated.getWidth(),bmRotated.getHeight());
                    float scale1280 = (float)maxP / 1280;
                    bmRotated = Bitmap.createScaledBitmap(bmRotated,(int)(bmRotated.getWidth()/scale1280),
                            (int)(bmRotated.getHeight()/scale1280), true);

                    cropImageView.setImageBitmap(bmRotated);
                    cropImageView.setCropMode(CropImageView.CropMode.SQUARE);
                    cropImageView.setCropMode(CropImageView.CropMode.RATIO_9_16);
                    cropImageView.setCropMode(CropImageView.CropMode.FREE);
               }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private String saveToInternalStorage(Bitmap bitmapImage, int source)
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File dir= new File(App.getQUOTATIONS());
        File mypath = null;
        if (source == 102)
        {
            if (!dir.exists())
                dir.mkdirs();

            mypath=new File(dir,timeStamp+"postt.png");
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 35, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        imagePath = mypath.getPath();
        return dir.getAbsolutePath();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.done:
                String path = saveToInternalStorage(cropImageView.getCroppedBitmap(),source);
                if (imagePath != null)
                {
                    Intent intent = new Intent();
                    intent.putExtra("PATH",imagePath);
                    setResult(1001,intent);
                    finish();
                }
                break;
            case R.id.cancel:
                finish();
                break;
            case R.id.buttonRotateLeft:
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
                break;
            case R.id.buttonRotateRight:
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                break;
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

}
