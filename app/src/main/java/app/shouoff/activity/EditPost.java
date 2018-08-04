package app.shouoff.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import app.shouoff.R;
import app.shouoff.adapter.PostImagesAdapter;
import app.shouoff.common.Constants;
import app.shouoff.drawer.Drawer;
import app.shouoff.model.HomePostModel;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class EditPost extends Drawer implements RetrofitResponse
{
    private RecyclerView recycler;
    private EditText edit_description;
    private TextView submit_post;
    private HomePostModel homePostModel;
    private PostImagesAdapter imagesAdapter;
    private Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        initialized();
    }

    private void initialized()
    {
        title1.setText("Edit Post");

        homePostModel= (HomePostModel) getIntent().getSerializableExtra("post_data");

        recycler=(RecyclerView)findViewById(R.id.recycler);
        edit_description=(EditText)findViewById(R.id.edit_description);
        edit_description.setText(homePostModel.getDescription());
        submit_post=(TextView)findViewById(R.id.submit_post);
        submit_post.setOnClickListener(this);
        setAdapter();
    }

    private void setAdapter()
    {
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(EditPost.this,LinearLayoutManager.HORIZONTAL,false));
        imagesAdapter=new PostImagesAdapter(context,homePostModel.getAttachmentModels());
        recycler.setAdapter(imagesAdapter);
    }

    @Override
    public void onClick(View view)
    {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.submit_post:
                if (edit_description.getText().toString().trim().isEmpty())
                {
                    return;
                }
                setUser_editPostService();
                break;
        }
    }

    private void setUser_editPostService()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("post_id",homePostModel.getId());
            object.put("description",edit_description.getText().toString());
            new Retrofit2(context,this,1,Constants.User_editPost,object).callService(true);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
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
                        Intent intent=new Intent();
                        intent.putExtra("description",edit_description.getText().toString());
                        setResult(201,intent);
                        finish();
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
