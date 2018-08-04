package app.shouoff.login;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;
import app.shouoff.R;
import app.shouoff.activity.HomeActivity;
import app.shouoff.common.Constants;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class HomePage extends AppCompatActivity implements View.OnClickListener,RetrofitResponse
{
    LinearLayout next;
    private RelativeLayout relate_view;
    private Context context=this;
    private ProgressBar awards_loader;
    private RoundedImageView award_image;
    private CardView card_view;
    private TextView text1;
    private VideoView exo_player;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        initViews();
        System.gc();

        new Retrofit2(context,this,0, Constants.getAwards).callServiceBack();
    }

    @SuppressLint("WrongViewCast")
    private void initViews()
    {
        relate_view=(RelativeLayout)findViewById(R.id.relate_view);
        text1=findViewById(R.id.text1);
        card_view=findViewById(R.id.card_view);

        exo_player = (VideoView)findViewById(R.id.video_view);
        exo_player.setHandleAudioFocus(false);


        award_image=(RoundedImageView)findViewById(R.id.award_image);
        awards_loader=(ProgressBar)findViewById(R.id.awards_loader);
        next=(LinearLayout)findViewById(R.id.next);

        VideoControls videoControls = exo_player.getVideoControls();
        assert videoControls != null;
        videoControls.setButtonListener(new ControlsListener());

        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.next:
                Intent intent=new Intent(HomePage.this,HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP
                |Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onServiceResponse(int requestCode, Response<ResponseBody> response)
    {
        try
        {
            JSONObject result=new JSONObject(response.body().string().toString());
            boolean status=result.getBoolean("response");
            if (status)
            {
                switch (requestCode)
                {
                    case 0:
                        awards_loader.setVisibility(View.GONE);
                        JSONObject data=result.getJSONObject("data");
                        if (data.getString("select").equalsIgnoreCase("image"))
                        {
                            relate_view.setBackgroundResource(R.drawable.white_corner_background);
                            award_image.setVisibility(View.VISIBLE);
                            exo_player.setVisibility(View.GONE);
                            Picasso.with(context).load(Constants.AWARDS_IMAGE_URL+data.getString("image")).placeholder(R.drawable.noimage).into(award_image);
                        }
                        else if (data.getString("select").equalsIgnoreCase("video"))
                        {
                            relate_view.setBackgroundResource(R.drawable.black_corner_background);
                            award_image.setVisibility(View.GONE);
                            exo_player.setVisibility(View.VISIBLE);
                            final String video=Constants.AWARDS_IMAGE_URL+data.getString("video")
                                    .replace(" ","%20");

                            exo_player.setVideoURI(Uri.parse(video));

                            exo_player.setOnPreparedListener(new OnPreparedListener()
                            {
                                @Override
                                public void onPrepared()
                                {
                                    //exo_player.start();
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
                        }
                        else if (data.getString("select").equalsIgnoreCase("description"))
                        {
                            relate_view.setBackgroundResource(R.drawable.white_corner_background);
                            card_view.setVisibility(View.VISIBLE);
                            text1.setText(data.getString("description"));
                        }
                        break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
        System.gc();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.gc();
    }

}
