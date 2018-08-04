package app.shouoff.photoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.List;

import app.shouoff.R;
import app.shouoff.common.Constants;

public class GifImageAdapter extends RecyclerView.Adapter<GifImageAdapter.ViewHolder> {

    private List<String> imageBitmaps;
    private LayoutInflater inflater;
    private OnImageClickListener onImageClickListener;

    public GifImageAdapter(@NonNull Context context, @NonNull List<String> imageBitmaps)
    {
        this.inflater = LayoutInflater.from(context);
        this.imageBitmaps = imageBitmaps;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.photo_editor_sdk_gif_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Uri loader = Uri.parse(Constants.FILTER_IMAGE_URL+imageBitmaps.get(position));
        DraweeController controllerOne = Fresco.newDraweeControllerBuilder()
                .setUri(loader)
                .setAutoPlayAnimations(true)
                .build();
        holder.imageView.setController(controllerOne);
    }

    @Override
    public int getItemCount() {
        return imageBitmaps.size();
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener)
    {
        this.onImageClickListener = onImageClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        SimpleDraweeView imageView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            imageView = (SimpleDraweeView) itemView.findViewById(R.id.profile_imagee);
            imageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (onImageClickListener != null)
                        onImageClickListener.onImageClickListener(imageBitmaps.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnImageClickListener
    {
        void onImageClickListener(String image);
    }
}
