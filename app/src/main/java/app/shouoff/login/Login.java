package app.shouoff.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import app.shouoff.R;
import app.shouoff.activity.HomeActivity;
import app.shouoff.common.Alerts;
import app.shouoff.common.CommonService;
import app.shouoff.common.Constants;
import app.shouoff.common.MyTextWatcher;
import app.shouoff.common.SharedPreference;
import app.shouoff.common.TextValidation;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class Login extends AppCompatActivity implements View.OnClickListener,RetrofitResponse,View.OnFocusChangeListener
{
    TextView login_button,forgot,new_account;
    ImageView backarrow;
    private EditText username,password;
    private TextInputLayout username_layout,password_layout;
    private Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findId();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (!Settings.canDrawOverlays(this))
            {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            }
        }
    }

    private void findId()
    {
        forgot=(TextView)findViewById(R.id.forgot);
        new_account=(TextView)findViewById(R.id.new_account);
        login_button=(TextView)findViewById(R.id.login_button);
        backarrow=(ImageView)findViewById(R.id.backarrow);

        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);

        username_layout=(TextInputLayout)findViewById(R.id.username_layout);
        password_layout=(TextInputLayout)findViewById(R.id.password_layout);

        username.addTextChangedListener(new MyTextWatcher(context,username,username_layout));
        password.addTextChangedListener(new MyTextWatcher(context,password,password_layout));

        password.setOnFocusChangeListener(this);

        backarrow.setOnClickListener(this);
        forgot.setOnClickListener(this);
        login_button.setOnClickListener(this);
        new_account.setOnClickListener(this);
    }

    private boolean validations()
    {
        if (!TextValidation.validateName(context,username,username_layout,"Enter Valid Username"))
        {
            return false;
        }
        if (!TextValidation.validatePassword(context,password,password_layout,getString(R.string.v_enter_password)))
        {
            return false;
        }
        return true;
    }

    private void setLoginService()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("email",username.getText().toString());
            object.put("password",password.getText().toString());
            new Retrofit2(context,this,1, Constants.login,object).callService(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.backarrow:
                onBackPressed();
                break;
            case R.id.login_button:
                if (validations())
                {
                    setLoginService();
                }
                break;
            case R.id.forgot:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
            case R.id.new_account:
                SharedPreference.removeKey(context,"username_reg");
                SharedPreference.removeKey(context,"game_reg");
                SharedPreference.removeKey(context,"country_id_reg");
                SharedPreference.removeKey(context,"city_id_reg");
                SharedPreference.removeKey(context,"country_name_reg");
                SharedPreference.removeKey(context,"city_name_reg");

                startActivity(new Intent(this, Register.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onServiceResponse(int requestCode, Response<ResponseBody> response)
    {
        try
        {
            JSONObject result=new JSONObject(response.body().string().toString());
            Log.e("result",""+result.toString());
            boolean status=result.getBoolean("response");
            if (status)
            {
                switch (requestCode)
                {
                    case 1:
                        JSONObject data=result.getJSONObject("data");
                        SharedPreference.storeAndParseJsonData(context,data);

                        /*Call Service Add Device*/
                        new CommonService().serviceAddDevice(context,data.getString("id"));

                        Toast.makeText(context, R.string.success_login, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, HomePage.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP|
                                        Intent.FLAG_ACTIVITY_NEW_TASK));

                        View view= Login.this.getCurrentFocus();
                        Constants.hideKeyboard(context,view);
                        break;
                }
            }
            else
            {
                if (result.getString("message").equalsIgnoreCase("Account not verified"))
                {
                    showDialog(context,result.getJSONObject("data").getString("id"));
                }
                else
                {
                    Alerts.showDialog(context,"",result.getString("message"));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void showDialog(final Context context, final String user_id)
    {
        TextView txt_msg, ok;
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.custom_alert_dialog);
        LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.verify_message, null);
        txt_msg = (TextView) dialogView.findViewById(R.id.message);
        txt_msg.setText("Your Account is not verified Please verify your account");
        ok = (TextView) dialogView.findViewById(R.id.submit);
        dialogBuilder.setView(dialogView);
        final android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.80);
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setLayout(width, height);
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                alertDialog.dismiss();
                startActivity(new Intent(context,OtpScreen.class)
                .putExtra("user_id",user_id));
            }
        });
        alertDialog.show();
    }


    @Override
    public void onFocusChange(View view, boolean hasFocus)
    {
        switch (view.getId())
        {
            case R.id.password:
                Constants.focus(password, hasFocus, R.drawable.eye_blue, R.drawable.eye_black);
                break;
        }
    }
}
