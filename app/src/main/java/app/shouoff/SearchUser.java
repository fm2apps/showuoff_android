package app.shouoff;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import app.shouoff.adapter.SearchPostAdapter;
import app.shouoff.common.CommonService;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.drawer.Drawer;
import app.shouoff.login.MyProfile;
import app.shouoff.model.SearchDataModel;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class SearchUser extends Drawer implements RetrofitResponse
{
    Context context = this;
    public static RecyclerView recycler;
    private ArrayList<SearchDataModel> dataModels=new ArrayList<>();
    private SearchPostAdapter adapter;
    private EditText search_data;
    public static TextView error;
    private ProgressBar data_progress;

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
        setContentView(R.layout.activity_search_posts);
        System.gc();
        title1.setText(R.string.search_data);
        handler = new Handler();
        recycler = (RecyclerView) findViewById(R.id.recycler);
        initViews();

        setSupportActionBar(toolbar1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        back_btn.setVisibility(View.VISIBLE);

        setService();
    }

    private void setService()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("user_id",SharedPreference.retriveData(context,Constants.ID));
            new Retrofit2(context,this,0, Constants.listUsers,object).callServiceBack();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void initViews()
    {
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

        search_data=(EditText)findViewById(R.id.search_data);
        error=(TextView)findViewById(R.id.error);
        data_progress=(ProgressBar)findViewById(R.id.data_progress);
        search_data.setEnabled(false);
        search_data.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                adapter.filterSnd(editable.toString());
            }
        });


        search_data.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId != 0 || event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    Constants.hideKeyboard(context,v);
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        recycler = (RecyclerView) findViewById(R.id.recycler);
        ((Drawer)context). showImage(R.drawable.search_blue,
                R.drawable.user,
                R.drawable.document,
                R.drawable.trophyblack,
                R.drawable.shop,
                R.drawable.contests,
                R.drawable.preferences,
                R.drawable.winners,
                R.drawable.about,
                R.drawable.contact);
        ((Drawer)context).showText(search_user,my_profile,my_post,highly_liked,creat_post1,contest,prefrence,winners,about,contact_support);

        showView(search_view,profile_view,profile_view,liked_view,create_view,contests_view,pref_view,winner_view,about_view,support_view);
        ((Drawer)context).setBottomBar(R.drawable.discovery_gray,
                R.drawable.profile_gray,
                R.drawable.creatpost_gray,
                R.drawable.notification_gray,
                R.drawable.more_colord, more, profile, creat_post, notification,discovery);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.play_button:
                video_view.start();
                play_button.setVisibility(View.GONE);
                play.setVisibility(View.GONE);
                break;
            case R.id.close_ads:
                if (video_view.isPlaying())
                {
                    video_view.pause();
                    video_view.reset();
                    video_view.stopPlayback();
                }
                ads_scroll.setVisibility(View.GONE);
                adMobService();
                handler.postDelayed(new Runnable()
                {
                    public void run()
                    {
                        ads_scroll.setVisibility(View.VISIBLE);
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
        new Retrofit2(context,this,11, Constants.getRandomAdvertisement+"search_user").callService(false);
    }

    private void setAdapter()
    {
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(context));
        adapter = new SearchPostAdapter(context,dataModels);
        recycler.setAdapter(adapter);
        adapter.click(new SearchPostAdapter.SelectUser()
        {
            @Override
            public void show(View view, int pos)
            {
                try {
                    if (!dataModels.get(pos).getId().equalsIgnoreCase(SharedPreference.retriveData(context,Constants.ID)))
                    {
                        startActivityForResult(new Intent(context, UserProfile.class)
                                .putExtra("fromSearch","fromSearch")
                                .putExtra("profile_id",dataModels.get(pos).getId()),504);
                    }
                    else
                    {
                        context.startActivity(new Intent(context, MyProfile.class));
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fade1, R.anim.fade2);

        handler.postDelayed(new Runnable()
        {
            public void run()
            {
                ads_scroll.setVisibility(View.VISIBLE);
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
                    case 0:
                        data_progress.setVisibility(View.GONE);
                        dataModels.clear();
                        JSONArray data=result.getJSONArray("data");
                        if (data.length()>0)
                        {
                            for (int i=0;i<data.length();i++)
                            {
                                JSONObject users=data.getJSONObject(i);


                                SearchDataModel searchDataModel=new SearchDataModel(users.getString("id"),
                                        users.getString("first_name")+" "+users.getString("family_name"),
                                        users.getString("family_name"),
                                        users.getString("email"),
                                        users.getString("image"),
                                        users.getString("username"),
                                        users.getString("nick_name"));

                                dataModels.add(searchDataModel);
                            }
                            search_data.setEnabled(true);
                            setAdapter();
                        }
                        adMobService();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==504 && resultCode==505)
        {
            for (int i = 0; i < dataModels.size(); i++)
            {
                if (dataModels.get(i).getId().equalsIgnoreCase(data.getStringExtra("profile_id")))
                {
                    dataModels.remove(i);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
