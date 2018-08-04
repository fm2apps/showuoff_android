package app.shouoff.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import app.shouoff.R;
import app.shouoff.model.FilterImageModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class FilterImagesAdapter extends RecyclerView.Adapter<FilterImagesAdapter.ViewHolder>
{
    Context context;
    ArrayList<FilterImageModel> imageModels;
    Check check;

    public FilterImagesAdapter(Context context, ArrayList<FilterImageModel> imageModels)
    {
        this.context=context;
        this.imageModels=imageModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filter_image_layout,
                viewGroup,false);
        return new FilterImagesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Picasso.with(context).load(imageModels.get(position).getId()).into(holder.image_filter);
    }

    @Override
    public int getItemCount()
    {
        return imageModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView image_filter;
        public ViewHolder(View itemView)
        {
            super(itemView);
            image_filter=(ImageView)itemView.findViewById(R.id.image_filter);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    check.show(v,getAdapterPosition());
                }
            });
        }
    }

    public void click(Check check)
    {
        this.check=check;
    }

    public interface Check
    {
        void show(View view,int pos);
    }
}
