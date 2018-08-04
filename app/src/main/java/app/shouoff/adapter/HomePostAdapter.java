package app.shouoff.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.shouoff.HashTagData;
import app.shouoff.R;
import app.shouoff.UserProfile;
import app.shouoff.common.Constants;
import app.shouoff.common.DataHandler;
import app.shouoff.model.HomePostModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomePostAdapter extends RecyclerView.Adapter
{
    Context context;
    private ArrayList<HomePostModel> postModels;

    private boolean isLoading;
    private int VIEW_TYPE_ITEM = 0;
    private int VIEW_TYPE_LOADING = 1;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private ItemClick itemClick;

    public HomePostAdapter(Context context,ArrayList<HomePostModel> postModels,RecyclerView recyclerView)
    {
        this.context=context;
        this.postModels=postModels;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <=(lastVisibleItem + visibleThreshold))
                {
                    if (itemClick != null)
                    {
                        itemClick.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        if (viewType == VIEW_TYPE_ITEM)
        {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.discovery_card, viewGroup, false);
            ViewHolder pvh = new ViewHolder(v);
            return pvh;
        }
        else if (viewType == VIEW_TYPE_LOADING)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pagination_loading, viewGroup, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        if (holder instanceof ViewHolder)
        {
            final ViewHolder userViewHolder = (ViewHolder) holder;
            userViewHolder.date.setText(Constants.date(postModels.get(position).getCreated_at()));
            userViewHolder.user.setText(postModels.get(position).getFirst_name());

            if (postModels.get(position).getDescription().equalsIgnoreCase("")||
                    postModels.get(position).getDescription().equalsIgnoreCase("null"))
            {
                userViewHolder.read_more.setVisibility(View.GONE);
                userViewHolder.post_description.setVisibility(View.GONE);
            }
            else
            {
                userViewHolder.read_more.setVisibility(View.VISIBLE);
                userViewHolder.post_description.setVisibility(View.VISIBLE);
            }

            /*Tag Data*/
            DataHandler.tagdata(context,userViewHolder.post_description,postModels.get(position));

            /*Post Image*/
            if (postModels.get(position).getAttachmentModels().size()>0)
            {
                if (postModels.get(position).getAttachmentModels().get(0).getAttachment_type().equalsIgnoreCase("video"))
                {

                    userViewHolder.video_image.setVisibility(View.VISIBLE);
                    userViewHolder.thumbnail.setVisibility(View.VISIBLE);
                    userViewHolder.post_image.setVisibility(View.GONE);
                    userViewHolder.gif_image.setVisibility(View.GONE);
                    Picasso.with(context).load(Constants.POST_URL+postModels.get(position).getAttachmentModels()
                            .get(0).getThumbnail())
                            .placeholder(R.drawable.noimage).into(userViewHolder.thumbnail);
                }
                else if (postModels.get(position).getAttachmentModels().get(0).getAttachment_type().equalsIgnoreCase("gif"))
                {

                    userViewHolder.video_image.setVisibility(View.VISIBLE);
                    userViewHolder.thumbnail.setVisibility(View.GONE);
                    userViewHolder.post_image.setVisibility(View.GONE);
                    userViewHolder.gif_image.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(Constants.POST_URL+postModels.get(position).getAttachmentModels()
                            .get(0).getThumbnail())
                            .placeholder(R.drawable.noimage).into(userViewHolder.gif_image);
                }
                else
                {
                    userViewHolder.video_image.setVisibility(View.GONE);
                    userViewHolder.thumbnail.setVisibility(View.GONE);
                    userViewHolder.post_image.setVisibility(View.VISIBLE);
                    userViewHolder.gif_image.setVisibility(View.GONE);
                    Picasso.with(context).load(Constants.POST_URL+postModels.get(position).getAttachmentModels()
                            .get(0).getAttachment_name())
                            .placeholder(R.drawable.noimage).into(userViewHolder.post_image);

                    userViewHolder.post_image.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event)
                        {
                            return userViewHolder.gestureDetector1.onTouchEvent(event);
                        }
                    });
                }
            }
            else
            {
                userViewHolder.video_image.setVisibility(View.GONE);
                userViewHolder.post_image.setVisibility(View.GONE);
                userViewHolder.thumbnail.setVisibility(View.GONE);
                userViewHolder.gif_image.setVisibility(View.GONE);
            }

            Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+postModels.get(position).getImage()).
                    placeholder(R.drawable.noimage).into(userViewHolder.user_image);

            userViewHolder.likes.setText(postModels.get(position).getLike_count()+" Likes");
            userViewHolder.comment.setText(postModels.get(position).getComment_count()+" Comments");
            userViewHolder.share.setText(postModels.get(position).getShare_count()+" Share");

            if (postModels.get(position).getLike_status().equalsIgnoreCase("0"))
            {
                userViewHolder.like_image.setImageResource(R.drawable.thumbup_grey);
            }
            else
            {
                userViewHolder.like_image.setImageResource(R.drawable.thumb_gold);
            }

        }
        else if (holder instanceof LoadingViewHolder)
        {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return postModels .get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position)
    {
        long i = super.getItemId(position);
        return i;
    }

    @Override
    public int getItemCount()
    {
        return postModels == null ? 0 : postModels .size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public CircleImageView user_image;
        public TextView user,date,post_description,read_more,likes,comment,share,likes_test;
        public ImageView video_image,like_image,thumbnail;
        public LinearLayout comment_lay,share_lay,likes_lay,main_view_lay;
        public ImageView post_image,gif_image;
        GestureDetector gestureDetector,gestureDetector1;

        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(View itemView)
        {
            super(itemView);
            gestureDetector = new GestureDetector(context,new GestureListener());
            gestureDetector1 = new GestureDetector(context,new GestureListenerImage());

            gif_image=(ImageView)itemView.findViewById(R.id.gif_image);
            main_view_lay=(LinearLayout)itemView.findViewById(R.id.main_view_lay);
            likes_lay=(LinearLayout)itemView.findViewById(R.id.likes_lay);
            user_image=(CircleImageView)itemView.findViewById(R.id.user_image);
            user=(TextView)itemView.findViewById(R.id.user);
            date=(TextView)itemView.findViewById(R.id.date);
            post_description=(TextView)itemView.findViewById(R.id.post_description);
            read_more=(TextView)itemView.findViewById(R.id.read_more);
            likes=(TextView)itemView.findViewById(R.id.likes);
            comment=(TextView)itemView.findViewById(R.id.comment);
            share=(TextView)itemView.findViewById(R.id.share);
            likes_test=(TextView)itemView.findViewById(R.id.likes_test);

            post_image=(ImageView)itemView.findViewById(R.id.post_image);
            thumbnail=(ImageView)itemView.findViewById(R.id.thumbnail);
            video_image=(ImageView)itemView.findViewById(R.id.video_image);
            like_image=(ImageView)itemView.findViewById(R.id.like_image);

            comment_lay=(LinearLayout)itemView.findViewById(R.id.comment_lay);
            share_lay=(LinearLayout)itemView.findViewById(R.id.share_lay);

            read_more.setOnClickListener(this);
            comment_lay.setOnClickListener(this);
            likes_lay.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    itemClick.details(view,getAdapterPosition());
                }
            });

            likes_lay.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    itemClick.likes(view,getAdapterPosition());
                }
            });

            main_view_lay.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    return gestureDetector.onTouchEvent(event);
                }
            });

            share_lay.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    itemClick.share(view,getAdapterPosition());
                }
            });

            likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClick.likeList(v,getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.read_more:
                case R.id.comment_lay:
                    itemClick.details(view,getAdapterPosition());
                    break;
            }
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
                itemClick.likes(main_view_lay,getAdapterPosition());
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e)
            {
                itemClick.details(main_view_lay,getAdapterPosition());
                return true;
            }
        }

        public class GestureListenerImage extends GestureDetector.SimpleOnGestureListener
        {
            @Override
            public boolean onDown(MotionEvent e)
            {
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e)
            {
                itemClick.likes(post_image,getAdapterPosition());
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e)
            {
                itemClick.showImage(post_image,getAdapterPosition());
                return true;
            }
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder
    {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view)
        {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    public void click(ItemClick itemClick)
    {
        this.itemClick=itemClick;
    }

    public interface ItemClick
    {
        void likes(View view,int pos);
        void share(View view,int pos);
        void details(View view,int pos);
        void showImage(View view,int pos);
        void likeList(View view,int pos);
        void onLoadMore();
    }
}
