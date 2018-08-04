package app.shouoff.appsettings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.shouoff.R;
import app.shouoff.common.CommonService;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.drawer.Drawer;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ContactUs extends Drawer implements RetrofitResponse
{
    Context context = this;
    private EditText message;
    private TextView submit;
    InterstitialAd mInterstitialAd;
    private LinearLayout contact_layout;

    /*For Ads*/
    private NestedScrollView ads_scroll;
    private ImageView close_ads,ad_image,play_button;
    private RelativeLayout for_video;
    private ProgressBar play;
    private TextView description,view_link;
    private com.devbrackets.android.exomedia.ui.widget.VideoView video_view;
    private String ad_link="",ad_count="",ad_id="",views="";
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        mInterstitialAd = new InterstitialAd(this);
        handler = new Handler();
        initialized();
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adMobService();
        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    public void onClick(View view)
    {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.submit:
                if (message.getText().toString().trim().isEmpty())
                {
                    return;
                }
                setContactService();
                break;
            case R.id.close_ads:
                if (video_view.isPlaying())
                {
                    video_view.pause();
                    video_view.reset();
                    video_view.stopPlayback();
                }
                contact_layout.setVisibility(View.VISIBLE);
                ads_scroll.setVisibility(View.GONE);
                adMobService();
                handler.postDelayed(new Runnable()
                {
                    public void run()
                    {
                        ads_scroll.setVisibility(View.VISIBLE);
                        contact_layout.setVisibility(View.GONE);
                    }
                }, 55000);
                break;
            case R.id.view_link:
                if (!ad_link.startsWith("http://"))
                {
                    Uri uri= Uri.parse("http://" + ad_link);
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent1);
                }
                else
                {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(ad_link));
                    startActivity(i);
                }

                int count =Integer.valueOf(ad_count)+1;
                new CommonService().clickCount(context,ad_id,String.valueOf(count));
                break;
        }
    }

    private void adMobService()
    {
        new Retrofit2(context,this,11, Constants.getRandomAdvertisement+"contact_us").callService(false);
    }

    private void setContactService()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("query",message.getText().toString());
            object.put("user_id", SharedPreference.retriveData(context,Constants.ID));
            new Retrofit2(context,this,1, Constants.contact_support,object).callService(true);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void initialized()
    {
        contact_layout=(LinearLayout)findViewById(R.id.contact_layout);
        ads_scroll=(NestedScrollView)findViewById(R.id.ads_scroll);
        ads_scroll.smoothScrollTo(0,0);
        close_ads=(ImageView)findViewById(R.id.close_ads);
        ad_image=(ImageView)findViewById(R.id.ad_image);
        play_button=(ImageView)findViewById(R.id.play_button);
        for_video=(RelativeLayout)findViewById(R.id.for_video);
        play=(ProgressBar)findViewById(R.id.play);
        description=(TextView)findViewById(R.id.description);
        view_link=(TextView)findViewById(R.id.view_link);
        video_view = (com.devbrackets.android.exomedia.ui.widget.VideoView)findViewById(R.id.video_view);
        video_view.setHandleAudioFocus(false);
        video_view.setMeasureBasedOnAspectRatioEnabled(true);

        close_ads.setOnClickListener(this);
        play_button.setOnClickListener(this);
        view_link.setOnClickListener(this);


        title1.setText("Contact Us");
        message=(EditText)findViewById(R.id.message_here);
        submit=(TextView)findViewById(R.id.submit);
        ((Drawer)context). showImage(R.drawable.search,
                R.drawable.user,
                R.drawable.document,
                R.drawable.trophyblack,
                R.drawable.shop,
                R.drawable.contests,
                R.drawable.preferences,
                R.drawable.winners,
                R.drawable.about,
                R.drawable.contact_blue);

        submit.setOnClickListener(this);

        ((Drawer)context).showText(contact_support,my_profile,search_user,highly_liked,creat_post1,contest,my_post,winners,about,prefrence);

        showView(support_view,search_view,profile_view,liked_view,create_view,contests_view,post_view,winner_view,about_view,pref_view);
        ((Drawer)context).setBottomBar(R.drawable.discovery_gray, R.drawable.profile_gray, R.drawable.creatpost_gray, R.drawable.notification_gray, R.drawable.more_colord, more, profile, creat_post, notification, discovery);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(new Runnable()
        {
            public void run()
            {
                ads_scroll.setVisibility(View.VISIBLE);
                contact_layout.setVisibility(View.GONE);
            }
        }, 20000);

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
                    case 1:
                        Toast.makeText(context, "Message Send Successfully", Toast.LENGTH_SHORT).show();
                        message.setText("");
                        break;
                    case 11:
                        Log.e("result",""+result.toString());
                        JSONArray array=result.getJSONArray("data");
                        if (array.length()>0)
                        {
                            if (!array.getJSONObject(0).getString("description").equalsIgnoreCase(""))
                            {
                                description.setText(array.getJSONObject(0).getString("description"));
                            }

                            if (array.getJSONObject(0).getString("type").equalsIgnoreCase("video"))
                            {
                                ad_image.setVisibility(View.GONE);
                                for_video.setVisibility(View.VISIBLE);
                                play_button.setVisibility(View.VISIBLE);
                                video_view.setVideoURI(Uri.parse(Constants.ADS_VIDEO + array.getJSONObject(0).getString("attachment")
                                        .replace(" ","%20")));

                                video_view.setOnPreparedListener(new OnPreparedListener()
                                {
                                    @Override
                                    public void onPrepared()
                                    {
                                        play.setVisibility(View.GONE);
                                    }
                                });

                                video_view.setOnCompletionListener(new OnCompletionListener()
                                {
                                    @Override
                                    public void onCompletion()
                                    {
                                        video_view.restart();
                                        //play_button.setVisibility(View.VISIBLE);
                                    }
                                });


                            }
                            else
                            {
                                ad_image.setVisibility(View.VISIBLE);
                                for_video.setVisibility(View.GONE);

                                Picasso.with(context).load(Constants.ADS_VIDEO+
                                        array.getJSONObject(0).getString("attachment")).placeholder(R.drawable.noimage)
                                        .into(ad_image);

                                video_view.pause();
                                video_view.reset();
                                video_view.stopPlayback();
                            }

                            ad_id=array.getJSONObject(0).getString("id");
                            ad_count=array.getJSONObject(0).getString("clicks");
                            ad_link=array.getJSONObject(0).getString("redirecting_link");

                            int views_count=array.getJSONObject(0).getInt("views")+1;
                            new CommonService().clickViews(context,ad_id,String.valueOf(views_count));

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
}
