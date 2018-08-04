package app.shouoff.login;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.common.MyTextWatcher;
import app.shouoff.common.TextValidation;

public class Register extends AppCompatActivity implements View.OnClickListener
{
    private DatePickerDialog.OnDateSetListener datee;
    private Calendar myCalendar;
    private Context context=this;
    public AutoCompleteTextView gender;
    private String[] userGender={"Boy","Girl"};
    private EditText name,family_name,date_of_birth,email,nick_name,guardian_father_name,contact;
    private TextInputLayout name_layout,family_name_layout,dob_layout,email_layout,nick_layout,guardian_layout,gender_layout,contact_layout;
    private DatePickerDialog pickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialized();
    }

    /*
    Initialized View
    * */
    private void initialized()
    {
        ImageView backarrow = (ImageView) findViewById(R.id.backarrow);

        contact_layout=(TextInputLayout)findViewById(R.id.contact_layout);
        name_layout=(TextInputLayout)findViewById(R.id.name_layout);
        family_name_layout=(TextInputLayout)findViewById(R.id.family_name_layout);
        dob_layout=(TextInputLayout)findViewById(R.id.dob_layout);
        email_layout=(TextInputLayout)findViewById(R.id.email_layout);
        nick_layout=(TextInputLayout)findViewById(R.id.nick_layout);
        guardian_layout=(TextInputLayout)findViewById(R.id.guardian_layout);
        gender_layout=(TextInputLayout)findViewById(R.id.gender_layout);

        gender=(AutoCompleteTextView) findViewById(R.id.gender);

        contact=(EditText)findViewById(R.id.contact);
        guardian_father_name=(EditText)findViewById(R.id.guardian_father_name);
        name=(EditText)findViewById(R.id.name);
        family_name=(EditText)findViewById(R.id.family_name);
        email=(EditText)findViewById(R.id.email);
        date_of_birth=(EditText)findViewById(R.id.date_of_birth);
        nick_name=(EditText)findViewById(R.id.nick_name);

        Constants.alphabet(nick_name);
        Constants.alphabet(name);
        Constants.alphabet(family_name);


        TextView next_btn = (TextView) findViewById(R.id.next_btn);

        gender.setFocusableInTouchMode(false);
        gender.setFocusable(false);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_dropdown_item, userGender);
        gender.setAdapter(adapter);

        contact.addTextChangedListener(new MyTextWatcher(context,contact,contact_layout));
        name.addTextChangedListener(new MyTextWatcher(context,name,name_layout));
        family_name.addTextChangedListener(new MyTextWatcher(context,family_name,family_name_layout));
        date_of_birth.addTextChangedListener(new MyTextWatcher(context,date_of_birth,dob_layout));
        email.addTextChangedListener(new MyTextWatcher(context,email,email_layout));
        nick_name.addTextChangedListener(new MyTextWatcher(context,nick_name,nick_layout));
        guardian_father_name.addTextChangedListener(new MyTextWatcher(context,guardian_father_name,guardian_layout));
        gender.addTextChangedListener(new MyTextWatcher(context,gender,gender_layout));

        myCalendar = Calendar.getInstance();
        datee = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth)
            {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                date_of_birth.setText(sdf.format(myCalendar.getTime()));
            }
        };


        date_of_birth.setOnClickListener(this);
        gender.setOnClickListener(this);
        next_btn.setOnClickListener(this);
        backarrow.setOnClickListener(this);
    }

   /* *
    * Validations
    * */
    private boolean validations()
    {
        if (!TextValidation.validateName(context,date_of_birth,dob_layout,getString(R.string.v_select_dob)))
        {
            return false;
        }
        if (!TextValidation.validateName(context,nick_name,nick_layout,getString(R.string.v_enter_your_nick_name)))
        {
            return false;
        }
        if (!TextValidation.validateName(context,gender,gender_layout,getString(R.string.v_select_gender)))
        {
            return false;
        }
        if (!TextValidation.validateName(context,guardian_father_name,guardian_layout,getString(R.string.v_father_name)))
        {
            return false;
        }
        if (!TextValidation.validateEmail(context,email,email_layout))
        {
            return false;
        }
        if (!TextValidation.validateName(context,contact,contact_layout,getString(R.string.v_contect)))
        {
            return false;
        }
        if (contact.getText().toString().length()<14)
        {
            contact_layout.setError(context.getString(R.string.v_number_not_valid));
            contact.setFocusable(true);
            return false;
        }
        return true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {

            case R.id.next_btn:
                if (validations())
                {
                    startActivity(new Intent(context,RegisterTwo.class)
                    .putExtra("name",name.getText().toString())
                            .putExtra("family_name",family_name.getText().toString())
                            .putExtra("dob",date_of_birth.getText().toString())
                            .putExtra("email",email.getText().toString())
                            .putExtra("nick",nick_name.getText().toString())
                            .putExtra("contact",contact.getText().toString())
                            .putExtra("father_name",guardian_father_name.getText().toString())
                            .putExtra("gender",gender.getText().toString()));
                }
                break;
            case R.id.date_of_birth:
                pickerDialog = new DatePickerDialog(context, datee, myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                Calendar c = Calendar.getInstance();
                c.add(Calendar.YEAR, -16);
                pickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                c.add(Calendar.YEAR, 12);
                pickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                pickerDialog.show();
                break;
            case R.id.backarrow:
                finish();
                break;
            case R.id.gender:
                showDropDown(view);
                break;
        }
    }

    private void showDropDown(View v)
    {
        InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(v.getWindowToken(),0);
        ((AutoCompleteTextView)v).showDropDown();
    }
}
