package app.shouoff.login;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import org.json.JSONObject;
import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class PromoVideo extends AppCompatActivity implements View.OnClickListener,RetrofitResponse
{
    private LinearLayout login_linear,signup_linear;
    private ProgressBar awards_loader;
    private Context context=this;
    private VideoView exo_player;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_video);

        exo_player = (VideoView)findViewById(R.id.video_view);
        exo_player.setHandleAudioFocus(false);

        awards_loader=(ProgressBar)findViewById(R.id.awards_loader);
        login_linear=(LinearLayout)findViewById(R.id.login_linear);
        signup_linear=(LinearLayout)findViewById(R.id.signup_linear);

        login_linear.setOnClickListener(this);
        signup_linear.setOnClickListener(this);

        VideoControls videoControls = exo_player.getVideoControls();
        assert videoControls != null;
        videoControls.setButtonListener(new ControlsListener());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        new Retrofit2(context,this,0, Constants.PromoVideo).callServiceBack();
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
                        final JSONObject data=result.getJSONObject("data");

                        exo_player.setVisibility(View.VISIBLE);
                        final String video=Constants.PROMO_VIDEO_URL+data.getString("video_name").replace(" ","%20");
                        exo_player.setVideoURI(Uri.parse(video));
                        exo_player.setOnPreparedListener(new OnPreparedListener()
                        {
                            @Override
                            public void onPrepared()
                            {
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

                        break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.login_linear:
                startActivity(new Intent(this,Login.class));
                break;
            case R.id.signup_linear:
                SharedPreference.removeKey(context,"username_reg");
                SharedPreference.removeKey(context,"game_reg");
                SharedPreference.removeKey(context,"country_id_reg");
                SharedPreference.removeKey(context,"city_id_reg");
                SharedPreference.removeKey(context,"country_name_reg");
                SharedPreference.removeKey(context,"city_name_reg");
                startActivity(new Intent(this,Register.class));
                break;
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
