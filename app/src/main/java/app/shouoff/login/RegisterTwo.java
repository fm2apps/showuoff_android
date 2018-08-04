package app.shouoff.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import app.shouoff.R;
import app.shouoff.appsettings.TermsPrivacy;
import app.shouoff.baseadapter.CityLocationAdapter;
import app.shouoff.baseadapter.GamesAdapter;
import app.shouoff.baseadapter.LocationAdapter;
import app.shouoff.common.Alerts;
import app.shouoff.common.Constants;
import app.shouoff.common.MyTextWatcher;
import app.shouoff.common.SharedPreference;
import app.shouoff.common.TextValidation;
import app.shouoff.model.GamesModel;
import app.shouoff.model.LocationModel;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class RegisterTwo extends AppCompatActivity implements RetrofitResponse,View.OnClickListener
{
    private MultiAutoCompleteTextView game;
    private List<GamesModel> gamesModels=new ArrayList<>();
    private List<GamesModel> temp1=new ArrayList<>();
    private List<String> temp=new ArrayList<>();
    String[] tag;

    private AutoCompleteTextView city,country;
    private List<LocationModel> locationModelList=new ArrayList<>();
    private List<LocationModel> modelList=new ArrayList<>();
    private String country_id="null",city_id="null";
    private ProgressBar progress_city;
    private Context context=this;
    private CheckBox terms;
    private TextView checktext;
    private TextInputLayout game_layout,
            other_game_layout,country_layout,city_layout,user_name_layout,password_layout,confirm_pass_layout;
    private EditText other_game_name,username,password,confirm_password;
    private boolean fABoolean =false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_two);

        initialized();
        new Retrofit2(context,this,0,Constants.countryGame,"").callService(true);
    }

    private void initialized()
    {
        checktext=findViewById(R.id.checktext);
        terms=(CheckBox)findViewById(R.id.terms);
        ImageView backarrow = (ImageView) findViewById(R.id.backarrow);
        progress_city=(ProgressBar)findViewById(R.id.progress_city);
        TextView submit_btn = (TextView) findViewById(R.id.submit_btn);

        other_game_layout=(TextInputLayout)findViewById(R.id.other_game_layout);
        game_layout=(TextInputLayout)findViewById(R.id.game_layout);
        country_layout=(TextInputLayout)findViewById(R.id.country_layout);
        city_layout=(TextInputLayout)findViewById(R.id.city_layout);

        other_game_name=(EditText)findViewById(R.id.other_game_name);

        game=(MultiAutoCompleteTextView)findViewById(R.id.game_select);

        city=(AutoCompleteTextView)findViewById(R.id.city);
        country=(AutoCompleteTextView)findViewById(R.id.country);

        user_name_layout=(TextInputLayout)findViewById(R.id.user_name_layout);
        password_layout=(TextInputLayout)findViewById(R.id.password_layout);
        confirm_pass_layout=(TextInputLayout)findViewById(R.id.confirm_pass_layout);

        username=(EditText)findViewById(R.id.username);
        confirm_password=(EditText)findViewById(R.id.confirm_password);
        password=(EditText)findViewById(R.id.password);

        username.addTextChangedListener(new MyTextWatcher(context,username,user_name_layout));
        password.addTextChangedListener(new MyTextWatcher(context,password,password_layout));
        confirm_password.addTextChangedListener(new MyTextWatcher(context,confirm_password,confirm_pass_layout));

        country.addTextChangedListener(new MyTextWatcher(context,country,country_layout));
        //city.addTextChangedListener(new MyTextWatcher(context,city,city_layout));
        game.addTextChangedListener(new MyTextWatcher(context,game,game_layout));
        other_game_name.addTextChangedListener(new MyTextWatcher(context,other_game_name,other_game_layout));

        city.setFocusableInTouchMode(false);
        country.setFocusableInTouchMode(false);

        city.setFocusable(false);
        country.setFocusable(false);

        game.setFocusableInTouchMode(false);
        game.setFocusable(false);

        city.setEnabled(false);

        if (SharedPreference.retriveData(context,"country_id_reg")!=null)
        {
            city.setText(SharedPreference.retriveData(context,"city_name_reg"));
            country.setText(SharedPreference.retriveData(context,"country_name_reg"));
            username.setText(SharedPreference.retriveData(context,"username_reg"));

            city_id=SharedPreference.retriveData(context,"city_id_reg");
            country_id=SharedPreference.retriveData(context,"country_id_reg");
        }

        game.setOnClickListener(this);
        backarrow.setOnClickListener(this);
        city.setOnClickListener(this);
        country.setOnClickListener(this);
        submit_btn.setOnClickListener(this);

        terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                    fABoolean=true;
                else
                    fABoolean=false;
            }
        });

        customTextView(checktext,context);
    }

    /*Term and condition text with hyper link*/
    public void customTextView(TextView view, final Context context)
    {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder("Please confirm ");
        spanTxt.append(context.getString(R.string.terms));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget)
            {
                startActivity(new Intent(context, TermsPrivacy.class));
            }
        }, spanTxt.length() - context.getString(R.string.terms).length(), spanTxt.length(), 0);
        spanTxt.append(" and");
        spanTxt.setSpan(new ForegroundColorSpan(Color.BLACK), spanTxt.length() - " and".length(),
                spanTxt.length(), 0);
        spanTxt.append(" Privacy Policy");
        spanTxt.setSpan(new ClickableSpan()
        {
            @Override
            public void onClick(View widget)
            {
                startActivity(new Intent(context, TermsPrivacy.class).putExtra("policy","policy"));
            }
        }, spanTxt.length() - " Privacy Policy".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    // TODO: 12/3/18 Games Adapter
    private void gamesAdapter()
    {
        if (SharedPreference.retriveData(context,"game_reg")!=null)
        {
            String[] tag1=SharedPreference.retriveData(context,"game_reg").split(",");

            if (tag1.length > 0) {
                temp.clear();
                Collections.addAll(temp, tag1);
            }

            temp1.clear();
            StringBuilder stringBuilder=new StringBuilder();
            for (int k = 0; k < gamesModels.size(); k++)
            {
                for (int L = 0; L < temp.size(); L++)
                {
                    if (temp.get(L).equalsIgnoreCase(gamesModels.get(k).getId()))
                    {
                        stringBuilder.append(gamesModels.get(k).getName());
                        stringBuilder.append(", ");
                        temp1.add(new GamesModel(gamesModels.get(k).getId(),gamesModels.get(k).getName()));
                    }
                }
            }

            game.setText(String.valueOf(stringBuilder).subSequence(0,String.valueOf(stringBuilder).length()-2));
            game.setText(Constants.checkRepeated(game.getText().toString()));
        }

        GamesAdapter gamesAdapter = new GamesAdapter(context, R.layout.custom_text, gamesModels);
        game.setAdapter(gamesAdapter);
        game.setThreshold(1);
        game.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        game.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {

                if (game.getText().toString().contains(gamesModels.get(i).getName()))
                {
                    game.setText(Constants.checkRepeated(game.getText().toString()));
                    tag=game.getText().toString().split(",");

                    if (tag.length > 0) {
                        temp.clear();
                        Collections.addAll(temp, tag);
                    }

                    temp1.clear();
                    for (int k = 0; k < gamesModels.size(); k++)
                    {
                        for (int L = 0; L < temp.size(); L++)
                        {
                            if (temp.get(L).contains(gamesModels.get(k).getName()))
                            {
                                temp1.add(new GamesModel(gamesModels.get(k).getId(),gamesModels.get(k).getName()));
                            }
                        }
                    }
                }

                if (game.getText().toString().contains("Other"))
                {
                    other_game_layout.setVisibility(View.VISIBLE);
                }
                else
                {
                    other_game_layout.setVisibility(View.GONE);
                    other_game_name.setText("");
                }
            }
        });
    }

    // TODO: 12/3/18 Country Adapter
    private void countryAdapter()
    {
        LocationAdapter locationAdapter = new LocationAdapter(context, R.layout.custom_text, locationModelList);
        country.setAdapter(locationAdapter);
        country.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                country_id=locationModelList.get(i).getId();
                city.setText("");

                progress_city.setVisibility(View.VISIBLE);
                getCitiesService(i);
            }
        });
    }

    // TODO: 12/3/18 Get Cities
    private void getCitiesService(int pos)
    {
        new Retrofit2(context,this,2, Constants.countryCity+"/"+locationModelList.get(pos).getId()).callServiceBack();
    }

    private void cityAdapter()
    {
        CityLocationAdapter cityLocationAdapter = new CityLocationAdapter(context, R.layout.custom_text, modelList);
        city.setAdapter(cityLocationAdapter);
        city.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                city_id=modelList.get(i).getId();
            }
        });
    }

    private boolean validations()
    {
        if (!TextValidation.validateName(context,game,game_layout,getString(R.string.v_enter_your_game)))
        {
            return false;
        }

        if (!TextValidation.validateName(context,country,country_layout,getString(R.string.v_choose_country)))
        {
            return false;
        }
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
        if (!fABoolean)
        {
            Alerts.showDialog(context,"","Please accept Terms & Conditions and Privacy Policy");
            return false;
        }
        return true;
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
                     case 0:
                         JSONObject data=result.getJSONObject("data");

                       /* Country*/
                        locationModelList.clear();
                        JSONArray country=data.getJSONArray("country");
                        if (country.length()>0)
                        {
                            for (int i=0;i<country.length();i++)
                            {
                                JSONObject value=country.getJSONObject(i);
                                LocationModel locationModel=new LocationModel(value.getString("id"),
                                        value.getString("name"));
                                locationModelList.add(locationModel);
                            }
                        }


                         countryAdapter();

                      /*  Games*/
                        gamesModels.clear();
                        JSONArray games=data.getJSONArray("game");
                        if (games.length()>0)
                        {
                            for (int i=0;i<games.length();i++)
                            {
                                JSONObject value=games.getJSONObject(i);
                                GamesModel locationModel=new GamesModel(value.getString("id"),
                                        value.getString("name"));
                                gamesModels.add(locationModel);
                            }
                            gamesModels.add(new GamesModel("0","Other"));
                            gamesAdapter();
                        }
                        break;
                    case 2:
                        progress_city.setVisibility(View.GONE);
                        modelList.clear();
                        city.setEnabled(true);
                        JSONArray city=result.getJSONArray("data");
                        if (city.length()>0)
                        {
                            for (int i=0;i<city.length();i++)
                            {
                                JSONObject value=city.getJSONObject(i);
                                LocationModel locationModel=new LocationModel(value.getString("id"),
                                        value.getString("name"));
                                modelList.add(locationModel);
                            }
                        }
                        cityAdapter();
                        break;
                    case 1:
                        JSONObject data1=result.getJSONObject("data");
                        Toast.makeText(context, result.getString("message"),Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context, OtpScreen.class)
                                .putExtra("user_id",data1.getString("id")));

                        SharedPreference.removeKey(context,"username_reg");
                        SharedPreference.removeKey(context,"game_reg");
                        SharedPreference.removeKey(context,"country_id_reg");
                        SharedPreference.removeKey(context,"city_id_reg");
                        SharedPreference.removeKey(context,"country_name_reg");
                        SharedPreference.removeKey(context,"city_name_reg");
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

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.submit_btn:
                if (validations())
                {
                    setRegisterService();
                }
                break;
            case R.id.backarrow:
                onBackPressed();
                break;
            case R.id.city:
                showDropDown(view);
                break;
            case R.id.country:
                showDropDown(view);
                break;
            case R.id.game_select:
                showDropDown(view);
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        setFillData();
        finish();
    }

    private void setFillData()
    {
        if (!username.getText().toString().equalsIgnoreCase(""))
        {
            SharedPreference.storeData(context,"username_reg",username.getText().toString());
        }

        if (!game.getText().toString().equalsIgnoreCase(""))
        {
            StringBuilder stringBuilder=new StringBuilder();
            for (int i=0;i<temp1.size();i++)
            {
                stringBuilder.append(temp1.get(i).getId());
                stringBuilder.append(",");
            }
            String game=stringBuilder.toString().replace("0,","");
            SharedPreference.storeData(context,"game_reg",game);
        }

        if (!country_id.equalsIgnoreCase(""))
        {
            SharedPreference.storeData(context,"country_name_reg",country.getText().toString());
            SharedPreference.storeData(context,"country_id_reg",country_id);
        }

        if (!city_id.equalsIgnoreCase(""))
        {
            SharedPreference.storeData(context,"city_id_reg",city_id);
            SharedPreference.storeData(context,"city_name_reg",city.getText().toString());
        }
    }

    /* *
    * Set Service Register
    * */
    private void setRegisterService()
    {
        StringBuilder stringBuilder=new StringBuilder();
        for (int i=0;i<temp1.size();i++)
        {
            stringBuilder.append(temp1.get(i).getId());
            stringBuilder.append(",");
        }
        String game=stringBuilder.toString().replace("0,","");

        JSONObject object=new JSONObject();
        try
        {
            object.put("other_game",other_game_name.getText().toString());
            object.put("father_name",getIntent().getStringExtra("father_name"));
            object.put("mother_name",getIntent().getStringExtra("gender"));
            object.put("gardian_contact",getIntent().getStringExtra("contact"));
            object.put("first_name",getIntent().getStringExtra("name"));
            object.put("family_name",getIntent().getStringExtra("family_name"));
            object.put("dob",getIntent().getStringExtra("dob"));
            object.put("email",getIntent().getStringExtra("email"));
            object.put("nick_name",getIntent().getStringExtra("nick"));
            object.put("goi",game.substring(0,game.length()-1));
            object.put("country",country_id);
            object.put("city",city_id);
            object.put("username",username.getText().toString());
            object.put("password",confirm_password.getText().toString());

            new Retrofit2(context,this,1, Constants.register,object).callService(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDropDown(View v)
    {
        InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert in != null;
        in.hideSoftInputFromWindow(v.getWindowToken(),0);
        ((AutoCompleteTextView)v).showDropDown();
    }
}
