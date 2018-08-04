package app.shouoff.mediadata;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.animation.ViewPosition;
import com.alexvasilkov.gestures.animation.ViewPositionAnimator;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.squareup.picasso.Picasso;
import app.shouoff.R;

public class FullImageActivity extends AppCompatActivity
{

    private static final String EXTRA_POSITION = "position";
    private static final String EXTRA_IMAGE_ID = "image_id";

    public static void open(Activity from, ViewPosition position, String imageId)
    {
        Intent intent = new Intent(from, FullImageActivity.class);
        intent.putExtra(EXTRA_POSITION, position.pack());
        intent.putExtra(EXTRA_IMAGE_ID, imageId);
        from.startActivity(intent);
        from.overridePendingTransition(0, 0);
    }

    private com.alexvasilkov.gestures.views.GestureImageView image;
    private boolean hideOrigImage;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
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

        Picasso.with(FullImageActivity.this).load(getIntent().getStringExtra(EXTRA_IMAGE_ID))
                .placeholder(R.drawable.noimage).into(image);

        // Ensuring that original image is hidden as soon as image is loaded and drawn
        image.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (hideOrigImage) {
                    image.getViewTreeObserver().removeOnPreDrawListener(this);
                    // Asking previous activity to hide original image
                    // Events.create(CrossActivitiesDemoActivity.EVENT_SHOW_IMAGE).param(false).post();
                } else if (image.getDrawable() != null) {
                    // Requesting hiding original image after first drawing
                    hideOrigImage = true;
                }
                return true;
            }
        });

        // Listening for end of exit animation
        image.getPositionAnimator().addPositionUpdateListener(new ViewPositionAnimator.PositionUpdateListener() {
            @Override
            public void onPositionUpdate(float position, boolean isLeaving) {
                if (position == 0f && isLeaving) { // Exit finished
                    // Asking previous activity to show back original image

                    // By default end of exit animation will return GestureImageView into
                    // fullscreen state, this will make the image blink. So we need to hack this
                    // behaviour and keep image in exit state until activity is finished.
                    image.getController().getSettings().disableBounds();
                    image.getPositionAnimator().setState(0f, false, false);
                    finish();
                }
            }
        });

        // Playing enter animation from provided position
        ViewPosition position = ViewPosition.unpack(getIntent().getStringExtra(EXTRA_POSITION));
        boolean animate = savedInstanceState == null; // No animation when restoring activity
        image.getPositionAnimator().enter(position,animate);

    }
}
