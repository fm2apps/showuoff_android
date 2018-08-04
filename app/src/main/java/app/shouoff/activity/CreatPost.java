package app.shouoff.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import app.shouoff.R;
import app.shouoff.baseadapter.TagUserAdapter;
import app.shouoff.common.Alerts;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.common.SpaceHashTokenizer;
import app.shouoff.common.SpaceTokenizer;
import app.shouoff.drawer.Drawer;
import app.shouoff.mediadata.FullScreenImage;
import app.shouoff.mediadata.PlayVideo;
import app.shouoff.model.CategoriesList;
import app.shouoff.model.SearchDataModel;
import app.shouoff.retrofit.ProgressRequestBody;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import app.shouoff.widget.Multi;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class CreatPost extends Drawer implements RetrofitResponse,ProgressRequestBody.UploadCallbacks
{
    Context context=this;
    private HashMap<String,RequestBody> map;
    private String post_id="",attachment_id="";
    private Multi post_description;
    private TextView submit_post;
    private ImageView delete,video_view;
    private ImageView post_image,thumb_nail,gif_image;
    private RelativeLayout layout_attachment;

    /*Image*/
    private File attachment,thumbnail;
    RequestBody req,thumb;
    MultipartBody.Part body,thumb_body;
    ProgressBar progressBar;
    private RelativeLayout start_progress;
    private TextView progress_text;


    /*For Tagging and hashTagging*/
    private TagUserAdapter userAdapter;
    private ArrayAdapter<String> Hashadapter;
    private ArrayList<SearchDataModel> dataModels=new ArrayList<>();
    private ArrayList<SearchDataModel> temp_list=new ArrayList<>();
    private LinkedHashSet<CategoriesList> uniqueStrings = new LinkedHashSet<CategoriesList>();
    private List<CategoriesList> list = new ArrayList<>();
    private ArrayList<String> hashTagList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_post);

        initview();
        System.gc();
        setAttachment();
    }

    private void setAttachment()
    {
        if (getIntent().hasExtra("video"))
        {
            if (!getIntent().getStringExtra("path").equalsIgnoreCase("DEFAULT"))
            {
                String videopath = getIntent().getStringExtra("path");
                attachment = new File(videopath);

                // TODO: 3/8/17 video thumbnail
                thumbnail=Constants.pathImage(videopath);
                thumb = RequestBody.create(MediaType.parse("multipart/form-data"),
                        Constants.pathImage(videopath));
                thumb_body = MultipartBody.Part.createFormData("thumbnail",
                        Constants.pathImage(videopath).getName(), thumb);

                start_progress.setVisibility(View.VISIBLE);
                ProgressRequestBody fileBody = new ProgressRequestBody(attachment, this);
                body = MultipartBody.Part.createFormData("attachment", attachment.getName(), fileBody);

                /*Submit Video*/
                setServiceOfPostVideos("video");
            }
        }
        else if (getIntent().hasExtra("image"))
        {
            // TODO: 18/4/18 Submit image
            if (getIntent().hasExtra("image_gif"))
            {
                attachment = new File(getIntent().getStringExtra("imagePath"));
                thumbnail=new File(getIntent().getStringExtra("thumbnail"));
                thumb = RequestBody.create(MediaType.parse("multipart/form-data"),thumbnail);

                thumb_body = MultipartBody.Part.createFormData("thumbnail",
                        thumbnail.getName(), thumb);
                start_progress.setVisibility(View.VISIBLE);
                ProgressRequestBody fileBody = new ProgressRequestBody(attachment, this);
                body = MultipartBody.Part.createFormData("attachment", attachment.getName(), fileBody);

                /* Submit Video*/
                setServiceOfPostVideos("gif");
            }
            else
            {
                start_progress.setVisibility(View.GONE);
                attachment = new File(getIntent().getStringExtra("imagePath"));
                req= RequestBody.create(MediaType.parse("multipart/form-data"), attachment);
                body= MultipartBody.Part.createFormData("attachment",attachment.getName(), req);

                setServiceOfPostImages();
            }
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

    private void initview()
    {
        start_progress=(RelativeLayout)findViewById(R.id.start_progress);
        progress_text=(TextView)findViewById(R.id.progress_text);

        progressBar = findViewById(R.id.myProgress);
        gif_image=(ImageView)findViewById(R.id.gif_image);
        thumb_nail=(ImageView)findViewById(R.id.thumb_nail);
        layout_attachment=(RelativeLayout)findViewById(R.id.layout_attachment);
        post_image=(ImageView)findViewById(R.id.post_image);
        delete=(ImageView)findViewById(R.id.delete);
        video_view = (ImageView) findViewById(R.id.video_view);

        post_description=(Multi)findViewById(R.id.post_description);
        submit_post=(TextView)findViewById(R.id.submit_post);

        showView(create_view,search_view,profile_view,liked_view,post_view,contests_view,pref_view,winner_view,about_view,support_view);
        ((Drawer)context).setBottomBar(R.drawable.discovery_gray,R.drawable.profile_gray,R.drawable.creatpost_colored,R.drawable.notification_gray,R.drawable.moregray,creat_post,profile,discovery,notification,more);

        title1.setText(R.string.create_post);

        post_description.addTextChangedListener(new TextWatcher() {
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
                       /* dataModels.clear();
                        dataModels.addAll(temp_list);*/
                    }
                }
            }
        });

        submit_post.setOnClickListener(this);
        delete.setOnClickListener(this);
        post_image.setOnClickListener(this);
        gif_image.setOnClickListener(this);
        thumb_nail.setOnClickListener(this);
    }

    private void setServiceDeleteFile()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("image_id",attachment_id);
            object.put("post_id",post_id);
            new Retrofit2(context,this,5,Constants.deleteFiles,object).callService(false);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void setServiceSubmitPost()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("description",post_description.getText().toString());
            object.put("post_id",post_id);
            object.put("type","final");
            object.put("tagged_by",SharedPreference.retriveData(context,Constants.ID));

            JSONArray data=new JSONArray();
            for (int i = 0; i < list.size(); i++)
            {
                if (!SharedPreference.retriveData(context,Constants.ID).equalsIgnoreCase(list.get(i).getId()))
                data.put(list.get(i).getId());
            }
            object.put("tagged_userId",data);
            new Retrofit2(context,this,2,Constants.discription,object).callService(true);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private boolean validation()
    {
        if (post_id.equalsIgnoreCase(""))
        {
            Alerts.showDialog(context,"","Please Add Your Moments");
            return false;
        }
        /*if (post_description.getText().toString().isEmpty())
        {
            Alerts.showDialog(context,"","Please Write Your Description");
            return false;
        }*/
        return true;
    }

    @Override
    public void onClick(View view)
    {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.submit_post:
                if (validation())
                {
                    setServiceSubmitPost();
                }
                break;
            case R.id.delete:
                setServiceDeleteFile();
                break;
            case R.id.post_image:
            case R.id.gif_image:
            case R.id.thumb_nail:
                if (getIntent().hasExtra("video")
                        ||getIntent().hasExtra("image_gif"))
                {
                    startActivity(new Intent(context, PlayVideo.class)
                            .putExtra("post_video",attachment.toString()));
                }
                else
                {
                    startActivity(new Intent(context, FullScreenImage.class)
                    .putExtra("post_imagee",attachment.toString()));
                }
                break;
        }
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
                    case 1:
                        JSONObject data=result.getJSONObject("data");
                        post_id=data.getString("post_id");
                        attachment_id=data.getJSONObject("imagedata").getString("id");

                        if (getIntent().hasExtra("video"))
                        {
                            layout_attachment.setVisibility(View.VISIBLE);
                            video_view.setVisibility(View.VISIBLE);
                            post_image.setVisibility(View.GONE);
                            thumb_nail.setVisibility(View.VISIBLE);
                            gif_image.setVisibility(View.GONE);
                            Picasso.with(context).load(thumbnail)
                                    .placeholder(R.drawable.noimage).
                                    into(thumb_nail);
                        }
                        else if (getIntent().hasExtra("image_gif"))
                        {
                            layout_attachment.setVisibility(View.VISIBLE);
                            video_view.setVisibility(View.VISIBLE);
                            post_image.setVisibility(View.GONE);
                            thumb_nail.setVisibility(View.GONE);
                            gif_image.setVisibility(View.VISIBLE);
                            Picasso.with(context).load(thumbnail)
                                    .placeholder(R.drawable.noimage).
                                    into(gif_image);
                        }
                        else
                        {
                            layout_attachment.setVisibility(View.VISIBLE);
                            video_view.setVisibility(View.GONE);
                            post_image.setVisibility(View.VISIBLE);
                            thumb_nail.setVisibility(View.GONE);
                            gif_image.setVisibility(View.GONE);
                            Picasso.with(context).load(attachment)
                                    .placeholder(R.drawable.noimage).
                                    into(post_image);
                        }

                        setService();
                        break;
                    case 2:
                        post_id="";
                        Toast.makeText(context, "Post Create Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context,HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP
                                |Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                        post_description.setText("");
                       // SharedPreference.storeData(context,"create_new","create_new");
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
                    case 5:
                        Log.e("DONE","DONE");
                        post_id="";
                        layout_attachment.setVisibility(View.GONE);
                        break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setAdapterTag(final ArrayList<SearchDataModel> dataModelss)
    {
        userAdapter = new TagUserAdapter(context, R.layout.tag_user_adapter, dataModelss);
        post_description.setAdapter(userAdapter);
        post_description.setTokenizer(new SpaceTokenizer());
        post_description.setThreshold(1);

        post_description.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String tag_id = dataModelss.get(position).getId();
                String tag_name = dataModelss.get(position).getUser_name();
                Log.e("tag_id","---"+tag_id+"--"+tag_name);

                uniqueStrings.add(new CategoriesList(tag_id, tag_name));
                list = new ArrayList<>(uniqueStrings);

                dataModels.clear();
                dataModels.addAll(temp_list);
            }
        });
    }

    private void setAdapterHashTag()
    {
        Hashadapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, hashTagList);
        post_description.setAdapter(Hashadapter);
        post_description.setThreshold(1);
        post_description.setTokenizer(new SpaceHashTokenizer());
        post_description.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                dataModels.clear();
                dataModels.addAll(temp_list);
            }
        });
    }

    // TODO: 14/3/18 For Images
    private void setServiceOfPostImages()
    {
        map=new HashMap<>();
        map.put("user_id", RequestBody.create(MediaType.parse("multipart/form-data"),SharedPreference.retriveData(context,Constants.ID)));
        map.put("post_id", RequestBody.create(MediaType.parse("multipart/form-data"),post_id));
        map.put("attachment_type", RequestBody.create(MediaType.parse("multipart/form-data"), "image"));

        new Retrofit2(context,this,1, Constants.createpost,map,body,"1")
                .callServiceMultipart(true);
    }

    // TODO: 14/3/18 For Video's
    private void setServiceOfPostVideos(String type)
    {
        map=new HashMap<>();
        map.put("user_id", RequestBody.create(MediaType.parse("multipart/form-data"), SharedPreference.retriveData(context,Constants.ID)));
        map.put("post_id", RequestBody.create(MediaType.parse("multipart/form-data"),post_id));
        map.put("attachment_type", RequestBody.create(MediaType.parse("multipart/form-data"),type));

        new Retrofit2(context,this,1,Constants.createpost,map,body,thumb_body,"2")
                .callServiceMultipart(false);
    }

    @Override
    public void onProgressUpdate(int percentage)
    {
        progressBar.setProgress(percentage);
        progress_text.setText(percentage+"%");
        if (percentage==99)
        {
            start_progress.setVisibility(View.GONE);
            progressBar.setProgress(0);
            progress_text.setText("");
        }
    }
}
