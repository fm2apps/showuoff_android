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

public class AboutUs extends Drawer implements RetrofitResponse
{
    private Context context=this;
    private TextView text1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        initialized();
        title1.setText(R.string.content_about);
        new Retrofit2(context,this,0, Constants.About_Us).callServiceBack();
     }

    private void initialized()
    {
        text1=(TextView)findViewById(R.id.text1);
        showView(about_view,search_view,post_view,liked_view,create_view,contests_view,pref_view,winner_view,profile_view,support_view);
        ((Drawer)context). showImage(R.drawable.search,
                R.drawable.profile_gray,
                R.drawable.document,
                R.drawable.trophyblack,
                R.drawable.shop,
                R.drawable.contests,
                R.drawable.preferences,
                R.drawable.winners,
                R.drawable.about_blue,
                R.drawable.contact);
        ((Drawer)context).showText(about,my_post,search_user,highly_liked,creat_post1,contest,prefrence,winners,my_profile,contact_support);
        ((Drawer)context).setBottomBar(R.drawable.discovery_gray,
                R.drawable.profile_gray,
                R.drawable.creatpost_gray,
                R.drawable.notification_gray,
                R.drawable.more_colord,
                more,discovery,creat_post,notification,profile);
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
                    case 0:
                        JSONObject data=result.getJSONObject("data");
                        text1.setText(data.getString("para1"));
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
