package app.shouoff.common;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import app.shouoff.R;

public class TextValidation
{
    public static Context mContext;

    public static boolean validateName(Context mContext, EditText inputName, TextInputLayout inputLayoutName, String message) {
        TextValidation.mContext = mContext;
        if (inputName.getText().toString().trim().isEmpty())
        {
            inputLayoutName.setError(message);
            requestFocus(inputName);
            return false;
        }
        else
        {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }

    public static boolean validateEmail(Context mContext, EditText inputEmail, TextInputLayout inputLayoutEmail)
    {
        String email = inputEmail.getText().toString().trim();
        TextValidation.mContext = mContext;
        if (email.isEmpty())
        {
            inputLayoutEmail.setError(mContext.getString(R.string.v_enter_email));
            requestFocus(inputEmail);
            return false;
        } else if (inputEmail.getText().toString().startsWith(" ")|| !isValidEmail(email)) {
            inputLayoutEmail.setError(mContext.getString(R.string.v_email_not_valid));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }
        return true;
    }

    public static boolean validateNumber(Context mContext, EditText inputPassword, TextInputLayout inputLayoutPassword, String message) {
        TextValidation.mContext = mContext;
        if (inputPassword.getText().toString().trim().isEmpty())
        {
            inputLayoutPassword.setError(message);
            requestFocus(inputPassword);
            return false;
        }else if (inputPassword.getText().toString().trim().length() < 10)
        {
            inputLayoutPassword.setError(mContext.getString(R.string.v_number_not_valid));
            requestFocus(inputPassword);
            return false;
        }
        else {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    public static boolean validatePassword(Context mContext, EditText inputPassword, TextInputLayout inputLayoutPassword, String message) {
        TextValidation.mContext = mContext;
        if (inputPassword.getText().toString().trim().isEmpty())
        {
            inputLayoutPassword.setError(message);
            requestFocus(inputPassword);
            return false;
        }
        else if (inputPassword.getText().toString().trim().length() < 4)
        {
            inputLayoutPassword.setError(mContext.getString(R.string.v_password_short));
            requestFocus(inputPassword);
            return false;
        }
        else
        {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidEmail(String email)
    {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static void requestFocus(View view) {
        if (view.requestFocus()) {
            ((Activity) mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
