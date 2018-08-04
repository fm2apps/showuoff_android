package app.shouoff.common;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

import org.json.JSONException;
import org.json.JSONObject;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class CommonService implements RetrofitResponse
{
    public static final int AddDevice=100;
    public static final int DELETEDevice=200;
    public static final int READNotif=300;
    private Context context;
    private GoToHome goToHome;

    /*Add Device*/
    public void serviceAddDevice(Context context,String user_id)
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("device_token",Constants.getFcmDeviceID(context));
            object.put("user_id",user_id);
            object.put("device_id",Constants.getUniqueId(context));
            object.put("device_type","android");
            new Retrofit2(context,this,AddDevice,Constants.create_user_device_info,object).callService(false);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void ReadStatusOfNotification(String sender_id,String notification_type,String post_id,Context context)
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("read_status","1");
            object.put("sender_id",sender_id);
            object.put("user_id",SharedPreference.retriveData(context,Constants.ID));
            object.put("notification_type",notification_type);
            object.put("post_id",post_id);
            new Retrofit2(context,this,READNotif,Constants.ReadStatusOfNotification,object).callService(false);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /*Delete Device*/
    public void DeleteDevice(Context context)
    {
        this.context=context;
        JSONObject object=new JSONObject();
        try
        {
            object.put("device_id",Constants.getUniqueId(context));
            object.put("user_id",SharedPreference.retriveData(context,Constants.ID));
            new Retrofit2(context,this,DELETEDevice, Constants.DeleteDevice,object).callService(false);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /*Share Post*/
    public void setServiceShare(String share_to,String id,Context context,int code,
                                RetrofitResponse retrofitResponse)
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("post_id",id);
            object.put("share_by", SharedPreference.retriveData(context,Constants.ID));
            object.put("share_to",share_to);
            new Retrofit2(context,retrofitResponse,code,Constants.User_sharing_post,object).callService(false);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /*Article like*/
    public void articleLike(String article_id,Context mContext)
    {
        JSONObject object = new JSONObject();
        try {
            object.put("post_id", article_id);
            object.put("user_id", SharedPreference.retriveData(mContext,Constants.ID));
            new Retrofit2(mContext, this,10,Constants.postlike, object).callService(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Article dislike*/
    public void articleDislike(String article_id,Context mContext)
    {
        JSONObject object = new JSONObject();
        try {
            object.put("post_id", article_id);
            object.put("user_id", SharedPreference.retriveData(mContext,Constants.ID));
            new Retrofit2(mContext,this,10,Constants.postunlike, object).callService(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Service Delete Comment*/
    public void setServiceDeleteComment(String comment_id,String user_id,RetrofitResponse retrofitResponse,Context context)
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("post_id",user_id);
            object.put("comment_id",comment_id);
            new Retrofit2(context,retrofitResponse,33,Constants.deletecomment,object).callService(true);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void UserAutoLogoutIfAdminDeactivate(Context context, GoToHome goToHome)
    {
        this.context=context;
        this.goToHome=goToHome;
        new Retrofit2(context,this,30,Constants.UserAutoLogoutIfAdminDeactivate
                +SharedPreference.retriveData(context, Constants.ID),"").callService(false);
    }

    private void LogoutOldDeviceIfNewshound(Context context)
    {
        JSONObject param=new JSONObject();
        try
        {
            param.put("device_id",Constants.getUniqueId(context));
            param.put("user_id",SharedPreference.retriveData(context, Constants.ID));
            new Retrofit2(context,this,40,Constants.LogoutOldDeviceIfNewfound,param).callServiceBack();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void clickCount(Context context,String ad_id,String click)
    {
        JSONObject param=new JSONObject();
        try
        {
            param.put("clicks",click);
            param.put("add_id",ad_id);
            new Retrofit2(context,this,41,Constants.incrementAdvertisementClicks,param).callServiceBack();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void clickViews(Context context,String ad_id,String click)
    {
        JSONObject param=new JSONObject();
        try
        {
            param.put("views",click);
            param.put("add_id",ad_id);
            new Retrofit2(context,this,41,Constants.incrementAdvertisementViews,param).callServiceBack();
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
            JSONObject result=new JSONObject(response.body().string().toString());
            boolean status=result.getBoolean("response");
            if (status)
            {
                switch (requestCode)
                {
                    case AddDevice:
                        Log.e("Add Device",""+result.getString("message"));
                        break;
                    case DELETEDevice:
                        Log.e("Delete Device",""+result.getString("message"));
                        break;
                    case READNotif:
                        Log.e("Read Notification",""+result.getString("message"));
                        break;
                    case 30:
                        goToHome.gotoHome(30);
                        break;
                    case 40:
                        goToHome.gotoHome(40);
                        break;
                    case 41:
                        Log.e("Read Ads",""+result.getString("message"));
                        break;
                }
            }
            else
            {
                switch (requestCode)
                {
                    case 30:
                        Log.e("message",result.getString("message"));
                        LogoutOldDeviceIfNewshound(context);
                        break;
                    case 40:
                        Log.e("message",result.getString("message"));
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
