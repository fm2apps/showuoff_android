package app.shouoff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alexvasilkov.gestures.animation.ViewPosition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.shouoff.adapter.SharedPostAdapter;
import app.shouoff.common.CommonService;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.drawer.Drawer;
import app.shouoff.mediadata.FullImageActivity;
import app.shouoff.model.HomePostModel;
import app.shouoff.model.PostAttachmentModel;
import app.shouoff.model.SearchDataModel;
import app.shouoff.model.TagUserData;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class SharedPost extends Drawer implements RetrofitResponse
{
    RecyclerView recycler;
    Context context = this;
    private ArrayList<HomePostModel> postModels = new ArrayList<>();
    private ArrayList<SearchDataModel> selected = new ArrayList<>();
    private ProgressBar home_post_loader;
    private SharedPostAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private int post_position = 0;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_post);

        initViews();
        title1.setText("My Shared Posts");
        callServiceHighlyLiked(true);
    }

    private void callServiceHighlyLiked(boolean b)
    {
        new Retrofit2(context, this, 0, Constants.User_got_Shared_post + SharedPreference.retriveData(context, Constants.ID),"").callService(b);
    }

    private void initViews()
    {
        error=(TextView)findViewById(R.id.error);
        home_post_loader = (ProgressBar) findViewById(R.id.home_post_loader);
        recycler = (RecyclerView) findViewById(R.id.recycler);

        showView(profile_view, search_view, post_view, liked_view, create_view, contests_view, pref_view, winner_view, about_view, support_view);
        ((Drawer) context).showImage(R.drawable.search,
                R.drawable.profile_colored,
                R.drawable.document,
                R.drawable.trophyblack,
                R.drawable.createpost,
                R.drawable.contests,
                R.drawable.preferences,
                R.drawable.winners,
                R.drawable.about,
                R.drawable.contact);
        ((Drawer) context).showText(my_profile, my_post, search_user, highly_liked, creat_post1, contest, prefrence, winners, about, contact_support);
        ((Drawer) context).setBottomBar(R.drawable.discovery_gray,
                R.drawable.profile_colored,
                R.drawable.creatpost_gray,
                R.drawable.notification_gray,
                R.drawable.moregray,
                profile, discovery, creat_post, notification, more);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.app_color, R.color.black, R.color.color_gray);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callServiceHighlyLiked(false);
                swipeContainer.setRefreshing(true);
            }
        });

    }

    private void setAdapter()
    {
        for (int t=0;t<postModels.size();t++)
        {
            if (postModels.get(t).getLike_status().equalsIgnoreCase("1"))
            {
                postModels.get(t).setaBoolean(true);
            }
        }

        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(context));
        adapter = new SharedPostAdapter(context,postModels);
        recycler.setAdapter(adapter);
        recycler.setNestedScrollingEnabled(false);
        adapter.click(new SharedPostAdapter.ItemClick()
        {
            @Override
            public void likes(View view, int pos)
            {
                 /*Services of like and dislike Post*/
                if (postModels.get(pos).isaBoolean())
                {
                    postModels.get(pos).setaBoolean(false);
                    new CommonService().articleDislike(postModels.get(pos).getId(),context);
                    String data=postModels.get(pos).getLike_count();
                    int j= Integer.parseInt(data);
                    int i = j - 1;
                    postModels.get(pos).setLike_count(String.valueOf(i));
                    postModels.get(pos).setLike_status("0");
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    postModels.get(pos).setaBoolean(true);
                    new CommonService().articleLike(postModels.get(pos).getId(),context);
                    String data=postModels.get(pos).getLike_count();
                    int i= Integer.parseInt(data)+1;
                    postModels.get(pos).setLike_count(String.valueOf(i));
                    postModels.get(pos).setLike_status("1");
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void share(View view, int pos)
            {
                post_position=pos;
                startActivityForResult(new Intent(context,SharePostActivity.class),100);
            }

            @Override
            public void showImage(View view, int pos) {
                ViewPosition position1 = ViewPosition.from(view);
                FullImageActivity.open((Activity) context, position1,
                        Constants.POST_URL+postModels.get(pos).getAttachmentModels()
                                .get(0).getAttachment_name());
            }

            @Override
            public void likeList(View view, int pos) {
                if (!postModels.get(pos).getLike_count().equalsIgnoreCase("0"))
                {
                    startActivity(new Intent(context, PostLikedUsers.class)
                            .putExtra("post_id",""+postModels.get(pos).getId()));
                }
            }

            @Override
            public void details(View view, int pos) {
                startActivityForResult(new Intent(context, PostDetails.class)
                        .putExtra("post_data",postModels.get(pos)),600);
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
                    ,postModels.get(post_position).getId(),context,6,this);
        }
        else if (requestCode==600 && resultCode==601)
        {
            home_post_loader.setVisibility(View.VISIBLE);
            callServiceHighlyLiked(false);
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
            JSONObject result=new JSONObject(response.body().string().toString());
            boolean status=result.getBoolean("response");
            if (status)
            {
                switch (requestCode)
                {
                    case 0:
                        home_post_loader.setVisibility(View.GONE);
                        postModels.clear();
                        JSONArray postData=result.getJSONArray("data");
                        if (postData.length()>0)
                        {
                            for (int i=0;i<postData.length();i++)
                            {
                                JSONObject value=postData.getJSONObject(i);

                                JSONObject post=value.getJSONObject("Shared_post_details");

                                /*Attachments*/
                                JSONArray attach=post.getJSONArray("postAttachment");
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
                                String like_status="";
                                if (post.getString("ifuserLike").equalsIgnoreCase("null"))
                                {
                                    like_status="0";
                                }
                                else
                                {
                                    like_status="1";
                                }

                                /*Tagged User data*/
                                ArrayList<TagUserData> tagUserData=new ArrayList<>();
                                JSONArray tag=post.getJSONArray("taggedUser");
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

                                HomePostModel postModel=new HomePostModel(post.getString("id"),
                                        post.getString("user_id"),
                                        post.getString("description"),
                                        post.getString("created_at"),
                                        post.getString("like_count"),
                                        post.getString("comment_count"),
                                        post.getString("share_count"),
                                        post.getJSONObject("postUserDetails").getString("nick_name"),
                                        post.getJSONObject("postUserDetails").getString("family_name"),
                                        post.getJSONObject("postUserDetails").getString("image"),
                                        like_status,false,models,
                                        value.getJSONObject("UserDetail_whoShared_post").getString("id"),
                                        value.getJSONObject("UserDetail_whoShared_post").getString("nick_name"),
                                        value.getJSONObject("UserDetail_whoShared_post").getString("image"),tagUserData);
                                postModels.add(postModel);
                            }
                            setAdapter();
                            error.setVisibility(View.GONE);
                            recycler.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            error.setVisibility(View.VISIBLE);
                            recycler.setVisibility(View.GONE);
                        }

                        break;
                    case 6:
                        String temp=postModels.get(post_position).getShare_count();
                        int j= Integer.parseInt(temp)+1;
                        postModels.get(post_position).setShare_count(String.valueOf(j));
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
            else
            {
                error.setVisibility(View.VISIBLE);
                recycler.setVisibility(View.GONE);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
