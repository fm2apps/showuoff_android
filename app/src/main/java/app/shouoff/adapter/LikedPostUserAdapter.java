package app.shouoff.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import app.shouoff.R;
import app.shouoff.UserProfile;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.login.MyProfile;
import app.shouoff.model.MyAdmireModel;

public class LikedPostUserAdapter extends RecyclerView.Adapter
{
    Context context;
    private ArrayList<MyAdmireModel> postModels;

    private boolean isLoading;
    private int VIEW_TYPE_ITEM = 0;
    private int VIEW_TYPE_LOADING = 1;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private ShowView showView;

    public LikedPostUserAdapter(Context context, ArrayList<MyAdmireModel> postModels, RecyclerView recyclerView)
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
                    if (showView != null)
                    {
                        showView.onLoadMore();
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
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fans_list_adapter, viewGroup, false);
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        if (holder instanceof ViewHolder)
        {
            final ViewHolder userViewHolder = (ViewHolder) holder;
            if (postModels.get(position).getId().equalsIgnoreCase(SharedPreference.retriveData(context,Constants.ID))
                    ||postModels.get(position).getId().equalsIgnoreCase("1"))
            {
                userViewHolder.follow.setVisibility(View.GONE);
            }
            else
            {
                userViewHolder.follow.setVisibility(View.VISIBLE);
            }

            if (postModels.get(position).getStatus().equalsIgnoreCase("1"))
            {
                userViewHolder.follow.setText("Unfollow");
            }
            else
            {
                userViewHolder.follow.setText("Follow");
            }

            userViewHolder.user_name.setText(postModels.get(position).getNick_name());
            Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+postModels.get(position).getImage())
                    .placeholder(R.drawable.noimage).into(userViewHolder.profile_image);
        }
        else if (holder instanceof LoadingViewHolder)
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
        public RoundedImageView profile_image;
        public TextView user_name,follow;

        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(View itemView)
        {
            super(itemView);
            profile_image=(RoundedImageView)itemView.findViewById(R.id.profile_image);
            user_name=(TextView)itemView.findViewById(R.id.user_name);
            follow=(TextView)itemView.findViewById(R.id.follow);
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    showView.profile(view,getAdapterPosition());
                }
            });

            follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showView.make(view,getAdapterPosition());
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

    public void click(ShowView showView)
    {
        this.showView=showView;
    }

    public interface ShowView
    {
        void make(View view,int pos);
        void profile(View view,int pos);
        void onLoadMore();
    }
}
