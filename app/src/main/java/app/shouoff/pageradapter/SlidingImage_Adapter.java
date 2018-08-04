package app.shouoff.pageradapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import java.util.List;
import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.model.PostAttachmentModel;

public class SlidingImage_Adapter extends PagerAdapter
{
    private List<PostAttachmentModel> listModelList;
    private LayoutInflater inflater;
    private Context context;
    doubleTap doubleTap;
    GestureDetector gestureDetector;
    View viewpos;
    int pos;

    public SlidingImage_Adapter(Context context, List<PostAttachmentModel> listModelList)
    {
        this.context = context;
        this.listModelList=listModelList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return listModelList.size();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public Object instantiateItem(ViewGroup view, final int position)
    {
        View imageLayout = inflater.inflate(R.layout.attachfile_adapter, view, false);

        ImageView video_image;
        ImageView post_image;
        RelativeLayout lay=(RelativeLayout)imageLayout.findViewById(R.id.lay);
        gestureDetector = new GestureDetector(context,new GestureListener());
        video_image=(ImageView)imageLayout.findViewById(R.id.video_image);
        post_image=(ImageView)imageLayout.findViewById(R.id.post_image);

        lay.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return gestureDetector.onTouchEvent(event);
            }
        });

        viewpos=view;
        pos=position;

        post_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                doubleTap.show(view,position);
            }
        });

        /* Post Image*/
        if (listModelList.get(position).getAttachment_type().equalsIgnoreCase("video")||
                listModelList.get(position).getAttachment_type().equalsIgnoreCase("gif"))
        {
            video_image.setVisibility(View.VISIBLE);
            Picasso.with(context).load(Constants.POST_URL+listModelList.get(position).getThumbnail()).placeholder(R.drawable.noimage).into(post_image);
        }
        else
        {
            video_image.setVisibility(View.GONE);
            Picasso.with(context).load(Constants.POST_URL+listModelList.get(position).getAttachment_name()).placeholder(R.drawable.noimage).into(post_image);
        }
        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader)
    {

    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    public void click(doubleTap doubleTap)
    {
        this.doubleTap=doubleTap;
    }


    public interface doubleTap
    {
        void show(View view,int pos);
        void like();
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
            doubleTap.like();
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            doubleTap.show(viewpos,pos);
            return true;
        }
    }
}

