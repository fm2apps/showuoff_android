package app.shouoff;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import app.shouoff.adapter.DiscoveryAdapter;
import app.shouoff.common.CommonService;
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

public class MyContest extends Drawer implements RetrofitResponse
{
    private RecyclerView mycontestlist;
    private Context context=this;
    private ArrayList<HomePostModel> postModels=new ArrayList<>();
    private DiscoveryAdapter adapter;
    private int post_position=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contest);

        title1.setText("Contests");
        mycontestlist=(RecyclerView)findViewById(R.id.mycontestlist);
        User_qualifiedPost();
    }

    private void User_qualifiedPost()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("user_id",getIntent().getStringExtra("user_id"));
            object.put("requestor_id",SharedPreference.retriveData(context,Constants.ID));
            new Retrofit2(context,this,1,Constants.User_qualifiedPost,object).callService(true);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
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
                        postModels.get(i).setDescription(SharedPreference.retriveData(context,"description"));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            SharedPreference.removeKey(context,"tmp");
        }

         /*Delete Post*/
        if (SharedPreference.retriveData(context,"delete_post_id")!=null)
        {
            for (int i=0;i<postModels.size();i++)
            {
                if (postModels.get(i).getId().equalsIgnoreCase(SharedPreference.retriveData(context,"delete_post_id")))
                {
                    postModels.remove(i);
                    adapter.notifyDataSetChanged();
                }
            }
            SharedPreference.removeKey(context,"delete_post_id");
        }
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

        mycontestlist.setHasFixedSize(true);
        mycontestlist.setLayoutManager(new LinearLayoutManager(context));
        adapter = new DiscoveryAdapter(context,postModels);
        mycontestlist.setAdapter(adapter);
        mycontestlist.setNestedScrollingEnabled(false);
        adapter.click(new DiscoveryAdapter.ItemClick()
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
        });
    }

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
                    ,postModels.get(post_position).getId(),context,6,this);
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
                    case 1:
                        postModels.clear();
                        JSONArray postData=result.getJSONArray("data");
                        if (postData.length()>0)
                        {
                            for (int i=0;i<postData.length();i++)
                            {
                                JSONObject value_main=postData.getJSONObject(i);
                                JSONObject value=value_main.getJSONObject("Qualified_post_details");

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
                                String like_status="";
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
                                        TagUserData userData=new TagUserData(tagData.getJSONObject("tagedUserDetails")
                                                .getString("id"),
                                                tagData.getJSONObject("tagedUserDetails")
                                                        .getString("username"),
                                                tagData.getJSONObject("tagedUserDetails")
                                                        .getString("nick_name"));
                                        tagUserData.add(userData);
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
                        }
                        setAdapter();
                        break;
                    case 6:
                        String temp=postModels.get(post_position).getShare_count();
                        int j= Integer.parseInt(temp)+1;
                        postModels.get(post_position).setShare_count(String.valueOf(j));
                        adapter.notifyDataSetChanged();
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
