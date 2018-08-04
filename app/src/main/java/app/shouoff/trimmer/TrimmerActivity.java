package app.shouoff.trimmer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import app.shouoff.R;
import app.shouoff.app.App;
import app.shouoff.common.Constants;
import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;

public class TrimmerActivity extends AppCompatActivity
{
    private K4LVideoTrimmer videoTrimmer;
    private String [] media_path;
    private String media_name;
    public static final String DEFAULT="DEFAULT";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimmer);
        Constants.freeMemory();
        videoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));
        try
        {
            if (videoTrimmer != null) {
                videoTrimmer.setVideoURI(Uri.parse(getIntent().getStringExtra("VIDEO")));
                media_path=getIntent().getStringExtra("VIDEO").split("/");
                media_name=media_path[media_path.length-1];
            }

            File dir = new File(App.getQUOTATIONS());
            if (!dir.exists())
                dir.mkdirs();

            videoTrimmer.setDestinationPath(App.getQUOTATIONS()+"/");
            videoTrimmer.setMaxDuration(60);
            videoTrimmer.setOnTrimVideoListener(new OnTrimVideoListener()
            {
                @Override
                public void getResult(final Uri uri)
                {
                    Intent intent = new Intent();
                    intent.putExtra("PATH",uri.getPath());
                    setResult(101,intent);
                    finish();
                }

                @Override
                public void cancelAction() {
                    onBackPressed();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(videoTrimmer!=null)
        {
            videoTrimmer.destroy();
        }
        Intent intent = new Intent();
        intent.putExtra("PATH", DEFAULT);
        setResult(101,intent);
        finish();
    }
}
