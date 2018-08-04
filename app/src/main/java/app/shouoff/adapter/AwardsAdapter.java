package app.shouoff.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import app.shouoff.PostDetails;
import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.model.HomePostModel;

public class AwardsAdapter extends RecyclerView.Adapter<AwardsAdapter.ViewHolder>
{
    Context context;
    ArrayList<HomePostModel> postModels;

    public AwardsAdapter(Context context,ArrayList<HomePostModel> postModels)
    {
        this.context=context;
        this.postModels=postModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.awards_adapter,viewGroup,false);
        return new AwardsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.award_date.setText(Constants.monthYear(postModels.get(position).getCreated_at()));

        if (postModels.get(position).getId().equalsIgnoreCase(""))
        {
            holder.video_image.setVisibility(View.GONE);
            holder.award_image.setImageDrawable(context.getResources().getDrawable( R.drawable.no_winners ));
        }
        else
        {
            if (postModels.get(position).getAttachmentModels().get(0).getAttachment_type().equalsIgnoreCase("video")
                    ||postModels.get(position).getAttachmentModels().get(0).getAttachment_type().equalsIgnoreCase("gif"))
            {
                holder.video_image.setVisibility(View.VISIBLE);
                Picasso.with(context).load(Constants.POST_URL+
                        postModels.get(position).getAttachmentModels().get(0).
                                getThumbnail()).placeholder(R.drawable.noimage).into(holder.award_image);
            }
            else
            {
                holder.video_image.setVisibility(View.GONE);
                Picasso.with(context).load(Constants.POST_URL+
                        postModels.get(position).getAttachmentModels().get(0).
                                getAttachment_name()).placeholder(R.drawable.noimage).into(holder.award_image);
            }
        }
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
        private TextView award_date;

        public ViewHolder(View itemView)
        {
            super(itemView);
            award_date=(TextView)itemView.findViewById(R.id.award_date);
            video_image=(ImageView)itemView.findViewById(R.id.video_image);
            award_image=(RoundedImageView)itemView.findViewById(R.id.award_image_set);
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (!postModels.get(getAdapterPosition()).getId().equalsIgnoreCase(""))
                    {
                        context.startActivity(new Intent(context,PostDetails.class)
                                .putExtra("post_data",postModels.get(getAdapterPosition()))
                                .putExtra("from_award","from_award"));
                    }
                }
            });
        }
    }

}
