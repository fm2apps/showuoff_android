package app.shouoff.app;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.IOException;

import app.shouoff.R;
import app.shouoff.common.Constants;

public class App extends Application
{
    public static App mInstance;
    public static String ENQUIRIES = null;
    public static Context mcontext;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mInstance = this;
        FontsOverride.setDefaultFont(this, "MONOSPACE", "arcon-regular-webfont.ttf");
        mcontext = getApplicationContext();
        Fresco.initialize(this);
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        System.gc();
        Constants.freeMemory();

        File directory = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            directory = new File(Environment.getExternalStorageDirectory() + File.separator + "ShowUoff");
            if (!directory.exists())
            {
                directory.mkdirs();
            }
        } else {
            directory = getApplicationContext().getDir("ShowUoff", Context.MODE_PRIVATE);
            if (!directory.exists())
            {
                directory.mkdirs();
            }
        }

        if (directory != null)
        {
            File video = new File(directory + File.separator + "CameraImage");


            if (!video.exists())
            {
                video.mkdirs();
            }


            ENQUIRIES = directory + File.separator + "CameraImage";
        }
    }

    public static String getQUOTATIONS()
    {
        return ENQUIRIES;
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}

