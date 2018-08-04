package app.shouoff.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.shouoff.PostDetails;
import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.common.DataHandler;
import app.shouoff.model.HomePostModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class SharedPostAdapter extends RecyclerView.Adapter<SharedPostAdapter.ViewHolder>
{
    Context context;
    ArrayList<HomePostModel> postModels;
    ItemClick itemClick;

    public SharedPostAdapter(Context context, ArrayList<HomePostModel> postModels)
    {
        this.context=context;
        this.postModels=postModels;
    }

    @Override
    public SharedPostAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.share_discovery_card, viewGroup, false);
        return new SharedPostAdapter.ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final SharedPostAdapter.ViewHolder userViewHolder, int position)
    {
        userViewHolder.date.setText(Constants.date(postModels.get(position).getCreated_at()));

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

        userViewHolder.user.setText(postModels.get(position).getFirst_name()+" "+postModels.get(position).getFamily_name()+" post");

        userViewHolder.share_user.setText("");
        DataHandler.addTextForPost(context,userViewHolder.share_user,postModels.get(position).getUser_name(),
                postModels.get(position).getFirst_name()+"'s"
        ,postModels.get(position));
        userViewHolder.share_user.setMovementMethod(LinkMovementMethod.getInstance());

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


        Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+postModels.get(position).getUser_image()).
                placeholder(R.drawable.noimage).into(userViewHolder.share_user_image);

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

    @Override
    public int getItemCount()
    {
        return postModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public CircleImageView user_image,share_user_image;
        public TextView user,date,post_description,read_more,likes,comment,share,likes_test,share_user;
        public ImageView video_image,like_image;
        public LinearLayout comment_lay,share_lay,likes_lay,main_view_lay;
        public ImageView post_image,gif_image,thumbnail;
        GestureDetector gestureDetector,gestureDetector1;

        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(View itemView)
        {
            super(itemView);
            gestureDetector = new GestureDetector(context,new GestureListener());
            gestureDetector1 = new GestureDetector(context,new GestureListenerImage());
            main_view_lay=(LinearLayout)itemView.findViewById(R.id.main_view_lay);
            likes_lay=(LinearLayout)itemView.findViewById(R.id.likes_lay);
            share_user_image=(CircleImageView)itemView.findViewById(R.id.share_user_image);
            user_image=(CircleImageView)itemView.findViewById(R.id.user_image);
            user=(TextView)itemView.findViewById(R.id.user);
            share_user=(TextView)itemView.findViewById(R.id.share_user);
            date=(TextView)itemView.findViewById(R.id.date);
            post_description=(TextView)itemView.findViewById(R.id.post_description);
            read_more=(TextView)itemView.findViewById(R.id.read_more);
            likes=(TextView)itemView.findViewById(R.id.likes);
            comment=(TextView)itemView.findViewById(R.id.comment);
            share=(TextView)itemView.findViewById(R.id.share);
            likes_test=(TextView)itemView.findViewById(R.id.likes_test);

            post_image=(ImageView)itemView.findViewById(R.id.post_image);
            gif_image=(ImageView)itemView.findViewById(R.id.gif_image);
            thumbnail=(ImageView)itemView.findViewById(R.id.thumbnail);

            video_image=(ImageView)itemView.findViewById(R.id.video_image);
            like_image=(ImageView)itemView.findViewById(R.id.like_image);

            comment_lay=(LinearLayout)itemView.findViewById(R.id.comment_lay);
            share_lay=(LinearLayout)itemView.findViewById(R.id.share_lay);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    itemClick.details(view,getAdapterPosition());
                }
            });

            likes_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClick.likes(view,getAdapterPosition());
                }
            });

            share_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClick.share(view,getAdapterPosition());
                }
            });

            main_view_lay.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    return gestureDetector.onTouchEvent(event);
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

    public void click(ItemClick itemClick)
    {
        this.itemClick=itemClick;
    }

    public interface ItemClick
    {
        void likes(View view, int pos);
        void share(View view, int pos);
        void showImage(View view,int pos);
        void likeList(View view,int pos);
        void details(View view,int pos);
    }

}
