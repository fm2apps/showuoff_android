package app.shouoff.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.joda.time.DateTime;
import java.util.ArrayList;
import app.shouoff.R;
import app.shouoff.UserProfile;
import app.shouoff.common.Constants;
import app.shouoff.common.DataHandler;
import app.shouoff.common.SharedPreference;
import app.shouoff.model.CommentListModel;
import app.shouoff.login.MyProfile;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentDetailAdapter extends RecyclerView.Adapter<CommentDetailAdapter.ViewHolder>
{
    ArrayList<CommentListModel> listModels;
    Context context;
    DeleteCmt deleteCmt;
    String user_id;

    public CommentDetailAdapter(Context context,ArrayList<CommentListModel> listModels,String user_id)
    {
        this.context=context;
        this.listModels=listModels;
        this.user_id=user_id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_adapter, viewGroup, false);
        return new CommentDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        if (user_id.equalsIgnoreCase(SharedPreference.retriveData(context,Constants.ID)))
        {
            holder.delete_comment.setVisibility(View.VISIBLE);
        }
        else
        {
            if (listModels.get(position).getUser_id().equalsIgnoreCase(SharedPreference.retriveData(context,Constants.ID)))
                holder.delete_comment.setVisibility(View.VISIBLE);
            else
                holder.delete_comment.setVisibility(View.GONE);
        }

        /*Tag Data*/
        DataHandler.commentTagData(context,holder.message,listModels.get(position));

        holder.user_name.setText(listModels.get(position).getFirst_name());
        Linkify.addLinks(holder.message, Linkify.WEB_URLS);
        holder.date.setText(Constants.getTimeAgo(new DateTime(listModels.get(position).getCreated_at()).getMillis(),context));
        Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+listModels.
                get(position).getImage()).placeholder(R.drawable.noimage).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return listModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public CircleImageView image;
        public ImageView delete_comment;
        public TextView user_name,message,date;

        public ViewHolder(View itemView)
        {
            super(itemView);
            delete_comment=(ImageView)itemView.findViewById(R.id.delete_comment);
            image=(CircleImageView)itemView.findViewById(R.id.image);
            user_name=(TextView)itemView.findViewById(R.id.user_name);
            message=(TextView)itemView.findViewById(R.id.message);
            date=(TextView)itemView.findViewById(R.id.date);

            delete_comment.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    deleteCmt.select(view,getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener()
             {
                @Override
                public void onClick(View view)
                {
                    try
                    {
                        if (!listModels.get(getLayoutPosition()).getUser_id().equalsIgnoreCase(SharedPreference.retriveData(context,Constants.ID)))
                        {
                            context.startActivity(new Intent(context, UserProfile.class)
                                    .putExtra("profile_id",listModels.get(getAdapterPosition()).getUser_id()));
                        }
                        else
                        {
                            context.startActivity(new Intent(context, MyProfile.class));
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    public void click(DeleteCmt deleteCmt)
    {
        this.deleteCmt=deleteCmt;
    }

    public interface DeleteCmt
    {
        void select(View view,int pos);
    }
}
