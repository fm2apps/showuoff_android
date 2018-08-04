package app.shouoff.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.model.PostAttachmentModel;

public class PostImagesAdapter extends RecyclerView.Adapter<PostImagesAdapter.ViewHolder>
{
    Context context;
    ArrayList<PostAttachmentModel> createIVModels;
    DeleteImage deleteImage;

    public PostImagesAdapter(Context context, ArrayList<PostAttachmentModel> createIVModels)
    {
        this.context=context;
        this.createIVModels=createIVModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.editpost_adapter, viewGroup, false);
        return new PostImagesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        if (createIVModels.get(position).getAttachment_type().equalsIgnoreCase("video"))
        {
            holder.video.setVisibility(View.VISIBLE);
            Picasso.with(context).load(Constants.POST_URL+createIVModels.get(position).getThumbnail()).placeholder(R.drawable.noimage).into(holder.file_image);
        }
        else
        {
            holder.video.setVisibility(View.GONE);
            Picasso.with(context).load(Constants.POST_URL+createIVModels.get(position).getAttachment_name()).placeholder(R.drawable.noimage).into(holder.file_image);
        }
    }

    @Override
    public int getItemCount() {
        return createIVModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView video;
        public RoundedImageView file_image;
        public ViewHolder(View itemView)
        {
            super(itemView);
            video=(ImageView)itemView.findViewById(R.id.video);
            file_image=(RoundedImageView)itemView.findViewById(R.id.file_image);

           /* file_image.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    try
                    {
                        context.startActivity(new Intent(context, PlayVideo.class)
                                .putExtra("attachment_file",createIVModels.get(getAdapterPosition()).getAttachment_name())
                                .putExtra("attachment",createIVModels.get(getAdapterPosition()).getThumbnail()));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });*/
        }
    }

    public void clickPerform(DeleteImage deleteImage)
    {
        this.deleteImage=deleteImage;
    }

    public interface DeleteImage
    {
        void selectData(View view, int position);
    }
}
