package app.shouoff.appsettings;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import org.json.JSONObject;

import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.drawer.Drawer;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class PrivacySettings extends Drawer implements RetrofitResponse
{
    Context context = this;
    private SwitchCompat like_btn,follow_btn;
    private boolean likeaBoolean=false,follow_boolean=false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_settings);
        initialized();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fade1, R.anim.fade2);
    }

    private void initialized()
    {
        follow_btn=(SwitchCompat)findViewById(R.id.follow_btn);
        like_btn=(SwitchCompat)findViewById(R.id.like_btn);

        if (SharedPreference.retriveData(context,"like_status")!=null)
        {
            if (SharedPreference.retriveData(context,"like_status").equalsIgnoreCase("1"))
            {
                like_btn.setChecked(true);
                likeaBoolean=true;
            }
            else
            {
                likeaBoolean=false;
                like_btn.setChecked(false);
            }

            if (SharedPreference.retriveData(context,"follow_status").equalsIgnoreCase("1"))
            {
                follow_btn.setChecked(true);
                follow_boolean=true;
            }
            else
            {
                follow_boolean=false;
                follow_btn.setChecked(false);
            }
        }
        else
        {
            notificationStatus("1","toggleLike");
            notificationStatus("1","toggleAdmire");
        }

        likeClick();
        followClick();

        title1.setText("Privacy Settings");
        ((Drawer)context). showImage(R.drawable.search,
                R.drawable.user,
                R.drawable.document,
                R.drawable.trophyblack,
                R.drawable.shop,
                R.drawable.contests,
                R.drawable.preferences_blue,
                R.drawable.winners,
                R.drawable.about,
                R.drawable.contact);

        ((Drawer)context).showText(prefrence,my_profile,search_user,highly_liked,creat_post1,contest,my_post,winners,about,contact_support);

        showView(pref_view,search_view,profile_view,liked_view,create_view,contests_view,post_view,winner_view,about_view,support_view);
        ((Drawer)context).setBottomBar(R.drawable.discovery_gray, R.drawable.profile_gray, R.drawable.creatpost_gray, R.drawable.notification_gray, R.drawable.more_colord, more, profile, creat_post, notification, discovery);

    }

    private void likeClick()
    {
        like_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (!likeaBoolean)
                {
                    likeaBoolean=true;
                    like_btn.setChecked(true);
                    notificationStatus("1","toggleLike");
                }
                else
                {
                    likeaBoolean=false;
                    like_btn.setChecked(false);
                    notificationStatus("0","toggleLike");
                }
            }
        });
    }

    private void notificationStatus(String value,String key)
    {
        JSONObject param=new JSONObject();
        try
        {
            param.put("user_id",SharedPreference.retriveData(context, Constants.ID));
            param.put(key,value);
            new Retrofit2(context,this,1,Constants.UserToggleNotification,param).callService(false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void followClick()
    {
        follow_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!follow_boolean)
                {
                    follow_boolean=true;
                    follow_btn.setChecked(true);
                    notificationStatus("1","toggleAdmire");
                }
                else
                {
                    follow_boolean=false;
                    follow_btn.setChecked(false);
                    notificationStatus("0","toggleAdmire");
                }
            }
        });
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
                        JSONObject data=result.getJSONObject("data");
                        SharedPreference.storeData(context,"like_status",data.getString("like_notification"));
                        SharedPreference.storeData(context,"follow_status",data.getString("admire_notification"));

                        if (SharedPreference.retriveData(context,"like_status").equalsIgnoreCase("1"))
                        {
                            like_btn.setChecked(true);
                            likeaBoolean=true;
                        }
                        else
                        {
                            likeaBoolean=false;
                            like_btn.setChecked(false);
                        }

                        if (SharedPreference.retriveData(context,"follow_status").equalsIgnoreCase("1"))
                        {
                            follow_btn.setChecked(true);
                            follow_boolean=true;
                        }
                        else
                        {
                            follow_boolean=false;
                            follow_btn.setChecked(false);
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
}
