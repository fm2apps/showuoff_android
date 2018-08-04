package app.shouoff.appsettings;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import app.shouoff.R;
import app.shouoff.common.Alerts;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.drawer.Drawer;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ChangePassword extends Drawer implements RetrofitResponse
{
    private Context context=this;
    private EditText old_password,new_password,confirm_password;
    private TextView submit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        title1.setText("Change Password");
        initialized();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fade1, R.anim.fade2);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.submit_button:
                if (validation())
                {
                    setChangePasswordService();
                }
                break;
        }
    }

    /*Validations*/
    private boolean validation()
    {
        if (old_password.getText().toString().isEmpty())
        {
            Alerts.showDialog(context,"","Enter Old Password");
            return false;
        }
        if (new_password.getText().toString().isEmpty())
        {
            Alerts.showDialog(context,"","Enter New Password");
            return false;
        }
        if (new_password.getText().toString().length()<6)
        {
            Alerts.showDialog(context,"","Password Is Too Short");
            return false;
        }
        if (confirm_password.getText().toString().isEmpty())
        {
            Alerts.showDialog(context,"","Enter Confirm Password");
            return false;
        }
        if (!confirm_password.getText().toString().equalsIgnoreCase(new_password.getText().toString()))
        {
            Alerts.showDialog(context,"","Confirm Password Not Match");
            return false;
        }
        return true;
    }

    // TODO: 18/9/17 Service
    private void setChangePasswordService()
    {
        JSONObject object=new JSONObject();
        try {
            object.put("user_id", SharedPreference.retriveData(context,Constants.ID));
            object.put("old_password",old_password.getText().toString());
            object.put("new_password",confirm_password.getText().toString());
            new Retrofit2(context,this,1,Constants.Update_password,object).callService(true);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void initialized()
    {
        old_password=(EditText)findViewById(R.id.old_password);
        new_password=(EditText)findViewById(R.id.new_password);
        confirm_password=(EditText)findViewById(R.id.confirm_password);
        submit_button=(TextView)findViewById(R.id.submit_button);

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
        submit_button.setOnClickListener(this);
    }

    @Override
    public void onServiceResponse(int requestCode, Response<ResponseBody> response)
    {
        try {
            JSONObject object=new JSONObject(response.body().string().toString());
            boolean success=object.getBoolean("response");
            String message=object.getString("message");
            if (success)
            {
                switch (requestCode)
                {
                    case 1:
                        Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }
            }
            else
            {
                Alerts.showDialog(context,"Oops!",message);
            }
        }
        catch (JSONException |IOException e)
        {
            e.printStackTrace();
        }
    }
}
