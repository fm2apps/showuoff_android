package app.shouoff;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import app.shouoff.adapter.HighlyLikedAdapter;
import app.shouoff.common.CommonService;
import app.shouoff.common.ConnectionDetector;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.drawer.Drawer;
import app.shouoff.model.HomePostModel;
import app.shouoff.model.PostAttachmentModel;
import app.shouoff.model.SearchDataModel;
import app.shouoff.model.TagUserData;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class DailyHighlyLiked extends Drawer implements RetrofitResponse
{
    RecyclerView recycler;
    Context context = this;
    private ArrayList<HomePostModel> postModels=new ArrayList<>();
    private ArrayList<HomePostModel> temp_list=new ArrayList<>();

    private ArrayList<SearchDataModel> selected=new ArrayList<>();
    private ProgressBar home_post_loader;
    private SwipeRefreshLayout swipeContainer;
    private int post_position=0;
    private TextView error;
    private HighlyLikedAdapter adapter;
    private int page_count=0,value=0,count=0;
    private boolean aBoolean=false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_posts);
        initViews();
        System.gc();
        if (getIntent().hasExtra("liked"))
        {
            showView(liked_view,search_view,post_view,contests_view,create_view,about_view,pref_view,winner_view,profile_view,support_view);
            ((Drawer)context). showImage(R.drawable.search,
                    R.drawable.profile_gray,
                    R.drawable.document,
                    R.drawable.highly_liked,
                    R.drawable.shop,
                    R.drawable.contests,
                    R.drawable.preferences,
                    R.drawable.winners,
                    R.drawable.about,
                    R.drawable.contact);

            ((Drawer)context).showText(highly_liked,my_post,search_user,contest,creat_post1,about,prefrence,winners,my_profile,contact_support);
            ((Drawer)context).setBottomBar(R.drawable.discovery_gray,
                    R.drawable.profile_gray,
                    R.drawable.creatpost_gray,
                    R.drawable.notification_gray,
                    R.drawable.more_colord,
                    more,discovery,creat_post,notification,profile);

            title1.setText("Daily Highly Liked");
            callServiceHighlyLiked(true);
        }
        else
        {
            showView(contests_view,search_view,post_view,liked_view,create_view,about_view,pref_view,winner_view,profile_view,support_view);
            ((Drawer)context). showImage(R.drawable.search,
                    R.drawable.profile_gray,
                    R.drawable.document,
                    R.drawable.trophyblack,
                    R.drawable.shop,
                    R.drawable.star,
                    R.drawable.preferences,
                    R.drawable.winners,
                    R.drawable.about,
                    R.drawable.contact);

            ((Drawer)context).showText(contest,my_post,search_user,highly_liked,creat_post1,about,prefrence,winners,my_profile,contact_support);
            ((Drawer)context).setBottomBar(R.drawable.discovery_gray,
                    R.drawable.profile_gray,
                    R.drawable.creatpost_gray,
                    R.drawable.notification_gray,
                    R.drawable.more_colord,
                    more,discovery,creat_post,notification,profile);

            title1.setText("Top 100");
            callServiceCompetitors(true);
        }
    }

    private void callServiceHighlyLiked(boolean b)
    {
        JSONObject param=new JSONObject();
        try
        {
            param.put("Offset",String.valueOf(count));
            param.put("user_id",SharedPreference.retriveData(context, Constants.ID));
            new Retrofit2(context,this,1, Constants.highlyLikedPost,param).callService(b);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void callServiceCompetitors(boolean b)
    {
        JSONObject param=new JSONObject();
        try
        {
            param.put("Offset",String.valueOf(count));
            param.put("requestor_id",SharedPreference.retriveData(context, Constants.ID));
            new Retrofit2(context,this,1, Constants.CurrentMonth_qualifiedPosts,param).callService(b);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void initViews()
    {
        error=(TextView)findViewById(R.id.error);
        home_post_loader=(ProgressBar)findViewById(R.id.home_post_loader);
        recycler = (RecyclerView) findViewById(R.id.recycler);

        swipeContainer=(SwipeRefreshLayout)findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.app_color,R.color.black,R.color.color_gray);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                 page_count=0;
                 value=0;
                 count=0;
                 aBoolean=false;

                if (getIntent().hasExtra("liked"))
                {
                    callServiceHighlyLiked(false);
                }
                else
                {
                    callServiceCompetitors(false);
                }
                swipeContainer.setRefreshing(true);
            }
        });
    }

    private void setAdapter(ArrayList<HomePostModel> homePostModels)
    {
        temp_list.clear();
        temp_list.addAll(homePostModels);

        for (int t=0;t<temp_list.size();t++)
        {
            if (temp_list.get(t).getLike_status().equalsIgnoreCase("1"))
            {
                temp_list.get(t).setaBoolean(true);
            }
        }

        recycler.setHasFixedSize(true);
        GridLayoutManager glm = new GridLayoutManager(this,3);
        recycler.setLayoutManager(glm);
        adapter = new HighlyLikedAdapter(context,temp_list,recycler);
        recycler.setAdapter(adapter);
        recycler.setNestedScrollingEnabled(false);
        adapter.click(new HighlyLikedAdapter.ItemClick()
        {
            @Override
            public void onLoadMore()
            {
                if (!ConnectionDetector.isInternetAvailable(context))
                {
                    return;
                }
                if (aBoolean)
                {
                    if (count<page_count)
                    {
                        temp_list.add(null);
                        adapter.notifyItemInserted(temp_list.size()+1);
                        if (getIntent().hasExtra("liked"))
                        {
                            callServiceHighlyLiked(false);
                        }
                        else
                        {
                            callServiceCompetitors(false);
                        }
                    }
                    aBoolean=false;
                }
            }

            @Override
            public void showProfile(View view, int pos)
            {
                startActivityForResult(new Intent(context, PostDetails.class)
                        .putExtra("post_data",temp_list.get(pos)),600);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100 && resultCode==101)
        {
            selected= (ArrayList<SearchDataModel>) data.getSerializableExtra("user_list");

            StringBuilder builder=new StringBuilder();
            for (int i=0;i<selected.size();i++)
            {
                builder.append(selected.get(i).getId());
                builder.append(",");
            }

             /*Set service to share post */
            new CommonService().setServiceShare(String.valueOf(builder).substring(0,String.valueOf(builder).length()-1)
                    ,temp_list.get(post_position).getId(),context,6,this);
        }
        else if (requestCode==600 && resultCode==601)
        {
            page_count=0;
            value=0;
            count=0;
            aBoolean=false;

            if (getIntent().hasExtra("liked"))
            {
                callServiceHighlyLiked(false);
            }
            else
            {
                callServiceCompetitors(false);
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fade1, R.anim.fade2);

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
    public void onServiceResponse(int requestCode, Response<ResponseBody> response)
    {
        try
        {
            swipeContainer.setRefreshing(false);
            JSONObject result=new JSONObject(response.body().string().toString());
            boolean status=result.getBoolean("response");
            if (status)
            {
                switch (requestCode)
                {
                    case 1:
                        home_post_loader.setVisibility(View.GONE);
                        postModels.clear();
                        JSONObject data=result.getJSONObject("data");
                        if (data.getInt("count")>100)
                        {
                            page_count=data.getInt("100");
                        }
                        else
                        {
                            page_count=data.getInt("count");
                        }
                        JSONArray postData1=data.getJSONArray("rows");
                        if (postData1.length()>0)
                        {
                            for (int i=0;i<postData1.length();i++)
                            {
                                JSONObject value_main=postData1.getJSONObject(i);

                                /*Attachments*/
                                JSONArray attach=value_main.getJSONArray("postAttachment");
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
                                if (value_main.getString("ifuserLike").equalsIgnoreCase("null"))
                                {
                                    like_status="0";
                                }
                                else
                                {
                                    like_status="1";
                                }

                                /*Tagged User data*/
                                ArrayList<TagUserData> tagUserData=new ArrayList<>();
                                JSONArray tag=value_main.getJSONArray("taggedUser");
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

                                HomePostModel postModel=new HomePostModel(value_main.getString("id"),
                                        value_main.getString("user_id"),
                                        value_main.getString("description"),
                                        value_main.getString("created_at"),
                                        value_main.getString("like_count"),
                                        value_main.getString("comment_count"),
                                        value_main.getString("share_count"),
                                        value_main.getJSONObject("postUserDetails").getString("nick_name"),
                                        value_main.getJSONObject("postUserDetails").getString("family_name"),
                                        value_main.getJSONObject("postUserDetails").getString("image"),
                                        like_status,false,models,value_main.getString("file_to_show"),tagUserData);
                                postModels.add(postModel);
                            }
                            pagination();
                            recycler.setVisibility(View.VISIBLE);
                            error.setVisibility(View.GONE);
                        }
                        else
                        {
                            recycler.setVisibility(View.GONE);
                            error.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 6:
                        String temp=temp_list.get(post_position).getShare_count();
                        int j= Integer.parseInt(temp)+1;
                        temp_list.get(post_position).setShare_count(String.valueOf(j));
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
            else
            {
                recycler.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);
            }

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
                temp_list.remove(temp_list.size() - 1);
                adapter.notifyItemRemoved(temp_list.size());
                temp_list.addAll(postModels);
                adapter.notifyDataSetChanged();
                adapter.setLoaded();
            }
        }
    }
}
