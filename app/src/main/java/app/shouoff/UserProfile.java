package app.shouoff;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import app.shouoff.activity.HomeActivity;
import app.shouoff.adapter.ProfilePostDataAdapter;
import app.shouoff.common.Alerts;
import app.shouoff.common.CommonService;
import app.shouoff.common.ConnectionDetector;
import app.shouoff.common.Constants;
import app.shouoff.common.FlingableNestedScrollView;
import app.shouoff.common.SharedPreference;
import app.shouoff.drawer.Drawer;
import app.shouoff.model.HomePostModel;
import app.shouoff.model.PostAttachmentModel;
import app.shouoff.model.TagUserData;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class UserProfile extends Drawer implements RetrofitResponse
{
    private RelativeLayout post_relative,contasts_relative;
    private TextView posts,invite,accept,reject,user_fans;
    private Context context=this;
    private CircleImageView imgProfile;
    private TextView fans;
    private boolean favStatus=false;
    private ImageView report,block;
    private LinearLayout fans_linear,main;
    android.support.v7.app.AlertDialog alertDialog;
    private String fan_count="";
    private TextView username,post_count,sport_name,fans_count,contast_count,about,winnings,city,about_click,error;
    private int page_count=0,value=0,count=0;
    private boolean aBoolean=false;
    private ProfilePostDataAdapter adapter;
    private ArrayList<HomePostModel> postModels=new ArrayList<>();
    private ArrayList<HomePostModel> temp_list=new ArrayList<>();
    private ProgressBar home_post_loader;
    private FlingableNestedScrollView main_view;
    private RecyclerView profile_list;
    private RelativeLayout post_details;
    private TextView bottom_text;
    private String temp="",follow_status="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_prodile);

        initViews();
        setProfileService();

        if (getIntent().hasExtra("invite"))
        {
            if (getIntent().getStringExtra("title").equalsIgnoreCase("Invite Request Rejected"))
            {
                accept.setVisibility(View.GONE);
                reject.setVisibility(View.GONE);
                fans.setVisibility(View.VISIBLE);
                invite.setVisibility(View.GONE);
            }
            else
            {
                accept.setVisibility(View.VISIBLE);
                reject.setVisibility(View.VISIBLE);
                fans.setVisibility(View.GONE);
                invite.setVisibility(View.GONE);
            }
        }
        else
        {
            accept.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
            fans.setVisibility(View.VISIBLE);
            invite.setVisibility(View.GONE);
        }

        if (getIntent().getStringExtra("profile_id").equalsIgnoreCase("1"))
        {
            block.setVisibility(View.GONE);
            main.setVisibility(View.GONE);
            report.setVisibility(View.GONE);
        }
        else
        {
            block.setVisibility(View.VISIBLE);
            main.setVisibility(View.VISIBLE);
            report.setVisibility(View.VISIBLE);
        }
    }

    private void setProfileService()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("user_id",SharedPreference.retriveData(context,Constants.ID));
            object.put("reqTO_userID",getIntent().getStringExtra("profile_id"));
            new Retrofit2(context,this,0,Constants.profileSearch,object).callService(true);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void setServiceInvite()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("req_by_user_id",SharedPreference.retriveData(context,Constants.ID));
            object.put("req_to_user_id",getIntent().getStringExtra("profile_id"));
            new Retrofit2(context,this,1,Constants.UserInviteReq,object).callService(true);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void setServiceResponse(String status)
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("req_by_user_id",SharedPreference.retriveData(context,Constants.ID));
            object.put("req_to_user_id",getIntent().getStringExtra("profile_id"));
            object.put("status",status);
            new Retrofit2(context,this,5,Constants.InviteResponse,object).callService(true);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void initViews()
    {
        bottom_text=(TextView)findViewById(R.id.bottom_text);
        user_fans=(TextView)findViewById(R.id.user_fans);
        main=(LinearLayout)findViewById(R.id.main);
        main_view=(FlingableNestedScrollView)findViewById(R.id.main_view);
        main_view.smoothScrollTo(0,0);

        post_details=(RelativeLayout)findViewById(R.id.post_details);
        about_click=(TextView)findViewById(R.id.about_click);
        error=(TextView)findViewById(R.id.error);
        home_post_loader=(ProgressBar)findViewById(R.id.home_post_loader);
        profile_list=(RecyclerView)findViewById(R.id.profile_list);

        block=(ImageView)findViewById(R.id.block);
        fans_linear=(LinearLayout)findViewById(R.id.fans_linear);
        city=(TextView)findViewById(R.id.city);
        accept=(TextView)findViewById(R.id.accept);
        reject=(TextView)findViewById(R.id.reject);
        invite=(TextView)findViewById(R.id.invite);
        report=(ImageView)findViewById(R.id.report);
        winnings=(TextView)findViewById(R.id.winnings);
        contasts_relative=(RelativeLayout)findViewById(R.id.contasts_relative);
        fans=(TextView)findViewById(R.id.fans);
        imgProfile=(CircleImageView)findViewById(R.id.imgProfile);
        username=(TextView)findViewById(R.id.username);
        post_count=(TextView)findViewById(R.id.post_count);
        sport_name=(TextView)findViewById(R.id.sport_name);
        fans_count=(TextView)findViewById(R.id.fans_count);
        contast_count=(TextView)findViewById(R.id.contast_count);
        about=(TextView)findViewById(R.id.about);

        ((Drawer)context).setBottomBar(R.drawable.discovery_gray,R.drawable.profile_colored,R.drawable.creatpost_gray,R.drawable.notification_gray,R.drawable.moregray,profile,discovery,creat_post,notification,more);
        post_relative=(RelativeLayout)findViewById(R.id.post_relative);
        posts=(TextView)findViewById(R.id.posts);

        posts.setBackgroundResource(R.drawable.corner_background);
        posts.setTextColor(ContextCompat.getColor(context,R.color.white));

        about_click.setBackgroundResource(R.drawable.corner_background_white);
        about_click.setTextColor(ContextCompat.getColor(context,R.color.app_color));

        about.setVisibility(View.GONE);
        post_details.setVisibility(View.VISIBLE);

        post_relative.setOnClickListener(this);
        posts.setOnClickListener(this);
        fans.setOnClickListener(this);
        contasts_relative.setOnClickListener(this);
        winnings.setOnClickListener(this);
        report.setOnClickListener(this);
        invite.setOnClickListener(this);
        reject.setOnClickListener(this);
        accept.setOnClickListener(this);
        fans_linear.setOnClickListener(this);
        block.setOnClickListener(this);
        about_click.setOnClickListener(this);
        user_fans.setOnClickListener(this);
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
    public void onClick(View view)
    {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.post_relative:
                /*if (post_count.getText().toString().equalsIgnoreCase("")||
                        post_count.getText().toString().equalsIgnoreCase("0"))
                {
                    Alerts.showDialog(context,"","No any post added by "+username.getText().toString());
                }
                else
                {
                    startActivity(new Intent(context,MyPost.class)
                            .putExtra("user_id",getIntent().getStringExtra("profile_id")));
                }*/
                break;
            case R.id.fans:
                if (favStatus)
                {
                    setFollowUnFollowService(Constants.UserUnsubscribe);
                    fans.setText("Follow");
                    favStatus=false;
                    temp="Unfollow";
                }
                else
                {
                    setFollowUnFollowService(Constants.FansReq);
                    fans.setText("Unfollow");
                    temp="follow";
                    favStatus=true;
                }
                break;
            case R.id.contasts_relative:
                if (!contast_count.getText().toString().equalsIgnoreCase("0"))
                {
                    startActivity(new Intent(context, MyContest.class)
                    .putExtra("user_id",getIntent().getStringExtra("profile_id")));
                }
                else
                {
                    Alerts.showDialog(context,"",username.getText().toString()+" didn't participated in any Contest");
                }
                break;
            case R.id.winnings:
                startActivity(new Intent(context, Awards.class)
                        .putExtra("user_id",getIntent().getStringExtra("profile_id")));
                break;
            case R.id.report:
                showAlertForReport();
                break;
            case R.id.invite:
                setServiceInvite();
                break;
            case R.id.accept:
                setServiceResponse("accepted");
                break;
            case R.id.reject:
                setServiceResponse("rejected");
                break;
            case R.id.fans_linear:
            case R.id.user_fans:
                if (!fan_count.equalsIgnoreCase("0"))
                {
                    startActivity(new Intent(context, MyFollowers.class)
                            .putExtra("anotheruser","anotheruser")
                            .putExtra("user_id", getIntent().getStringExtra("profile_id")));
                }
                else
                {
                    Alerts.showDialog(context,"",username.getText().toString()+" have no fans");
                }
                break;
            case R.id.block:
                showDialogBlock();
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

                        if (!data.getString("about_us").equalsIgnoreCase("null"))
                        {
                            about.setText(data.getString("about_us"));
                        }

                        title1.setText(data.getString("nick_name"));
                        username.setText(data.getString("nick_name"));
                        Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+data.getString("image")).placeholder(R.drawable.noimage).into(imgProfile);
                        fan_count=data.getString("fan_count");
                        fans_count.setText(data.getString("fan_count"));
                        contast_count.setText(result.getString("ContestCount"));
                        post_count.setText(result.getString("PostCount"));

                        if (!data.getString("cityDetails").equalsIgnoreCase("null"))
                        city.setText(data.getJSONObject("cityDetails").getString("name"));


                        StringBuilder stringBuilder=new StringBuilder();
                        JSONArray games=data.getJSONArray("gameDetails");
                        if (games.length()>0)
                        {
                            for (int i=0;i<games.length();i++)
                            {
                                JSONObject value=games.getJSONObject(i);
                                stringBuilder.append(value.getJSONObject("fullGameDetails").getString("name"));
                                stringBuilder.append(",");
                            }
                            sport_name.setText(String.valueOf(stringBuilder).subSequence(0,String.valueOf(stringBuilder).length()-1));
                        }


                        if (data.getString("fan_status").equalsIgnoreCase("null"))
                        {
                            fans.setText("Follow");
                            follow_status="0";
                            favStatus=false;
                        }
                        else
                        {
                            fans.setText("Unfollow");
                            follow_status="1";
                            favStatus=true;
                        }

                        if (data.getString("post_winning_count").equalsIgnoreCase("0"))
                            winnings.setVisibility(View.GONE);
                        else
                            winnings.setVisibility(View.VISIBLE);

                        if (getIntent().hasExtra("invite"))
                        {
                            if (!data.getString("invite_responseCheck").equalsIgnoreCase("null"))
                            {
                                if (data.getJSONObject("invite_responseCheck").getString("status").equalsIgnoreCase("rejected")||
                                        data.getJSONObject("invite_responseCheck").getString("status").equalsIgnoreCase("accepted"))
                                {
                                    invite.setVisibility(View.VISIBLE);
                                    fans.setVisibility(View.VISIBLE);
                                    accept.setVisibility(View.GONE);
                                    reject.setVisibility(View.GONE);
                                }
                                else
                                {
                                    invite.setVisibility(View.GONE);
                                    fans.setVisibility(View.GONE);
                                    accept.setVisibility(View.VISIBLE);
                                    reject.setVisibility(View.VISIBLE);
                                }

                                if (!data.getString("userInvite_check").equalsIgnoreCase("null"))
                                {
                                    JSONObject statusInvite=data.getJSONObject("userInvite_check");
                                    if (statusInvite.getString("status").equalsIgnoreCase("rejected"))
                                    {
                                        invite.setVisibility(View.VISIBLE);
                                        fans.setVisibility(View.VISIBLE);
                                        accept.setVisibility(View.GONE);
                                        reject.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        invite.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                        else
                        {
                            if (data.getString("userInvite_check").equalsIgnoreCase("null"))
                            {
                                invite.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                JSONObject statusInvite=data.getJSONObject("userInvite_check");
                                if (statusInvite.getString("status").equalsIgnoreCase("rejected"))
                                {
                                    invite.setVisibility(View.VISIBLE);
                                    fans.setVisibility(View.VISIBLE);
                                    accept.setVisibility(View.GONE);
                                    reject.setVisibility(View.GONE);
                                }
                                else
                                {
                                    invite.setVisibility(View.GONE);
                                }
                            }
                        }


                        if (getIntent().hasExtra("invite"))
                        {
                             /*Read Notification*/
                            new CommonService().ReadStatusOfNotification(getIntent().getStringExtra("profile_id")
                                    ,getIntent().getStringExtra("notification_type")
                                    ,getIntent().getStringExtra("post_id"),context);
                        }

                        setServiceMyPosts();
                        home_post_loader.setVisibility(View.VISIBLE);

                        break;
                    case 3:
                        if (temp.equalsIgnoreCase("follow"))
                        {
                           bottom_text.setVisibility(View.VISIBLE);
                           bottom_text.setText("You will start seeing more posts from "
                                   +username.getText().toString());
                            follow_status="1";
                            new Handler().postDelayed(
                                    new Runnable()
                                    {
                                        public void run()
                                        {
                                            bottom_text.setVisibility(View.GONE);
                                        }
                                    }, 2000);
                        }
                        else
                        {
                            follow_status="0";
                            bottom_text.setVisibility(View.GONE);
                        }
                        break;
                    case 2:
                        Alerts.showDialog(context,"",result.getString("message"));
                        break;
                    case 1:
                        invite.setVisibility(View.GONE);
                        break;
                    case 5:
                        accept.setVisibility(View.GONE);
                        reject.setVisibility(View.GONE);
                        fans.setVisibility(View.VISIBLE);
                        break;
                    case 6:
                        if (getIntent().hasExtra("fromSearch"))
                        {
                            Intent intent=new Intent();
                            intent.putExtra("profile_id",getIntent().getStringExtra("profile_id"));
                            setResult(505,intent);
                            finish();
                        }
                        else
                        {
                            Intent intent=new Intent();
                            setResult(301,intent);
                            finish();
                        }
                        break;
                    case 7:
                        home_post_loader.setVisibility(View.GONE);
                        JSONObject data1=result.getJSONObject("data");
                        page_count=data1.getInt("count");
                        postModels.clear();
                        JSONArray postData=data1.getJSONArray("rows");
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
            param.put("user_id",getIntent().getStringExtra("profile_id"));
            new Retrofit2(context,this,7, Constants.perticularUserPost,param).callServiceBack();
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
                        .putExtra("post_data",temp_list.get(pos)));
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

    private void setFollowUnFollowService(String service)
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("req_by_user_id", SharedPreference.retriveData(context,Constants.ID));
            object.put("req_to_user_id",getIntent().getStringExtra("profile_id"));
            new Retrofit2(context,this,3,service,object).callService(false);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        if (getIntent().hasExtra("notification"))
        {
            startActivity(new Intent(context, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|
                    Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
        else if (getIntent().hasExtra("fromFollow"))
        {
            Intent intent=new Intent();
            intent.putExtra("follow_status",follow_status);
            intent.putExtra("profile_id",getIntent().getStringExtra("profile_id"));
            setResult(10001,intent);
            finish();
        }
        else
        {
            super.onBackPressed();
        }
    }

    private void showAlertForReport()
    {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.custom_alert_dialog);
        LayoutInflater inflater=this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.report_alert_layout, null);
        final EditText report_message=(EditText) dialogView.findViewById(R.id.report_message);

        TextView report_text=(TextView)dialogView.findViewById(R.id.report_text);
        report_text.setText("Report User");
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

    private void setServiceReport(String message)
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("requested_for_user_id",getIntent().getStringExtra("profile_id"));
            object.put("requested_by_user_id", SharedPreference.retriveData(context,Constants.ID));
            object.put("message",message);
            new Retrofit2(context,this,2,Constants.user_report,object).callService(true);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void setServiceBlock()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("blocked_to",getIntent().getStringExtra("profile_id"));
            object.put("blocked_by", SharedPreference.retriveData(context,Constants.ID));
            new Retrofit2(context,this,6,Constants.USER_BLOCK,object).callService(true);

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
}
