package app.shouoff;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import app.shouoff.adapter.UserListAdapter;
import app.shouoff.adapter.UserShareListAdapter;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.drawer.Drawer;
import app.shouoff.model.SearchDataModel;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class SharePostActivity extends Drawer implements RetrofitResponse
{
    private RecyclerView selected_user,user_list;
    Context context = this;
    private UserListAdapter listAdapter;
    private UserShareListAdapter shareListAdapter;
    private ProgressBar data_progress;
    private EditText search_data;
    private LinearLayout selected_users;
    private TextView error;

    private ArrayList<SearchDataModel> dataModels=new ArrayList<>();
    private ArrayList<SearchDataModel> selected=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_post);

        initialized();
        setService();
    }

    private void setService()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("check_user_id",SharedPreference.retriveData(context,Constants.ID));
            object.put("user_id", SharedPreference.retriveData(context,Constants.ID));
            new Retrofit2(context,this,0, Constants.Myfollowers_list,object).callServiceBack();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void initialized()
    {
        error=(TextView)findViewById(R.id.error);
        selected_users=(LinearLayout)findViewById(R.id.selected_users);
        search_data=(EditText)findViewById(R.id.search_data);
        selected_user=(RecyclerView)findViewById(R.id.selected_user);
        user_list=(RecyclerView)findViewById(R.id.user_list);
        data_progress=(ProgressBar)findViewById(R.id.data_progress);

        title1.setText("Search users");
        ((Drawer)context). showImage(R.drawable.search,
                R.drawable.user,
                R.drawable.document,
                R.drawable.trophyblack,
                R.drawable.shop,
                R.drawable.contests,
                R.drawable.preferences_blue,
                R.drawable.winners,
                R.drawable.about,
                R.drawable.contact);

        ((Drawer)context).showText(prefrence,my_profile,search_user,highly_liked,creat_post1,contest,my_post,winners,about,contact_support);

        showView(pref_view,search_view,profile_view,liked_view,create_view,contests_view,post_view,winner_view,about_view,support_view);
        ((Drawer)context).setBottomBar(R.drawable.discovery_gray, R.drawable.profile_gray, R.drawable.creatpost_gray, R.drawable.notification_gray, R.drawable.more_colord, more, profile, creat_post, notification, discovery);


         search_data.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (listAdapter!=null)
                listAdapter.filterSnd(editable.toString());
            }
        });

          search_data.setOnEditorActionListener(new TextView.OnEditorActionListener()
          {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId != 0 || event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    Constants.hideKeyboard(context, v);

                    return true;
                } else {
                    return false;
                }
            }
        });

        selected_user();
        selected_users.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.selected_users:
                Intent intent=new Intent();
                intent.putExtra("user_list",selected);
                setResult(101,intent);
                finish();
                break;
        }
    }

    private void user_list()
    {
        user_list.setHasFixedSize(true);
        user_list.setLayoutManager(new LinearLayoutManager(context));
        listAdapter = new UserListAdapter(context,dataModels);
        user_list.setAdapter(listAdapter);
        listAdapter.click(new UserListAdapter.UserSelect()
        {
            @Override
            public void show(View view, int pos)
            {
                if (dataModels.get(pos).isaBoolean())
                {
                    dataModels.get(pos).setaBoolean(false);

                    if (selected.size()>0)
                    {
                        for (int i=0;i<selected.size();i++)
                        {
                            if (selected.get(i).getId().equalsIgnoreCase(dataModels.get(pos).getId()))
                            {
                                selected.remove(i);
                            }
                        }

                        shareListAdapter.notifyDataSetChanged();
                        selected_users.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        selected_users.setVisibility(View.GONE);
                    }
                }
                else
                {
                    dataModels.get(pos).setaBoolean(true);
                    selected.add(dataModels.get(pos));
                    Collections.reverse(selected);
                    shareListAdapter.notifyDataSetChanged();
                    selected_users.setVisibility(View.VISIBLE);

                }
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    private void selected_user()
    {
        selected_user.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        selected_user.setLayoutManager(layoutManager);
        shareListAdapter = new UserShareListAdapter(context,selected);
        selected_user.setAdapter(shareListAdapter);
        shareListAdapter.click(new UserShareListAdapter.UserSelect()
        {
            @Override
            public void show(View view, int pos)
            {
                for (int i=0;i<dataModels.size();i++)
                {
                    if (selected.get(pos).getId().equalsIgnoreCase(dataModels.get(i).getId()))
                    {
                        dataModels.get(i).setaBoolean(false);
                    }
                }
                listAdapter.notifyDataSetChanged();
                selected.remove(pos);
                shareListAdapter.notifyDataSetChanged();

                if (selected.size()==0)
                {
                    selected_users.setVisibility(View.GONE);
                }
                else
                {
                    selected_users.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fade1, R.anim.fade2);

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
                    case 0:
                        data_progress.setVisibility(View.GONE);
                        dataModels.clear();
                        JSONArray data=result.getJSONArray("data");
                        if (data.length()>0)
                        {
                            for (int i=0;i<data.length();i++)
                            {
                                JSONObject users=data.getJSONObject(i);
                                JSONObject user_Data=users.getJSONObject("User_followers");
                                SearchDataModel searchDataModel=new SearchDataModel(user_Data.getString("id"),
                                        user_Data.getString("first_name")+" "+user_Data.getString("family_name"),
                                        user_Data.getString("nick_name"),
                                        user_Data.getString("email"),
                                        user_Data.getString("image"),false);

                                dataModels.add(searchDataModel);
                            }
                            user_list();
                            user_list.setVisibility(View.VISIBLE);
                            error.setVisibility(View.GONE);
                        }
                        else
                        {
                            user_list.setVisibility(View.GONE);
                            error.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
