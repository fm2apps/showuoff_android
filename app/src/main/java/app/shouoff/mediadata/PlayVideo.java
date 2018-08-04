package app.shouoff.mediadata;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.devbrackets.android.exomedia.core.video.scale.ScaleType;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;

import java.io.File;

import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.model.PostCreateIVModel;

public class PlayVideo extends AppCompatActivity
{
    private String attachment_file;
    private ProgressBar play;
    private com.devbrackets.android.exomedia.ui.widget.VideoView exo_player;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        attachment_file=getIntent().getStringExtra("attachment_file");

        play=(ProgressBar)findViewById(R.id.play);

        /*exo player*/
        exo_player = (com.devbrackets.android.exomedia.ui.widget.VideoView)findViewById(R.id.video_view);
        exo_player.setHandleAudioFocus(false);
        exo_player.setMeasureBasedOnAspectRatioEnabled(true);

        if (getIntent().hasExtra("ads"))
        {
            exo_player.setVideoURI(Uri.parse(Constants.ADS_VIDEO+attachment_file.replace(" ","%20")));
        }
        else if (getIntent().hasExtra("post_video"))
        {
            File file=new File(getIntent().getStringExtra("post_video"));
            exo_player.setVideoURI(Uri.fromFile(file));
        }
        else
        {
            exo_player.setVideoURI(Uri.parse(Constants.POST_URL+attachment_file.replace(" ","%20")));
        }

        exo_player.setOnPreparedListener(new OnPreparedListener()
        {
            @Override
            public void onPrepared()
            {
                exo_player.start();
                play.setVisibility(View.GONE);
            }
        });

        exo_player.setOnCompletionListener(new OnCompletionListener()
        {
            @Override
            public void onCompletion()
            {
                exo_player.restart();
            }
        });

        /*VideoControls videoControls = exo_player.getVideoControls();
        assert videoControls != null;
        videoControls.hide(true);
        videoControls.setButtonListener(new ControlsListener());*/
    }

    private class ControlsListener implements VideoControlsButtonListener
    {
        @Override
        public boolean onPlayPauseClicked() {
            if(exo_player.isPlaying())
            {
                exo_player.pause();
            }
            else
            {
                exo_player.start();
            }
            return true;
        }

        @Override
        public boolean onPreviousClicked() {
            return false;
        }

        @Override
        public boolean onNextClicked() {
            return false;
        }

        @Override
        public boolean onRewindClicked() {
            return false;
        }

        @Override
        public boolean onFastForwardClicked() {
            return false;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (exo_player.isPlaying())
        {
            exo_player.pause();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (exo_player.isPlaying())
        {
            exo_player.pause();
            exo_player.reset();
        }
    }
}
