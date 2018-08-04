package app.shouoff;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import app.shouoff.activity.CreatPost;
import app.shouoff.adapter.CompetitorsAdapter;
import app.shouoff.common.Constants;
import app.shouoff.common.DataHandler;
import app.shouoff.common.FlingableNestedScrollView;
import app.shouoff.common.SharedPreference;
import app.shouoff.drawer.Drawer;
import app.shouoff.model.HomePostModel;
import app.shouoff.model.PostAttachmentModel;
import app.shouoff.model.TagUserData;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ContestsActivity extends Drawer implements RetrofitResponse
{
    private Context context=this;
    private TextView contestName,description,start_date,end_date,apply_btn,error;
    private SimpleDraweeView contest_image;
    private FlingableNestedScrollView  main_view;
    private RecyclerView competitors_list;
    private String contest_ID="";
    private CompetitorsAdapter competitorsAdapter;
    private ArrayList<HomePostModel> postModels=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contests);

        initialized();
        getContestService();
    }

    private void getContestService()
    {
        new Retrofit2(context,this,0, Constants.User_see_AllContest+SharedPreference.retriveData(context,Constants.ID),"").callService(true);
    }

    private void initialized()
    {
        competitors_list=(RecyclerView)findViewById(R.id.competitors_list);
        main_view=(FlingableNestedScrollView)findViewById(R.id.main_view);
        error=(TextView)findViewById(R.id.error);
        contestName=(TextView)findViewById(R.id.contestName);
        description=(TextView)findViewById(R.id.description);
        start_date=(TextView)findViewById(R.id.start_date);
        end_date=(TextView)findViewById(R.id.end_date);
        apply_btn=(TextView)findViewById(R.id.apply_btn);
        contest_image=(SimpleDraweeView)findViewById(R.id.contest_image);

        title1.setText("Competitors");
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
        apply_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.apply_btn:
                startActivityForResult(new Intent(context,CreatPost.class)
                        .putExtra("fromContest",contest_ID),100);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100 && resultCode==101)
        {
            UserApplyingToContest();
        }
    }

    private void competitorsList()
    {
        competitors_list.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);
        competitors_list.setLayoutManager(layoutManager);
        competitorsAdapter = new CompetitorsAdapter(context,postModels);
        competitors_list.setAdapter(competitorsAdapter);
        competitors_list.setNestedScrollingEnabled(false);
    }

    private void UserApplyingToContest()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("user_id",SharedPreference.retriveData(context,Constants.ID));
            object.put("contest_id",contest_ID);
            new Retrofit2(context,this,1,Constants.UserApplyingToContest,object).callService(true);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
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
                        JSONObject data=result.getJSONObject("data");
                        DataHandler.forContest(contestName,"Title: ",data.getString("name"));
                        DataHandler.forContest(description,"Description: ",data.getString("discription"));
                        DataHandler.forContest(start_date,"Start: ",data.getString("start_date"));
                        DataHandler.forContest(end_date,"End: ",data.getString("end_date"));
                        contest_ID=data.getString("contest_id");
                        Uri uri = Uri.parse(Constants.CONTEST_UTL+data.getString("image"));
                        contest_image.setImageURI(uri);
                        error.setVisibility(View.GONE);
                        main_view.setVisibility(View.VISIBLE);

                        if (data.getString("AppliedUserD_details").equalsIgnoreCase("null"))
                        {
                            apply_btn.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            apply_btn.setVisibility(View.GONE);
                        }
                        getCompetitors();
                        break;
                    case 1:
                        Toast.makeText(context, "Contest Apply Successfully", Toast.LENGTH_SHORT).show();
                        getContestService();
                        break;
                    case 2:
                        postModels.clear();
                        JSONArray postData=result.getJSONArray("data");
                        if (postData.length()>0)
                        {
                            for (int i=0;i<postData.length();i++)
                            {
                                JSONObject value=postData.getJSONObject(i);
                                JSONObject userData =value.getJSONObject("contest_participantDetails");
                                JSONObject postUserData=userData.getJSONObject("UserOnlyOnepost");

                                /*Attachments*/
                                JSONArray attach=postUserData.getJSONArray("postAttachment");
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
                                if (postUserData.getString("ifuserLike").equalsIgnoreCase("null"))
                                {
                                    like_status="0";
                                }
                                else
                                {
                                    like_status="1";
                                }

                                /*Tagged User data*/
                                ArrayList<TagUserData> tagUserData=new ArrayList<>();
                                JSONArray tag=postUserData.getJSONArray("taggedUser");
                                if (tag.length()>0)
                                {
                                    for (int j = 0; j < tag.length(); j++)
                                    {
                                        JSONObject tagData=tag.getJSONObject(j);
                                        TagUserData userData1=new TagUserData(tagData.getJSONObject("tagedUserDetails")
                                                .getString("id"),
                                                tagData.getJSONObject("tagedUserDetails")
                                                        .getString("username"),
                                                tagData.getJSONObject("tagedUserDetails")
                                                        .getString("nick_name"));
                                        tagUserData.add(userData1);
                                    }
                                }

                                HomePostModel postModel=new HomePostModel(postUserData.getString("id"),
                                        postUserData.getString("user_id"),
                                        postUserData.getString("description"),
                                        postUserData.getString("created_at"),
                                        postUserData.getString("like_count"),
                                        postUserData.getString("comment_count"),
                                        postUserData.getString("share_count"),
                                        userData.getString("first_name"),
                                        userData.getString("family_name"),
                                        userData.getString("image"),
                                        like_status,false,models,"",tagUserData);
                                postModels.add(postModel);
                            }
                            competitorsList();
                        }
                        break;
                }
            }
            else
            {
                switch (requestCode)
                {
                    case 0:
                        error.setVisibility(View.VISIBLE);
                        main_view.setVisibility(View.GONE);
                        break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void getCompetitors()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("user_id",SharedPreference.retriveData(context,Constants.ID));
            object.put("contest_id",contest_ID);
            new Retrofit2(context,this,2,Constants.Compititor_list,object).callService(true);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }
}
