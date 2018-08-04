package app.shouoff;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import app.shouoff.activity.HomeActivity;
import app.shouoff.adapter.CommentDetailAdapter;
import app.shouoff.baseadapter.TagUserAdapter;
import app.shouoff.common.Alerts;
import app.shouoff.common.CommonService;
import app.shouoff.common.Constants;
import app.shouoff.common.DataHandler;
import app.shouoff.common.SharedPreference;
import app.shouoff.common.SpaceHashTokenizer;
import app.shouoff.common.SpaceTokenizer;
import app.shouoff.drawer.Drawer;
import app.shouoff.interfaces.DeleteComment;
import app.shouoff.mediadata.FullScreenImage;
import app.shouoff.mediadata.PlayVideo;
import app.shouoff.model.CategoriesList;
import app.shouoff.model.CommentListModel;
import app.shouoff.model.PostAttachmentModel;
import app.shouoff.model.SearchDataModel;
import app.shouoff.model.TagUserData;
import app.shouoff.pageradapter.SlidingImage_Adapter;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import app.shouoff.widget.Multi;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class NotificationPostDetails extends Drawer implements RetrofitResponse,DeleteComment{
    Context context = this;
    private RecyclerView comment_list;
    private NestedScrollView nest_scroll;
    private CircleImageView user_image;
    private TextView user,date,post_description,likes,comment,share,count,view_more;
    private ImageView like_image,report,post_image,video_image,thumbnail,gif_image;
    private ViewPager pager;
    private LinearLayout share_lay,likes_lay;
    private SlidingImage_Adapter image_adapter;
    private LinearLayout send_message;
    private CommentDetailAdapter adapter;
    private ProgressBar comment_progress;
    private RelativeLayout show_layer;

    private ArrayList<CommentListModel> listModels=new ArrayList<>();
    private ArrayList<CommentListModel> sub_list=new ArrayList<>();
    private ArrayList<SearchDataModel> selected=new ArrayList<>();
    private ArrayList<String> offensive=new ArrayList<>();
    private List<PostAttachmentModel> listModelList=new ArrayList<>();
    private boolean aBoolean=true;
    android.support.v7.app.AlertDialog alertDialog;
    private RelativeLayout header_fav;
    private boolean add_delete=false;
    private String post_id="",share_count="",like_count="",user_id="";

    /*For Tagging and hashTagging*/
    private TagUserAdapter userAdapter;
    private ArrayAdapter<String> Hashadapter;
    private ArrayList<SearchDataModel> dataModels=new ArrayList<>();
    private ArrayList<SearchDataModel> temp_list=new ArrayList<>();
    private LinkedHashSet<CategoriesList> uniqueStrings = new LinkedHashSet<>();
    private List<CategoriesList> list = new ArrayList<>();
    private ArrayList<String> hashTagList=new ArrayList<>();
    private Multi make_comment;
    private GestureDetector gestureDetector;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_post_details);

        title1.setText("Post Detail");
        post_id=getIntent().getStringExtra("post_id");

        user_id=SharedPreference.retriveData(context,Constants.ID);
        initViews();
        setServicePostDetails();

        if (getIntent().getStringExtra("notification_type").equalsIgnoreCase("post_rejected"))
        {
            show_layer.setVisibility(View.VISIBLE);
            likes_lay.setClickable(false);
            make_comment.setEnabled(false);
            share_lay.setClickable(false);
            header_fav.setClickable(false);
            likes.setClickable(false);
            nest_scroll.setSmoothScrollingEnabled(false);
        }
        else
        {
            likes.setClickable(true);
            show_layer.setVisibility(View.GONE);
            likes_lay.setClickable(true);
            make_comment.setEnabled(true);
            share_lay.setClickable(true);
            header_fav.setClickable(true);
            nest_scroll.setSmoothScrollingEnabled(true);

            post_image.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    return gestureDetector.onTouchEvent(event);
                }
            });

            thumbnail.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    return gestureDetector.onTouchEvent(event);
                }
            });

            gif_image.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    return gestureDetector.onTouchEvent(event);
                }
            });


        }
    }

    @Override
    public void onClick(View view)
    {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.send_message:
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
            case R.id.view_more:
                startActivity(new Intent(context,CommentList.class)
                        .putExtra("post_id",post_id)
                        .putExtra("user_id",user_id)
                        .putExtra("commentList",listModels));
                break;
            case R.id.likes_lay:
                SharedPreference.storeData(context,"tmp","tmp");
                if (aBoolean)
                {
                    aBoolean=false;
                    new CommonService().articleLike(post_id,context);
                    like_image.setImageResource(R.drawable.thumb_gold);

                    String data=like_count;
                    int j= Integer.parseInt(data)+1;
                    likes.setText(String.valueOf(j)+" Likes");
                }
                else
                {
                    aBoolean=true;
                    new CommonService().articleDislike(post_id,context);
                    like_image.setImageResource(R.drawable.thumbup_grey);

                    String data=like_count;
                    int i = Integer.parseInt(data)-1;
                    likes.setText(String.valueOf(i)+" Likes");
                }
                break;
            case R.id.report:
                showAlertForReport();
                break;
            case R.id.share_lay:
                startActivityForResult(new Intent(context,SharePostActivity.class),100);
                break;
            case R.id.header_fav:
                context.startActivity(new Intent(context, UserProfile.class)
                        .putExtra("profile_id",user_id));
                break;
            case R.id.likes:
                if (!post_id.equalsIgnoreCase("0"))
                {
                    startActivity(new Intent(context, PostLikedUsers.class)
                            .putExtra("post_id",""+post_id));
                }
                break;
        }
    }

    private void setServiceCommentList()
    {
        new Retrofit2(context,this,0,Constants.perticular_post+post_id).callServiceBack();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100 && resultCode==101)
        {
            selected= (ArrayList<SearchDataModel>) data.getSerializableExtra("user_list");

            StringBuilder builder=new StringBuilder();
            for (int i=0;i<selected.size();i++)
            {
                builder.append(selected.get(i).getId());
                builder.append(",");
            }


            new CommonService().setServiceShare(String.valueOf(builder).substring(0,String.valueOf(builder).length()-1)
                    ,post_id,context,6,this);
        }
    }

    private void showAlertForReport()
    {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.custom_alert_dialog);
        LayoutInflater inflater=this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.report_alert_layout, null);
        final EditText report_message=(EditText) dialogView.findViewById(R.id.report_message);

        TextView report_text=(TextView)dialogView.findViewById(R.id.report_text);
        report_text.setText(R.string.report_post);
        TextView report=(TextView)dialogView.findViewById(R.id.report_submit);

        report.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (report_message.getText().toString().trim().isEmpty())
                {
                    return;
                }
                setServiceReport(report_message.getText().toString());
                alertDialog.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setLayout(width, height);
        alertDialog.show();
    }

    private void setServiceReport(String message)
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("post_id",post_id);
            object.put("user_id", SharedPreference.retriveData(context,Constants.ID));
            object.put("message",message);
            new Retrofit2(context,this,2, Constants.Post_report_byUser,object).callService(true);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /*Particular Post Details*/
    private void setServicePostDetails()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("post_id",post_id);
            object.put("requestor_id", SharedPreference.retriveData(context,Constants.ID));
            new Retrofit2(context,this,7,Constants.PostDetailsByPostId,object).callService(true);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
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
            object.put("post_id",post_id);
            object.put("user_id", SharedPreference.retriveData(context,Constants.ID));
            object.put("description",make_comment.getText().toString());
            object.put("tagged_by", SharedPreference.retriveData(context,Constants.ID));
            JSONArray data=new JSONArray();
            for (int i = 0; i < list.size(); i++)
            {
                if (!SharedPreference.retriveData(context,Constants.ID).equalsIgnoreCase(list.get(i).getId()))
                data.put(list.get(i).getId());
            }
            object.put("tagged_userId",data);
            new Retrofit2(context,this,1, Constants.createcomment,object).callService(true);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void setImageAdapter()
    {
        image_adapter=new SlidingImage_Adapter(context,listModelList);
        pager.setAdapter(image_adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                count.setText(String.valueOf(listModelList.size())+"/"+(pager.getCurrentItem()+1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        image_adapter.click(new SlidingImage_Adapter.doubleTap()
        {
            @Override
            public void show(View view, int pos)
            {
                try
                {
                    if (listModelList.get(pos).getAttachment_type().equalsIgnoreCase("video")||
                            listModelList.get(pos).getAttachment_type().equalsIgnoreCase("gif"))
                    {
                        startActivity(new Intent(context, PlayVideo.class)
                                .putExtra("attachment_file",listModelList.get(pos).getAttachment_name())
                                .putExtra("attachment",listModelList.get(pos).getThumbnail()));
                    }
                    else
                    {
                        startActivity(new Intent(context, FullScreenImage.class)
                                .putExtra("image",listModelList.get(pos).getAttachment_name()));
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void like()
            {
                SharedPreference.storeData(context,"tmp","tmp");
                if (aBoolean)
                {
                    aBoolean=false;
                    new CommonService().articleLike(post_id,context);
                    like_image.setImageResource(R.drawable.thumb_gold);
                    int j= Integer.parseInt(like_count)+1;
                    likes.setText(String.valueOf(j)+" Likes");
                }
                else
                {
                    aBoolean=true;
                    new CommonService().articleDislike(post_id,context);
                    like_image.setImageResource(R.drawable.thumbup_grey);
                    int i = Integer.parseInt(like_count)-1;
                    likes.setText(String.valueOf(i)+" Likes");
                }
            }
        });


        count.setText(String.valueOf(listModelList.size())+"/1");
    }

    private void initViews()
    {
        gif_image=(ImageView)findViewById(R.id.gif_image);
        gestureDetector = new GestureDetector(context,new GestureListener());
        thumbnail=(ImageView)findViewById(R.id.thumbnail);
        post_image=(ImageView)findViewById(R.id.post_image);
        video_image=(ImageView)findViewById(R.id.video_image);

        show_layer=(RelativeLayout)findViewById(R.id.show_layer);
        header_fav=(RelativeLayout)findViewById(R.id.header_fav);
        report=(ImageView)findViewById(R.id.report);
        likes_lay=(LinearLayout)findViewById(R.id.likes_lay);
        comment_list=(RecyclerView)findViewById(R.id.comment_list);
        view_more=(TextView)findViewById(R.id.view_more);
        comment_progress=(ProgressBar)findViewById(R.id.comment_progress);
        send_message=(LinearLayout)findViewById(R.id.send_message);
        make_comment=(Multi) findViewById(R.id.make_comment);
        count=(TextView)findViewById(R.id.count);
        pager=(ViewPager)findViewById(R.id.pager);
        user_image=(CircleImageView)findViewById(R.id.user_image);
        user=(TextView)findViewById(R.id.user);
        date=(TextView)findViewById(R.id.date);
        post_description=(TextView)findViewById(R.id.post_description);
        likes=(TextView)findViewById(R.id.likes);
        comment=(TextView)findViewById(R.id.comment);
        share=(TextView)findViewById(R.id.share);

        like_image=(ImageView)findViewById(R.id.like_image);

        share_lay=(LinearLayout)findViewById(R.id.share_lay);

        nest_scroll=(NestedScrollView)findViewById(R.id.nest_scroll);
        nest_scroll.smoothScrollTo(0,0);
        ((Drawer)context).setBottomBar(R.drawable.discovery_gray, R.drawable.profile_gray, R.drawable.creatpost_gray, R.drawable.notification_gray, R.drawable.more_colord, more, profile, creat_post, notification, discovery);

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
                    } else
                    {
                        dataModels.clear();
                        dataModels.addAll(temp_list);
                    }
                }
            }
        });


        send_message.setOnClickListener(this);
        view_more.setOnClickListener(this);
        likes_lay.setOnClickListener(this);
        report.setOnClickListener(this);
        share_lay.setOnClickListener(this);
        gif_image.setOnClickListener(this);
        likes.setOnClickListener(this);
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
        Hashadapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, hashTagList);
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

    private void comment_list()
    {
        Collections.reverse(listModels);
        sub_list.clear();
        if (listModels.size()>6)
        {
            sub_list.addAll(listModels.subList(0,6));
            view_more.setVisibility(View.VISIBLE);
        }
        else
        {
            sub_list.addAll(listModels);
            view_more.setVisibility(View.GONE);
        }
        comment_list.setHasFixedSize(true);
        comment_list.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CommentDetailAdapter(context,sub_list,getIntent().getStringExtra("sender_id"));
        comment_list.setAdapter(adapter);
        comment_list.setNestedScrollingEnabled(false);
        adapter.click(new CommentDetailAdapter.DeleteCmt()
        {
            @Override
            public void select(View view, int pos)
            {
                deleteComment(pos);
            }
        });
    }

    /*Delete Comment Alert to confirm*/
    private void deleteComment(int pos)
    {
        Alerts.showDialog(context,pos,this,"");
    }

    /*Delete Comment Service*/
    private void setServiceDeleteComment(int pos)
    {
        new CommonService().setServiceDeleteComment(listModels.get(pos).getId(),post_id,this,context);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fade1, R.anim.fade2);
    }

    private void setService()
    {
        JSONObject object=new JSONObject();
        try
        {
            object.put("user_id",SharedPreference.retriveData(context,Constants.ID));
            new Retrofit2(context,this,4, Constants.listUsers,object).callServiceBack();

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
                                        TagUserData userData=new TagUserData(tagData.getJSONObject("tagedUserDetails")
                                                .getString("id"),
                                                tagData.getJSONObject("tagedUserDetails")
                                                        .getString("username"),
                                                tagData.getJSONObject("tagedUserDetails")
                                                        .getString("nick_name"));
                                        tagUserData.add(userData);
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
                        comment.setText(listModels.size()+" Comments");
                        comment_list();
                        if (!add_delete)
                        {
                            new Retrofit2(context,this,5, Constants.offensive_Words).callServiceBack();
                        }

                        break;
                    case 1:
                        comment_progress.setVisibility(View.VISIBLE);
                        setServiceCommentList();
                        nest_scroll.scrollTo(0, view_more.getBottom());
                        make_comment.setText("");
                        add_delete=true;
                        break;
                    case 2:
                        Alerts.showDialog(context,"",result.getString("message"));
                        break;
                    case 33:
                        setServiceCommentList();
                        add_delete=true;
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
                    case 6:
                        String temp=share_count;
                        int j= Integer.parseInt(temp)+1;
                        share.setText(String.valueOf(j)+" Share");
                        break;
                    case 7:
                        setServiceCommentList();
                        JSONObject details=result.getJSONObject("data");
                        if (details.getString("user_id").equalsIgnoreCase(SharedPreference.
                                retriveData(context,Constants.ID)))
                        {
                            report.setVisibility(View.GONE);
                        }
                        else
                        {
                            report.setVisibility(View.VISIBLE);
                        }

                        date.setText(Constants.date(details.getString("created_at")));

                        if (details.getString("description").equalsIgnoreCase("")||
                                details.getString("description").equalsIgnoreCase("null"))
                        {
                            post_description.setVisibility(View.GONE);
                        }
                        else
                        {
                            post_description.setVisibility(View.VISIBLE);
                        }

                        /*Tagged User data*/
                        ArrayList<TagUserData> tagUserData=new ArrayList<>();
                        JSONArray tagg=details.getJSONArray("taggedUser");
                        if (tagg.length()>0)
                        {
                            for (int jj = 0; jj < tagg.length(); jj++)
                            {
                                JSONObject tagData=tagg.getJSONObject(jj);
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

                        /*Tag Data*/
                        DataHandler.notificationTagData(context,post_description,details.getString("description"),tagUserData);

                        user.setText(details.getJSONObject("postUserDetails").getString("nick_name"));

                        Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+details.getJSONObject("postUserDetails").getString("image")).
                                placeholder(R.drawable.noimage).into(user_image);

                        likes.setText(details.getString("like_count")+" Likes");
                        comment.setText(details.getString("comment_count")+" Comments");
                        share.setText(details.getString("share_count")+" Share");

                        share_count=details.getString("share_count");
                        like_count=details.getString("like_count");

                        if (details.getString("ifuserLike").equalsIgnoreCase("null"))
                        {
                            aBoolean=true;
                            like_image.setImageResource(R.drawable.thumbup_grey);
                        }
                        else
                        {
                            aBoolean=false;
                            like_image.setImageResource(R.drawable.thumb_gold);
                        }

                        listModelList.clear();
                        JSONArray attach=details.getJSONArray("postAttachment");
                        if (attach.length()>0)
                        {
                            for (int k=0;k<attach.length();k++)
                            {
                                JSONObject attach_value=attach.getJSONObject(k);
                                PostAttachmentModel attachmentModel=new PostAttachmentModel(attach_value.getString("attachment_name"),
                                        attach_value.getString("attachment_type"),
                                        attach_value.getString("thumbnail"));
                                listModelList.add(attachmentModel);
                            }
                            setAttachment();
                            //setImageAdapter();
                        }

                        if (!details.getString("user_id").equalsIgnoreCase(SharedPreference.retriveData(context,Constants.ID)))
                        {
                            header_fav.setOnClickListener(this);
                        }
                        break;
                    case 4:
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
                        new Retrofit2(context,this,8, Constants.Hash_Words).callServiceBack();
                    case 8:
                        hashTagList.clear();
                        JSONArray tag=result.getJSONArray("data");
                        if (tag.length()>0)
                        {
                            for (int i = 0; i < tag.length(); i++) {
                                JSONObject tagData=tag.getJSONObject(i);
                                hashTagList.add(tagData.getString("word"));
                            }
                        }
                        /*Read Notification*/
                        new CommonService().ReadStatusOfNotification(getIntent().getStringExtra("sender_id")
                                ,getIntent().getStringExtra("notification_type")
                                ,getIntent().getStringExtra("post_id"),context);
                        break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setAttachment()
    {
        /*Post Image*/
        if (listModelList.size()>0)
        {
            if (listModelList.get(0).getAttachment_type().equalsIgnoreCase("video"))
            {
                video_image.setVisibility(View.VISIBLE);
                thumbnail.setVisibility(View.VISIBLE);
                post_image.setVisibility(View.GONE);
                gif_image.setVisibility(View.GONE);
                Picasso.with(context).load(Constants.POST_URL+listModelList.get(0).getThumbnail())
                        .placeholder(R.drawable.noimage).into(thumbnail);
            }
            else if (listModelList.get(0).getAttachment_type().equalsIgnoreCase("gif"))
            {
                video_image.setVisibility(View.VISIBLE);
                thumbnail.setVisibility(View.VISIBLE);
                post_image.setVisibility(View.GONE);
                gif_image.setVisibility(View.VISIBLE);
                Picasso.with(context).load(Constants.POST_URL+listModelList.get(0).getThumbnail())
                        .placeholder(R.drawable.noimage).into(gif_image);
            }
            else
            {
                video_image.setVisibility(View.GONE);
                thumbnail.setVisibility(View.GONE);
                post_image.setVisibility(View.VISIBLE);
                gif_image.setVisibility(View.GONE);
                Picasso.with(context).load(Constants.POST_URL+listModelList.get(0).getAttachment_name())
                        .placeholder(R.drawable.noimage).into(post_image);
            }
        }
        else
        {
            video_image.setVisibility(View.GONE);
            post_image.setVisibility(View.GONE);
            thumbnail.setVisibility(View.GONE);
            gif_image.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (getIntent().hasExtra("notification"))
        {
            finish();
        }
        else
        {
            startActivity(new Intent(context, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|
                    Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    }

    @Override
    public void delete(View view, int pos)//Delete Comment
    {
        setServiceDeleteComment(pos);
        sub_list.remove(pos);
        adapter.notifyDataSetChanged();
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onDown(MotionEvent e)
        {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e)
        {
            SharedPreference.storeData(context,"tmp","tmp");
            if (aBoolean)
            {
                aBoolean=false;
                new CommonService().articleLike(post_id,context);
                like_image.setImageResource(R.drawable.thumb_gold);
                int j= Integer.parseInt(like_count)+1;
                likes.setText(String.valueOf(j)+" Likes");
            }
            else
            {
                aBoolean=true;
                new CommonService().articleDislike(post_id,context);
                like_image.setImageResource(R.drawable.thumbup_grey);
                int i = Integer.parseInt(like_count)-1;
                likes.setText(String.valueOf(i)+" Likes");
            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            if (listModelList.get(0).getAttachment_type().equalsIgnoreCase("video")||
                    listModelList.get(0).getAttachment_type().equalsIgnoreCase("gif"))
            {
                startActivity(new Intent(context, PlayVideo.class)
                        .putExtra("attachment_file",listModelList.get(0).getAttachment_name())
                        .putExtra("attachment",listModelList.get(0).getThumbnail()));
            }
            else
            {
                startActivity(new Intent(context, FullScreenImage.class)
                        .putExtra("image",listModelList.get(0).getAttachment_name()));
            }
            return true;
        }
    }
}
