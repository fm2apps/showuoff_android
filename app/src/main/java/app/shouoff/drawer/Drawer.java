package app.shouoff.drawer;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import app.shouoff.appsettings.AboutUs;
import app.shouoff.Awards;
import app.shouoff.appsettings.ContactUs;
import app.shouoff.DailyHighlyLiked;
import app.shouoff.MyPost;
import app.shouoff.R;
import app.shouoff.SearchUser;
import app.shouoff.appsettings.Settings;
import app.shouoff.activity.HomeActivity;
import app.shouoff.activity.Notification;
import app.shouoff.common.Alerts;
import app.shouoff.common.CommonService;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.login.MyProfile;
import app.shouoff.login.PromoVideo;
import app.shouoff.mediadata.CustomCamera;
import de.hdodenhof.circleimageview.CircleImageView;

public class Drawer extends AppCompatActivity implements View.OnClickListener
{
    public static Toolbar toolbar1;
    public static TextView title1;
    FrameLayout content_frame_layout;
    DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    View view;
    Context context=Drawer.this;
    public static CircleImageView profile_image_drawer;
    public static TextView drawer_name;
    public static LinearLayout bottom_bar;
    public static View bottom_view;
    private TextView bottom_text;
    LinearLayout discovery_lay, profile_lay, creat_post_lay, notification_lay, more_lay,my_posts_lay,search_user_lay,
            my_profile_lay,highly_liked_lay,creat_post_lay1,winner_lay,logout_lay,about_lay,settings,contact_now,contest_lay;
    public static TextView discovery, profile, creat_post, notification, more;
    public static ImageView discovery_icon, profile_icon, creat_post_icon, notification_icon, more_icon,back_btn;
    public static View search_view,profile_view,post_view,liked_view,create_view,contests_view,pref_view,winner_view,about_view,support_view;
    public static ImageView searchuser_icon,my_profile_icon,my_post_icon,highly_liked_icon,creat_post_icon1,contest_icon,
            prefrence_icon,winner_icon,about_icon,contact_support_icon;
    public TextView search_user,my_profile,my_post,highly_liked,creat_post1,contest,prefrence,winners,about,contact_support;

    String[] mPermission = {android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private MediaProjection mMediaProjection;

    private static final int CAST_PERMISSION_CODE = 1000;
    private MediaProjectionManager mProjectionManager;


    /*Set bottom bar view*/
    public void setBottomBar(int discovery1, int profile1, int creatpost1, int notification1, int more1, TextView discoverytext, TextView profiletext, TextView creatposttext, TextView notificationtext, TextView moretext) {

        discovery_icon.setImageResource(discovery1);
        profile_icon.setImageResource(profile1);
        creat_post_icon.setImageResource(creatpost1);
        notification_icon.setImageResource(notification1);
        more_icon.setImageResource(more1);

        discoverytext.setTextColor(ContextCompat.getColor(Drawer.this, R.color.app_color));
        profiletext.setTextColor(getResources().getColor(R.color.gray_text));
        creatposttext.setTextColor(getResources().getColor(R.color.gray_text));
        notificationtext.setTextColor(getResources().getColor(R.color.gray_text));
        moretext.setTextColor(getResources().getColor(R.color.gray_text));
    }

    /*Drawer corner view*/
    public static void showView(View search,View profile,View post,View liked,View create,View contests,View pref,View winner,View about,View support)
    {
        search.setVisibility(View.VISIBLE);
        profile.setVisibility(View.GONE);
        post.setVisibility(View.GONE);
        liked.setVisibility(View.GONE);
        create.setVisibility(View.GONE);
        contests.setVisibility(View.GONE);
        pref.setVisibility(View.GONE);
        winner.setVisibility(View.GONE);
        about.setVisibility(View.GONE);
        support.setVisibility(View.GONE);
    }

    /*Change Drawer Image*/
    public void showImage(int search,int profile,int post,int liked,int create,int contests,int pref,int winner_icon1,int about,int support)
    {
        searchuser_icon.setImageResource(search);
        my_profile_icon.setImageResource(profile);
        my_post_icon.setImageResource(post);
        highly_liked_icon.setImageResource(liked);
        creat_post_icon1.setImageResource(create);
        contest_icon.setImageResource(contests);
        prefrence_icon.setImageResource(pref);
        winner_icon.setImageResource(winner_icon1);
        about_icon.setImageResource(about);
        contact_support_icon.setImageResource(support);
    }

    /*Change Drawer Text*/
    public void showText(TextView search,TextView profile,TextView post,TextView liked,TextView create,TextView con,TextView pref,TextView winner_view, TextView abo,TextView cont)
    {
        search.setTextColor(ContextCompat.getColor(Drawer.this, R.color.app_color));
        profile.setTextColor(ContextCompat.getColor(Drawer.this, R.color.black));
        post.setTextColor(ContextCompat.getColor(Drawer.this, R.color.black));
        liked.setTextColor(ContextCompat.getColor(Drawer.this, R.color.black));
        create.setTextColor(ContextCompat.getColor(Drawer.this, R.color.black));
        con.setTextColor(ContextCompat.getColor(Drawer.this, R.color.black));
        pref.setTextColor(ContextCompat.getColor(Drawer.this, R.color.black));
        winner_view.setTextColor(ContextCompat.getColor(Drawer.this, R.color.black));
        abo.setTextColor(ContextCompat.getColor(Drawer.this, R.color.black));
        cont.setTextColor(ContextCompat.getColor(Drawer.this, R.color.black));

    }

    /*Set Toolbar*/
    public void setToolbarContent()
    {
        toolbar1 = (Toolbar) findViewById(R.id.toolbar1);
        title1 = (TextView) findViewById(R.id.title1);
        title1.setText("Discover");
        title1.setVisibility(View.VISIBLE);
        toolbar1.setNavigationIcon(R.drawable.menu_new);

        setSupportActionBar(toolbar1);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /*Initialized View*/
    private void initView()
    {
        bottom_text=(TextView)findViewById(R.id.bottom_text);

        bottom_view=(View)findViewById(R.id.bottom_view);
        bottom_bar=(LinearLayout)findViewById(R.id.bottom_bar);
        profile_image_drawer=(CircleImageView)findViewById(R.id.profile_image_drawer);
        drawer_name=(TextView)findViewById(R.id.drawer_name);

        searchuser_icon = (ImageView) findViewById(R.id.searchuser_icon);
        my_profile_icon = (ImageView) findViewById(R.id.my_profile_icon);
        my_post_icon = (ImageView) findViewById(R.id.my_post_icon);
        highly_liked_icon = (ImageView) findViewById(R.id.highly_liked_icon);
        creat_post_icon1 = (ImageView) findViewById(R.id.creat_post_icon1);
        contest_icon = (ImageView) findViewById(R.id.contest_icon);
        prefrence_icon = (ImageView) findViewById(R.id.prefrence_icon);
        winner_icon = (ImageView) findViewById(R.id.winner_icon);
        about_icon = (ImageView) findViewById(R.id.about_icon);
        contact_support_icon = (ImageView) findViewById(R.id.contact_support_icon);
        back_btn=(ImageView)findViewById(R.id.back_btn);

        search_user = (TextView) findViewById(R.id.search_user);
        my_profile = (TextView) findViewById(R.id.my_profile);
        my_post = (TextView) findViewById(R.id.my_post);
        highly_liked = (TextView) findViewById(R.id.highly_liked);
        creat_post1 = (TextView) findViewById(R.id.creat_post1);
        contest = (TextView) findViewById(R.id.contest);
        prefrence = (TextView) findViewById(R.id.prefrence);
        winners = (TextView) findViewById(R.id.winners);
        about = (TextView) findViewById(R.id.about);
        contact_support = (TextView) findViewById(R.id.contact_support);

        search_view=(View)findViewById(R.id.search_view);
        profile_view=(View)findViewById(R.id.profile_view);
        post_view=(View)findViewById(R.id.post_view);
        liked_view=(View)findViewById(R.id.liked_view);
        create_view=(View)findViewById(R.id.create_view);
        contests_view=(View)findViewById(R.id.contests_view);
        pref_view=(View)findViewById(R.id.pref_view);
        winner_view=(View)findViewById(R.id.winner_view);
        about_view=(View)findViewById(R.id.about_view);
        support_view=(View)findViewById(R.id.support_view);

        contest_lay=(LinearLayout)findViewById(R.id.contest_lay);
        contact_now=(LinearLayout)findViewById(R.id.contact_now);
        settings=(LinearLayout)findViewById(R.id.settings);
        about_lay=(LinearLayout)findViewById(R.id.about_lay);
        winner_lay=(LinearLayout)findViewById(R.id.winner_lay);
        my_posts_lay = (LinearLayout) findViewById(R.id.my_posts_lay);
        search_user_lay = (LinearLayout) findViewById(R.id.search_user_lay);
        my_profile_lay = (LinearLayout) findViewById(R.id.my_profile_lay);
        highly_liked_lay = (LinearLayout) findViewById(R.id.highly_liked_lay);
        creat_post_lay1 = (LinearLayout) findViewById(R.id.creat_post_lay1);
        discovery_lay = (LinearLayout) findViewById(R.id.discovery_lay);
        profile_lay = (LinearLayout) findViewById(R.id.profile_lay);
        creat_post_lay = (LinearLayout) findViewById(R.id.creat_post_lay);
        notification_lay = (LinearLayout) findViewById(R.id.notification_lay);
        more_lay = (LinearLayout) findViewById(R.id.more_lay);
        logout_lay=(LinearLayout)findViewById(R.id.logout_lay);

        discovery = (TextView) findViewById(R.id.discovery);
        profile = (TextView) findViewById(R.id.profile);
        creat_post = (TextView) findViewById(R.id.creat_post);
        more = (TextView) findViewById(R.id.more);
        notification = (TextView) findViewById(R.id.notification);

        discovery_icon = (ImageView) findViewById(R.id.discovery_icon);
        profile_icon = (ImageView) findViewById(R.id.profile_icon);
        creat_post_icon = (ImageView) findViewById(R.id.creat_post_icon);
        notification_icon = (ImageView) findViewById(R.id.notification_icon);
        more_icon = (ImageView) findViewById(R.id.more_icon);

        profile_lay.setOnClickListener(this);
        discovery_lay.setOnClickListener(this);
        creat_post_lay.setOnClickListener(this);
        notification_lay.setOnClickListener(this);
        more_lay.setOnClickListener(this);

        my_posts_lay.setOnClickListener(this);
        search_user_lay.setOnClickListener(this);
        my_profile_lay.setOnClickListener(this);
        highly_liked_lay.setOnClickListener(this);
        creat_post_lay1.setOnClickListener(this);
        winner_lay.setOnClickListener(this);
        logout_lay.setOnClickListener(this);
        about_lay.setOnClickListener(this);
        settings.setOnClickListener(this);
        contact_now.setOnClickListener(this);
        contest_lay.setOnClickListener(this);
        back_btn.setOnClickListener(this);
    }

    private boolean startRecording()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        }

        if (mMediaProjection == null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                startActivityForResult(mProjectionManager.createScreenCaptureIntent(), CAST_PERMISSION_CODE);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode==CAST_PERMISSION_CODE)
        {
            if (resultCode != RESULT_OK)
            {
                bottom_text.setVisibility(View.VISIBLE);
                new Handler().postDelayed(
                        new Runnable()
                        {
                            public void run()
                            {
                                bottom_text.setVisibility(View.GONE);
                            }
                        }, 2000);
                return;
            }
            else
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
                }
                bottom_text.setVisibility(View.GONE);
                startActivity(new Intent(this, CustomCamera.class));
            }
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        view = getLayoutInflater().inflate(R.layout.activity_drawer, null);
        content_frame_layout = (FrameLayout) view.findViewById(R.id.content_frame_layout);
        getLayoutInflater().inflate(layoutResID, content_frame_layout, true);
        super.setContentView(view);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        initView();
        setToolbarContent();

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar1, R.string.app_name, R.string.app_name)
        {
            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu();
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(false);
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle.syncState();

        drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DrawerLayout drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer_layout.isDrawerOpen(GravityCompat.START))
                {
                    drawer_layout.closeDrawer(GravityCompat.START);
                }
                else
                {
                    drawer_layout.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        drawer_name.setText(SharedPreference.retriveData(context, Constants.NICK_NAME));
        Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+SharedPreference.retriveData(context,Constants.Image)).placeholder(R.drawable.noimage).into(profile_image_drawer);
    }

    @Override
    public void onClick(View view)
    {
        drawerLayout.closeDrawers();
        switch (view.getId())
        {
            case R.id.profile_lay:
                startActivity(new Intent(this, MyProfile.class));
                break;
            case R.id.discovery_lay:
                startActivity(new Intent(this, HomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|
                                Intent.FLAG_ACTIVITY_CLEAR_TOP|
                                Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            case R.id.creat_post_lay:
                if (SharedPreference.retriveData(context,"never_ask")==null)
                {
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
                        if (startRecording())
                        {
                            startActivity(new Intent(this, CustomCamera.class));
                        }
                    }
                }
                else
                {
                    Alerts.showDialog(context,"","You declined app permission so, you can't post any image's or video's");
                }
                break;
            case R.id.notification_lay:
                startActivity(new Intent(this, Notification.class));
                break;
            case R.id.more_lay:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.my_posts_lay:
                startActivity(new Intent(this, MyPost.class)
                        .putExtra("user_id",SharedPreference.retriveData(context,Constants.ID))
                .putExtra("my","my"));
                break;
            case R.id.search_user_lay:
                startActivity(new Intent(this, SearchUser.class));
                break;
            case R.id.my_profile_lay:
                startActivity(new Intent(this, MyProfile.class));
                break;
            case R.id.highly_liked_lay:
                startActivity(new Intent(this, DailyHighlyLiked.class)
                        .putExtra("liked","liked"));
                break;
            case R.id.creat_post_lay1:
                Alerts.showDialog(context,"","Store Coming Soon");
               // startActivity(new Intent(this, ShopNow.class));
                break;
            case R.id.winner_lay:
                startActivity(new Intent(this, Awards.class));
                break;
            case R.id.logout_lay:
                confirmLogout();
                break;
            case R.id.about_lay:
                startActivity(new Intent(this, AboutUs.class));
                break;
            case R.id.settings:
                startActivity(new Intent(this, Settings.class));
                break;
            case R.id.contact_now:
                startActivity(new Intent(this, ContactUs.class));
                break;
            case R.id.contest_lay:
                startActivity(new Intent(this, DailyHighlyLiked.class));
                break;
            case R.id.back_btn:
                finish();
                break;
        }
    }

    /*Logout Account*/
    private void confirmLogout()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(R.string.logout_account);
        builder.setCancelable(false);
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                new CommonService().DeleteDevice(context);
                startActivity(new Intent(context, PromoVideo.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|
                                Intent.FLAG_ACTIVITY_CLEAR_TOP|
                                Intent.FLAG_ACTIVITY_NEW_TASK));
                SharedPreference.removeAll(context);
                dialogInterface.dismiss();

            }
        });
        AlertDialog alertDialog  = builder.create();
        alertDialog .show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setAllCaps(false);
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setAllCaps(false);
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


                        } else
                        {
                            //set to never ask again
                            SharedPreference.storeDataForPermission(context,"never_ask","never_ask");
                        }
                    }
                }
                startRecording();
                break;
            default:
            {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

}
