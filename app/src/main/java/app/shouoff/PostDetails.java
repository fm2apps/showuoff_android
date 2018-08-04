package app.shouoff;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.shouoff.activity.EditPost;
import app.shouoff.adapter.CommentDetailAdapter;
import app.shouoff.baseadapter.TagUserAdapter;
import app.shouoff.common.Alerts;
import app.shouoff.common.CommonService;
import app.shouoff.common.Constants;
import app.shouoff.common.DataHandler;
import app.shouoff.common.SharedPreference;
import app.shouoff.common.SpaceHashTokenizer;
import app.shouoff.common.SpaceTokenizer;
import app.shouoff.drawer.Drawer;
import app.shouoff.interfaces.DeleteComment;
import app.shouoff.interfaces.DeletePost;
import app.shouoff.mediadata.FullScreenImage;
import app.shouoff.mediadata.PlayVideo;
import app.shouoff.model.CategoriesList;
import app.shouoff.model.CommentListModel;
import app.shouoff.model.HomePostModel;
import app.shouoff.model.PostAttachmentModel;
import app.shouoff.model.SearchDataModel;
import app.shouoff.model.TagUserData;
import app.shouoff.pageradapter.SlidingImage_Adapter;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import app.shouoff.widget.Multi;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class PostDetails extends Drawer implements RetrofitResponse,DeleteComment,DeletePost
{
    Context context = this;
    private RecyclerView comment_list;
    private NestedScrollView nest_scroll;
    private HomePostModel homePostModel;
    private CircleImageView user_image;
    private TextView user,date,post_description,likes,comment,share,likes_test,count,view_more;
    private ImageView like_image,report,block,post_image,video_image,thumbnail,gif_image;
    private ViewPager pager;
    private CommentDetailAdapter adapter;
    private ProgressBar comment_progress;
    Handler handler;

    private ArrayList<CommentListModel> listModels=new ArrayList<>();
    private ArrayList<CommentListModel> sub_list=new ArrayList<>();
    private ArrayList<String> offensive=new ArrayList<>();
    private List<PostAttachmentModel> listModelList=new ArrayList<>();
    private boolean aBoolean=true;
    android.support.v7.app.AlertDialog alertDialog;
    private RelativeLayout header_fav;

    private ArrayList<SearchDataModel> dataModels=new ArrayList<>();
    private ArrayList<SearchDataModel> temp_list=new ArrayList<>();
    private LinkedHashSet<CategoriesList> uniqueStrings = new LinkedHashSet<>();
    private List<CategoriesList> list = new ArrayList<>();
    private ArrayList<String> hashTagList=new ArrayList<>();
    private Multi make_comment;
    private boolean add_delete=true;
    private GestureDetector gestureDetector;

    /*For Ads*/
    private NestedScrollView ads_scroll;
    private ImageView close_ads,ad_image,play_button;
    private RelativeLayout for_video;
    private ProgressBar play;
    private TextView description,view_link;
    private com.devbrackets.android.exomedia.ui.widget.VideoView video_view;
    private String ad_link="",ad_count="",ad_id="",views="";
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        handler = new Handler();
        title1.setText("Post Details");
        initViews();

        homePostModel= (HomePostModel) getIntent().getSerializableExtra("post_data");
        if (homePostModel.getUser_id().equalsIgnoreCase(SharedPreference.retriveData(context,Constants.ID))
                ||homePostModel.getUser_id().equalsIgnoreCase("1"))
        {
            report.setVisibility(View.GONE);
            block.setVisibility(View.GONE);
        }
        else
        {
            report.setVisibility(View.VISIBLE);
            block.setVisibility(View.GONE);
        }
        listModelList=homePostModel.getAttachmentModels();
        setData();
        adMob();
    }

    private void adMob()
    {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        AdRequest adRequest = new AdRequest.Builder()
                .build();

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
            case R.id.send_message:
                if (make_comment.getText().toString().trim().isEmpty())
                {
                    return;
                }
                if (Patterns.WEB_URL.matcher(make_comment.getText().toString()).matches())
                {
                    Toast.makeText(context, "Links not allowed", Toast.LENGTH_SHORT).show();
                    return;
                }
                setServiceComment();
                break;
            case R.id.view_more:
                 startActivity(new Intent(context,CommentList.class)
                .putExtra("post_id",homePostModel.getId())
                .putExtra("user_id",homePostModel.getUser_id())
                .putExtra("commentList",listModels));
                break;
            case R.id.likes_lay:
                SharedPreference.storeData(context,"tmp","tmp");
                if (aBoolean)
                {
                    aBoolean=false;
                    new CommonService().articleLike(homePostModel.getId(),context);
                    like_image.setImageResource(R.drawable.thumb_gold);

                    String data=homePostModel.getLike_count();
                    int j= Integer.parseInt(data)+1;
                    likes.setText(String.valueOf(j)+" Likes");
                    homePostModel.setLike_count(String.valueOf(j));
                    homePostModel.setaBoolean(true);
                    homePostModel.setLike_status("1");
                }
                else
                {
                    aBoolean=true;
                    new CommonService().articleDislike(homePostModel.getId(),context);
                    like_image.setImageResource(R.drawable.thumbup_grey);

                    String data=homePostModel.getLike_count();
                    int i = Integer.parseInt(data)-1;
                    likes.setText(String.valueOf(i)+" Likes");
                    homePostModel.setLike_count(String.valueOf(i));
                    homePostModel.setaBoolean(false);
                    homePostModel.setLike_status("0");
                }
                break;
            case R.id.report:
                showAlertForReport();
                break;
            case R.id.share_lay:
                startActivityForResult(new Intent(context,SharePostActivity.class),100);
                break;
            case R.id.header_fav:
                startActivityForResult(new Intent(context, UserProfile.class)
                        .putExtra("profile_id",homePostModel.getUser_id()),300);
                break;
            case R.id.block:
                showDialogBlock();
                break;
            case R.id.likes:
                if (!homePostModel.getLike_count().equalsIgnoreCase("0"))
                {
                    startActivity(new Intent(context, PostLikedUsers.class)
                            .putExtra("post_id",""+homePostModel.getId()));
                }
                break;
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
                nest_scroll.setVisibility(View.VISIBLE);
                adMobService();
                handler.postDelayed(new Runnable()
                {
                    public void run()
                    {
                        ads_scroll.setVisibility(View.VISIBLE);
                        nest_scroll.setVisibility(View.GONE);
                    }
                }, 60000);
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

    public class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onDown(MotionEvent e)
        {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e)
        {
            SharedPreference.storeData(context,"tmp","tmp");
            if (aBoolean)
            {
                aBoolean=false;
                new CommonService().articleLike(homePostModel.getId(),context);
                like_image.setImageResource(R.drawable.thumb_gold);

                String data=homePostModel.getLike_count();
                int j= Integer.parseInt(data)+1;
                likes.setText(String.valueOf(j)+" Likes");
                homePostModel.setLike_count(String.valueOf(j));
                homePostModel.setaBoolean(true);
                homePostModel.setLike_status("1");
            }
            else
            {
                aBoolean=true;
                new CommonService().articleDislike(homePostModel.getId(),context);
                like_image.setImageResource(R.drawable.thumbup_grey);

                String data=homePostModel.getLike_count();
                int i = Integer.parseInt(data)-1;
                likes.setText(String.valueOf(i)+" Likes");
                homePostModel.setLike_count(String.valueOf(i));
                homePostModel.setaBoolean(false);
                homePostModel.setLike_status("0");
            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            if (listModelList.get(0).getAttachment_type().equalsIgnoreCase("video")||
                    listModelList.get(0).getAttachment_type().equalsIgnoreCase("gif"))
            {
                startActivity(new Intent(context, PlayVideo.class)
                        .putExtra("attachment_file",listModelList.get(0).getAttachment_name())
                        .putExtra("attachment",listModelList.get(0).getThumbnail()));
            }
            else
            {
                startActivity(new Intent(context, FullScreenImage.class)
                        .putExtra("image",listModelList.get(0).getAttachment_name()));
            }
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100 && resultCode==101)
        {
            ArrayList<SearchDataModel> selected = (ArrayList<SearchDataModel>) data.getSerializableExtra("user_list");

            StringBuilder builder=new StringBuilder();
            for (int i = 0; i< selected.size(); i++)
            {
                builder.append(selected.get(i).getId());
                builder.append(",");
            }

             /*Set service to share post */
            new CommonService().setServiceShare(String.valueOf(builder).substring(0,String.valueOf(builder).length()-1)
                    ,homePostModel.getId(),context,6,this);
        }
        else if (requestCode==200 && resultCode==201)
        {
            SharedPreference.storeData(context,"tmp","tmp");
            homePostModel.setDescription(data.getStringExtra("description"));
            post_description.setText(homePostModel.getDescription());
        }
        else if (requestCode==300 && resultCode==301)
        {
            Intent intent1=new Intent();
            intent1.putExtra("user_id",homePostModel.getUser_id());
            setResult(601,intent1);
            finish();
        }
    }

    private void showAlertForReport()
    {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.custom_alert_dialog);
        LayoutInflater inflater=this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.report_alert_layout, null);
        final EditText report_message=(EditText) dialogView.findViewById(R.id.report_message);

        TextView report_text=(TextView)dialogView.findViewById(R.id.report_text);
        report_text.setText(R.string.report_post);
        TextView report=(TextView)dialogView.findViewById(R.id.report_submit);

        report.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (report_message.getText().toString().trim().isEmpty())
                {
                    return;
                }
                setServiceReport(report_message.getText().toString());
                alertDialog.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setLayout(width, height);
        alertDialog.show();
    }

    private void adMobService()
    {
        new Retrofit2(context,this,11,Constants.getRandomAdvertisement+"post_detail").callService(false);
    }

    private void setServiceReport(String message)
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("post_id",homePostModel.getId());
            object.put("user_id", SharedPreference.retriveData(context,Constants.ID));
            object.put("message",message);
            new Retrofit2(context,this,2, Constants.Post_report_byUser,object).callService(true);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void setServiceComment()
    {
        for (int i=0;i<offensive.size();i++)
        {
            if (make_comment.getText().toString().toLowerCase().matches(offensive.get(i).toLowerCase()))
            {
                Alerts.showDialog(context,"",getString(R.string.not_alow_to_give_comment));
                return;
            }
        }

        JSONObject object=new JSONObject();
        try
        {
            object.put("post_id",homePostModel.getId());
            object.put("user_id", SharedPreference.retriveData(context,Constants.ID));
            object.put("description",make_comment.getText().toString());
            object.put("tagged_by", SharedPreference.retriveData(context,Constants.ID));
            JSONArray data=new JSONArray();
            for (int i = 0; i < list.size(); i++)
            {
                if (!SharedPreference.retriveData(context,Constants.ID).equalsIgnoreCase(list.get(i).getId()))
                {
                    data.put(list.get(i).getId());
                }
            }
            object.put("tagged_userId",data);
            new Retrofit2(context,this,1, Constants.createcomment,object).callService(true);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void setServiceCommentList()
    {
        new Retrofit2(context,this,0,Constants.perticular_post+
                homePostModel.getId()).callServiceBack();
    }

    private void setData()
    {
        /*Tag Data*/
        DataHandler.tagdata(context,post_description,homePostModel);

        date.setText(Constants.date(homePostModel.getCreated_at()));

        if (homePostModel.getDescription().equalsIgnoreCase("")||
                homePostModel.getDescription().equalsIgnoreCase("null"))
        {
            post_description.setVisibility(View.GONE);
        }
        else
        {
            post_description.setVisibility(View.VISIBLE);
        }

        user.setText(homePostModel.getFirst_name());

        Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+homePostModel.getImage()).
                placeholder(R.drawable.noimage).into(user_image);

        Log.e("homePostModel","->>>>>>>"+homePostModel.getLike_count());
        likes.setText(homePostModel.getLike_count()+" Likes");
        comment.setText(homePostModel.getComment_count()+" Comments");
        share.setText(homePostModel.getShare_count()+" Share");

        if (homePostModel.getLike_status().equalsIgnoreCase("0"))
        {
            aBoolean=true;
            like_image.setImageResource(R.drawable.thumbup_grey);
        }
        else
        {
            aBoolean=false;
            like_image.setImageResource(R.drawable.thumb_gold);
        }
        
        setAttachment();
        //setImageAdapter();

        if (!homePostModel.getUser_id().equalsIgnoreCase(SharedPreference.retriveData(context,Constants.ID)))
        {
            header_fav.setOnClickListener(this);
        }

    }
    
    private void setAttachment()
    {
        /*Post Image*/
        if (homePostModel.getAttachmentModels().size()>0)
        {
            if (homePostModel.getAttachmentModels().get(0).getAttachment_type().equalsIgnoreCase("video"))
            {
                video_image.setVisibility(View.VISIBLE);
                thumbnail.setVisibility(View.VISIBLE);
                post_image.setVisibility(View.GONE);
                gif_image.setVisibility(View.GONE);
                Picasso.with(context).load(Constants.POST_URL+homePostModel.getAttachmentModels()
                        .get(0).getThumbnail())
                        .placeholder(R.drawable.noimage).into(thumbnail);

            }
            else if (homePostModel.getAttachmentModels().get(0).getAttachment_type().equalsIgnoreCase("gif"))
            {

                video_image.setVisibility(View.VISIBLE);
                thumbnail.setVisibility(View.GONE);
                post_image.setVisibility(View.GONE);
                gif_image.setVisibility(View.VISIBLE);
                Picasso.with(context).load(Constants.POST_URL+homePostModel.getAttachmentModels()
                        .get(0).getThumbnail())
                        .placeholder(R.drawable.noimage).into(gif_image);

            }
            else
            {
                video_image.setVisibility(View.GONE);
                thumbnail.setVisibility(View.GONE);
                post_image.setVisibility(View.VISIBLE);
                gif_image.setVisibility(View.GONE);
                Picasso.with(context).load(Constants.POST_URL+homePostModel.getAttachmentModels()
                        .get(0).getAttachment_name())
                        .placeholder(R.drawable.noimage).into(post_image);
            }
        }
        else
        {
            video_image.setVisibility(View.GONE);
            post_image.setVisibility(View.GONE);
            thumbnail.setVisibility(View.GONE);
            gif_image.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (getIntent().hasExtra("from_comp"))
        {
            return true;
        }
        if (getIntent().hasExtra("from_award"))
        {
            return true;
        }
        if (homePostModel.getUser_id().equalsIgnoreCase(SharedPreference.retriveData(context,Constants.ID)))
        {
            getMenuInflater().inflate(R.menu.post_menu,menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.edit:
                startActivityForResult(new Intent(context,EditPost.class)
                .putExtra("post_data",homePostModel),200);
                return true;
            case R.id.delete:
                Alerts.showDialogDeletePost(context,this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*Delete Post*/
    private void deletePost()
    {
        new Retrofit2(context,this,40,Constants.Post_delete+homePostModel.getId()).callServiceBack();
    }

    private void setImageAdapter()
    {
        SlidingImage_Adapter image_adapter = new SlidingImage_Adapter(context, listModelList);
        pager.setAdapter(image_adapter);
        image_adapter.click(new SlidingImage_Adapter.doubleTap() {
            @Override
            public void show(View view, int pos)
            {
                try
                {
                    if (listModelList.get(pos).getAttachment_type().equalsIgnoreCase("video")||
                            listModelList.get(pos).getAttachment_type().equalsIgnoreCase("gif"))
                    {
                        startActivity(new Intent(context, PlayVideo.class)
                                .putExtra("attachment_file",listModelList.get(pos).getAttachment_name())
                                .putExtra("attachment",listModelList.get(pos).getThumbnail()));
                    }
                    else
                    {
                        startActivity(new Intent(context, FullScreenImage.class)
                                .putExtra("image",listModelList.get(pos).getAttachment_name()));
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void like()
            {
                SharedPreference.storeData(context,"tmp","tmp");
                if (aBoolean)
                {
                    aBoolean=false;
                    new CommonService().articleLike(homePostModel.getId(),context);
                    like_image.setImageResource(R.drawable.thumb_gold);

                    String data=homePostModel.getLike_count();
                    int j= Integer.parseInt(data)+1;
                    likes.setText(String.valueOf(j)+" Likes");
                    homePostModel.setLike_count(String.valueOf(j));
                    homePostModel.setaBoolean(true);
                    homePostModel.setLike_status("1");
                }
                else
                {
                    aBoolean=true;
                    new CommonService().articleDislike(homePostModel.getId(),context);
                    like_image.setImageResource(R.drawable.thumbup_grey);

                    String data=homePostModel.getLike_count();
                    int i = Integer.parseInt(data)-1;
                    likes.setText(String.valueOf(i)+" Likes");
                    homePostModel.setLike_count(String.valueOf(i));
                    homePostModel.setaBoolean(false);
                    homePostModel.setLike_status("0");
                }
            }
        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                count.setText(String.valueOf(listModelList.size())+"/"+(pager.getCurrentItem()+1));
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

        pager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        count.setText(String.valueOf(listModelList.size())+"/1");
    }

    @SuppressLint("ClickableViewAccessibility")
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


        gif_image=(ImageView)findViewById(R.id.gif_image);
        gestureDetector = new GestureDetector(context,new GestureListener());

        thumbnail=(ImageView)findViewById(R.id.thumbnail);
        post_image=(ImageView)findViewById(R.id.post_image);
        video_image=(ImageView)findViewById(R.id.video_image);
        block=(ImageView)findViewById(R.id.block);
        header_fav=(RelativeLayout)findViewById(R.id.header_fav);
        report=(ImageView)findViewById(R.id.report);

        LinearLayout likes_lay = (LinearLayout) findViewById(R.id.likes_lay);
        comment_list=(RecyclerView)findViewById(R.id.comment_list);
        view_more=(TextView)findViewById(R.id.view_more);
        comment_progress=(ProgressBar)findViewById(R.id.comment_progress);
        LinearLayout send_message = (LinearLayout) findViewById(R.id.send_message);
        make_comment=(Multi) findViewById(R.id.make_comment);
        count=(TextView)findViewById(R.id.count);
        pager=(ViewPager)findViewById(R.id.pager);
        user_image=(CircleImageView)findViewById(R.id.user_image);
        user=(TextView)findViewById(R.id.user);
        date=(TextView)findViewById(R.id.date);
        post_description=(TextView)findViewById(R.id.post_description);
        likes=(TextView)findViewById(R.id.likes);
        comment=(TextView)findViewById(R.id.comment);
        share=(TextView)findViewById(R.id.share);
        likes_test=(TextView)findViewById(R.id.likes_test);

        like_image=(ImageView)findViewById(R.id.like_image);

        LinearLayout share_lay = (LinearLayout) findViewById(R.id.share_lay);

        post_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return gestureDetector.onTouchEvent(event);
            }
        });

        thumbnail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return gestureDetector.onTouchEvent(event);
            }
        });

        gif_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return gestureDetector.onTouchEvent(event);
            }
        });

        nest_scroll=(NestedScrollView)findViewById(R.id.nest_scroll);
        nest_scroll.smoothScrollTo(0,0);
        ((Drawer)context).setBottomBar(R.drawable.discovery_gray,
                R.drawable.profile_gray,
                R.drawable.creatpost_gray,
                R.drawable.notification_gray,
                R.drawable.more_colord, more, profile, creat_post, notification, discovery);

        make_comment.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId != 0 || event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    Constants.hideKeyboard(context,v);
                    if (make_comment.getText().toString().trim().isEmpty())
                    {
                        return false;
                    }

                    setServiceComment();
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        make_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.toString().length()>0)
                {
                    if ((s.charAt(s.length() - 1))=='@')
                    {
                        setAdapterTag(dataModels);

                    }
                    else if ((s.charAt(s.length() - 1))=='#')
                    {
                        dataModels.clear();
                        dataModels.addAll(temp_list);
                        setAdapterHashTag();
                    }
                    else
                    {
                       // dataModels.clear();
                       // dataModels.addAll(temp_list);
                    }
                }
            }
        });

        send_message.setOnClickListener(this);
        view_more.setOnClickListener(this);
        likes_lay.setOnClickListener(this);
        report.setOnClickListener(this);
        share_lay.setOnClickListener(this);
        block.setOnClickListener(this);
        post_image.setOnClickListener(this);
        gif_image.setOnClickListener(this);
        likes.setOnClickListener(this);
        close_ads.setOnClickListener(this);
        play_button.setOnClickListener(this);
        view_link.setOnClickListener(this);
    }

    private void comment_list()
    {
        Collections.reverse(listModels);
        sub_list.clear();
        if (listModels.size()>6)
        {
            sub_list.addAll(listModels.subList(0,6));
            view_more.setVisibility(View.VISIBLE);
        }
        else
        {
            sub_list.addAll(listModels);
            view_more.setVisibility(View.GONE);
        }
        comment_list.setHasFixedSize(true);
        comment_list.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CommentDetailAdapter(context,sub_list,homePostModel.getUser_id());
        comment_list.setAdapter(adapter);
        comment_list.setNestedScrollingEnabled(false);
        adapter.click(new CommentDetailAdapter.DeleteCmt()
        {
            @Override
            public void select(View view, int pos)
            {
                deleteComment(pos);
            }
        });
    }

    /*Delete Comment Alert to confirm*/
    private void deleteComment(int pos)
    {
        Alerts.showDialog(context,pos,this,"");
    }

    /*Delete Comment Service*/
    private void setServiceDeleteComment(int pos)
    {
        new CommonService().setServiceDeleteComment(listModels.get(pos).getId(),homePostModel.getId(),this,context);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fade1,R.anim.fade2);
        setServiceCommentList();
        handler.postDelayed(new Runnable()
        {
            public void run()
            {
                ads_scroll.setVisibility(View.VISIBLE);
                nest_scroll.setVisibility(View.GONE);
            }
        }, 25000);
        comment_progress.setVisibility(View.VISIBLE);
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
                        comment_progress.setVisibility(View.GONE);
                        listModels.clear();
                        JSONArray data=result.getJSONArray("data");
                        if (data.length()>0)
                        {
                            for (int i=0;i<data.length();i++)
                            {
                                JSONObject value=data.getJSONObject(i);

                                /*Tagged User data*/
                                ArrayList<TagUserData> tagUserData=new ArrayList<>();
                                JSONArray tag=value.getJSONArray("taggedUser");
                                if (tag.length()>0)
                                {
                                    for (int j = 0; j < tag.length(); j++)
                                    {
                                        JSONObject tagData=tag.getJSONObject(j);

                                        if (!tagData.getString("tagedUserDetails").equalsIgnoreCase("null"))
                                        {
                                            TagUserData userData=new TagUserData(tagData.getJSONObject("tagedUserDetails")
                                                    .getString("id"),
                                                    tagData.getJSONObject("tagedUserDetails")
                                                            .getString("username"),
                                                    tagData.getJSONObject("tagedUserDetails")
                                                            .getString("nick_name"));
                                            tagUserData.add(userData);
                                        }
                                    }
                                }

                                CommentListModel listModel=new CommentListModel(value.getString("id"),
                                        value.getString("user_id"),
                                        value.getString("description"),
                                        value.getString("created_at"),
                                        value.getJSONObject("commentUserinfo").getString("nick_name"),
                                        value.getJSONObject("commentUserinfo").getString("family_name"),
                                        value.getJSONObject("commentUserinfo").getString("image"),tagUserData);
                                listModels.add(listModel);
                            }
                        }
                        comment.setText(listModels.size()+" Comments");
                        homePostModel.setComment_count(String.valueOf(listModels.size()));
                        SharedPreference.storeData(context,"tmp","tmp");
                        comment_list();

                        if (add_delete)
                        {
                            new Retrofit2(context,this,5, Constants.offensive_Words).callServiceBack();
                        }
                        break;
                    case 1:
                        comment_progress.setVisibility(View.VISIBLE);
                        setServiceCommentList();
                        nest_scroll.scrollTo(0, view_more.getBottom());
                        make_comment.setText("");
                        list.clear();
                        break;
                    case 2:
                        Alerts.showDialog(context,"",result.getString("message"));
                        break;
                    case 33:
                        setServiceCommentList();
                        break;
                    case 5:
                        add_delete=false;
                        offensive.clear();
                        JSONArray off=result.getJSONArray("data");
                        if (off.length()>0)
                        {
                            for (int i=0;i<off.length();i++)
                            {
                                JSONObject value=off.getJSONObject(i);
                                offensive.add(value.getString("word"));
                            }
                        }
                        setService();
                        break;
                    case 6:
                        SharedPreference.storeData(context,"tmp","tmp");
                        String temp=homePostModel.getShare_count();
                        int j= Integer.parseInt(temp)+1;
                        share.setText(String.valueOf(j)+" Share");
                        homePostModel.setShare_count(String.valueOf(j));
                        break;
                    case 4:
                        dataModels.clear();
                        temp_list.clear();
                        JSONArray data1=result.getJSONArray("data");
                        if (data1.length()>0)
                        {
                            for (int i=0;i<data1.length();i++)
                            {
                                JSONObject users=data1.getJSONObject(i);
                                SearchDataModel searchDataModel=new SearchDataModel(users.getString("id"),
                                        users.getString("first_name")+" "+users.getString("family_name"),
                                        users.getString("family_name"),
                                        users.getString("email"),
                                        users.getString("image"),
                                        users.getString("username"),
                                        users.getString("nick_name"));

                                dataModels.add(searchDataModel);
                            }
                            temp_list.addAll(dataModels);
                        }
                        new Retrofit2(context,this,7, Constants.Hash_Words).callServiceBack();
                    case 7:
                        hashTagList.clear();
                        JSONArray tag=result.getJSONArray("data");
                        if (tag.length()>0)
                        {
                            for (int i = 0; i < tag.length(); i++) {
                                JSONObject tagData=tag.getJSONObject(i);
                                hashTagList.add(tagData.getString("word"));
                            }
                        }
                        adMobService();
                        break;
                    case 10:
                        Intent intent1=new Intent();
                        intent1.putExtra("user_id",homePostModel.getUser_id());
                        setResult(601,intent1);
                        finish();
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

    private void setService()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("user_id",SharedPreference.retriveData(context,Constants.ID));
            new Retrofit2(context,this,4, Constants.listUsers,object).callServiceBack();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (video_view.isPlaying())
        {
            video_view.pause();
            video_view.reset();
        }
        if (SharedPreference.retriveData(context,"tmp")!=null)
        {
            finish();
            SharedPreference.storeData(context,"status",homePostModel.getLike_status());
            SharedPreference.storeData(context,"likess",homePostModel.getLike_count());
            SharedPreference.storeData(context,"article_id",homePostModel.getId());
            SharedPreference.storeData(context,"share_count",homePostModel.getShare_count());
            SharedPreference.storeData(context,"like_satsis", String.valueOf(homePostModel.isaBoolean()));
            SharedPreference.storeData(context,"comments", homePostModel.getComment_count());
            SharedPreference.storeData(context,"description", homePostModel.getDescription());
        }
    }

    @Override
    public void delete(View view, int pos)
    {
        setServiceDeleteComment(pos);
        sub_list.remove(pos);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void deleteService(View view)
    {
        SharedPreference.storeData(context,"delete_post_id",homePostModel.getId());
        deletePost();
        finish();
    }

    private void setAdapterTag(final ArrayList<SearchDataModel> dataModelss)
    {
        TagUserAdapter userAdapter = new TagUserAdapter(context, R.layout.tag_user_adapter, dataModelss);
        make_comment.setAdapter(userAdapter);
        make_comment.setTokenizer(new SpaceTokenizer());
        make_comment.setThreshold(1);

        make_comment.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String tag_id = dataModelss.get(position).getId();
                String tag_name = dataModelss.get(position).getUser_name();
                uniqueStrings.add(new CategoriesList(tag_id,tag_name));
                list = new ArrayList<>(uniqueStrings);
                dataModels.clear();
                dataModels.addAll(temp_list);
            }
        });
    }

    private void setAdapterHashTag()
    {
        ArrayAdapter<String> hashadapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, hashTagList);
        make_comment.setAdapter(hashadapter);
        make_comment.setThreshold(1);
        make_comment.setTokenizer(new SpaceHashTokenizer());
        make_comment.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

            }
        });
    }

    private void setServiceBlock()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("blocked_to",homePostModel.getUser_id());
            object.put("blocked_by", SharedPreference.retriveData(context,Constants.ID));
            new Retrofit2(context,this,10,Constants.USER_BLOCK,object).callService(true);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /*Alert to delete post*/
    public void showDialogBlock()
    {
        TextView txt_msg, ok,cancel;
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.custom_alert_dialog);
        LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.block_alert_message, null);
        cancel = (TextView) dialogView.findViewById(R.id.cancel);
        txt_msg = (TextView) dialogView.findViewById(R.id.message);
        txt_msg.setText("Are you sure you want to block this user?");
        ok = (TextView) dialogView.findViewById(R.id.submit);
        dialogBuilder.setView(dialogView);
        final android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.80);
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setLayout(width, height);
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                setServiceBlock();
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (video_view.isPlaying())
        {
            video_view.pause();
        }
    }
}
