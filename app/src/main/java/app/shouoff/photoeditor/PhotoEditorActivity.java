package app.shouoff.photoeditor;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.viewpagerindicator.PageIndicator;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.photoSdk.BrushDrawingView;
import app.shouoff.photoSdk.OnPhotoEditorSDKListener;
import app.shouoff.photoSdk.PhotoEditorSDK;
import app.shouoff.photoSdk.ViewType;
import app.shouoff.widget.SlidingUpPanelLayout;

public class PhotoEditorActivity extends AppCompatActivity implements View.OnClickListener, OnPhotoEditorSDKListener
{
    private RelativeLayout parentImageRelativeLayout;
    private TextView undoTextView, undoTextTextView, doneDrawingTextView, eraseDrawingTextView,saving;
    private SlidingUpPanelLayout mLayout;
    private Typeface emojiFont,newFont;
    private View topShadow;
    private RelativeLayout topShadowRelativeLayout;
    private View bottomShadow;
    private RelativeLayout bottomShadowRelativeLayout;
    private PhotoEditorSDK photoEditorSDK;
    private TextView closeTextView,deleteTextView,clearAllTextView,clearAllTextTextView,goToNextTextView,header;
    private ImageView photoEditImageView,addImageEmojiTextView;
    private RelativeLayout deleteRelativeLayout;
    private ViewPager pager;
    private PageIndicator indicator;
    private BrushDrawingView brushDrawingView;
    private Bitmap bitmap;
    private List<Fragment> fragmentsList = new ArrayList<>();
    private PreviewSlidePagerAdapter adapter;

    /*GIF*/
    private DisplayMetrics mDisplayMetrics;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaRecorder mMediaRecorder;
    private MediaProjectionManager mProjectionManager;
    String filePath;
    private boolean aBoolean=false;

    private static final int CAST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Window window=getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.transparent));
        }
        Constants.freeMemory();
        setContentView(R.layout.activity_photo_editor);
        saving=(TextView)findViewById(R.id.saving);
        String selectedImagePath = getIntent().getExtras().getString("selectedImagePath");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        bitmap = BitmapFactory.decodeFile(selectedImagePath, options);

        newFont = Typeface.createFromAsset(getAssets(), "Eventtus-Icons.ttf");
        emojiFont = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

        setInitialComponentToRecord();
        initialized();
    }

    private void initialized()
    {
        header=(TextView)findViewById(R.id.header);
        brushDrawingView = (BrushDrawingView) findViewById(R.id.drawing_view);
        parentImageRelativeLayout = (RelativeLayout) findViewById(R.id.parent_image_rl);

        undoTextView = (TextView) findViewById(R.id.undo_tv);
        undoTextTextView = (TextView) findViewById(R.id.undo_text_tv);
        doneDrawingTextView = (TextView) findViewById(R.id.done_drawing_tv);
        eraseDrawingTextView = (TextView) findViewById(R.id.erase_drawing_tv);
        closeTextView = (TextView) findViewById(R.id.close_tv);
        deleteRelativeLayout = (RelativeLayout) findViewById(R.id.delete_rl);
        deleteTextView = (TextView) findViewById(R.id.delete_tv);
        addImageEmojiTextView = (ImageView) findViewById(R.id.add_image_emoji_tv);
        clearAllTextView = (TextView) findViewById(R.id.clear_all_tv);
        clearAllTextTextView = (TextView) findViewById(R.id.clear_all_text_tv);
        goToNextTextView = (TextView) findViewById(R.id.go_to_next_screen_tv);

        pager = (ViewPager) findViewById(R.id.image_emoji_view_pager);
        indicator = (PageIndicator) findViewById(R.id.image_emoji_indicator);
        photoEditImageView = (ImageView) findViewById(R.id.photo_edit_iv);
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        topShadow = findViewById(R.id.top_shadow);
        topShadowRelativeLayout = (RelativeLayout) findViewById(R.id.top_parent_rl);
        bottomShadow = findViewById(R.id.bottom_shadow);
        bottomShadowRelativeLayout = (RelativeLayout) findViewById(R.id.bottom_parent_rl);

        photoEditImageView.setImageBitmap(bitmap);
        undoTextView.setVisibility(View.GONE);
        undoTextTextView.setVisibility(View.GONE);

        setFontIcon();

        fragmentsList.add(new GifFragment());
        fragmentsList.add(new ImageFragment());
        fragmentsList.add(new EmojiFragment());

        adapter = new PreviewSlidePagerAdapter(getSupportFragmentManager(), fragmentsList);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(5);
        indicator.setViewPager(pager);

        photoEditorSDK = new PhotoEditorSDK.PhotoEditorSDKBuilder(PhotoEditorActivity.this)
                .parentView(parentImageRelativeLayout)
                .childView(photoEditImageView)
                .deleteView(deleteRelativeLayout)
                .brushDrawingView(brushDrawingView)
                .buildPhotoEditorSDK();
        photoEditorSDK.setOnPhotoEditorSDKListener(this);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position)
            {
                if (position == 0)
                    mLayout.setScrollableView(((GifFragment) fragmentsList.get(position)).imageRecyclerView);
                else if (position == 1)
                    mLayout.setScrollableView(((ImageFragment) fragmentsList.get(position)).imageRecyclerView);
                else if (position == 2)
                    mLayout.setScrollableView(((EmojiFragment) fragmentsList.get(position)).emojiRecyclerView);

            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

        closeTextView.setOnClickListener(this);
        addImageEmojiTextView.setOnClickListener(this);
        undoTextView.setOnClickListener(this);
        undoTextTextView.setOnClickListener(this);
        doneDrawingTextView.setOnClickListener(this);
        eraseDrawingTextView.setOnClickListener(this);
        clearAllTextView.setOnClickListener(this);
        clearAllTextTextView.setOnClickListener(this);
        goToNextTextView.setOnClickListener(this);

        new CountDownTimer(500, 100)
        {

            public void onTick(long millisUntilFinished)
            {
            }

            public void onFinish()
            {
                mLayout.setScrollableView(((ImageFragment) fragmentsList.get(1)).imageRecyclerView);
            }

        }.start();

        new CountDownTimer(500, 100)
        {

            public void onTick(long millisUntilFinished)
            {
            }

            public void onFinish()
            {
                mLayout.setScrollableView(((GifFragment) fragmentsList.get(0)).imageRecyclerView);
            }

        }.start();
    }


    private void setFontIcon()
    {
        closeTextView.setTypeface(newFont);
        undoTextView.setTypeface(newFont);
        clearAllTextView.setTypeface(newFont);
        goToNextTextView.setTypeface(newFont);
        deleteTextView.setTypeface(newFont);
    }

    public void addEmoji(String emojiName) {
        photoEditorSDK.addEmoji(emojiName, emojiFont);
        if (mLayout != null)
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public void addImage(String image)
    {
        photoEditorSDK.addImage(image);
        if (mLayout != null)
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public void addImageGif(String image)
    {
        photoEditorSDK.addGif(image);
        aBoolean=true;
        if (mLayout != null)

            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    private void clearAllViews()
    {
        photoEditorSDK.clearAllViews();
    }

    private void undoViews()
    {
        photoEditorSDK.viewUndo();
    }

    private void updateView(int visibility)
    {
        topShadow.setVisibility(visibility);
        topShadowRelativeLayout.setVisibility(visibility);
        bottomShadow.setVisibility(visibility);
        bottomShadowRelativeLayout.setVisibility(visibility);
    }

    private void returnBackWithSavedImage()
    {
        if (aBoolean)
        {
            if(startRecording())
            {
                startupload();
            }
        }
        else
        {
            updateView(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            parentImageRelativeLayout.setLayoutParams(layoutParams);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageName = "IMG_" + timeStamp + ".jpg";
            Intent returnIntent = new Intent();
            returnIntent.putExtra("imagePath",photoEditorSDK.saveImage("PhotoEditorSDK", imageName));
            setResult(2001,returnIntent);
            finish();
        }
    }

    private void startupload()
    {
        mMediaRecorder.start();
        mVirtualDisplay = getVirtualDisplay();

        new CountDownTimer(2500, 1000)
        {
            public void onTick(long millisUntilFinished)
            {
            }
            public void onFinish()
            {
                stopRecording();
                closeTextView.setVisibility(View.VISIBLE);
                addImageEmojiTextView.setVisibility(View.VISIBLE);
                undoTextView.setVisibility(View.VISIBLE);
                undoTextTextView.setVisibility(View.VISIBLE);
                goToNextTextView.setVisibility(View.VISIBLE);
                header.setVisibility(View.VISIBLE);
                Toast.makeText(PhotoEditorActivity.this, "Save Successfully", Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close_tv)
        {
            onBackPressed();
        } else if (v.getId() == R.id.add_image_emoji_tv) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }else if (v.getId() == R.id.clear_all_tv || v.getId() == R.id.clear_all_text_tv) {
            clearAllViews();
        } else if (v.getId() == R.id.undo_text_tv || v.getId() == R.id.undo_tv)
        {
            undoViews();
        }else if (v.getId() == R.id.go_to_next_screen_tv)
        {
            closeTextView.setVisibility(View.GONE);
            addImageEmojiTextView.setVisibility(View.GONE);
            undoTextView.setVisibility(View.GONE);
            undoTextTextView.setVisibility(View.GONE);
            goToNextTextView.setVisibility(View.GONE);
            header.setVisibility(View.GONE);
            returnBackWithSavedImage();
        }
    }

    @Override
    public void onEditTextChangeListener(String text, int colorCode)
    {

    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews)
    {
        if (numberOfAddedViews > 0)
        {
            undoTextView.setVisibility(View.VISIBLE);
            undoTextTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews)
    {
        if (numberOfAddedViews == 0) {
            undoTextView.setVisibility(View.GONE);
            undoTextTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType)
    {

    }

    private class PreviewSlidePagerAdapter extends FragmentStatePagerAdapter
    {
        private List<Fragment> mFragments;

        PreviewSlidePagerAdapter(FragmentManager fm, List<Fragment> fragments)
        {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position)
        {
            if (mFragments == null) {
                return (null);
            }
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }


    private void setInitialComponentToRecord()
    {
        mMediaRecorder = new MediaRecorder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        }
        mDisplayMetrics = new DisplayMetrics();


        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
    }


    private boolean startRecording()
    {
        // If mMediaProjection is null that means we didn't get a context, lets ask the user
        if (mMediaProjection == null)
        {
            // This asks for user permissions to capture the screen
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivityForResult(mProjectionManager.createScreenCaptureIntent(), CAST_PERMISSION_CODE);
            }
            return false;
        }
        prepareRecording();
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void stopRecording()
    {
        try
        {
            if (mMediaRecorder != null) {
               // mMediaRecorder.stop();
               // mMediaRecorder.reset();
            }
            if (mVirtualDisplay != null) {
               // mVirtualDisplay.release();
            }
            if (mMediaProjection != null) {
               // mMediaProjection.stop();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            parentImageRelativeLayout.setLayoutParams(layoutParams);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageName = "IMG_" + timeStamp + ".jpg";

            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
            }

            Intent returnIntent = new Intent();
            returnIntent.putExtra("gif_image","gif_image");
            returnIntent.putExtra("imagePath",filePath);
            returnIntent.putExtra("thumbnail",photoEditorSDK.saveImage("PhotoEditorSDK", imageName));
            setResult(2001,returnIntent);
            finish();

        }

    }

    @TargetApi(Build.VERSION_CODES.N)
    public String getCurSysDate() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
    }

    private void prepareRecording()
    {
        final String directory = Environment.getExternalStorageDirectory().getAbsolutePath();

        final File folder = new File(directory);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }

        if (success)
        {
            String videoName = ("capture_" + getCurSysDate() + ".mp4");
            filePath = directory + File.separator + videoName;
        } else
            {
            Toast.makeText(this, "Failed to create Recordings directory", Toast.LENGTH_SHORT).show();
            return;
        }

        int width = mDisplayMetrics.widthPixels;
        int height = mDisplayMetrics.heightPixels;

        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoEncodingBitRate(1024 * 2000);
        mMediaRecorder.setVideoFrameRate(1000);
        mMediaRecorder.setVideoSize(width, height);
        mMediaRecorder.setOutputFile(filePath);
        try
        {
            mMediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != RESULT_OK)
        {
            closeTextView.setVisibility(View.VISIBLE);
            addImageEmojiTextView.setVisibility(View.VISIBLE);
            undoTextView.setVisibility(View.VISIBLE);
            undoTextTextView.setVisibility(View.VISIBLE);
            goToNextTextView.setVisibility(View.VISIBLE);
            header.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            prepareRecording();
            mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);

            closeTextView.setVisibility(View.INVISIBLE);
            addImageEmojiTextView.setVisibility(View.INVISIBLE);
            undoTextView.setVisibility(View.INVISIBLE);
            undoTextTextView.setVisibility(View.INVISIBLE);
            goToNextTextView.setVisibility(View.INVISIBLE);
            startupload();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private VirtualDisplay getVirtualDisplay()
    {
        int screenDensity = mDisplayMetrics.densityDpi;
        int width = mDisplayMetrics.widthPixels;
        int height = mDisplayMetrics.heightPixels;

        return mMediaProjection.createVirtualDisplay(this.getClass().getSimpleName(),
                width, height, screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder.getSurface(), null /*Callbacks*/, null /*Handler*/);
    }

}
