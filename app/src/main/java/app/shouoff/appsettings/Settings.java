package app.shouoff.appsettings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import app.shouoff.BlockedUser;
import app.shouoff.R;
import app.shouoff.common.CommonService;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.drawer.Drawer;
import app.shouoff.login.PromoVideo;
import app.shouoff.retrofit.Retrofit2;
import app.shouoff.retrofit.RetrofitResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class Settings extends Drawer implements RetrofitResponse
{

    private TextView privacy_setting,share,terms,change_pass,privacy,deleteAccount,block_user;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initialized();
        title1.setText("Settings");
    }

    private void initialized()
    {
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

        block_user=(TextView)findViewById(R.id.block_user);
        deleteAccount=(TextView)findViewById(R.id.deleteAccount);
        privacy=(TextView)findViewById(R.id.privacy);
        change_pass=(TextView)findViewById(R.id.change_pass);
        terms=(TextView)findViewById(R.id.terms);
        privacy_setting=(TextView)findViewById(R.id.privacy_setting);
        share=(TextView)findViewById(R.id.share);

        share.setOnClickListener(this);
        change_pass.setOnClickListener(this);
        terms.setOnClickListener(this);
        privacy_setting.setOnClickListener(this);
        privacy.setOnClickListener(this);
        deleteAccount.setOnClickListener(this);
        block_user.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.share:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "ShowUoff");
                    String sAux = "\nLet me recommend you ShowUoff application\n\n";
                    // sAux = sAux + "https://play.google.com/store/apps/details?id=Orion.Soft \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch(Exception e)
                {
                    //e.toString();
                }
                break;
            case R.id.change_pass:
                startActivity(new Intent(context,ChangePassword.class));
                break;
            case R.id.terms:
                startActivity(new Intent(context,Terms.class));
                break;
            case R.id.privacy_setting:
                startActivity(new Intent(context,PrivacySettings.class));
                break;
            case R.id.privacy:
                startActivity(new Intent(context,Terms.class).putExtra("policy","policy"));
                break;
            case R.id.deleteAccount:
                startActivity(new Intent(context,DeleteAccount.class));
                break;
            case R.id.block_user:
                startActivity(new Intent(context,BlockedUser.class));
                break;
        }
    }

    /*Delete Account*/
    private void deleteAccountAlert()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete account.");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                deleteAccount();
                new CommonService().DeleteDevice(context);
                startActivity(new Intent(context, PromoVideo.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|
                                Intent.FLAG_ACTIVITY_CLEAR_TOP|
                                Intent.FLAG_ACTIVITY_NEW_TASK));
                SharedPreference.removeAll(context);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void deleteAccount()
    {
        new Retrofit2(context,this,10, Constants.DELETE_ACCOUNT+
        SharedPreference.retriveData(context,Constants.ID)).callServiceBack();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.fade1, R.anim.fade2);

    }


    @Override
    public void onServiceResponse(int requestCode, Response<ResponseBody> response) {

    }
}
