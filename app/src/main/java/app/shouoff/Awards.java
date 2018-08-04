package app.shouoff;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import app.shouoff.adapter.AwardsAdapter;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.drawer.Drawer;
import app.shouoff.model.HomePostModel;
import app.shouoff.model.PostAttachmentModel;
import app.shouoff.model.TagUserData;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class Awards extends Drawer implements RetrofitResponse
{
    private RecyclerView awards_list;
    private ArrayList<HomePostModel> postModels=new ArrayList<>();
    Context context = this;
    private ProgressBar home_post_loader;
    private SwipeRefreshLayout swipeContainer;
    private TextView error;
    private AwardsAdapter adapter;
    private LinearLayout awards;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awards);

        awards_list=(RecyclerView)findViewById(R.id.recycler);
        title1.setText("Winners");
        initViews();
        home_post_loader.setVisibility(View.VISIBLE);

        if (getIntent().hasExtra("user_id"))
        {
            setMyAwardsService();
            awards.setVisibility(View.GONE);
        }
        else
        {
            awards.setVisibility(View.VISIBLE);
            setService();
        }
    }

    /*Monthly Awards*/
    private void setService()
    {
        new Retrofit2(context,this,1, Constants.MonthlyWinner+
                SharedPreference.retriveData(context,Constants.ID),"").callService(false);
    }

    /*Monthly Awards*/
    private void setMyAwardsService()
    {
        new Retrofit2(context,this,1, Constants.User_winningDetails+getIntent().
                getStringExtra("user_id"),"").callService(false);
    }

    private void initViews()
    {
        awards=(LinearLayout)findViewById(R.id.awards);
        error=(TextView)findViewById(R.id.error);
        home_post_loader=(ProgressBar)findViewById(R.id.home_post_loader);

        swipeContainer=(SwipeRefreshLayout)findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.app_color,R.color.black,R.color.color_gray);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                setService();
                swipeContainer.setRefreshing(true);
            }
        });

        ((Drawer)context). showImage(R.drawable.search,
                R.drawable.user,
                R.drawable.document,
                R.drawable.trophyblack,
                R.drawable.shop,
                R.drawable.contests,
                R.drawable.preferences,
                R.drawable.winners_blue,
                R.drawable.about,
                R.drawable.contact);
        ((Drawer)context).showText(winners,my_post,search_user,my_profile,highly_liked,creat_post1,prefrence,contest,about,contact_support);

        showView(winner_view,search_view,profile_view,liked_view,create_view,contests_view,pref_view,post_view,about_view,support_view);
        ((Drawer)context).setBottomBar(R.drawable.discovery_gray, R.drawable.profile_gray, R.drawable.creatpost_gray, R.drawable.notification_gray, R.drawable.more_colord, more, profile, creat_post, notification, discovery);
    }

    private void setAdapter()
    {
        awards_list.setHasFixedSize(true);
        GridLayoutManager glm = new GridLayoutManager(this,3);
        awards_list.setLayoutManager(glm);
        adapter = new AwardsAdapter(context,postModels);
        awards_list.setAdapter(adapter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fade1, R.anim.fade2);

        /*notify adapter after like/dislike */
        if (SharedPreference.retriveData(context,"tmp")!=null)
        {
            for (int i=0;i<postModels.size();i++)
            {
                if (SharedPreference.retriveData(context,"article_id")!=null)
                {
                    if (SharedPreference.retriveData(context,"article_id").contains(postModels.get(i).getId()))
                    {
                        postModels.get(i).setLike_count(SharedPreference.retriveData(context,"likess"));
                        postModels.get(i).setLike_status(SharedPreference.retriveData(context,"status"));
                        postModels.get(i).setShare_count(SharedPreference.retriveData(context,"share_count"));
                        boolean b= Boolean.parseBoolean(SharedPreference.retriveData(context,"like_satsis"));
                        postModels.get(i).setaBoolean(b);
                        postModels.get(i).setComment_count(SharedPreference.retriveData(context,"comments"));
                    }
                }
                adapter.notifyDataSetChanged();
            }
            SharedPreference.removeKey(context,"tmp");
        }
    }

    @Override
    public void onServiceResponse(int requestCode, Response<ResponseBody> response)
    {
        try
        {
            swipeContainer.setRefreshing(false);
            home_post_loader.setVisibility(View.GONE);
            JSONObject result=new JSONObject(response.body().string().toString());
            boolean status=result.getBoolean("response");
            if (status)
            {
                switch (requestCode)
                {
                    case 1:
                        postModels.clear();
                        JSONArray postData=result.getJSONArray("data");
                        if (postData.length()>0)
                        {
                            for (int i=0;i<postData.length();i++)
                            {
                                JSONObject value=postData.getJSONObject(i);
                                HomePostModel postModel = null;

                                if (!value.getString("winning_post_detail").equalsIgnoreCase("null"))
                                {
                                    JSONObject awardValue=value.getJSONObject("winning_post_detail");

                                    /*Attachments*/
                                    JSONArray attach=awardValue.getJSONArray("postAttachment");
                                    ArrayList<PostAttachmentModel> models=new ArrayList<>();
                                    if (attach.length()>0)
                                    {
                                        for (int j=0;j<attach.length();j++)
                                        {
                                            JSONObject attach_value=attach.getJSONObject(j);
                                            PostAttachmentModel attachmentModel=new PostAttachmentModel(attach_value.getString("attachment_name"),
                                                    attach_value.getString("attachment_type"),
                                                    attach_value.getString("thumbnail"),
                                                    attach_value.getString("id"));
                                            models.add(attachmentModel);
                                        }
                                    }

                                    /*Like Status*/
                                    String like_status="";
                                    if (awardValue.getString("ifuserLike").equalsIgnoreCase("null"))
                                    {
                                        like_status="0";
                                    }
                                    else
                                    {
                                        like_status="1";
                                    }

                                    /*Tagged User data*/
                                    ArrayList<TagUserData> tagUserData=new ArrayList<>();
                                    JSONArray tag=awardValue.getJSONArray("taggedUser");
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

                                    postModel=new HomePostModel(awardValue.getString("id"),
                                            awardValue.getString("user_id"),
                                            awardValue.getString("description"),
                                            awardValue.getString("created_at"),
                                            awardValue.getString("like_count"),
                                            awardValue.getString("comment_count"),
                                            awardValue.getString("share_count"),
                                            awardValue.getJSONObject("postUserDetails").getString("nick_name"),
                                            awardValue.getJSONObject("postUserDetails").getString("family_name"),
                                            awardValue.getJSONObject("postUserDetails").getString("image"),
                                            like_status,false,models,
                                            "",awardValue.getString("file_to_show"),tagUserData);
                                }
                                else
                                {
                                    ArrayList<PostAttachmentModel> models=new ArrayList<>();
                                    ArrayList<TagUserData> tagUserData=new ArrayList<>();
                                    postModel=new HomePostModel("","","",
                                            value.getString("created_at"),"",
                                            "","","",
                                            "","","",false,models,
                                            "","",tagUserData);
                                }

                                postModels.add(postModel);
                            }

                            setAdapter();
                            error.setVisibility(View.GONE);
                            awards_list.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            error.setVisibility(View.VISIBLE);
                            awards_list.setVisibility(View.GONE);
                        }
                        break;
                }
            }
            else
            {
                error.setVisibility(View.VISIBLE);
                awards_list.setVisibility(View.GONE);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
