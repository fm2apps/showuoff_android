package app.shouoff;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.shouoff.adapter.FansPostAdapter;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.drawer.Drawer;
import app.shouoff.login.MyProfile;
import app.shouoff.model.MyAdmireModel;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class MyFollowers extends Drawer implements RetrofitResponse
{
    private RecyclerView fans_lists;
    private Context context=this;
    private ArrayList<MyAdmireModel> admireModels=new ArrayList<>();
    private FansPostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_followers);

        fans_lists=(RecyclerView)findViewById(R.id.fans_lists);
        if (getIntent().hasExtra("anotheruser"))
        {
            title1.setText("Fans");
            setService();
        }
        else if (getIntent().hasExtra("followings"))
        {
            title1.setText("My Following");
            new Retrofit2(context,this,9,Constants.
                    Myfollowing_list+SharedPreference.retriveData(context,Constants.ID)).callServiceBack();
        }
        else
        {
            title1.setText("My Fans");
            setService();
        }
        initViews();
    }

    private void setService()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("user_id",getIntent().getStringExtra("user_id"));
            object.put("check_user_id",SharedPreference.retriveData(context,Constants.ID));
            new Retrofit2(context,this,8,Constants.Myfollowers_list,object).callService(true);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void initViews()
    {
        ((Drawer)context).setBottomBar(R.drawable.discovery_gray,
                R.drawable.profile_colored,
                R.drawable.creatpost_gray,
                R.drawable.notification_gray,
                R.drawable.moregray, profile,discovery,creat_post,notification,more);
    }

    private void setAdapter()
    {
        fans_lists.setHasFixedSize(true);
        fans_lists.setLayoutManager(new LinearLayoutManager(context));
        adapter = new FansPostAdapter(context,admireModels);
        fans_lists.setAdapter(adapter);
        adapter.click(new FansPostAdapter.ShowView()
        {
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
                        adapter.notifyDataSetChanged();
                    }
                }
                else
                {
                    setFollowUnFollowService(Constants.FansReq,admireModels.get(pos).getId());
                    admireModels.get(pos).setStatus("1");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void profile(View view, int pos)
            {
                if (admireModels.get(pos).getId().equalsIgnoreCase(SharedPreference.retriveData(context,Constants.ID)))
                {
                    startActivity(new Intent(context,MyProfile.class));
                }
                else
                {
                    startActivityForResult(new Intent(context,UserProfile.class)
                            .putExtra("profile_id",admireModels.get(pos).getId())
                            .putExtra("fromFollow","fromFollow"),10000);
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
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fade1, R.anim.fade2);
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
                        JSONArray data=result.getJSONArray("data");
                        if (data.length()>0)
                        {
                            for (int i=0;i<data.length();i++)
                            {
                                JSONObject value=data.getJSONObject(i);

                                String st="";

                                if (value.getJSONObject("User_followers").getString("detail_if_i_folllow_back").equalsIgnoreCase("null"))
                                {
                                    st="0";
                                }
                                else
                                {
                                    st="1";
                                }

                                MyAdmireModel admireModel=new MyAdmireModel(value.getJSONObject("User_followers").getString("id"),
                                        value.getJSONObject("User_followers").getString("first_name")+" "+value.getJSONObject("User_followers").getString("family_name"),
                                        value.getJSONObject("User_followers").getString("image"),
                                        st,value.getJSONObject("User_followers").getString("email")
                                        ,false,value.getJSONObject("User_followers").getString("nick_name"));
                                admireModels.add(admireModel);
                            }
                            setAdapter();
                        }
                        break;
                    case 9:
                        admireModels.clear();
                        JSONArray data1=result.getJSONArray("data");
                        if (data1.length()>0)
                        {
                            for (int i=0;i<data1.length();i++)
                            {
                                JSONObject value=data1.getJSONObject(i);

                                MyAdmireModel admireModel=new MyAdmireModel(value.getJSONObject("whom_userFollow")
                                        .getString("id"),value.getJSONObject("whom_userFollow")
                                        .getString("first_name")+" "+value.getJSONObject("whom_userFollow")
                                        .getString("family_name"),
                                        value.getJSONObject("whom_userFollow").getString("image"),
                                        "1",value.getJSONObject("whom_userFollow").getString("email")
                                        ,false,value.getJSONObject("whom_userFollow").getString("nick_name"));
                                admireModels.add(admireModel);
                            }
                            setAdapter();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==10000 && resultCode==10001)
        {
            for (int i=0;i<admireModels.size();i++)
            {
                if (!admireModels.get(i).getId().equalsIgnoreCase("1"))
                {
                    if (admireModels.get(i).getId().equalsIgnoreCase(data.getStringExtra("profile_id")))
                    {
                        admireModels.get(i).setStatus(data.getStringExtra("follow_status"));

                        if (getIntent().hasExtra("followings"))
                        {
                            admireModels.remove(i);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }
    }
}
