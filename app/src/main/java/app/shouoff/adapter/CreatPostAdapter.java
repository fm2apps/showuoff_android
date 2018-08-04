package app.shouoff.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import app.shouoff.mediadata.FullScreenImage;
import app.shouoff.mediadata.PlayVideo;
import app.shouoff.R;
import app.shouoff.model.PostCreateIVModel;

public class CreatPostAdapter extends RecyclerView.Adapter<CreatPostAdapter.ViewHolder>
{
    Context context;
    ArrayList<PostCreateIVModel> createIVModels;
    DeleteImage deleteImage;

    public CreatPostAdapter(Context context,ArrayList<PostCreateIVModel> createIVModels)
    {
        this.context=context;
        this.createIVModels=createIVModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.creatpost_adapter, viewGroup, false);
        return new CreatPostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        if (createIVModels.get(position).getType().equalsIgnoreCase("video"))
        {
            holder.video.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.video.setVisibility(View.GONE);
        }

        Picasso.with(context).load(createIVModels.get(position).getAttachment())
                .placeholder(R.drawable.noimage).into(holder.file_image);

        holder.file_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!createIVModels.get(position).getType().equalsIgnoreCase("video"))
                {
                    context.startActivity(new Intent(context, FullScreenImage.class)
                            .putExtra("post_imagee",createIVModels.get(position)));
                }
                else
                {
                    context.startActivity(new Intent(context, PlayVideo.class)
                            .putExtra("post_video",createIVModels.get(position)));
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return createIVModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView video,delete;
        public RoundedImageView file_image;
        public ViewHolder(View itemView)
        {
            super(itemView);

            video=(ImageView)itemView.findViewById(R.id.video);
            delete=(ImageView)itemView.findViewById(R.id.delete);

            file_image=(RoundedImageView)itemView.findViewById(R.id.file_image);

            delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    deleteImage.selectData(view,getAdapterPosition());
                }
            });
        }
    }

    public void clickPerform(DeleteImage deleteImage)
    {
        this.deleteImage=deleteImage;
    }

    public interface DeleteImage
    {
        void selectData(View view,int position);
    }
}
