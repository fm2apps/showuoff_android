package app.shouoff.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

import app.shouoff.Awards;
import app.shouoff.MyContest;
import app.shouoff.MyFollowers;
import app.shouoff.PostDetails;
import app.shouoff.R;
import app.shouoff.SharedPost;
import app.shouoff.adapter.ProfilePostDataAdapter;
import app.shouoff.common.Alerts;
import app.shouoff.common.ConnectionDetector;
import app.shouoff.common.Constants;
import app.shouoff.common.FlingableNestedScrollView;
import app.shouoff.common.SharedPreference;
import app.shouoff.drawer.Drawer;
import app.shouoff.login.EditProfile;
import app.shouoff.model.HomePostModel;
import app.shouoff.model.PostAttachmentModel;
import app.shouoff.model.TagUserData;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class MyProfile extends Drawer implements RetrofitResponse
{
    private RelativeLayout post_relative,fans_relative,contasts_relative;
    private TextView posts,edit_profile,fans,followings;
    private Context context=this;
    private CircleImageView imgProfile;
    private String admire="";
    private RecyclerView profile_list;
    private TextView username,sport_name,post_count,fans_count,contast_count,
            about,winnings,city,error,about_click;
    private RelativeLayout post_details;

    private int page_count=0,value=0,count=0;
    private boolean aBoolean=false;
    private ProfilePostDataAdapter adapter;
    private ArrayList<HomePostModel> postModels=new ArrayList<>();
    private ArrayList<HomePostModel> temp_list=new ArrayList<>();
    private ProgressBar home_post_loader;
    private FlingableNestedScrollView main_view;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        setServiceMyPosts();
        home_post_loader.setVisibility(View.VISIBLE);
    }

    private void initViews()
    {
        main_view=(FlingableNestedScrollView)findViewById(R.id.main_view);
        main_view.smoothScrollTo(0,0);

        post_details=(RelativeLayout)findViewById(R.id.post_details);
        about_click=(TextView)findViewById(R.id.about_click);
        error=(TextView)findViewById(R.id.error);
        home_post_loader=(ProgressBar)findViewById(R.id.home_post_loader);
        profile_list=(RecyclerView)findViewById(R.id.profile_list);
        followings=(TextView)findViewById(R.id.followings);
        city=(TextView)findViewById(R.id.city);
        winnings=(TextView)findViewById(R.id.winnings);
        contasts_relative=(RelativeLayout)findViewById(R.id.contasts_relative);
        imgProfile=(CircleImageView)findViewById(R.id.imgProfile);
        username=(TextView)findViewById(R.id.username);
        fans=(TextView)findViewById(R.id.fans);
        post_count=(TextView)findViewById(R.id.post_count);
        sport_name=(TextView)findViewById(R.id.sport_name);
        fans_count=(TextView)findViewById(R.id.fans_count);
        contast_count=(TextView)findViewById(R.id.contast_count);
        about=(TextView)findViewById(R.id.about);

        title1.setText("My Profile");

        showView(profile_view,search_view,post_view,liked_view,create_view,contests_view,pref_view,winner_view,about_view,support_view);
        ((Drawer)context). showImage(R.drawable.search,
                R.drawable.profile_colored,
                R.drawable.document,
                R.drawable.trophyblack,
                R.drawable.shop,
                R.drawable.contests,
                R.drawable.preferences,
                R.drawable.winners,
                R.drawable.about,
                R.drawable.contact);

        ((Drawer)context).showText(my_profile,my_post,search_user,highly_liked,creat_post1,contest,prefrence,winners,about,contact_support);
        ((Drawer)context).setBottomBar(R.drawable.discovery_gray,
                R.drawable.profile_colored,
                R.drawable.creatpost_gray,
                R.drawable.notification_gray,
                R.drawable.moregray,
                profile,discovery,creat_post,notification,more);
        post_relative=(RelativeLayout)findViewById(R.id.post_relative);
        posts=(TextView)findViewById(R.id.posts);
        edit_profile=(TextView)findViewById(R.id.edit_profile);
        fans_relative=(RelativeLayout)findViewById(R.id.fans_relative);

        posts.setBackgroundResource(R.drawable.corner_background);
        posts.setTextColor(ContextCompat.getColor(context,R.color.white));
        about_click.setBackgroundResource(R.drawable.corner_background_white);
        about_click.setTextColor(ContextCompat.getColor(context,R.color.app_color));

        about.setVisibility(View.GONE);
        post_details.setVisibility(View.VISIBLE);

        setData();
        fans.setOnClickListener(this);
        post_relative.setOnClickListener(this);
        posts.setOnClickListener(this);
        fans_relative.setOnClickListener(this);
        edit_profile.setOnClickListener(this);
        contasts_relative.setOnClickListener(this);
        winnings.setOnClickListener(this);
        followings.setOnClickListener(this);
        about_click.setOnClickListener(this);
    }

    private void setData()
    {
        Drawer.drawer_name.setText(SharedPreference.retriveData(context,Constants.NICK_NAME));
        Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+SharedPreference.retriveData(context,Constants.Image)).placeholder(R.drawable.noimage).into(Drawer.profile_image_drawer);
        username.setText(SharedPreference.retriveData(context,Constants.NICK_NAME));
        Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+SharedPreference.retriveData(context,Constants.Image)).placeholder(R.drawable.noimage).into(imgProfile);
        about.setText(SharedPreference.retriveData(context,Constants.ABOUT));
        sport_name.setText(SharedPreference.retriveData(context,Constants.GAMES_NAME));

        if (SharedPreference.retriveData(context,Constants.CITY).equalsIgnoreCase("empty"))
        {
            city.setVisibility(View.GONE);
        }
        else
        {
            city.setVisibility(View.VISIBLE);
        }

        city.setText(SharedPreference.retriveData(context,Constants.CITY));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fade1, R.anim.fade2);
        new Retrofit2(context,this,7,Constants.CountsOf_post_Fans_contasts+SharedPreference.retriveData(context,Constants.ID))
                .callServiceBack();
        setData();

        /*notify adapter after like/dislike */
        if (SharedPreference.retriveData(context,"tmp")!=null)
        {
            for (int i=0;i<temp_list.size();i++)
            {
                if (SharedPreference.retriveData(context,"article_id")!=null)
                {
                    if (SharedPreference.retriveData(context,"article_id").contains(temp_list.get(i).getId()))
                    {
                        temp_list.get(i).setLike_count(SharedPreference.retriveData(context,"likess"));
                        temp_list.get(i).setLike_status(SharedPreference.retriveData(context,"status"));
                        temp_list.get(i).setShare_count(SharedPreference.retriveData(context,"share_count"));
                        boolean b= Boolean.parseBoolean(SharedPreference.retriveData(context,"like_satsis"));
                        temp_list.get(i).setaBoolean(b);
                        temp_list.get(i).setComment_count(SharedPreference.retriveData(context,"comments"));
                        temp_list.get(i).setDescription(SharedPreference.retriveData(context,"description"));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            SharedPreference.removeKey(context,"tmp");
        }

        /*Delete Post*/
        if (SharedPreference.retriveData(context,"delete_post_id")!=null)
        {
            for (int i=0;i<temp_list.size();i++)
            {
                if (temp_list.get(i).getId().equalsIgnoreCase(SharedPreference.retriveData(context,"delete_post_id")))
                {
                    temp_list.remove(i);
                    adapter.notifyDataSetChanged();
                }
            }
            SharedPreference.removeKey(context,"delete_post_id");
        }
    }

    @Override
    public void onClick(View view)
    {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.post_relative:
                /*if (post_count.getText().toString().equalsIgnoreCase("0"))
                {
                    Alerts.showDialog(context,"",getString(R.string.no_post_added_by_you));
                }
                else
                {
                    startActivity(new Intent(context,MyPost.class)
                            .putExtra("user_id",SharedPreference.retriveData(context,Constants.ID))
                            .putExtra("my","my"));
                }*/
                break;
            case R.id.fans:
            case R.id.fans_relative:
                if (!admire.equalsIgnoreCase("0"))
                {
                    startActivity(new Intent(context, MyFollowers.class).putExtra("user_id",
                            SharedPreference.retriveData(context,Constants.ID)));
                }
                else
                {
                    Alerts.showDialog(context,"",getString(R.string.no_admires_you));
                }
                break;
            case R.id.edit_profile:
            startActivity(new Intent(context, EditProfile.class));
            break;
            case R.id.contasts_relative:
                if (!contast_count.getText().toString().equalsIgnoreCase("0"))
                {
                    startActivity(new Intent(context, MyContest.class)
                    .putExtra("user_id",SharedPreference.retriveData(context,Constants.ID)));
                }
                else
                {
                    Alerts.showDialog(context,"",getString(R.string.no_contest_participated));
                }
                break;
            case R.id.winnings:
                    startActivity(new Intent(context, Awards.class)
                    .putExtra("user_id",SharedPreference.retriveData(context,Constants.ID)));
                break;
            case R.id.followings:
                startActivity(new Intent(context, MyFollowers.class)
                        .putExtra("followings","followings"));
                break;
            case R.id.about_click:
                posts.setBackgroundResource(R.drawable.corner_background_white);
                posts.setTextColor(ContextCompat.getColor(context,R.color.app_color));

                about_click.setBackgroundResource(R.drawable.corner_background);
                about_click.setTextColor(ContextCompat.getColor(context,R.color.white));
                about.setVisibility(View.VISIBLE);
                post_details.setVisibility(View.GONE);
                break;
            case R.id.posts:
                posts.setBackgroundResource(R.drawable.corner_background);
                posts.setTextColor(ContextCompat.getColor(context,R.color.white));

                about_click.setBackgroundResource(R.drawable.corner_background_white);
                about_click.setTextColor(ContextCompat.getColor(context,R.color.app_color));

                about.setVisibility(View.GONE);
                post_details.setVisibility(View.VISIBLE);
                main_view.smoothScrollTo(0,0);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.share_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.shared:
                startActivity(new Intent(context, SharedPost.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                    case 7:
                        admire=result.getJSONObject("data").getString("fan_count");
                        fans_count.setText(admire);
                        contast_count.setText(result.getString("ContastsCount"));
                        post_count.setText(result.getString("PostCount"));
                        if (!result.getJSONObject("data").getString("post_winning_count")
                                .equalsIgnoreCase("0"))
                        {
                            winnings.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            winnings.setVisibility(View.GONE);
                        }
                        break;
                    case 0:
                        home_post_loader.setVisibility(View.GONE);
                        JSONObject data=result.getJSONObject("data");
                        page_count=data.getInt("count");
                        postModels.clear();
                        JSONArray postData=data.getJSONArray("rows");
                        if (postData.length()>0)
                        {
                            for (int i=0;i<postData.length();i++)
                            {
                                JSONObject value=postData.getJSONObject(i);

                                /*Attachments*/
                                JSONArray attach=value.getJSONArray("postAttachment");
                                ArrayList<PostAttachmentModel> models=new ArrayList<>();
                                if (attach.length()>0)
                                {
                                    for (int j=0;j<attach.length();j++)
                                    {
                                        JSONObject attach_value=attach.getJSONObject(j);
                                        PostAttachmentModel attachmentModel=new PostAttachmentModel(attach_value.getString("attachment_name"),
                                                attach_value.getString("attachment_type"),
                                                attach_value.getString("thumbnail"));
                                        models.add(attachmentModel);
                                    }
                                }

                                /*Like Status*/
                                String like_status;
                                if (value.getString("ifuserLike").equalsIgnoreCase("null"))
                                {
                                    like_status="0";
                                }
                                else
                                {
                                    like_status="1";
                                }

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

                                HomePostModel postModel=new HomePostModel(value.getString("id"),
                                        value.getString("user_id"),
                                        value.getString("description"),
                                        value.getString("created_at"),
                                        value.getString("like_count"),
                                        value.getString("comment_count"),
                                        value.getString("share_count"),
                                        value.getJSONObject("postUserDetails").getString("first_name"),
                                        value.getJSONObject("postUserDetails").getString("family_name"),
                                        value.getJSONObject("postUserDetails").getString("image"),
                                        like_status,false,models,"",tagUserData);
                                postModels.add(postModel);
                            }

                            pagination();
                            error.setVisibility(View.GONE);
                            profile_list.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            error.setVisibility(View.VISIBLE);
                            profile_list.setVisibility(View.GONE);
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

    private void setServiceMyPosts()
    {
        JSONObject param=new JSONObject();
        try
        {
            param.put("Offset",String.valueOf(count));
            param.put("user_id",SharedPreference.retriveData(context,Constants.ID));
            new Retrofit2(context,this,0, Constants.perticularUserPost,param).callServiceBack();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // TODO: 19/3/18 Pagination Code
    private void pagination()
    {
        if (value==0)
        {
            aBoolean=true;
            setAdapter(postModels);
            value=1;
            count=count+10;
        }
        else if (value==1)
        {
            if (count<page_count)
            {
                aBoolean=true;
                count=count+10;
                temp_list.addAll(postModels);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void setAdapter(ArrayList<HomePostModel> homePostModels)
    {
        temp_list.clear();
        temp_list.addAll(homePostModels);

        profile_list.setHasFixedSize(true);
        GridLayoutManager glm = new GridLayoutManager(this,3);
        profile_list.setLayoutManager(glm);
        profile_list.setHasFixedSize(true);

        for (int t=0;t<temp_list.size();t++)
        {
            if (temp_list.get(t).getLike_status().equalsIgnoreCase("1"))
            {
                temp_list.get(t).setaBoolean(true);
            }
        }

        adapter=new ProfilePostDataAdapter(context,temp_list);
        profile_list.setAdapter(adapter);
        profile_list.setNestedScrollingEnabled(false);
        adapter.click(new ProfilePostDataAdapter.ItemClick()
        {

            @Override
            public void showProfile(View view, int pos)
            {
                startActivity(new Intent(context, PostDetails.class)
                        .putExtra("post_data",postModels.get(pos)));
            }
        });

        main_view.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener()
        {
            @Override
            public void onScrollChanged()
            {
                View view = (View)main_view.getChildAt(main_view.getChildCount() - 1);
                int diff = (view.getBottom() - (main_view.getHeight() + main_view.getScrollY()));

                if (diff == 0)
                {
                    if (!ConnectionDetector.isInternetAvailable(context))
                    {
                        return;
                    }
                    if (aBoolean)
                    {
                        if (count<page_count)
                        {
                            setServiceMyPosts();
                        }
                        aBoolean=false;
                    }
                }
            }
        });
    }
}
