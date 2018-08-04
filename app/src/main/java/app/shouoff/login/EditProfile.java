package app.shouoff.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.test.mock.MockPackageManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import app.shouoff.Croping.Croping;
import app.shouoff.R;
import app.shouoff.app.App;
import app.shouoff.baseadapter.CityLocationAdapter;
import app.shouoff.baseadapter.GamesAdapter;
import app.shouoff.baseadapter.LocationAdapter;
import app.shouoff.common.Alerts;
import app.shouoff.common.ConnectionDetector;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.drawer.Drawer;
import app.shouoff.model.GamesModel;
import app.shouoff.model.LocationModel;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class EditProfile extends Drawer implements RetrofitResponse
{
    private AutoCompleteTextView city,country;
    private MultiAutoCompleteTextView game;
    private Context context=this;
    android.support.v7.app.AlertDialog alertDialog;
    private CircleImageView profile_image;

    File file,video_path;
    private MultipartBody.Part body;
    private ProgressBar progress_city;

    private EditText name,family_name,date_of_birth,email,nick_name,about,guardian_father_name,
            gender,contact,other_game_name;
    private TextInputLayout other_game_layout;

    private List<GamesModel> gamesModels=new ArrayList<>();
    private List<GamesModel> temp1=new ArrayList<>();
    private List<String> temp=new ArrayList<>();
    private List<LocationModel> locationModelList=new ArrayList<>();
    private List<LocationModel> modelList=new ArrayList<>();
    private List<GamesModel> game_temp=new ArrayList<>();
    String s;
    private String country_id="",city_id="",search="";
    String[] tag,tag1,school_tag,school_id;
    String[] mPermission = {android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private DatePickerDialog pickerDialog;
    private DatePickerDialog.OnDateSetListener datee;
    private Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        checkPermission();
        initialized();
        new Retrofit2(context,this,0,Constants.countryGame,"").callService(true);
    }

    @SuppressLint("SetTextI18n")
    private void initialized()
    {
        TextView next_btn = findViewById(R.id.next_btn);
        CardView pick_image = findViewById(R.id.pick_image);
        profile_image=findViewById(R.id.profile_image);
        progress_city=findViewById(R.id.progress_city);
        other_game_layout=findViewById(R.id.other_game_layout);

        other_game_name=findViewById(R.id.other_game_name);
        guardian_father_name=findViewById(R.id.guardian_father_name);
        gender=findViewById(R.id.gender);
        contact=findViewById(R.id.contact);
        name=(findViewById(R.id.name));
        family_name=findViewById(R.id.family_name);
        email=findViewById(R.id.email);
        date_of_birth=findViewById(R.id.date_of_birth);
        nick_name=findViewById(R.id.nick_name);

        Constants.alphabet(nick_name);
        Constants.alphabet(name);
        Constants.alphabet(family_name);

        about=(findViewById(R.id.about));

        city=findViewById(R.id.city);
        country=findViewById(R.id.country);
        game=findViewById(R.id.game_select);

        city.setEnabled(false);

        city.setFocusable(false);
        city.setFocusableInTouchMode(false);

        game.setFocusable(false);
        game.setFocusableInTouchMode(false);

        country.setFocusable(false);
        country.setFocusableInTouchMode(false);

        title1.setText(SharedPreference.retriveData(context, Constants.NICK_NAME));

        ((Drawer)context).setBottomBar(R.drawable.discovery_gray,
                R.drawable.profile_colored,
                R.drawable.creatpost_gray,
                R.drawable.notification_gray,
                R.drawable.moregray,
                profile,discovery,creat_post,notification,more);
        NestedScrollView nest_scroll = findViewById(R.id.nest_scroll);
        nest_scroll.smoothScrollTo(0,0);

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
        city.setOnClickListener(this);
        game.setOnClickListener(this);
        pick_image.setOnClickListener(this);
        country.setOnClickListener(this);
        next_btn.setOnClickListener(this);
        setData();
    }

    // TODO: 13/3/18  Set Data Fields
    private void setData()
    {
        name.setText(SharedPreference.retriveData(context, Constants.NAME));
        family_name.setText(SharedPreference.retriveData(context, Constants.FAMILY_NAME));


        if (SharedPreference.retriveData(context, Constants.GENDER)!=null)
        {
            gender.setOnClickListener(null);
        }

        date_of_birth.setText(SharedPreference.retriveData(context, Constants.DOB));
        email.setText(SharedPreference.retriveData(context, Constants.EMAIL));
        nick_name.setText(SharedPreference.retriveData(context, Constants.NICK_NAME));
        gender.setText(SharedPreference.retriveData(context, Constants.GENDER));

        if (SharedPreference.retriveData(context,Constants.COUNTRY).equalsIgnoreCase("empty"))
        {
            country.setText("");
        }
        else
        {
            country.setText(SharedPreference.retriveData(context,Constants.COUNTRY));
        }

        if (SharedPreference.retriveData(context,Constants.CITY).equalsIgnoreCase("empty"))
        {
            city.setText("");
        }
        else
        {
            city.setText(SharedPreference.retriveData(context,Constants.CITY));
        }

        about.setText(SharedPreference.retriveData(context, Constants.ABOUT));
        guardian_father_name.setText(SharedPreference.retriveData(context, Constants.FATHER));
        contact.setText(SharedPreference.retriveData(context, Constants.CONTACT));

        Picasso.with(context).load(Constants.PROFILE_IMAGE_URL + SharedPreference.retriveData(context, Constants.Image))
                .placeholder(R.drawable.noimage).into(profile_image);

        city_id = (SharedPreference.retriveData(context, Constants.CITY_ID));
        country_id = (SharedPreference.retriveData(context, Constants.COUNTRY_ID));

        if (SharedPreference.retriveData(context,Constants.GAMES_NAME)!=null)
        {
            game_temp.clear();
            school_tag=SharedPreference.retriveData(context,Constants.GAMES_NAME).split(",");
            school_id=SharedPreference.retriveData(context,Constants.GAME).split(",");
            if (school_tag.length > 0)
            {
                for (int j = 0; j < school_tag.length; j++)
                {
                    game_temp.add(new GamesModel(school_id[j],school_tag[j]));
                }
            }
        }

    }

    private void setServiceEditProfile()
    {
        HashMap<String, RequestBody> map = new HashMap<>();

        StringBuilder stringBuilder=new StringBuilder("");

        List<String> temp = new ArrayList<>();
        for (int i=0;i<temp1.size();i++)
        {
            temp.add(temp1.get(i).getId());
        }

        Set<String> hs1 = new LinkedHashSet<String>(temp);
        List<String> al2 = new ArrayList<>(hs1);
        for (int i=0;i<al2.size();i++)
        {
            stringBuilder.append(al2.get(i));
            stringBuilder.append(",");
        }

        if (stringBuilder.equals(""))
        {
            map.put("goi", RequestBody.create(MediaType.parse("multipart/form-data"),"0"));
        }
        else
        {
            Log.e("String",""+String.valueOf(stringBuilder).substring(0,String.valueOf(stringBuilder).length()-1));
            map.put("goi", RequestBody.create(MediaType.parse("multipart/form-data"),
                    String.valueOf(stringBuilder).substring(0,String.valueOf(stringBuilder).length()-1)));
        }

        map.put("id", RequestBody.create(MediaType.parse("multipart/form-data"), SharedPreference.retriveData(context,Constants.ID)));
        map.put("first_name", RequestBody.create(MediaType.parse("multipart/form-data"), name.getText().toString()));
        map.put("family_name", RequestBody.create(MediaType.parse("multipart/form-data"), family_name.getText().toString()));
        map.put("dob", RequestBody.create(MediaType.parse("multipart/form-data"), date_of_birth.getText().toString()));
        map.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), email.getText().toString()));
        map.put("nick_name", RequestBody.create(MediaType.parse("multipart/form-data"), nick_name.getText().toString()));
        map.put("father_name", RequestBody.create(MediaType.parse("multipart/form-data"), guardian_father_name.getText().toString()));
        map.put("mother_name", RequestBody.create(MediaType.parse("multipart/form-data"), gender.getText().toString()));
        map.put("gardian_contact", RequestBody.create(MediaType.parse("multipart/form-data"), contact.getText().toString()));
        map.put("country", RequestBody.create(MediaType.parse("multipart/form-data"), country_id));
        map.put("city", RequestBody.create(MediaType.parse("multipart/form-data"), city_id));
        map.put("about_us", RequestBody.create(MediaType.parse("multipart/form-data"), about.getText().toString()));
        map.put("other_game", RequestBody.create(MediaType.parse("multipart/form-data"), other_game_name.getText().toString()));

        new Retrofit2(context,this,2,Constants.UpdateUser, map,body,"1").callServiceMultipart(true);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
    }

    private void showDropDown(View v)
    {
        InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert in != null;
        in.hideSoftInputFromWindow(v.getWindowToken(),0);
        ((AutoCompleteTextView)v).showDropDown();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.city:
                showDropDown(view);
                break;
            case R.id.country:
                showDropDown(view);
                break;
            case R.id.game_select:
                showDropDown(view);
                break;
            case R.id.next_btn:
                if (!ConnectionDetector.isInternetAvailable(context))
                {
                    Alerts.showDialog(context,"Oops!","Internet connection is not available!");
                    return;
                }
                if (contact.getText().toString().length()<14)
                {
                    contact.setError(context.getString(R.string.v_number_not_valid));
                    contact.setFocusable(true);
                    return;
                }
                setServiceEditProfile();
                break;
            case R.id.pick_image:
                if (SharedPreference.retriveDataForPermission(context,"never_ask")==null)
                {
                    try {
                        if (ActivityCompat.checkSelfPermission(this, mPermission[0])
                                != MockPackageManager.PERMISSION_GRANTED ||
                                ActivityCompat.checkSelfPermission(this, mPermission[1])
                                        != MockPackageManager.PERMISSION_GRANTED ||
                                ActivityCompat.checkSelfPermission(this, mPermission[2])
                                        != MockPackageManager.PERMISSION_GRANTED)
                        {

                            ActivityCompat.requestPermissions(this,mPermission, 2);
                        }
                        else
                        {
                            showAlertForImageSelection();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Alerts.showDialog(context,"","You declined app permission so, you can't add profile image");
                }
                break;
            case R.id.date_of_birth:
                if (SharedPreference.retriveData(context, Constants.DOB)==null
                        ||SharedPreference.retriveData(context, Constants.DOB).equalsIgnoreCase(""))
                {
                    pickerDialog = new DatePickerDialog(context, datee, myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));

                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.YEAR, -16);
                    pickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                    c.add(Calendar.YEAR, 12);
                    pickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                    pickerDialog.show();
                }
                break;
        }
    }

    /*Alert To Choose Profile Image From Gallery And Camera*/
    private void showAlertForImageSelection()
    {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.custom_alert_dialog);
        LayoutInflater inflater=this.getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.choose_image_layout, null);
        TextView take_photo=dialogView.findViewById(R.id.take_photo);
        TextView from_gallery=dialogView.findViewById(R.id.from_gallery);
        take_photo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dispatchTakePictureIntent();
                alertDialog.dismiss();
            }
        });

        from_gallery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent edit_profile_image = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                edit_profile_image.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());
                startActivityForResult(edit_profile_image,22);
                alertDialog.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setLayout(width, height);
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 33 && resultCode == RESULT_OK)
        {
            // TODO:  image from Camera
            Uri imageUri = Uri.parse(video_path.getPath());
            cropImage(imageUri.getPath());
        }
        else if (requestCode == 22 && resultCode == RESULT_OK && data != null)
        {
            // TODO: 18/4/18 Gallery Image
            try {
                Uri uri = data.getData();
                String path;
                path = Constants.getPath(context,uri);
                cropImage(path);
            } catch (URISyntaxException e)
            {
                e.printStackTrace();
            }
        }
        else if (requestCode == 301 && resultCode==1001)
        {
            file = new File(Objects.requireNonNull(Objects.requireNonNull(data.getExtras()).get("PATH")).toString());
            Picasso.with(context).load(file).placeholder(R.drawable.noimage).into(profile_image);
            RequestBody req = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body= MultipartBody.Part.createFormData("image", file.getName(), req);
        }
    }

    /*Runtime permissions*/
    public void checkPermission()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            int hasCameraPermission = checkSelfPermission(android.Manifest.permission.CAMERA);
            int hasReadPermission = checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int hasWritePermission = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            ArrayList<String> permissionList = new ArrayList<>();

            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(android.Manifest.permission.CAMERA);
            }
            if (hasReadPermission != PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (hasWritePermission != PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!permissionList.isEmpty()) {
                requestPermissions(permissionList.toArray(new String[permissionList.size()]), 2);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case 2:
                for(String permission: permissions)
                {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                        //denied
                        Log.e("denied", permission);
                    }else{
                        if(ActivityCompat.checkSelfPermission(this, permission) ==
                                PackageManager.PERMISSION_GRANTED)
                        {
                            //allowed
                            Log.e("allowed", permission);
                        } else
                        {
                            //set to never ask again
                            SharedPreference.storeDataForPermission(context,"never_ask","never_ask");
                        }
                    }
                }
                break;
            default:
            {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
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
                progress_city.setVisibility(View.VISIBLE);
                search="search";
                getCitiesService(country_id);
            }
        });
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

    // TODO: 12/3/18 Get Cities
    private void getCitiesService(String id)
    {
        new Retrofit2(context,this,1,Constants.countryCity+"/"+id).callServiceBack();
    }

    // TODO: 12/3/18 Games Adapter
    private void gamesAdapter()
    {
        if (SharedPreference.retriveData(context,Constants.GAMES_NAME)!=null)
        {
            tag1=SharedPreference.retriveData(context,Constants.GAMES_NAME).split(",");
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
                    if (temp.get(L).equalsIgnoreCase(gamesModels.get(k).getName()))
                    {
                        stringBuilder.append(gamesModels.get(k).getName());
                        stringBuilder.append(", ");
                        temp1.add(new GamesModel(gamesModels.get(k).getId(),gamesModels.get(k).getName()));
                    }
                }
            }
            game.setText(String.valueOf(stringBuilder).subSequence(0,String.valueOf(stringBuilder).length()-2));
           // game.setText(removeduplicate(game.getText().toString()));
            game.setText(Constants.checkRepeated(game.getText().toString()));
        }

        GamesAdapter gamesAdapter = new GamesAdapter(context, R.layout.custom_text, gamesModels);
        game.setAdapter(gamesAdapter);
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

    public String removeduplicate(String input)
    {
        String out="";
        //convert the string to array by splitting it in words where space comes
        String[] words=input.split(" ");
        //put a for loop for outer comparison where words will start from "i" string
        for(int i=0;i<words.length;i++)
        {
            //check if already duplicate word is replaced with null
            if(words[i]!=null)
            {
                //put a for loop to compare outer words at i with inner word at j
                for(int j =i+1;j<words.length;j++)
                {
                    //if there is any duplicate word then make is Null
                    if(words[i].equals(words[j]))
                    {
                        words[j]=null;
                    }
                }
            }
        }
        //Print the output string where duplicate has been replaced with null
        for(int k=0;k<words.length;k++)
        {
            //check if word is a null then don't print it
            if(words[k]!=null)
            {
                out=out+words[k];
                System.out.print(words[k]+" ");
            }
        }
        return out;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onServiceResponse(int requestCode, Response<ResponseBody> response)
    {
        try
        {
            JSONObject result=new JSONObject(response.body().string());
            boolean status=result.getBoolean("response");
            if (status)
            {
                switch (requestCode)
                {
                    case 0:
                        JSONObject data=result.getJSONObject("data");
                        getCitiesService(country_id);
                        /*Country*/
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
                            countryAdapter();
                        }

                        /*Games*/
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

                            for (int j=0;j<gamesModels.size();j++)
                            {
                                for (int i=0;i<game_temp.size();i++)
                                {
                                    if (game_temp.get(i).getName().equalsIgnoreCase(gamesModels.get(j).getName()))
                                    {
                                        game_temp.remove(i);
                                    }
                                }
                            }

                            if (game_temp.size()>0)
                            {
                               // gamesModels.addAll(game_temp);
                            }

                            gamesModels.add(new GamesModel("0","Other"));
                            gamesAdapter();
                        }
                        break;
                    case 1:
                        progress_city.setVisibility(View.GONE);
                        city.setEnabled(true);
                        modelList.clear();
                        JSONArray city1=result.getJSONArray("data");
                        if (city1.length()>0)
                        {
                            for (int i=0;i<city1.length();i++)
                            {
                                JSONObject value=city1.getJSONObject(i);
                                LocationModel locationModel=new LocationModel(value.getString("id"),
                                        value.getString("name"));
                                modelList.add(locationModel);
                            }

                            if (search.equalsIgnoreCase("search"))
                            {
                                city.setText("");
                                city_id="0";
                            }
                            else
                            {
                                if (!SharedPreference.retriveData(context,Constants.CITY).equalsIgnoreCase("empty"))
                                {
                                    city.setText(SharedPreference.retriveData(context,Constants.CITY));
                                }
                                else
                                {
                                    city.setText("");
                                    city_id="0";
                                }
                            }
                            cityAdapter();
                        }
                        else
                        {
                            city.setText("");
                            city_id="0";
                        }

                        break;
                    case 2:
                        JSONObject updateData=result.getJSONObject("result");
                        Log.e("updateData",""+updateData.toString());
                        SharedPreference.storeAndParseJsonData(context,updateData);
                        Toast.makeText(this, R.string.update_success, Toast.LENGTH_SHORT).show();
                        drawer_name.setText(SharedPreference.retriveData(context,Constants.NICK_NAME));
                        Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+SharedPreference.
                                retriveData(context,Constants.Image)).placeholder(R.drawable.noimage)
                                .into(profile_image_drawer);
                        setData();
                        break;
                }
            }
            else
            {
                switch (requestCode)
                {
                    case 1:
                        city.setText("");
                        city_id="0";
                        break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*Get Image Through Camera*/
    private void dispatchTakePictureIntent()
    {
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());

        if (cameraIntent.resolveActivity(getPackageManager())!=null){
            try{
                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                video_path=new File(App.getQUOTATIONS(),timeStamp+"post.png");
                Uri photoURI = FileProvider.getUriForFile(context, app.shouoff.BuildConfig.APPLICATION_ID + ".provider", video_path);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(cameraIntent,33);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /*Crop Image*/
    private void cropImage(String path)
    {
        Intent intent = new Intent(this, Croping.class);
        intent.putExtra("PROFILE", path);
        startActivityForResult(intent, 301);
    }
}
