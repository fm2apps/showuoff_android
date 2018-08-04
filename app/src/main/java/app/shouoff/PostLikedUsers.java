package app.shouoff;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import app.shouoff.adapter.LikedPostUserAdapter;
import app.shouoff.common.ConnectionDetector;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.login.MyProfile;
import app.shouoff.model.MyAdmireModel;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class PostLikedUsers extends AppCompatActivity implements RetrofitResponse,View.OnClickListener
{
    private RecyclerView liked_user;
    private Context context=this;
    private ImageView backarrow;
    private ArrayList<MyAdmireModel> admireModels=new ArrayList<>();
    private ArrayList<MyAdmireModel> temp_list=new ArrayList<>();
    private LikedPostUserAdapter postUserAdapter;
    private int page_count=0,value=0,count=0;
    private boolean aBoolean=false;
    private ProgressBar home_post_loader;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_liked_users);

        initialized();
        setServiceLiked();
    }

    private void initialized()
    {
        backarrow=(ImageView)findViewById(R.id.backarrow);
        liked_user=(RecyclerView)findViewById(R.id.liked_user);
        home_post_loader=(ProgressBar)findViewById(R.id.home_post_loader);
        swipeContainer=(SwipeRefreshLayout)findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(R.color.app_color,R.color.black,R.color.color_gray);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                page_count=0;value=0;count=0;
                aBoolean=false;
                setServiceLiked();
                swipeContainer.setRefreshing(true);
            }
        });

        backarrow.setOnClickListener(this);
    }

    private void setServiceLiked()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("post_id",getIntent().getStringExtra("post_id"));
            object.put("Offset",String.valueOf(count));
            object.put("user_id", SharedPreference.retriveData(context, Constants.ID));
            new Retrofit2(context,this,8,Constants.LIKED_USER,object).callService(false);
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
                    case 8:
                        admireModels.clear();
                        swipeContainer.setRefreshing(false);
                        home_post_loader.setVisibility(View.GONE);
                        JSONObject value1=result.getJSONObject("data");

                        page_count=value1.getInt("count");

                        JSONArray data=value1.getJSONArray("rows");
                        if (data.length()>0)
                        {
                            for (int i=0;i<data.length();i++)
                            {
                                JSONObject value=data.getJSONObject(i);

                                String st="";

                                if (value.getString("check_FollowStatus")
                                        .equalsIgnoreCase("null"))
                                {
                                    st="0";
                                }
                                else
                                {
                                    st="1";
                                }

                                if (!value.getString("like_ownerDetails").equalsIgnoreCase("null"))
                                {
                                    MyAdmireModel admireModel=new MyAdmireModel(value.getJSONObject("like_ownerDetails").getString("id"),
                                            value.getJSONObject("like_ownerDetails").getString("first_name")+"" +
                                                    " "+value.getJSONObject("like_ownerDetails").getString("family_name"),
                                            value.getJSONObject("like_ownerDetails").getString("image"),
                                            st,value.getJSONObject("like_ownerDetails").getString("email")
                                            ,false,value.getJSONObject("like_ownerDetails").getString("nick_name"));
                                    admireModels.add(admireModel);
                                }
                            }
                            pagination();
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
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fade1, R.anim.fade2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.backarrow:
                finish();
                break;
        }
    }

    // TODO: 19/3/18 Pagination Code
    private void pagination()
    {
        if (value==0)
        {
            aBoolean=true;
            setAdapter(admireModels);
            value=1;
            count=count+20;
        }
        else if (value==1)
        {
            if (count<page_count)
            {
                aBoolean=true;
                count=count+20;
                temp_list.remove(temp_list.size() - 1);
                postUserAdapter.notifyItemRemoved(temp_list.size());
                temp_list.addAll(admireModels);
                postUserAdapter.notifyDataSetChanged();
                postUserAdapter.setLoaded();
            }
        }
    }

    private void setAdapter(ArrayList<MyAdmireModel> homePostModels)
    {
        temp_list.clear();
        temp_list.addAll(homePostModels);

        liked_user.setLayoutManager(new LinearLayoutManager(context));
        liked_user.setHasFixedSize(true);
        postUserAdapter=new LikedPostUserAdapter(context,temp_list,liked_user);
        liked_user.setAdapter(postUserAdapter);

        postUserAdapter.click(new LikedPostUserAdapter.ShowView() {
            @Override
            public void make(View view, int pos)
            {
                if (admireModels.get(pos).getStatus().equalsIgnoreCase("1"))
                {
                    setFollowUnFollowService(Constants.UserUnsubscribe,admireModels.get(pos).getId());
                    admireModels.get(pos).setStatus("0");

                    if (getIntent().hasExtra("followings"))
                    {
                        admireModels.remove(pos);
                        postUserAdapter.notifyDataSetChanged();
                    }
                }
                else
                {
                    setFollowUnFollowService(Constants.FansReq,admireModels.get(pos).getId());
                    admireModels.get(pos).setStatus("1");
                }
                postUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void profile(View view, int pos)
            {
                    if (temp_list.get(pos).getId().equalsIgnoreCase(SharedPreference.
                            retriveData(context,Constants.ID)))
                    {
                        startActivity(new Intent(context,MyProfile.class));
                    }
                    else
                    {
                        startActivityForResult(new Intent(context,UserProfile.class)
                                .putExtra("profile_id",temp_list.get(pos).getId())
                                .putExtra("fromFollow","fromFollow"),10000);
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
                        postUserAdapter.notifyItemInserted(temp_list.size()+1);


                    }
                    aBoolean=false;
                }
            }
        });
    }

    private void setFollowUnFollowService(String service,String id)
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("req_by_user_id",SharedPreference.retriveData(context,Constants.ID));
            object.put("req_to_user_id",id);
            new Retrofit2(context,this,3,service,object).callService(false);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==10000 && resultCode==10001)
        {
            Log.e("11111","111");
            for (int i=0;i<temp_list.size();i++)
            {
                if (temp_list.get(i).getId().equalsIgnoreCase(data.getStringExtra("profile_id")))
                {
                    temp_list.get(i).setStatus(data.getStringExtra("follow_status"));
                }
            }
            postUserAdapter.notifyDataSetChanged();
        }
    }
}
