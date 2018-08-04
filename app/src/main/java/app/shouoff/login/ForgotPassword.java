package app.shouoff.login;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.common.MyTextWatcher;
import app.shouoff.common.SharedPreference;
import app.shouoff.common.TextValidation;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener,RetrofitResponse
{
    private TextView submit_btn,resend_code;
    private EditText email;
    private TextInputLayout email_layout;
    private Context context=this;
    private ImageView backarrow;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initialized();
    }

    private void initialized()
    {
        backarrow=(ImageView)findViewById(R.id.backarrow);
        email=(EditText)findViewById(R.id.email);
        email_layout=(TextInputLayout)findViewById(R.id.email_layout);

        resend_code=(TextView)findViewById(R.id.resend_code);
        submit_btn=(TextView)findViewById(R.id.submit_btn);
        email.addTextChangedListener(new MyTextWatcher(context,email,email_layout));

        submit_btn.setOnClickListener(this);
        resend_code.setOnClickListener(this);
        backarrow.setOnClickListener(this);
    }

    private boolean validations()
    {
        if (!TextValidation.validateEmail(context,email,email_layout))
        {
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.submit_btn:
               if (validations())
               {
                   setForgotPasswordService(email.getText().toString());
               }
                break;
            case R.id.backarrow:
                finish();
                break;
            case R.id.resend_code:
                if (validations())
                {
                    setForgotPasswordService(email.getText().toString());
                }
                break;
        }
    }

    private void setForgotPasswordService(String email)
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("email",email);
            new Retrofit2(context,this,1, Constants.ForgetPassword,object).callService(true);

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
                    case 1:
                        Toast.makeText(context, R.string.success_forgot, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            else
            {
                Toast.makeText(context, result.getString("message"), Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
