package app.shouoff.photoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.shouoff.R;
import app.shouoff.common.Constants;

/**
 * Created by Ahmed Adel on 5/4/17.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<String> imageBitmaps;
    private LayoutInflater inflater;
    Context context;
    private OnImageClickListener onImageClickListener;

    public ImageAdapter(@NonNull Context context, @NonNull List<String> imageBitmaps) {
        this.inflater = LayoutInflater.from(context);
        this.imageBitmaps = imageBitmaps;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_photo_edit_image_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Picasso.with(context).load(Constants.FILTER_IMAGE_URL+imageBitmaps.get(position)).into(holder.imageView);
       // holder.imageView.setImageBitmap(imageBitmaps.get(position));
    }

    @Override
    public int getItemCount() {
        return imageBitmaps.size();
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.fragment_photo_edit_image_iv);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
