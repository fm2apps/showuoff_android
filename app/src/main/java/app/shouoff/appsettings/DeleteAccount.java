package app.shouoff.appsettings;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import app.shouoff.R;
import app.shouoff.common.Alerts;
import app.shouoff.common.CommonService;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.login.PromoVideo;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class DeleteAccount extends AppCompatActivity implements RetrofitResponse
{
    private EditText password;
    private TextView deleteAccount;
    private Context context=this;
    ImageView backarrow;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        backarrow=(ImageView)findViewById(R.id.backarrow);
        deleteAccount=(TextView)findViewById(R.id.deleteAccount);
        password=(EditText)findViewById(R.id.password);

        deleteAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().trim().isEmpty())
                {
                    return;
                }

                deleteAccount();
            }
        });

        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void deleteAccount()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("password",password.getText().toString());
            object.put("user_id", SharedPreference.retriveData(context,Constants.ID));
            new Retrofit2(context,this,10,Constants.DELETE_ACCOUNT,object).callService(true);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
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
                    case 10:
                        new CommonService().DeleteDevice(context);
                        startActivity(new Intent(context, PromoVideo.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP|
                                        Intent.FLAG_ACTIVITY_NEW_TASK));
                        SharedPreference.removeAll(context);
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

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fade1, R.anim.fade2);
    }
}
