package app.shouoff.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import app.shouoff.R;
import app.shouoff.common.Constants;
import app.shouoff.common.SharedPreference;
import app.shouoff.model.BlockedUserListModel;

public class BlockedUsersAdapter extends RecyclerView.Adapter<BlockedUsersAdapter.ViewHolder>
{
    Context context;
    ArrayList<BlockedUserListModel> admireModels;
    ShowView showView;

    public BlockedUsersAdapter(Context context, ArrayList<BlockedUserListModel> admireModels)
    {
        this.context=context;
        this.admireModels=admireModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fans_list_adapter, viewGroup, false);
        return new BlockedUsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {

        holder.follow.setText("UnBlock");
        holder.user_name.setText(admireModels.get(position).getNick_name());
        holder.user_email.setText(admireModels.get(position).getEmail());
        Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+admireModels.get(position).getImage())
                .placeholder(R.drawable.noimage).into(holder.profile_image);
    }

    @Override
    public int getItemCount() {
        return admireModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public RoundedImageView profile_image;
        public TextView user_email,user_name,follow;

        public ViewHolder(View itemView)
        {
            super(itemView);
            profile_image=(RoundedImageView)itemView.findViewById(R.id.profile_image);
            user_email=(TextView)itemView.findViewById(R.id.user_email);
            user_name=(TextView)itemView.findViewById(R.id.user_name);
            follow=(TextView)itemView.findViewById(R.id.follow);

            /*itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    showView.profile(view,getAdapterPosition());
                }
            });*/

            follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showView.make(view,getAdapterPosition());
                }
            });
        }
    }

    public void click(ShowView showView)
    {
        this.showView=showView;
    }

    public interface ShowView
    {
        void make(View view, int pos);
        void profile(View view, int pos);
    }
}
