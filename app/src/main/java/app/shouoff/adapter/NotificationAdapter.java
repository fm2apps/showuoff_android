package app.shouoff.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.joda.time.DateTime;
import java.util.ArrayList;

import app.shouoff.NotificationPostDetails;
import app.shouoff.R;
import app.shouoff.UserProfile;
import app.shouoff.activity.HomeActivity;
import app.shouoff.common.CommonService;
import app.shouoff.common.Constants;
import app.shouoff.common.DataHandler;
import app.shouoff.model.NotificationHistoryModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>
{
    Context context;
    ArrayList<NotificationHistoryModel> historyModels;
    Read read;

    public NotificationAdapter(Context context,ArrayList<NotificationHistoryModel> historyModels)
    {
        this.context=context;
        this.historyModels=historyModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_adapter, viewGroup,false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {

        if (!historyModels.get(position).getStatus().equalsIgnoreCase("1"))
        {
            holder.linear.setBackgroundColor(Color.parseColor("#0e005cff"));
        }
        else
        {
            holder.linear.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        DataHandler.notificationText(holder.text,historyModels.get(position).getName()
                +" "+historyModels.get(position).getBody(),"  "+
                Constants.getTimeAgo(new DateTime(historyModels.get(position).getCreated_at()).getMillis(),context));
        Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+historyModels.get(position).getSender_image()).
                placeholder(R.drawable.noimage).into(holder.user_image);
    }

    @Override
    public int getItemCount()
    {
        return historyModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView text;
        private CircleImageView user_image;
        private LinearLayout linear;
        public ViewHolder(View itemView)
        {
            super(itemView);
            linear=(LinearLayout)itemView.findViewById(R.id.linear);
            user_image=(CircleImageView)itemView.findViewById(R.id.user_image);
            text=(TextView)itemView.findViewById(R.id.text);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    read.show(getAdapterPosition(),view);
                }
            });
        }
    }

    public void click(Read read)
    {
        this.read=read;
    }

    public interface Read
    {
        void show(int pos,View view);
    }

}
