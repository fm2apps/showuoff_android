package app.shouoff.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexvasilkov.gestures.animation.ViewPosition;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import app.shouoff.PostDetails;
import app.shouoff.PostLikedUsers;
import app.shouoff.R;
import app.shouoff.SharePostActivity;
import app.shouoff.adapter.PostAdapter;
import app.shouoff.drawer.Drawer;
import app.shouoff.login.PromoVideo;
import app.shouoff.mediadata.FullImageActivity;
import app.shouoff.model.HomePostModel;
import app.shouoff.model.PostAttachmentModel;
import app.shouoff.model.SearchDataModel;
import app.shouoff.model.TagUserData;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class TempFile
{

}

//package app.shouoff.activity;
//
//        import android.app.Activity;
//        import android.app.AlertDialog;
//        import android.app.DatePickerDialog;
//        import android.content.Context;
//        import android.content.DialogInterface;
//        import android.content.Intent;
//        import android.os.Bundle;
//        import android.support.v4.widget.SwipeRefreshLayout;
//        import android.support.v7.widget.LinearLayoutManager;
//        import android.support.v7.widget.RecyclerView;
//        import android.util.Log;
//        import android.view.KeyEvent;
//        import android.view.Menu;
//        import android.view.MenuItem;
//        import android.view.View;
//        import android.view.inputmethod.InputMethodManager;
//        import android.widget.AdapterView;
//        import android.widget.ArrayAdapter;
//        import android.widget.AutoCompleteTextView;
//        import android.widget.DatePicker;
//        import android.widget.EditText;
//        import android.widget.ImageView;
//        import android.widget.LinearLayout;
//        import android.widget.ProgressBar;
//        import android.widget.RelativeLayout;
//        import android.widget.TextView;
//        import android.widget.Toast;
//
//        import com.alexvasilkov.gestures.animation.ViewPosition;
//        import com.google.android.gms.ads.AdListener;
//        import com.google.android.gms.ads.AdLoader;
//        import com.google.android.gms.ads.AdRequest;
//        import com.google.android.gms.ads.MobileAds;
//        import com.google.android.gms.ads.formats.NativeAd;
//        import com.google.android.gms.ads.formats.NativeAppInstallAd;
//        import com.google.android.gms.ads.formats.NativeContentAd;
//        import com.squareup.picasso.Picasso;
//        import org.json.JSONArray;
//        import org.json.JSONException;
//        import org.json.JSONObject;
//        import java.util.ArrayList;
//        import java.util.Calendar;
//
//        import app.shouoff.PostDetails;
//        import app.shouoff.PostLikedUsers;
//        import app.shouoff.R;
//        import app.shouoff.SharePostActivity;
//        import app.shouoff.adapter.PostAdapter;
//        import app.shouoff.common.CommonService;
//        import app.shouoff.common.ConnectionDetector;
//        import app.shouoff.common.Constants;
//        import app.shouoff.common.GoToHome;
//        import app.shouoff.common.SharedPreference;
//        import app.shouoff.drawer.Drawer;
//        import app.shouoff.login.PromoVideo;
//        import app.shouoff.mediadata.FullImageActivity;
//        import app.shouoff.model.HomePostModel;
//        import app.shouoff.model.PostAttachmentModel;
//        import app.shouoff.model.SearchDataModel;
//        import app.shouoff.model.TagUserData;
//        import app.shouoff.retrofit.Retrofit2;
//        import app.shouoff.retrofit.RetrofitResponse;
//        import okhttp3.ResponseBody;
//        import retrofit2.Response;
//
//public class HomeActivity extends Drawer implements RetrofitResponse,GoToHome
//{
//    private RecyclerView recycler;
//    private ArrayList<Object> postModels=new ArrayList<>();
//    private ArrayList<Object> temp_list=new ArrayList<>();
//
//    Context context = this;
//    private int page_count=0,value=0,count=0,index=0;
//    private boolean aBoolean=false;
//    private PostAdapter postAdapter;
//    private ProgressBar home_post_loader;
//    private SwipeRefreshLayout swipeContainer;
//    private TextView error;
//    private int post_position=0;
//
//    /*For Filter*/
//    private EditText search_value;
//    private AutoCompleteTextView content;
//    private TextView from_date;
//    private TextView to_date;
//    private RelativeLayout layer;
//    private LinearLayout filter_layout;
//    private String[] searchData={"Weekly","Monthly","Yearly"};
//
//    private DatePickerDialog pickerDialog;
//    private String searchDayWise="",filter_post="",filter_name="",filter_from="",filter_to="",native_ad="";
//    private boolean temp=true;
//
//    private EditText search_data;
//    private RelativeLayout main;
//
//    NativeAd nativeAd;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//        MobileAds.initialize(this, getString(R.string.admob_app_id));
//        Constants.freeMemory();
//        initViews();
//
//        //loadNativeAd();
//        //native_ad="content";
//    }
//
//    private void initViews()
//    {
//        ImageView cancel = (ImageView) findViewById(R.id.cancel);
//        search_data=(EditText)findViewById(R.id.search_data);
//        main=(RelativeLayout)findViewById(R.id.main);
//        search_value=(EditText)findViewById(R.id.search_value);
//        content=(AutoCompleteTextView)findViewById(R.id.content);
//        from_date=(TextView)findViewById(R.id.from_date);
//        to_date=(TextView)findViewById(R.id.to_date);
//        TextView filter = (TextView) findViewById(R.id.filter);
//        layer=(RelativeLayout)findViewById(R.id.layer);
//        filter_layout=(LinearLayout)findViewById(R.id.filter_layout);
//
//        error=(TextView)findViewById(R.id.error);
//        home_post_loader=(ProgressBar)findViewById(R.id.home_post_loader);
//        title1.setText(R.string.discover);
//
//        ((Drawer)context).setBottomBar(R.drawable.discovery_colored, R.drawable.profile_gray, R.drawable.creatpost_gray, R.drawable.notification_gray, R.drawable.moregray, discovery, profile, creat_post, notification, more);
//        recycler = (RecyclerView) findViewById(R.id.recycler);
//
//        swipeContainer=(SwipeRefreshLayout)findViewById(R.id.swipeContainer);
//        swipeContainer.setColorSchemeResources(R.color.app_color,R.color.black,R.color.color_gray);
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
//        {
//            @Override
//            public void onRefresh()
//            {
//                page_count=0;value=0;count=0;
//                aBoolean=false;
//                filter_post="";
//                search_data.setText("");
//
//                setServiceHomePosts();
//
//                swipeContainer.setRefreshing(true);
//            }
//        });
//
//        setData();
//        content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
//            {
//                from_date.setText("");
//                to_date.setText("");
//                searchDayWise=content.getText().toString();
//            }
//        });
//
//        search_data.setOnEditorActionListener(new TextView.OnEditorActionListener()
//        {
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
//            {
//                if (actionId != 0 || event.getAction() == KeyEvent.ACTION_DOWN)
//                {
//                    page_count=0;value=0;count=0;
//                    aBoolean=false;
//                    temp=true;
//                    filter_post="Search";
//                    setServiceSearchPosts(search_data.getText().toString());
//                    Constants.hideKeyboard(context,v);
//                    return true;
//                }
//                else
//                {
//                    return false;
//                }
//            }
//        });
//
//        content.setFocusableInTouchMode(false);
//        content.setFocusable(false);
//
//        from_date.setOnClickListener(this);
//        to_date.setOnClickListener(this);
//        filter.setOnClickListener(this);
//        layer.setOnClickListener(this);
//        content.setOnClickListener(this);
//        cancel.setOnClickListener(this);
//        setServiceHomePosts();
//
//    }
//
//    private void showDropDown(View v)
//    {
//        InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        in.hideSoftInputFromWindow(v.getWindowToken(),0);
//        ((AutoCompleteTextView)v).showDropDown();
//    }
//
//    private void setFilterService()
//    {
//        JSONObject object=new JSONObject();
//        try
//        {
//            object.put("Offset",String.valueOf(count));
//            object.put("user_id",SharedPreference.retriveData(context,Constants.ID));
//            object.put("search_name",filter_name);
//            object.put("search_postByDiscription","");
//            object.put("dateCondition",searchDayWise.toLowerCase());
//
//            if (!from_date.getText().toString().equalsIgnoreCase(""))
//            {
//                object.put("toDate",filter_to);
//                object.put("fromDate",filter_from);
//            }
//
//            new Retrofit2(context,this,0,Constants.postList_filter,object).callService(false);
//            home_post_loader.setVisibility(View.VISIBLE);
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    private void setServiceHomePosts()
//    {
//        JSONObject param=new JSONObject();
//        try
//        {
//            param.put("Offset",String.valueOf(count));
//            param.put("user_id",SharedPreference.retriveData(context, Constants.ID));
//            new Retrofit2(context,this,0, Constants.postList,param).callServiceBack();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    private void setServiceSearchPosts(String text)
//    {
//        JSONObject param=new JSONObject();
//        try
//        {
//            param.put("Offset",String.valueOf(count));
//            param.put("search",text);
//            param.put("user_id",SharedPreference.retriveData(context, Constants.ID));
//            new Retrofit2(context,this,0, Constants.SEARCH_POST,param).callServiceBack();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onClick(View view)
//    {
//        super.onClick(view);
//        switch (view.getId())
//        {
//            case R.id.from_date:
//                DatePicker_FromText();
//                break;
//            case R.id.to_date:
//                if (from_date.getText().toString().equalsIgnoreCase(""))
//                {
//                    return;
//                }
//                DatePicker_ToText();
//                break;
//            case R.id.filter:
//                if (!from_date.getText().toString().equalsIgnoreCase(""))
//                {
//                    if (to_date.getText().toString().trim().equalsIgnoreCase(""))
//                    {
//                        Toast.makeText(context, "Please Select both Start and End Date",
//                                Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }
//
//                page_count=0;value=0;count=0;
//                aBoolean=false;
//                temp=true;
//                filter_post="filter";
//
//                filter_name=search_value.getText().toString();
//                filter_from=from_date.getText().toString();
//                filter_to=to_date.getText().toString();
//
//                setFilterService();
//                clearViews();
//                break;
//            case R.id.layer:
//                temp=true;
//                layer.setVisibility(View.GONE);
//                filter_layout.setVisibility(View.GONE);
//                break;
//            case R.id.content:
//                showDropDown(view);
//                break;
//            case R.id.cancel:
//                main.setVisibility(View.GONE);
//                page_count=0;value=0;count=0;
//                aBoolean=false;
//                filter_post="";
//                search_data.setText("");
//                setServiceHomePosts();
//                swipeContainer.setRefreshing(true);
//                break;
//        }
//    }
//
//    private void clearViews()
//    {
//        search_value.setText("");
//        layer.setVisibility(View.GONE);
//        filter_layout.setVisibility(View.GONE);
//        from_date.setText("");
//        to_date.setText("");
//        setData();
//    }
//
//    private void DatePicker_ToText()
//    {
//        searchDayWise="dynamic";
//        content.setAdapter(new ArrayAdapter<>(context,R.layout.drop_item,searchData));
//
//        java.util.Calendar myCalendar1;
//        myCalendar1 = java.util.Calendar.getInstance();
//        DatePickerDialog.OnDateSetListener _to = Constants.date_picker(to_date, myCalendar1);
//
//        String getfromdate = from_date.getText().toString().trim();
//        String getfrom[] = getfromdate.split("-");
//        int year, month, day;
//        year = Integer.parseInt(getfrom[0]);
//        month = Integer.parseInt(getfrom[1]);
//        day = Integer.parseInt(getfrom[2]);
//        final Calendar c = Calendar.getInstance();
//        c.set(year, month - 1, day + 1);
//
//        pickerDialog = new DatePickerDialog(context, _to, year, month - 1, day);
//        DatePicker pickerDialog1 = pickerDialog.getDatePicker();
//        pickerDialog1.setMinDate(c.getTimeInMillis());
//        pickerDialog.show();
//    }
//
//    private void DatePicker_FromText()
//    {
//        java.util.Calendar myCalendar;
//        myCalendar = java.util.Calendar.getInstance();
//        DatePickerDialog.OnDateSetListener _from = Constants.date_picker(from_date, myCalendar);
//        pickerDialog = new DatePickerDialog(context, _from, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                myCalendar.get(Calendar.DAY_OF_MONTH));
//        pickerDialog.show();
//        setData();
//
//    }
//
//    private void setData()
//    {
//        content.setText("");
//        content.setHint("Search By");
//        content.setAdapter(new ArrayAdapter<>(context,R.layout.drop_item,searchData));
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        getMenuInflater().inflate(R.menu.side_menu,menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        switch (item.getItemId())
//        {
//            case R.id.filter:
//                main.setVisibility(View.GONE);
//                if (!temp)
//                {
//                    temp=true;
//                    layer.setVisibility(View.GONE);
//                    filter_layout.setVisibility(View.GONE);
//                    from_date.setText("");
//                    to_date.setText("");
//                    setData();
//                }
//                else
//                {
//                    temp=false;
//                    layer.setVisibility(View.VISIBLE);
//                    filter_layout.setVisibility(View.VISIBLE);
//                }
//                return true;
//            case R.id.search:
//                main.setVisibility(View.VISIBLE);
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void setAdapter(ArrayList<Object> homePostModels)
//    {
//
//        temp_list.clear();
//        temp_list.addAll(homePostModels);
//
//        /*for (int t=0;t<temp_list.size();t++)
//        {
//            Log.e("index","=>>>>"+t);
//            HomePostModel menuItem = (HomePostModel) temp_list.get(t);
//            if (menuItem.getLike_status().equalsIgnoreCase("1"))
//            {
//                menuItem.setaBoolean(true);
//            }
//        }*/
//
//        Log.e("index","=>>>>"+temp_list.size());
//        recycler.setLayoutManager(new LinearLayoutManager(app.shouoff.activity.HomeActivity.this));
//        recycler.setHasFixedSize(true);
//        postAdapter=new PostAdapter(context,temp_list,recycler);
//        recycler.setAdapter(postAdapter);
//        postAdapter.notifyDataSetChanged();
//
//        postAdapter.click(new PostAdapter.ItemClick()
//        {
//            @Override
//            public void likes(View view, int pos)
//            {
//                /*Services of like and dislike Post*/
//                HomePostModel menuItem = (HomePostModel) temp_list.get(pos);
//                if (menuItem.isaBoolean())
//                {
//                    menuItem.setaBoolean(false);
//                    new CommonService().articleDislike(menuItem.getId(),context);
//                    String data=menuItem.getLike_count();
//                    int j= Integer.parseInt(data);
//                    int i = j - 1;
//                    menuItem.setLike_count(String.valueOf(i));
//                    menuItem.setLike_status("0");
//                    postAdapter.notifyDataSetChanged();
//                }
//                else
//                {
//                    menuItem.setaBoolean(true);
//                    new CommonService().articleLike(menuItem.getId(),context);
//                    String data=menuItem.getLike_count();
//                    int i= Integer.parseInt(data)+1;
//                    menuItem.setLike_count(String.valueOf(i));
//                    menuItem.setLike_status("1");
//                    postAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void share(View view, int pos)
//            {
//                post_position=pos;
//                startActivityForResult(new Intent(context,SharePostActivity.class),100);
//            }
//
//            @Override
//            public void details(View view, int pos)
//            {
//                HomePostModel menuItem = (HomePostModel) temp_list.get(pos);
//                startActivityForResult(new Intent(context, PostDetails.class)
//                        .putExtra("post_data",menuItem),600);
//            }
//
//            @Override
//            public void showImage(View view, int pos)
//            {
//                HomePostModel menuItem = (HomePostModel) temp_list.get(pos);
//                ViewPosition position1 = ViewPosition.from(view);
//                FullImageActivity.open((Activity) context, position1,
//                        Constants.POST_URL+menuItem.getAttachmentModels()
//                                .get(0).getAttachment_name());
//            }
//
//            @Override
//            public void likeList(View view, int pos)
//            {
//                HomePostModel menuItem = (HomePostModel) temp_list.get(pos);
//                if (!menuItem.getLike_count().equalsIgnoreCase("0"))
//                {
//                    startActivity(new Intent(context, PostLikedUsers.class)
//                            .putExtra("post_id",""+menuItem.getId()));
//                }
//            }
//
//            @Override
//            public void onLoadMore()
//            {
//                if (!ConnectionDetector.isInternetAvailable(context))
//                {
//                    return;
//                }
//                if (aBoolean)
//                {
//                    if (count<page_count)
//                    {
//                        temp_list.add(null);
//                        postAdapter.notifyItemInserted(temp_list.size() + 1);
//
//                        if (filter_post.equalsIgnoreCase("filter"))
//                        {
//                            setFilterService();
//                        }
//                        else if (filter_post.equalsIgnoreCase("Search"))
//                        {
//                            setServiceSearchPosts(search_data.getText().toString());
//                        }
//                        else
//                        {
//                            setServiceHomePosts();
//                        }
//                    }
//                    aBoolean=false;
//                }
//            }
//        });
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode==100 && resultCode==101)
//        {
//            ArrayList<SearchDataModel> selected = (ArrayList<SearchDataModel>) data.getSerializableExtra("user_list");
//
//            StringBuilder builder=new StringBuilder();
//            for (int i = 0; i< selected.size(); i++)
//            {
//                builder.append(selected.get(i).getId());
//                builder.append(",");
//            }
//
//            /*Set service to share post */
//            HomePostModel menuItem = (HomePostModel) temp_list.get(post_position);
//            new CommonService().setServiceShare(String.valueOf(builder).substring(0,String.valueOf(builder).length()-1)
//                    ,menuItem.getId(),context,6,this);
//        }
//        else if (requestCode==600 && resultCode==601)
//        {
//            page_count=0;value=0;count=0;
//            aBoolean=false;
//
//            if (filter_post.equalsIgnoreCase("filter"))
//            {
//                setFilterService();
//            }
//            else if (filter_post.equalsIgnoreCase("Search"))
//            {
//                setServiceSearchPosts(search_data.getText().toString());
//            }
//            else
//            {
//                home_post_loader.setVisibility(View.VISIBLE);
//                setServiceHomePosts();
//            }
//        }
//    }
//
//    @Override
//    protected void onResume()
//    {
//        super.onResume();
//        overridePendingTransition(R.anim.fade1, R.anim.fade2);
//        if (value>0)
//        {
//            new CommonService().UserAutoLogoutIfAdminDeactivate(context,this);
//        }
//        filter_post="";
//        Drawer.drawer_name.setText(SharedPreference.retriveData(context,Constants.NICK_NAME));
//        Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+SharedPreference.
//                retriveData(context,Constants.Image)).placeholder(R.drawable.noimage).into(Drawer.profile_image_drawer);
//
//        /*Call service after create new post*/
//        if (SharedPreference.retriveData(context,"create_new")!=null)
//        {
//            page_count=0;value=0;count=0;
//            aBoolean=false;
//            setServiceHomePosts();
//            home_post_loader.setVisibility(View.VISIBLE);
//            SharedPreference.removeKey(context,"create_new");
//        }
//
//        /*notify adapter after like/dislike */
//        if (SharedPreference.retriveData(context,"tmp")!=null)
//        {
//            for (int i=0;i<temp_list.size();i++)
//            {
//                HomePostModel menuItem = (HomePostModel) temp_list.get(i);
//                if (SharedPreference.retriveData(context,"article_id")!=null)
//                {
//                    if (SharedPreference.retriveData(context,"article_id").contains(menuItem.getId()))
//                    {
//                        menuItem.setLike_count(SharedPreference.retriveData(context,"likess"));
//                        menuItem.setLike_status(SharedPreference.retriveData(context,"status"));
//                        menuItem.setShare_count(SharedPreference.retriveData(context,"share_count"));
//                        boolean b= Boolean.parseBoolean(SharedPreference.retriveData(context,"like_satsis"));
//                        menuItem.setaBoolean(b);
//                        menuItem.setComment_count(SharedPreference.retriveData(context,"comments"));
//                        menuItem.setDescription(SharedPreference.retriveData(context,"description"));
//                    }
//                }
//                postAdapter.notifyDataSetChanged();
//            }
//
//            SharedPreference.removeKey(context,"tmp");
//        }
//
//        /*Delete Post*/
//        if (SharedPreference.retriveData(context,"delete_post_id")!=null)
//        {
//            for (int i=0;i<temp_list.size();i++)
//            {
//                HomePostModel menuItem = (HomePostModel) temp_list.get(i);
//                if (menuItem.getId().equalsIgnoreCase(SharedPreference.retriveData(context,"delete_post_id")))
//                {
//                    temp_list.remove(i);
//                    postAdapter.notifyDataSetChanged();
//                }
//            }
//            SharedPreference.removeKey(context,"delete_post_id");
//        }
//    }
//
//    @Override
//    public void onServiceResponse(int requestCode, Response<ResponseBody> response)
//    {
//        try
//        {
//            swipeContainer.setRefreshing(false);
//            home_post_loader.setVisibility(View.GONE);
//            JSONObject result=new JSONObject(response.body().string().toString());
//            boolean status=result.getBoolean("response");
//            if (status)
//            {
//                switch (requestCode)
//                {
//                    case 0:
//
//                        if (value==0)
//                        {
//                            new CommonService().UserAutoLogoutIfAdminDeactivate(context,this);
//                        }
//                        JSONObject data;
//                        if (result.has("data"))
//                        {
//                            data=result.getJSONObject("data");
//                        }
//                        else
//                        {
//                            data=result.getJSONObject("postData");
//                        }
//                        page_count=data.getInt("count");
//                        postModels.clear();
//                        JSONArray postData=data.getJSONArray("rows");
//                        if (postData.length()>0)
//                        {
//                            for (int i=0;i<postData.length();i++)
//                            {
//                                JSONObject value=postData.getJSONObject(i);
//
//                                /*Attachments*/
//                                JSONArray attach=value.getJSONArray("postAttachment");
//                                ArrayList<PostAttachmentModel> models=new ArrayList<>();
//                                if (attach.length()>0)
//                                {
//                                    for (int j=0;j<attach.length();j++)
//                                    {
//                                        JSONObject attach_value=attach.getJSONObject(j);
//                                        PostAttachmentModel attachmentModel=new PostAttachmentModel(attach_value.getString("attachment_name"),
//                                                attach_value.getString("attachment_type"),
//                                                attach_value.getString("thumbnail"));
//                                        models.add(attachmentModel);
//                                    }
//                                }
//
//                                /*LikeStatus*/
//                                String like_status="";
//                                if (value.getString("ifuserLike").equalsIgnoreCase("null"))
//                                {
//                                    like_status="0";
//                                }
//                                else
//                                {
//                                    like_status="1";
//                                }
//
//                                /*Tagged User data*/
//                                ArrayList<TagUserData> tagUserData=new ArrayList<>();
//                                JSONArray tag=value.getJSONArray("taggedUser");
//                                if (tag.length()>0)
//                                {
//                                    for (int j = 0; j < tag.length(); j++)
//                                    {
//                                        JSONObject tagData=tag.getJSONObject(j);
//
//                                        if (!tagData.getString("tagedUserDetails").equalsIgnoreCase("null"))
//                                        {
//                                            TagUserData userData=new TagUserData(tagData.getJSONObject("tagedUserDetails")
//                                                    .getString("id"),
//                                                    tagData.getJSONObject("tagedUserDetails")
//                                                            .getString("username"),
//                                                    tagData.getJSONObject("tagedUserDetails")
//                                                            .getString("nick_name"));
//                                            tagUserData.add(userData);
//                                        }
//                                    }
//                                }
//
//
//                                HomePostModel postModel=new HomePostModel(value.getString("id"),
//                                        value.getString("user_id"),
//                                        value.getString("description"),
//                                        value.getString("created_at"),
//                                        value.getString("like_count"),
//                                        value.getString("comment_count"),
//                                        value.getString("share_count"),
//                                        value.getJSONObject("postUserDetails").getString("nick_name"),
//                                        value.getJSONObject("postUserDetails").getString("family_name"),
//                                        value.getJSONObject("postUserDetails").getString("image"),
//                                        like_status,false,models,"",tagUserData);
//                                postModels.add(postModel);
//                            }
//                            pagination();
//                            error.setVisibility(View.GONE);
//                            recycler.setVisibility(View.VISIBLE);
//                        }
//                        else
//                        {
//                            error.setVisibility(View.VISIBLE);
//                            recycler.setVisibility(View.GONE);
//                        }
//                        break;
//                    case 6:
//                        HomePostModel menuItem = (HomePostModel) temp_list.get(post_position);
//                        String temp=menuItem.getShare_count();
//                        int j= Integer.parseInt(temp)+1;
//                        menuItem.setShare_count(String.valueOf(j));
//                        postAdapter.notifyDataSetChanged();
//                        break;
//                }
//            }
//            else
//            {
//                switch (requestCode)
//                {
//                    case 0:
//                        if (value==0)
//                        {
//                            error.setVisibility(View.VISIBLE);
//                            recycler.setVisibility(View.GONE);
//                        }
//                        break;
//                }
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    private void backToSplash(String message)
//    {
//        AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        builder.setCancelable(false);
//        builder.setMessage(message);
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                new CommonService().DeleteDevice(context);
//                startActivity(new Intent(context, PromoVideo.class)
//                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|
//                                Intent.FLAG_ACTIVITY_CLEAR_TOP|
//                                Intent.FLAG_ACTIVITY_NEW_TASK));
//                SharedPreference.removeAll(context);
//                dialog.dismiss();
//            }
//        });
//        builder.create().show();
//    }
//
//    // TODO: 19/3/18 Pagination Code
//    private void pagination()
//    {
//        if (value==0)
//        {
//            aBoolean=true;
//            //  index = 0;
//            // postModels.add(index,nativeAd);
//
//            setAdapter(postModels);
//            value=1;
//            count=count+10;
//            //index = index + count;
//        }
//        else if (value==1)
//        {
//            if (count<page_count)
//            {
//                aBoolean=true;
//
//               /* if (native_ad.equalsIgnoreCase("content"))
//                {
//                    install();
//                    native_ad="install";
//                }
//                else
//                {
//                    loadNativeAd();
//                    native_ad="content";
//                }*/
//
//                temp_list.remove(temp_list.size() - 1);
//
//                // temp_list.add(index,nativeAd);
//                postAdapter.notifyItemRemoved(temp_list.size());
//                temp_list.addAll(postModels);
//                postAdapter.notifyDataSetChanged();
//                postAdapter.setLoaded();
//
//                count=count+10;
//                //index = index + count;
//            }
//        }
//    }
//
//    @Override
//    public void gotoHome(int code)
//    {
//        switch (code)
//        {
//            case 30:
//                backToSplash("Your account is deactivated by admin");
//                break;
//            case 40:
//                backToSplash("This account is login in another device");
//                break;
//        }
//    }
//
//    private void loadNativeAd()
//    {
//        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.ad_unit_id));
//        AdLoader adLoader = builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener()
//        {
//            @Override
//            public void onContentAdLoaded(NativeContentAd ad)
//            {
//                nativeAd=ad;
//            }
//        }).build();
//        adLoader.loadAd(new AdRequest.Builder().build());
//    }
//
//    private void install()
//    {
//        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.ad_unit_id));
//        AdLoader adLoader = builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener()
//        {
//            @Override
//            public void onAppInstallAdLoaded(NativeAppInstallAd ad)
//            {
//                nativeAd=ad;
//
//            }
//        }).build();
//        adLoader.loadAd(new AdRequest.Builder().build());
//    }
//}

