package app.shouoff.common;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import app.shouoff.R;

public class MyTextWatcher implements TextWatcher
{
    private View view;
    private Context mContext;
    private TextInputLayout inputLayout;

    public MyTextWatcher(Context mContext, View view, TextInputLayout inputLayout)
    {
        this.mContext=mContext;
        this.view=view;
        this.inputLayout=inputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {

    }

    @Override
    public void afterTextChanged(Editable s)
    {
        switch (view.getId())
        {
            case R.id.name:
                TextValidation.validateName(mContext,(EditText)view,inputLayout,mContext.getString(R.string.v_enter_name));
                break;
            case R.id.family_name:
                TextValidation.validateName(mContext,(EditText)view,inputLayout,mContext.getString(R.string.v_enter_family_name));
                break;
            case R.id.date_of_birth:
                TextValidation.validateName(mContext,(EditText)view,inputLayout,mContext.getString(R.string.v_select_dob));
                break;
            case R.id.email:
                TextValidation.validateEmail(mContext,(EditText)view,inputLayout);
                break;
            case R.id.nick_name:
                TextValidation.validateName(mContext,(EditText)view,inputLayout,mContext.getString(R.string.v_enter_your_nick_name));
                break;
            case R.id.game_select:
                TextValidation.validateName(mContext,(EditText)view,inputLayout,mContext.getString(R.string.v_enter_your_game));
                break;
            case R.id.country:
                TextValidation.validateName(mContext,(EditText)view,inputLayout,mContext.getString(R.string.v_choose_country));
                break;
            case R.id.city:
                TextValidation.validateName(mContext,(EditText)view,inputLayout,mContext.getString(R.string.v_choose_city));
                break;
            case R.id.username:
                TextValidation.validateName(mContext,(EditText)view,inputLayout,mContext.getString(R.string.v_enter_username));
                break;
            case R.id.password:
                TextValidation.validatePassword(mContext,(EditText)view,inputLayout,mContext.getString(R.string.v_enter_password));
                break;
            case R.id.confirm_password:
                TextValidation.validatePassword(mContext,(EditText)view,inputLayout,mContext.getString(R.string.v_enter_confirm_password));
                break;
            case R.id.guardian_father_name:
                TextValidation.validateName(mContext,(EditText)view,inputLayout,mContext.getString(R.string.v_father_name));
                break;
            case R.id.gender:
                TextValidation.validateName(mContext,(EditText)view,inputLayout,mContext.getString(R.string.v_select_gender));
                break;
        }
    }
}
