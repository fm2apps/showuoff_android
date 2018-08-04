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

public class HighlyLikedAdapter extends RecyclerView.Adapter
{
    Context context;
    ArrayList<HomePostModel> postModels;

    private boolean isLoading;
    private int VIEW_TYPE_ITEM = 0;
    private int VIEW_TYPE_LOADING = 1;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private ItemClick itemClick;

    public HighlyLikedAdapter(Context context, ArrayList<HomePostModel> postModels,RecyclerView recyclerView)
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

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold))
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
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.highly_liked_conpetatiors_adapter, viewGroup, false);
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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof ViewHolder)
        {
            final ViewHolder userViewHolder = (ViewHolder) holder;

        if (postModels.get(position).getFile_to_show().equalsIgnoreCase("0")
                ||postModels.get(position).getFile_to_show().equalsIgnoreCase("1"))
        {
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
        else
        {
            for (int i = 0; i < postModels.get(position).getAttachmentModels().size(); i++)
            {
                if (postModels.get(position).getAttachmentModels().get(i).getId().equalsIgnoreCase(postModels.get(position).getFile_to_show()))
                {
                    if (postModels.get(position).getAttachmentModels().get(i).getAttachment_type().equalsIgnoreCase("video")
                            ||postModels.get(position).getAttachmentModels().get(i).getAttachment_type().equalsIgnoreCase("gif"))
                    {
                        userViewHolder.video_image.setVisibility(View.VISIBLE);
                        Picasso.with(context).load(Constants.POST_URL+
                                postModels.get(position).getAttachmentModels().get(i).
                                        getThumbnail()).placeholder(R.drawable.noimage).into(userViewHolder.award_image);
                    }
                    else
                    {
                        userViewHolder.video_image.setVisibility(View.GONE);
                        Picasso.with(context).load(Constants.POST_URL+
                                postModels.get(position).getAttachmentModels().get(i).
                                        getAttachment_name()).placeholder(R.drawable.noimage).into(userViewHolder.award_image);
                    }
                }
            }
        }

        }else if (holder instanceof LoadingViewHolder)
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
        void onLoadMore();
        void showProfile(View view,int pos);
    }
}
