package app.shouoff.login;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import app.shouoff.R;
import app.shouoff.common.CommonService;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class OtpScreen extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener,
        TextWatcher,View.OnFocusChangeListener,RetrofitResponse
{
    private TextView submit_btn;
    private EditText one,two,three,four,five,six,seven;
    private Context context=this;
    private String otp="",user_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(new MainLayout(this,null));
        initialized();

        user_id=getIntent().getStringExtra("user_id");

        /*Call Service Add Device*/
        new CommonService().serviceAddDevice(context,user_id);
    }

    private void initialized()
    {
        submit_btn=(TextView)findViewById(R.id.submit_btn);
        one=(EditText)findViewById(R.id.one);
        two=(EditText)findViewById(R.id.two);
        three=(EditText)findViewById(R.id.three);
        four=(EditText)findViewById(R.id.four);
        five=(EditText)findViewById(R.id.five);
        six=(EditText)findViewById(R.id.six);
        seven=(EditText)findViewById(R.id.seven);

        one.setOnFocusChangeListener(this);
        two.setOnFocusChangeListener(this);
        three.setOnFocusChangeListener(this);
        four.setOnFocusChangeListener(this);
        five.setOnFocusChangeListener(this);
        six.setOnFocusChangeListener(this);

        one.setOnKeyListener(this);
        two.setOnKeyListener(this);
        three.setOnKeyListener(this);
        four.setOnKeyListener(this);
        five.setOnKeyListener(this);
        six.setOnKeyListener(this);
        seven.setOnKeyListener(this);

        seven.addTextChangedListener(this);
        submit_btn.setOnClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int i, int i1, int i2)
    {
        if (s.length() == 0)
        {
            one.setText("");
        }
        else if (s.length() == 1)
        {
            one.setText(s.charAt(0) + "");

            two.setText("");
            three.setText("");
            four.setText("");
            five.setText("");
            six.setText("");
        }
        else if (s.length() == 2)
        {
            two.setText(s.charAt(1) + "");

            three.setText("");
            four.setText("");
            five.setText("");
            six.setText("");
        }
        else if (s.length() == 3)
        {
            three.setText(s.charAt(2) + "");

            four.setText("");
            five.setText("");
            six.setText("");
        }
        else if (s.length() == 4)
        {
            four.setText(s.charAt(3) + "");
            five.setText("");
            six.setText("");
        }
        else if (s.length() == 5)
        {
            five.setText(s.charAt(4) + "");
            six.setText("");
        }
        else if (s.length() == 6)
        {
            six.setText(s.charAt(5) + "");
            hideSoftKeyboard(five);
        }
    }

    public void hideSoftKeyboard(EditText editText)
    {
        if (editText == null)
            return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    public static void setFocus(EditText editText) {
        if (editText == null)
            return;
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    @Override
    public void afterTextChanged(Editable editable)
    {

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.submit_btn:
                otp=one.getText().toString()+
                        two.getText().toString()+
                        three.getText().toString()+
                        four.getText().toString()+
                        five.getText().toString()+
                        six.getText().toString();

                if (otp.length()<6)
                {
                    return;
                }
                setotp_verification();
                break;
        }
    }

    private void setotp_verification()
    {

        JSONObject object=new JSONObject();
        try
        {
            object.put("user_id",user_id);
            object.put("otp",otp);
            new Retrofit2(context,this,1,Constants.otp_verification,object).callService(true);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    private void serviceGetFirstNotification()
    {
        new Retrofit2(context,this,3,Constants.RegisterationNotification+user_id).callServiceBack();
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus)
    {
        final int id = view.getId();
        switch (id)
        {
            case R.id.one:
                if (hasFocus)
                {
                    setFocus(seven);
                    showSoftKeyboard(seven);
                }
                break;
            case R.id.two:
                if (hasFocus)
                {
                    setFocus(seven);
                    showSoftKeyboard(seven);
                }
                break;
            case R.id.three:
                if (hasFocus)
                {
                    setFocus(seven);
                    showSoftKeyboard(seven);
                }
                break;
            case R.id.four:
                if (hasFocus)
                {
                    setFocus(seven);
                    showSoftKeyboard(seven);
                }
                break;
            case R.id.five:
                if (hasFocus)
                {
                    setFocus(seven);
                    showSoftKeyboard(seven);
                }
                break;
            case R.id.six:
                if (hasFocus)
                {
                    setFocus(seven);
                    showSoftKeyboard(seven);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
        {
            final int id = view.getId();
            switch (id) {
                case R.id.seven:
                    if (keyCode == KeyEvent.KEYCODE_DEL)
                    {
                        if(seven.getText().length() == 6)
                        {
                            six.setText("");
                        }
                        else if (seven.getText().length() == 5)
                        {
                            five.setText("");
                        }
                        else if (seven.getText().length() == 4)
                        {
                            four.setText("");
                        }
                        else if (seven.getText().length() == 3)
                        {
                            three.setText("");
                        }
                        else if (seven.getText().length() == 2)
                        {
                            two.setText("");
                        }
                        else if (seven.getText().length() == 1)
                        {
                            one.setText("");
                        }

                        if (seven.length() > 0)
                            seven.setText(seven.getText().subSequence(0, seven.length() - 1));

                        return true;
                    }

                    break;

                default:
                    return false;
            }
        }

        return false;
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
                        SharedPreference.storeAndParseJsonData(context,data);
                        serviceGetFirstNotification();
                        clearViews();
                        setFollowService();
                        Toast.makeText(context, result.getString("message"),Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this,HomePage.class));
                        break;
                    case 2:
                        Log.e("Device added","Device");
                        break;
                }
            }
            else
            {
                clearViews();
                Toast.makeText(context, result.getString("message"),Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void clearViews()
    {
        one.setText("");
        two.setText("");
        three.setText("");
        four.setText("");
        five.setText("");
        six.setText("");
        six.setText("");
        seven.setText("");
    }

    public class MainLayout extends LinearLayout
    {

        public MainLayout(Context context, AttributeSet attributeSet)
        {
            super(context, attributeSet);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.activity_otp_screen, this);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int proposedHeight = MeasureSpec.getSize(heightMeasureSpec);
            final int actualHeight = getHeight();
            if (actualHeight >= proposedHeight) {
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void onBackPressed()
    {

    }

    private void setFollowService()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("req_by_user_id", SharedPreference.retriveData(context,Constants.ID));
            object.put("req_to_user_id","1");
            new Retrofit2(context,this,3,Constants.FansReq,object).callService(false);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
