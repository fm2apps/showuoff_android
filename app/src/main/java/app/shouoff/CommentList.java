package app.shouoff;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import app.shouoff.adapter.CommentDetailAdapter;
import app.shouoff.baseadapter.TagUserAdapter;
import app.shouoff.common.Alerts;
import app.shouoff.common.CommonService;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.common.SpaceHashTokenizer;
import app.shouoff.common.SpaceTokenizer;
import app.shouoff.drawer.Drawer;
import app.shouoff.interfaces.DeleteComment;
import app.shouoff.model.CategoriesList;
import app.shouoff.model.CommentListModel;
import app.shouoff.model.SearchDataModel;
import app.shouoff.model.TagUserData;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import app.shouoff.widget.Multi;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class CommentList extends Drawer implements RetrofitResponse,DeleteComment
{
    private RecyclerView comment_list;
    private ArrayList<CommentListModel> listModels=new ArrayList<>();
    private LinearLayout send_message;
    private CommentDetailAdapter adapter;
    private ProgressBar comment_progress;
    Context context = this;
    private ArrayList<String> offensive=new ArrayList<>();

    /*For Tagging and hashTagging*/
    private TagUserAdapter userAdapter;
    private ArrayAdapter<String> Hashadapter;
    private ArrayList<SearchDataModel> dataModels=new ArrayList<>();
    private ArrayList<SearchDataModel> temp_list=new ArrayList<>();
    private LinkedHashSet<CategoriesList> uniqueStrings = new LinkedHashSet<CategoriesList>();
    private List<CategoriesList> list = new ArrayList<>();
    private ArrayList<String> hashTagList=new ArrayList<>();
    private Multi make_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);

        title1.setText(R.string.comment);
        bottom_bar.setVisibility(View.GONE);
        bottom_view.setVisibility(View.GONE);
        listModels= (ArrayList<CommentListModel>) getIntent().getSerializableExtra("commentList");
        initialized();
        Collections.reverse(listModels);
        comment_list();

        new Retrofit2(context,this,5, Constants.offensive_Words).callServiceBack();
    }

    private void setServiceDeleteComment(int pos)
    {
        new CommonService().setServiceDeleteComment(listModels.get(pos).getId(),getIntent().getStringExtra("post_id"),this,context);
    }

    private void initialized()
    {
        comment_list=(RecyclerView)findViewById(R.id.comment_list);
        comment_progress=(ProgressBar)findViewById(R.id.comment_progress);
        comment_progress.setVisibility(View.GONE);
        send_message=(LinearLayout)findViewById(R.id.send_message);
        make_comment=(Multi)findViewById(R.id.make_comment);

        make_comment.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId != 0 || event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    Constants.hideKeyboard(context,v);
                    if (make_comment.getText().toString().trim().isEmpty())
                    {
                        return false;
                    }
                    setServiceComment();
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        make_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.toString().length()>0)
                {
                    if ((s.charAt(s.length() - 1))=='@')
                    {
                        setAdapterTag(dataModels);
                    }
                    else if ((s.charAt(s.length() - 1))=='#')
                    {
                        dataModels.clear();
                        dataModels.addAll(temp_list);
                        setAdapterHashTag();
                    }
                    else
                    {
                        /*dataModels.clear();
                        dataModels.addAll(temp_list);*/
                    }
                }
            }
        });
        send_message.setOnClickListener(this);
    }

    private void setServiceComment()
    {

        for (int i=0;i<offensive.size();i++)
        {
            if (make_comment.getText().toString().toLowerCase().matches(offensive.get(i).toLowerCase()))
            {
                Alerts.showDialog(context,"",getString(R.string.not_alow_to_give_comment));
                return;
            }
        }

        JSONObject object=new JSONObject();
        try
        {
            object.put("post_id",getIntent().getStringExtra("post_id"));
            object.put("user_id", SharedPreference.retriveData(context,Constants.ID));
            object.put("tagged_by", SharedPreference.retriveData(context,Constants.ID));
            object.put("description",make_comment.getText().toString());
            JSONArray data=new JSONArray();
            for (int i = 0; i < list.size(); i++)
            {
                if (!SharedPreference.retriveData(context,Constants.ID).equalsIgnoreCase(list.get(i).getId()))
                data.put(list.get(i).getId());
            }
            object.put("tagged_userId",data);
            new Retrofit2(context,this,1,Constants.createcomment,object).callService(true);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void setServiceCommentList()
    {
        new Retrofit2(context,this,0,Constants.perticular_post+getIntent().getStringExtra("post_id")).callServiceBack();
    }

    @Override
    public void onClick(View view)
    {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.send_message:
                Constants.hideKeyboard(context,view);
                if (make_comment.getText().toString().trim().isEmpty())
                {
                    return;
                }
                if (Patterns.WEB_URL.matcher(make_comment.getText().toString()).matches())
                {
                    Toast.makeText(context, "Links not allowed", Toast.LENGTH_SHORT).show();
                    return;
                }
                setServiceComment();
                break;
        }
    }

    private void comment_list()
    {
        comment_list.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);
        comment_list.setLayoutManager(layoutManager);
        adapter = new CommentDetailAdapter(context,listModels,getIntent().getStringExtra("user_id"));
        comment_list.setAdapter(adapter);
        comment_list.setNestedScrollingEnabled(false);

        comment_list.post(new Runnable()
        {
            @Override
            public void run() {
                comment_list.smoothScrollToPosition(adapter.getItemCount());
            }
        });

        adapter.click(new CommentDetailAdapter.DeleteCmt()
        {
            @Override
            public void select(View view, int pos)
            {
                deleteComment(pos);
            }
        });
    }

    private void deleteComment(int pos)
    {
        Alerts.showDialog(context,pos,this,"");
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
                        comment_progress.setVisibility(View.GONE);
                        listModels.clear();
                        JSONArray data=result.getJSONArray("data");
                        if (data.length()>0)
                        {
                            for (int i=0;i<data.length();i++)
                            {
                                JSONObject value=data.getJSONObject(i);

                                /*Tagged User data*/
                                ArrayList<TagUserData> tagUserData=new ArrayList<>();
                                JSONArray tag=value.getJSONArray("taggedUser");
                                if (tag.length()>0)
                                {
                                    for (int j = 0; j < tag.length(); j++)
                                    {
                                        JSONObject tagData=tag.getJSONObject(j);
                                        if (!tagData.getString("tagedUserDetails").equalsIgnoreCase("null"))
                                        {
                                            TagUserData userData=new TagUserData(tagData.getJSONObject("tagedUserDetails")
                                                    .getString("id"),
                                                    tagData.getJSONObject("tagedUserDetails")
                                                            .getString("username"),
                                                    tagData.getJSONObject("tagedUserDetails")
                                                            .getString("nick_name"));
                                            tagUserData.add(userData);
                                        }
                                    }
                                }

                                CommentListModel listModel=new CommentListModel(value.getString("id"),
                                        value.getString("user_id"),
                                        value.getString("description"),
                                        value.getString("created_at"),
                                        value.getJSONObject("commentUserinfo").getString("nick_name"),
                                        value.getJSONObject("commentUserinfo").getString("family_name"),
                                        value.getJSONObject("commentUserinfo").getString("image"),tagUserData);
                                listModels.add(listModel);
                            }
                        }
                        comment_list();
                        break;
                    case 1:
                        comment_progress.setVisibility(View.VISIBLE);
                        setServiceCommentList();
                        make_comment.setText("");
                        break;
                    case 5:
                        offensive.clear();
                        JSONArray off=result.getJSONArray("data");
                        if (off.length()>0)
                        {
                            for (int i=0;i<off.length();i++)
                            {
                                JSONObject value=off.getJSONObject(i);
                                offensive.add(value.getString("word"));
                            }
                        }
                        setService();
                        break;
                    case 3:
                        dataModels.clear();
                        temp_list.clear();
                        JSONArray data1=result.getJSONArray("data");
                        if (data1.length()>0)
                        {
                            for (int i=0;i<data1.length();i++)
                            {
                                JSONObject users=data1.getJSONObject(i);


                                SearchDataModel searchDataModel=new SearchDataModel(users.getString("id"),
                                        users.getString("first_name")+" "+users.getString("family_name"),
                                        users.getString("family_name"),
                                        users.getString("email"),
                                        users.getString("image"),
                                        users.getString("username"),
                                        users.getString("nick_name"));

                                dataModels.add(searchDataModel);
                            }
                            temp_list.addAll(dataModels);
                        }
                        new Retrofit2(context,this,4, Constants.Hash_Words).callServiceBack();
                    case 4:
                        hashTagList.clear();
                        JSONArray tag=result.getJSONArray("data");
                        if (tag.length()>0)
                        {
                            for (int i = 0; i < tag.length(); i++) {
                                JSONObject tagData=tag.getJSONObject(i);
                                hashTagList.add(tagData.getString("word"));
                            }
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

    private void setService()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("user_id",SharedPreference.retriveData(context,Constants.ID));
            new Retrofit2(context,this,3, Constants.listUsers,object).callServiceBack();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fade1, R.anim.fade2);
    }

    @Override
    public void delete(View view,int pos)
    {
        setServiceDeleteComment(pos);
        listModels.remove(pos);
        adapter.notifyDataSetChanged();
    }

    private void setAdapterTag(final ArrayList<SearchDataModel> dataModelss)
    {
        userAdapter = new TagUserAdapter(context, R.layout.tag_user_adapter, dataModelss);
        make_comment.setAdapter(userAdapter);
        make_comment.setThreshold(1);
        make_comment.setTokenizer(new SpaceTokenizer());

        make_comment.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String tag_id = dataModelss.get(position).getId();
                String tag_name = dataModelss.get(position).getUser_name();
                uniqueStrings.add(new CategoriesList(tag_id, tag_name));
                list = new ArrayList<>(uniqueStrings);

                dataModels.clear();
                dataModels.addAll(temp_list);
            }
        });
    }

    private void setAdapterHashTag()
    {
        Hashadapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,hashTagList);
        make_comment.setAdapter(Hashadapter);
        make_comment.setThreshold(1);
        make_comment.setTokenizer(new SpaceHashTokenizer());
        make_comment.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
            }
        });
    }
}
