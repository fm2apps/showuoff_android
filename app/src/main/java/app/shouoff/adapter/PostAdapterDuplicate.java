package app.shouoff.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.shouoff.AdMobEx.NativeAppInstallAdViewHolder;
import app.shouoff.AdMobEx.NativeContentAdViewHolder;
import app.shouoff.R;
import app.shouoff.UserProfile;
import app.shouoff.common.Constants;
import app.shouoff.common.DataHandler;
import app.shouoff.common.SharedPreference;
import app.shouoff.login.MyProfile;
import app.shouoff.model.HomePostModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapterDuplicate extends RecyclerView.Adapter
{
    Context context;
    private ArrayList<Object> postModels;

    private boolean isLoading;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private static final int NATIVE_CONTENT_AD_VIEW_TYPE = 2;
    private static final int NATIVE_APP_INSTALL_AD_VIEW_TYPE = 3;

    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private ItemClick itemClick;

    public PostAdapterDuplicate(Context context, ArrayList<Object> postModels, RecyclerView recyclerView)
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

                if (!isLoading && totalItemCount <=(lastVisibleItem + visibleThreshold))
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
        switch (viewType)
        {
            case NATIVE_APP_INSTALL_AD_VIEW_TYPE:
                View nativeAppInstallLayoutView = LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.ad_app_install,
                        viewGroup, false);
                return new NativeAppInstallAdViewHolder(nativeAppInstallLayoutView);
            case NATIVE_CONTENT_AD_VIEW_TYPE:
                View nativeContentLayoutView = LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.ad_content,
                        viewGroup, false);
                return new NativeContentAdViewHolder(nativeContentLayoutView);
            case VIEW_TYPE_ITEM:
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.discovery_card, viewGroup, false);
                return new ViewHolder(v);
            case VIEW_TYPE_LOADING:
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pagination_loading, viewGroup, false);
                return new LoadingViewHolder(view);
        }
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        int viewType = getItemViewType(position);
        switch (viewType)
        {
            case NATIVE_APP_INSTALL_AD_VIEW_TYPE:
                NativeAppInstallAd appInstallAd = (NativeAppInstallAd) postModels.get(position);
                populateAppInstallAdView(appInstallAd, (NativeAppInstallAdView) holder.itemView);
                break;
            case NATIVE_CONTENT_AD_VIEW_TYPE:
                NativeContentAd contentAd = (NativeContentAd) postModels.get(position);
                populateContentAdView(contentAd, (NativeContentAdView) holder.itemView);
                break;
            case VIEW_TYPE_ITEM:
                final ViewHolder userViewHolder = (ViewHolder) holder;
                final HomePostModel menuItem = (HomePostModel) postModels.get(position);

                userViewHolder.date.setText(Constants.date(menuItem.getCreated_at()));
                userViewHolder.user.setText(menuItem.getFirst_name());

                if (menuItem.getDescription().equalsIgnoreCase("")||
                        menuItem.getDescription().equalsIgnoreCase("null"))
                {
                    userViewHolder.read_more.setVisibility(View.GONE);
                    userViewHolder.post_description.setVisibility(View.GONE);
                }
                else
                {
                    userViewHolder.read_more.setVisibility(View.VISIBLE);
                    userViewHolder.post_description.setVisibility(View.VISIBLE);
                }

                /*Tag Data*/
                DataHandler.tagdata(context,userViewHolder.post_description,menuItem);

                /*Post Image*/
                if (menuItem.getAttachmentModels().size()>0)
                {
                    if (menuItem.getAttachmentModels().get(0).getAttachment_type().equalsIgnoreCase("video"))
                    {

                        userViewHolder.video_image.setVisibility(View.VISIBLE);
                        userViewHolder.thumbnail.setVisibility(View.VISIBLE);
                        userViewHolder.post_image.setVisibility(View.GONE);
                        userViewHolder.gif_image.setVisibility(View.GONE);
                        Picasso.with(context).load(Constants.POST_URL+menuItem.getAttachmentModels()
                                .get(0).getThumbnail())
                                .placeholder(R.drawable.noimage).into(userViewHolder.thumbnail);
                    }
                    else if (menuItem.getAttachmentModels().get(0).getAttachment_type().equalsIgnoreCase("gif"))
                    {

                        userViewHolder.video_image.setVisibility(View.VISIBLE);
                        userViewHolder.thumbnail.setVisibility(View.GONE);
                        userViewHolder.post_image.setVisibility(View.GONE);
                        userViewHolder.gif_image.setVisibility(View.VISIBLE);
                        Picasso.with(context).load(Constants.POST_URL+menuItem.getAttachmentModels()
                                .get(0).getThumbnail())
                                .placeholder(R.drawable.noimage).into(userViewHolder.gif_image);
                    }
                    else
                    {
                        userViewHolder.video_image.setVisibility(View.GONE);
                        userViewHolder.thumbnail.setVisibility(View.GONE);
                        userViewHolder.post_image.setVisibility(View.VISIBLE);
                        userViewHolder.gif_image.setVisibility(View.GONE);
                        Picasso.with(context).load(Constants.POST_URL+menuItem.getAttachmentModels()
                                .get(0).getAttachment_name())
                                .placeholder(R.drawable.noimage).into(userViewHolder.post_image);

                        userViewHolder.post_image.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event)
                            {
                                return userViewHolder.gestureDetector1.onTouchEvent(event);
                            }
                        });
                    }
                }
                else
                {
                    userViewHolder.video_image.setVisibility(View.GONE);
                    userViewHolder.post_image.setVisibility(View.GONE);
                    userViewHolder.thumbnail.setVisibility(View.GONE);
                    userViewHolder.gif_image.setVisibility(View.GONE);
                }

                Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+menuItem.getImage()).
                        placeholder(R.drawable.noimage).into(userViewHolder.user_image);

                userViewHolder.likes.setText(menuItem.getLike_count()+" Likes");
                userViewHolder.comment.setText(menuItem.getComment_count()+" Comments");
                userViewHolder.share.setText(menuItem.getShare_count()+" Share");

                if (menuItem.getLike_status().equalsIgnoreCase("0"))
                {
                    userViewHolder.like_image.setImageResource(R.drawable.thumbup_grey);
                }
                else
                {
                    userViewHolder.like_image.setImageResource(R.drawable.thumb_gold);
                }

                userViewHolder.user_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (menuItem.getUser_id().equalsIgnoreCase(SharedPreference.retriveData(context,Constants.ID)))
                        {
                            context.startActivity(new Intent(context,MyProfile.class));
                        }
                        else
                        {
                            context.startActivity(new Intent(context,UserProfile.class)
                                    .putExtra("profile_id",menuItem.getUser_id()));
                        }
                    }
                });

                userViewHolder.go_to_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (menuItem.getUser_id().equalsIgnoreCase(SharedPreference.retriveData(context,Constants.ID)))
                        {
                            context.startActivity(new Intent(context,MyProfile.class));
                        }
                        else
                        {
                            context.startActivity(new Intent(context,UserProfile.class)
                                    .putExtra("profile_id",menuItem.getUser_id()));
                        }
                    }
                });

                break;
            case VIEW_TYPE_LOADING:
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
                break;
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        Object recyclerViewItem = postModels.get(position);
        if (recyclerViewItem instanceof NativeAppInstallAd)
        {
            return NATIVE_APP_INSTALL_AD_VIEW_TYPE;
        }
        else if (recyclerViewItem instanceof NativeContentAd)
        {
            return NATIVE_CONTENT_AD_VIEW_TYPE;
        }
        return postModels.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public CircleImageView user_image;
        public TextView user,date,post_description,read_more,likes,comment,share,likes_test;
        public ImageView video_image,like_image,thumbnail;
        public LinearLayout comment_lay,share_lay,likes_lay,main_view_lay,go_to_profile;
        public ImageView post_image,gif_image;
        GestureDetector gestureDetector,gestureDetector1;

        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(View itemView)
        {
            super(itemView);
            gestureDetector = new GestureDetector(context,new GestureListener());
            gestureDetector1 = new GestureDetector(context,new GestureListenerImage());

            go_to_profile=(LinearLayout)itemView.findViewById(R.id.go_to_profile);
            gif_image=(ImageView)itemView.findViewById(R.id.gif_image);
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

            post_image=(ImageView)itemView.findViewById(R.id.post_image);
            thumbnail=(ImageView)itemView.findViewById(R.id.thumbnail);
            video_image=(ImageView)itemView.findViewById(R.id.video_image);
            like_image=(ImageView)itemView.findViewById(R.id.like_image);

            comment_lay=(LinearLayout)itemView.findViewById(R.id.comment_lay);
            share_lay=(LinearLayout)itemView.findViewById(R.id.share_lay);

            read_more.setOnClickListener(this);
            comment_lay.setOnClickListener(this);
            likes_lay.setOnClickListener(this);
            go_to_profile.setOnClickListener(this);
            user_image.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    itemClick.details(view,getAdapterPosition());
                }
            });

            likes_lay.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    itemClick.likes(view,getAdapterPosition());
                }
            });

            main_view_lay.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    return gestureDetector.onTouchEvent(event);
                }
            });

            share_lay.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    itemClick.share(view,getAdapterPosition());
                }
            });

            likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClick.likeList(v,getAdapterPosition());
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
                    itemClick.details(view,getAdapterPosition());
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
                itemClick.details(main_view_lay,getAdapterPosition());
                return true;
            }
        }

        public class GestureListenerImage extends GestureDetector.SimpleOnGestureListener
        {
            @Override
            public boolean onDown(MotionEvent e)
            {
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e)
            {
                itemClick.likes(post_image,getAdapterPosition());
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e)
            {
                itemClick.showImage(post_image,getAdapterPosition());
                return true;
            }
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
        void likes(View view, int pos);
        void share(View view, int pos);
        void details(View view, int pos);
        void showImage(View view, int pos);
        void likeList(View view, int pos);
        void onLoadMore();
    }

    private void populateAppInstallAdView(NativeAppInstallAd nativeAppInstallAd,
                                          NativeAppInstallAdView adView) {
        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((ImageView) adView.getIconView()).setImageDrawable(nativeAppInstallAd.getIcon()
                .getDrawable());
        ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());

        // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
        // check before trying to display them.
        if (nativeAppInstallAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAppInstallAd.getPrice());
        }

        if (nativeAppInstallAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAppInstallAd.getStore());
        }

        if (nativeAppInstallAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAppInstallAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAppInstallAd);
    }

    private void populateContentAdView(NativeContentAd nativeContentAd,
                                       NativeContentAdView adView)
    {
        // Some assets are guaranteed to be in every NativeContentAd.
        ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(nativeContentAd.getCallToAction());
        ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());

        List<NativeAd.Image> images = nativeContentAd.getImages();

        if (images.size() > 0)
        {
            ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }

        // Some aren't guaranteed, however, and should be checked.
        NativeAd.Image logoImage = nativeContentAd.getLogo();

        if (logoImage == null)
        {
            adView.getLogoView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getLogoView()).setImageDrawable(logoImage.getDrawable());
            adView.getLogoView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeContentAd);
    }
}
