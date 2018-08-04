package app.shouoff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alexvasilkov.gestures.animation.ViewPosition;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import app.shouoff.adapter.HomePostAdapter;
import app.shouoff.common.CommonService;
import app.shouoff.common.ConnectionDetector;
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

public class MyPost extends Drawer implements RetrofitResponse
{
    RecyclerView recycler;
    private ArrayList<HomePostModel> postModels=new ArrayList<>();
    private ArrayList<HomePostModel> temp_list=new ArrayList<>();

    Context context = this;
    private int page_count=0,value=0,count=0;
    private boolean aBoolean=false;
    private HomePostAdapter postAdapter;
    private ProgressBar home_post_loader;
    private int post_position=0;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        title1.setText("My Posts");

        initViews();
        setServiceMyPosts();
    }

    private void initViews()
    {
        error=(TextView)findViewById(R.id.error);
        home_post_loader=(ProgressBar)findViewById(R.id.home_post_loader);
        recycler = (RecyclerView) findViewById(R.id.recycler);

        ((Drawer)context). showImage(R.drawable.search,
                R.drawable.user,
                R.drawable.post_blue,
                R.drawable.trophyblack,
                R.drawable.shop,
                R.drawable.contests,
                R.drawable.preferences,
                R.drawable.winners,
                R.drawable.about,
                R.drawable.contact);
        ((Drawer)context).showText(my_post,my_profile,search_user,highly_liked,creat_post1,contest,prefrence,winners,about,contact_support);
        showView(post_view,search_view,profile_view,liked_view,create_view,contests_view,pref_view,winner_view,about_view,support_view);
        ((Drawer)context).setBottomBar(R.drawable.discovery_gray, R.drawable.profile_gray, R.drawable.creatpost_gray, R.drawable.notification_gray, R.drawable.more_colord, more, profile, creat_post, notification, discovery);
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
                postAdapter.notifyDataSetChanged();
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
                    postAdapter.notifyDataSetChanged();
                }
            }
            SharedPreference.removeKey(context,"delete_post_id");
        }
    }

    private void setAdapter(ArrayList<HomePostModel> homePostModels)
    {
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(context));


        temp_list.clear();
        temp_list.addAll(homePostModels);
        recycler.setHasFixedSize(true);

        for (int t=0;t<temp_list.size();t++)
        {
            if (temp_list.get(t).getLike_status().equalsIgnoreCase("1"))
            {
                temp_list.get(t).setaBoolean(true);
            }
        }

        postAdapter=new HomePostAdapter(context,temp_list,recycler);
        recycler.setAdapter(postAdapter);
        recycler.setNestedScrollingEnabled(false);
        postAdapter.click(new HomePostAdapter.ItemClick()
        {
            @Override
            public void likes(View view, int pos)
            {
                 /*Services of like and dislike Post*/
                if (temp_list.get(pos).isaBoolean())
                {
                    temp_list.get(pos).setaBoolean(false);
                    new CommonService().articleDislike(temp_list.get(pos).getId(),context);
                    String data=temp_list.get(pos).getLike_count();
                    int j= Integer.parseInt(data);
                    int i = j - 1;
                    temp_list.get(pos).setLike_count(String.valueOf(i));
                    temp_list.get(pos).setLike_status("0");
                    postAdapter.notifyDataSetChanged();
                }
                else
                {
                    temp_list.get(pos).setaBoolean(true);
                    new CommonService().articleLike(temp_list.get(pos).getId(),context);
                    String data=temp_list.get(pos).getLike_count();
                    int i= Integer.parseInt(data)+1;
                    temp_list.get(pos).setLike_count(String.valueOf(i));
                    temp_list.get(pos).setLike_status("1");
                    postAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void share(View view, int pos)
            {
                post_position=pos;
                startActivityForResult(new Intent(context,SharePostActivity.class),100);
            }

            @Override
            public void details(View view, int pos)
            {
                startActivity(new Intent(context, PostDetails.class)
                        .putExtra("post_data",postModels.get(pos)));
            }

            @Override
            public void showImage(View view, int pos) {
                ViewPosition position1 = ViewPosition.from(view);
                FullImageActivity.open((Activity) context, position1,
                        Constants.POST_URL+temp_list.get(pos).getAttachmentModels()
                                .get(0).getAttachment_name());
            }

            @Override
            public void likeList(View view, int pos) {
                if (!temp_list.get(pos).getLike_count().equalsIgnoreCase("0"))
                {
                    startActivity(new Intent(context, PostLikedUsers.class)
                            .putExtra("post_id",""+temp_list.get(pos).getId()));
                }
            }

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
                        postAdapter.notifyItemInserted(temp_list.size()+1);
                        setServiceMyPosts();
                    }
                    aBoolean=false;
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100 && resultCode==101)
        {
        ArrayList<SearchDataModel> selected = (ArrayList<SearchDataModel>)data.getSerializableExtra("user_list");

            StringBuilder builder=new StringBuilder();
            for (int i = 0; i< selected.size(); i++)
            {
                builder.append(selected.get(i).getId());
                builder.append(",");
            }

             /*Set service to share post */
            new CommonService().setServiceShare(String.valueOf(builder).substring(0,String.valueOf(builder).length()-1)
                    ,temp_list.get(post_position).getId(),context,6,this);
        }
    }

    private void setServiceMyPosts()
    {
        JSONObject param=new JSONObject();
        try
        {
            param.put("Offset",String.valueOf(count));
            param.put("user_id",getIntent().getStringExtra("user_id"));
            new Retrofit2(context,this,0, Constants.perticularUserPost,param).callServiceBack();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceResponse(int requestCode, Response<ResponseBody> response)
    {
        try
        {
            JSONObject result=new JSONObject(response.body().string());
            boolean status=result.getBoolean("response");
            if (status)
            {
                switch (requestCode)
                {
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
                                        value.getJSONObject("postUserDetails").getString("nick_name"),
                                        value.getJSONObject("postUserDetails").getString("family_name"),
                                        value.getJSONObject("postUserDetails").getString("image"),
                                        like_status,false,models,"",tagUserData);
                                postModels.add(postModel);
                            }

                            pagination();
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
                        String temp=temp_list.get(post_position).getShare_count();
                        int j= Integer.parseInt(temp)+1;
                        temp_list.get(post_position).setShare_count(String.valueOf(j));
                        postAdapter.notifyDataSetChanged();
                        break;

                }
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
                postAdapter.notifyItemRemoved(temp_list.size());
                temp_list.addAll(postModels);
                postAdapter.notifyDataSetChanged();
                postAdapter.setLoaded();
            }
        }
    }
}
