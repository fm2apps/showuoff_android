package app.shouoff;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import app.shouoff.adapter.BlockedUsersAdapter;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.model.BlockedUserListModel;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class BlockedUser extends AppCompatActivity implements RetrofitResponse
{
    private Context context=this;
    ImageView backarrow;
    private RecyclerView recycler;
    private ProgressBar post_loader;
    private TextView error;
    private BlockedUsersAdapter usersAdapter;
    private ArrayList<BlockedUserListModel> userListModels=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_user);

        initialized();

        new Retrofit2(context,this,0, Constants.BLOCKED_USERS+
                SharedPreference.retriveData(context,Constants.ID)).callServiceBack();
    }

    private void initialized()
    {
        recycler=(RecyclerView)findViewById(R.id.recycler);
        post_loader=(ProgressBar)findViewById(R.id.post_loader);
        error=(TextView)findViewById(R.id.error);

        backarrow=(ImageView)findViewById(R.id.backarrow);
        backarrow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                finish();
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
    public void onServiceResponse(int requestCode, Response<ResponseBody> response) {
        try
        {
            JSONObject result=new JSONObject(response.body().string().toString());
            boolean status=result.getBoolean("response");
            if (status)
            {
                switch (requestCode)
                {
                    case 0:
                        post_loader.setVisibility(View.GONE);
                        JSONArray array=result.getJSONArray("data");
                        if (array.length()>0)
                        {

                            userListModels.clear();
                            for (int i = 0; i < array.length(); i++)
                            {
                                JSONObject value=array.getJSONObject(i);
                                JSONObject data=value.getJSONObject("blocked_user_detail");
                                BlockedUserListModel userListModel=new BlockedUserListModel(data.getString("id"),
                                        data.getString("email"),
                                        data.getString("nick_name"),
                                        data.getString("image"),
                                        data.getString("username"));
                                userListModels.add(userListModel);
                            }

                            setAdapter();
                            recycler.setVisibility(View.VISIBLE);
                            error.setVisibility(View.GONE);
                        }
                        else
                        {
                            recycler.setVisibility(View.GONE);
                            error.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 1:
                        Log.e("result",""+result.toString());
                        break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setAdapter()
    {
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(context));
        usersAdapter = new BlockedUsersAdapter(context,userListModels);
        recycler.setAdapter(usersAdapter);
        usersAdapter.click(new BlockedUsersAdapter.ShowView()
        {
            @Override
            public void make(View view, int pos)
            {
                setUnblockUserService(userListModels.get(pos).getId());

                userListModels.remove(pos);
                usersAdapter.notifyDataSetChanged();

                if (userListModels.size()==0)
                {
                    recycler.setVisibility(View.GONE);
                    error.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void profile(View view, int pos)
            {

            }
        });
    }

    private void setUnblockUserService(String id)
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("blocked_by",SharedPreference.retriveData(context,Constants.ID));
            object.put("blocked_to",id);
            new Retrofit2(context,this,1,Constants.UNBLOCK_USER,object).callService(false);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
