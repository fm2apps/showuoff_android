package app.shouoff.appsettings;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONObject;

import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class TermsPrivacy extends AppCompatActivity implements RetrofitResponse
{
    private Context context=this;
    private TextView text1,header_text;
    ImageView backarrow;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_privacy);

        text1=(TextView)findViewById(R.id.text1);
        backarrow=(ImageView)findViewById(R.id.backarrow);
        header_text=(TextView)findViewById(R.id.header_text);
        if (getIntent().hasExtra("policy"))
        {
            header_text.setText(R.string.policy);
            new Retrofit2(context,this,1, Constants.PrivacyPolicy).callServiceBack();
        }
        else
        {
           header_text.setText(R.string.terms);
            new Retrofit2(context,this,0, Constants.Terms).callServiceBack();
        }

        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fade1, R.anim.fade2);
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
