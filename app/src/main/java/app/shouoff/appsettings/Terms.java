package app.shouoff.appsettings;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import org.json.JSONObject;

import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.drawer.Drawer;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class Terms extends Drawer implements RetrofitResponse
{
    private Context context=this;
    private TextView text1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        initialized();
        if (getIntent().hasExtra("policy"))
        {
            title1.setText(R.string.policy);
            new Retrofit2(context,this,1, Constants.PrivacyPolicy).callServiceBack();
        }
        else
        {
            title1.setText(R.string.terms);
            new Retrofit2(context,this,0, Constants.Terms).callServiceBack();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fade1, R.anim.fade2);
    }

    private void initialized()
    {
        text1=(TextView)findViewById(R.id.text1);
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

    @Override
    public void onServiceResponse(int requestCode, Response<ResponseBody> response) {
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
                        text1.setText(data.getString("discription"));
                        break;
                    case 1:
                        JSONObject data1=result.getJSONObject("data");
                        text1.setText(data1.getString("description"));
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
