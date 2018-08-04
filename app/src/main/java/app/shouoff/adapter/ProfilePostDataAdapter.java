package app.shouoff.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.model.HomePostModel;

public class ProfilePostDataAdapter extends RecyclerView.Adapter
{
    Context context;
    ArrayList<HomePostModel> postModels;
    ItemClick itemClick;

    public ProfilePostDataAdapter(Context context, ArrayList<HomePostModel> postModels)
    {
        this.context=context;
        this.postModels=postModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.highly_liked_conpetatiors_adapter, viewGroup, false);
        ViewHolder pvh = new ViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof ViewHolder)
        {
            final ViewHolder userViewHolder = (ViewHolder) holder;

            if (postModels.get(position).getAttachmentModels().get(0).getAttachment_type().equalsIgnoreCase("video")
                    ||postModels.get(position).getAttachmentModels().get(0).getAttachment_type().equalsIgnoreCase("gif"))
            {
                userViewHolder.video_image.setVisibility(View.VISIBLE);
                Picasso.with(context).load(Constants.POST_URL+
                        postModels.get(position).getAttachmentModels().get(0).
                                getThumbnail()).placeholder(R.drawable.noimage).into(userViewHolder.award_image);
            }
            else
            {
                userViewHolder.video_image.setVisibility(View.GONE);
                Picasso.with(context).load(Constants.POST_URL+
                        postModels.get(position).getAttachmentModels().get(0).
                                getAttachment_name()).placeholder(R.drawable.noimage).into(userViewHolder.award_image);
            }

        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    @Override
    public int getItemCount()
    {
        return postModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public RoundedImageView award_image;
        public ImageView video_image;
        public ViewHolder(View itemView)
        {
            super(itemView);
            video_image=(ImageView)itemView.findViewById(R.id.video_image);
            award_image=(RoundedImageView)itemView.findViewById(R.id.award_image_set);
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    itemClick.showProfile(view,getAdapterPosition());
                }
            });

        }
    }

    public void click(ItemClick itemClick)
    {
        this.itemClick=itemClick;
    }

    public interface ItemClick
    {
        void showProfile(View view, int pos);
    }
}
