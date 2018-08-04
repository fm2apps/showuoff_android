package app.shouoff.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
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
import app.shouoff.model.HomePostModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class DiscoveryAdapter extends RecyclerView.Adapter<DiscoveryAdapter.ViewHolder>
{
    Context context;
    ArrayList<HomePostModel> postModels;
    ItemClick itemClick;

    public DiscoveryAdapter(Context context,ArrayList<HomePostModel> postModels)
    {
        this.context=context;
        this.postModels=postModels;
    }

    @Override
    public DiscoveryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.discovery_card, viewGroup, false);
        return new DiscoveryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DiscoveryAdapter.ViewHolder userViewHolder, int position)
    {
        userViewHolder.date.setText(Constants.date(postModels.get(position).getCreated_at()));
        userViewHolder.post_description.setText(postModels.get(position).getDescription());
        userViewHolder.user.setText(postModels.get(position).getFirst_name()+" "+postModels.get(position).getFamily_name());

            /*Post Image*/
        if (postModels.get(position).getAttachmentModels().size()>0)
        {
            if (postModels.get(position).getAttachmentModels().get(0).getAttachment_type().equalsIgnoreCase("video")
                    ||postModels.get(position).getAttachmentModels().get(0).getAttachment_type().equalsIgnoreCase("gif"))
            {
                userViewHolder.video_image.setVisibility(View.VISIBLE);
                Uri uri = Uri.parse(Constants.POST_URL+postModels.get(position).getAttachmentModels().get(0).getThumbnail());
                userViewHolder.post_image.setImageURI(uri);
            }
            else
            {
                userViewHolder.video_image.setVisibility(View.GONE);
                Uri uri = Uri.parse(Constants.POST_URL+postModels.get(position).getAttachmentModels().get(0).getAttachment_name());
                userViewHolder.post_image.setImageURI(uri);
            }
        }

        Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+postModels.get(position).getImage()).
                placeholder(R.drawable.noimage).into(userViewHolder.user_image);

        userViewHolder.likes.setText(postModels.get(position).getLike_count()+" Likes");
        userViewHolder.comment.setText(postModels.get(position).getComment_count()+" Comments");
        userViewHolder.share.setText(postModels.get(position).getShare_count()+" Share");

        if (postModels.get(position).getLike_status().equalsIgnoreCase("0"))
        {
            userViewHolder.like_image.setImageResource(R.drawable.thumbup_grey);
          //  userViewHolder.likes_test.setTextColor(ContextCompat.getColor(context,R.color.color_gray));
        }
        else
        {
           // userViewHolder.likes_test.setTextColor(ContextCompat.getColor(context,R.color.yellow_text));
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
        public CircleImageView user_image;
        public TextView user,date,post_description,read_more,likes,comment,share,likes_test;
        public ImageView video_image,like_image;
        public LinearLayout comment_lay,share_lay,likes_lay,main_view_lay;
        public SimpleDraweeView post_image;
        GestureDetector gestureDetector;

        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(View itemView)
        {
            super(itemView);
            gestureDetector = new GestureDetector(context,new GestureListener());
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

            post_image=(SimpleDraweeView)itemView.findViewById(R.id.post_image);
            video_image=(ImageView)itemView.findViewById(R.id.video_image);
            like_image=(ImageView)itemView.findViewById(R.id.like_image);

            comment_lay=(LinearLayout)itemView.findViewById(R.id.comment_lay);
            share_lay=(LinearLayout)itemView.findViewById(R.id.share_lay);


            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, PostDetails.class)
                            .putExtra("post_data",postModels.get(getAdapterPosition())));
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

        }

        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.read_more:
                case R.id.comment_lay:
                    context.startActivity(new Intent(context, PostDetails.class)
                            .putExtra("post_data",postModels.get(getAdapterPosition())));
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
                context.startActivity(new Intent(context, PostDetails.class)
                        .putExtra("post_data",postModels.get(getAdapterPosition())));
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
        void likes(View view,int pos);
        void share(View view,int pos);
    }

}
