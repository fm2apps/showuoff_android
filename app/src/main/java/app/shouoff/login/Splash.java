package app.shouoff.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;

public class Splash extends AppCompatActivity
{
    Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setBackgroundDrawableResource(R.drawable.splash_latest_origional);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(
                new Runnable()
                {
                    public void run()
                    {
                        if (SharedPreference.retriveData(context, Constants.ID)!=null)
                        {
                            Intent intent=new Intent(Splash.this,HomePage.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Intent intent=new Intent(Splash.this,PromoVideo.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.gc();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.gc();
    }



}
