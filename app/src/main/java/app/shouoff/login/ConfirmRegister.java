package app.shouoff.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
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
import app.shouoff.common.TextValidation;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ConfirmRegister extends AppCompatActivity implements View.OnClickListener,RetrofitResponse
{
    private TextInputLayout user_name_layout,password_layout,confirm_pass_layout;
    private EditText username,password,confirm_password;
    private Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_register);

        initialized();
    }

    private void initialized()
    {
        ImageView backarrow = (ImageView) findViewById(R.id.backarrow);

        user_name_layout=(TextInputLayout)findViewById(R.id.user_name_layout);
        password_layout=(TextInputLayout)findViewById(R.id.password_layout);
        confirm_pass_layout=(TextInputLayout)findViewById(R.id.confirm_pass_layout);

        username=(EditText)findViewById(R.id.username);
        confirm_password=(EditText)findViewById(R.id.confirm_password);
        password=(EditText)findViewById(R.id.password);

        username.addTextChangedListener(new MyTextWatcher(context,username,user_name_layout));
        password.addTextChangedListener(new MyTextWatcher(context,password,password_layout));
        confirm_password.addTextChangedListener(new MyTextWatcher(context,confirm_password,confirm_pass_layout));

        TextView submit_btn = (TextView) findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(this);
        backarrow.setOnClickListener(this);
    }

    private boolean validations()
    {
        if (!TextValidation.validateName(context,username,user_name_layout,getString(R.string.v_enter_username)))
        {
            return false;
        }
        if (!TextValidation.validatePassword(context,password,password_layout,getString(R.string.v_enter_password)))
        {
            return false;
        }
        if (!TextValidation.validatePassword(context,confirm_password,confirm_pass_layout,getString(R.string.v_enter_confirm_password)))
        {
            return false;
        }

        if (!confirm_password.getText().toString().equalsIgnoreCase(password.getText().toString()))
        {
            confirm_password.setError(getString(R.string.v_not_match));
            confirm_password.requestFocus();
            return false;
        }
        return true;
    }

    private void setRegisterService()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("other_game",getIntent().getStringExtra("other_game"));
            object.put("father_name",getIntent().getStringExtra("father_name"));
            object.put("mother_name",getIntent().getStringExtra("mother_name"));
            object.put("gardian_contact",getIntent().getStringExtra("contact"));
            object.put("first_name",getIntent().getStringExtra("name"));
            object.put("family_name",getIntent().getStringExtra("family_name"));
            object.put("dob",getIntent().getStringExtra("dob"));
            object.put("email",getIntent().getStringExtra("email"));
            object.put("nick_name",getIntent().getStringExtra("nick"));
            object.put("school_name",getIntent().getStringExtra("school"));
            object.put("goi",getIntent().getStringExtra("games").substring(0,getIntent().getStringExtra("games").length()-1));
            object.put("country",getIntent().getStringExtra("country"));
            object.put("city",getIntent().getStringExtra("city"));
            object.put("username",username.getText().toString());
            object.put("password",confirm_password.getText().toString());

            new Retrofit2(context,this,1, Constants.register,object).callService(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.submit_btn:
                if (validations())
                {
                    setRegisterService();
                }
                break;
            case R.id.backarrow:
                finish();
                break;
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
                        JSONObject data=result.getJSONObject("data");
                        Toast.makeText(context, result.getString("message"),Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context, OtpScreen.class).putExtra("user_id",data.getString("id")));
                    break;
                }
            }
            else
            {
                Toast.makeText(context, result.getString("Message"),Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
