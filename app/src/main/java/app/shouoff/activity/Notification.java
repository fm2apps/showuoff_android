package app.shouoff.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import app.shouoff.NotificationPostDetails;
import app.shouoff.R;
import app.shouoff.UserProfile;
import app.shouoff.adapter.NotificationAdapter;
import app.shouoff.common.CommonService;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.drawer.Drawer;
import app.shouoff.model.NotificationHistoryModel;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class Notification extends Drawer implements RetrofitResponse
{
    RecyclerView recycler;
    Context context=this;
    private TextView error;
    private NotificationAdapter adapter;
    private TextView clear_all;
    private LinearLayout main_view;
    private ProgressBar post_loader;
    private ArrayList<NotificationHistoryModel> historyModels=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initViews();

        getNotification();
    }

    private void getNotification()
    {
        new Retrofit2(context,this,1,Constants.MyNotifications+
                SharedPreference.retriveData(context,Constants.ID),"").callService(false);
    }

    private void initViews()
    {
        post_loader=(ProgressBar)findViewById(R.id.post_loader);
        post_loader.setVisibility(View.VISIBLE);
        main_view=(LinearLayout)findViewById(R.id.main_view);
        clear_all=(TextView)findViewById(R.id.clear_all);
        error=(TextView)findViewById(R.id.error);
        title1.setText("Notifications");
        ((Drawer)context).setBottomBar(R.drawable.discovery_gray, R.drawable.profile_gray, R.drawable.creatpost_gray, R.drawable.notification_colored, R.drawable.moregray, notification, profile, creat_post, discovery, more);
        recycler=(RecyclerView)findViewById(R.id.recycler);
        clear_all.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.clear_all:
                UserDeletingNotifications();
                break;
        }
    }

    private void UserDeletingNotifications()
    {
        JSONObject object=new JSONObject();
        try
        {
            JSONArray array=new JSONArray();
            for (int i=0;i<historyModels.size();i++)
            {
                array.put(historyModels.get(i).getId());
            }
            object.put("notification_id",array);
            new Retrofit2(context,this,2,Constants.UserDeletingNotifications,object).callService(true);
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
                    case 1:
                        post_loader.setVisibility(View.GONE);
                        JSONArray data=result.getJSONArray("data");
                        if (data.length()>0)
                        {
                            for (int i=0;i<data.length();i++)
                            {
                                JSONObject value=data.getJSONObject(i);

                                NotificationHistoryModel historyModel;
                                if (value.getString("notificationSenderDetails").equalsIgnoreCase("null"))
                                {
                                    historyModel=new NotificationHistoryModel(value.getString("id"),
                                            value.getString("sender_id"),
                                            value.getString("notification_type"),
                                            value.getString("post_id"),
                                            value.getString("created_at"),
                                            SharedPreference.retriveData(context,Constants.Image),
                                            value.getString("title"),
                                            SharedPreference.retriveData(context,Constants.NICK_NAME),
                                            value.getString("read_status"));
                                    historyModels.add(historyModel);
                                }
                                else
                                {
                                    historyModel=new NotificationHistoryModel(value.getString("id"),
                                            value.getString("sender_id"),
                                            value.getString("notification_type"),
                                            value.getString("post_id"),
                                            value.getString("created_at"),
                                            value.getJSONObject("notificationSenderDetails").getString("image"),
                                            value.getString("title"),
                                            value.getJSONObject("notificationSenderDetails").getString("nick_name"),
                                            value.getString("read_status"));
                                    historyModels.add(historyModel);
                                }
                            }
                            main_view.setVisibility(View.VISIBLE);
                            setAdapter();
                        }
                        else
                        {
                            main_view.setVisibility(View.GONE);
                            error.setVisibility(View.VISIBLE);
                            recycler.setVisibility(View.GONE);
                        }
                        break;
                    case 2:
                        getNotification();
                        break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setAdapter()
    {
        recycler.setHasFixedSize(true);
        error.setVisibility(View.GONE);
        recycler.setVisibility(View.VISIBLE);
        recycler.setLayoutManager(new LinearLayoutManager(Notification.this));
        adapter = new NotificationAdapter(context,historyModels);
        recycler.setAdapter(adapter);

        adapter.click(new NotificationAdapter.Read()
        {
            @Override
            public void show(int pos, View view)
            {
                historyModels.get(pos).setStatus("1");
                adapter.notifyDataSetChanged();

                new CommonService().ReadStatusOfNotification(historyModels.get(pos).getSender_id()
                        ,historyModels.get(pos).getNotification_type()
                        ,historyModels.get(pos).getPost_id(),context);

                if (historyModels.get(pos).getNotification_type().equalsIgnoreCase("like")
                        ||historyModels.get(pos).getNotification_type().equalsIgnoreCase("comment")
                        ||historyModels.get(pos).getNotification_type().equalsIgnoreCase("comment_tag")
                        ||historyModels.get(pos).getNotification_type().equalsIgnoreCase("post_tagging")
                        ||historyModels.get(pos).getNotification_type().equalsIgnoreCase("Qualify Posts")
                        ||historyModels.get(pos).getNotification_type().equalsIgnoreCase("post_rejected")
                        ||historyModels.get(pos).getNotification_type().equalsIgnoreCase("post_accepted")
                        ||historyModels.get(pos).getNotification_type().equalsIgnoreCase("winner")
                        ||historyModels.get(pos).getNotification_type().equalsIgnoreCase("share"))
                {
                    context.startActivity(new Intent(new Intent(context, NotificationPostDetails.class)
                            .putExtra("notification","notification")
                            .putExtra("sender_id",historyModels.get(pos).getSender_id())
                            .putExtra("notification_type",historyModels.get(pos).getNotification_type())
                            .putExtra("post_id",historyModels.get(pos).getPost_id())));
                }
                else if (historyModels.get(pos).getNotification_type().equalsIgnoreCase("admire"))
                {
                    context.startActivity(new Intent(new Intent(context, UserProfile.class)
                            .putExtra("profile_id",historyModels.get(pos).getSender_id())));
                }else if (historyModels.get(pos).getNotification_type().equalsIgnoreCase("invite"))
                {
                    context.startActivity(new Intent(new Intent(context, UserProfile.class)
                            .putExtra("invite","invite")
                            .putExtra("title",historyModels.get(pos).getBody())
                            .putExtra("profile_id",historyModels.get(pos).getSender_id())));
                }
            }
        });
    }

}
