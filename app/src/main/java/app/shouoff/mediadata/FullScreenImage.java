package app.shouoff.mediadata;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.squareup.picasso.Picasso;
import java.io.File;
import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.widget.TouchImageView;

public class FullScreenImage extends AppCompatActivity
{
    private TouchImageView into;
    private com.alexvasilkov.gestures.views.GestureImageView image;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        into=(TouchImageView) findViewById(R.id.image);
        image=(GestureImageView)findViewById(R.id.cross_to);

        image.getController().getSettings()
                .setMaxZoom(7f)
                .setDoubleTapZoom(-5f)
                .setPanEnabled(true)
                .setZoomEnabled(true)
                .setDoubleTapEnabled(true)
                .setRotationEnabled(false)
                .setRestrictRotation(false)
                .setOverscrollDistance(0f, 0f)
                .setOverzoomFactor(4f)
                .setFillViewport(false)
                .setFitMethod(Settings.Fit.INSIDE)
                .setGravity(Gravity.CENTER);

        if (getIntent().hasExtra("post_imagee"))
        {
            File file=new File(getIntent().getStringExtra("post_imagee"));
            Picasso.with(FullScreenImage.this).load(file)
                    .placeholder(R.drawable.noimage).
                    into(image);
        }
        else
        {
            Picasso.with(FullScreenImage.this).load(Constants.POST_URL+getIntent().getStringExtra("image"))
                    .placeholder(R.drawable.noimage).
                    into(image);
        }
    }
}
