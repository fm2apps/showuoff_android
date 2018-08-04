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
import app.shouoff.common.Constants;
import app.shouoff.model.SearchDataModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserShareListAdapter extends RecyclerView.Adapter<UserShareListAdapter.ViewHolder>
{
    Context context;
    ArrayList<SearchDataModel> dataModels;
    UserSelect userSelect;

    public UserShareListAdapter(Context context, ArrayList<SearchDataModel> dataModels)
    {
        this.context = context;
        this.dataModels = dataModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_select_adapter, viewGroup, false);
        return new UserShareListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Picasso.with(context).load(Constants.PROFILE_IMAGE_URL+dataModels.get(position).getImage())
                .placeholder(R.drawable.noimage).into(holder.profile_image);
    }

    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public CircleImageView profile_image;
        public ImageView unselect;

        public ViewHolder(View itemView)
        {
            super(itemView);

            profile_image=(CircleImageView)itemView.findViewById(R.id.profile_image);
            unselect=(ImageView)itemView.findViewById(R.id.unselect);

            unselect.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    userSelect.show(view,getAdapterPosition());
                }
            });
        }
    }

    public void click(UserSelect userSelect)
    {
        this.userSelect=userSelect;
    }

    public interface UserSelect
    {
        void show(View view, int pos);
    }
}
